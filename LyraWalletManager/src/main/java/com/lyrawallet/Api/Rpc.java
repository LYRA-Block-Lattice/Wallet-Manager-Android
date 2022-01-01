package com.lyrawallet.Api;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.Network.RpcSocket;
import com.lyrawallet.Crypto.Signatures;
import com.lyrawallet.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rpc extends AsyncTask<String, Void, String[]> implements RpcSocket.RpcSocketTaskInformer {
    public interface RpcTaskInformer {
        void onRpcTaskDone(String[] output);
        void onRpcNewEvent(String[] output);
    }

    public enum state {
        IDLE,
        DONE,
        RUNNING,
        CONNECTION_TIMEOUT,
        CONNECTION_INTERRUPTED,
        CONNECTION_ERROR,
        CONNECTED,
        EMPTY_RESPONSE,
        ID_MISSMATCH,
        COMPOSE_MESSAGE_ERROR,
        FOUND_DISCONNECTED,
        ERROR_RECEIVED_JSON,
        NOT_SIGNING
    }

    final WeakReference<RpcTaskInformer> mCallBack;
    private RpcSocket socket = null;
    private state internalState = state.IDLE;
    private int Id = 1;
    int timeoutCount = 0;
    private String api = "";
    private String instanceName = "";
    List<String> lastParameters = null;
    private String password = null;

    public Rpc(@Nullable RpcTaskInformer callback) {
        this.mCallBack = new WeakReference<>(callback);
    }

    public Rpc(@Nullable RpcTaskInformer callback, String password) {
        this.mCallBack = new WeakReference<>(callback);
        this.password = password;
    }

    protected void onPreExecute() {
    }
    @Override
    //params[0] Is the identifier to be able to identify the request in callback function.
    //params[1] Is the uri
    //params[2] API name
    //params[3...] Params to compose the request.
    protected String[] doInBackground(String... params) {
        try {
            List<String> paramList = Arrays.asList(params);
            if(paramList.size() > 2) {
                List<String> argList = new ArrayList<>();
                for (int i = 2; i < paramList.size(); i++) {
                    argList.add(params[i]);
                }
                instanceName = params[0];
                send(params[1], argList);
            } else {
                throw new Exception("Rpc.java: invalid arguments: Given" + paramList.size() + " instead of minimum 4 arguments.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getState(state.DONE);
    }
    @Override
    protected void onPostExecute(String[] st) {
        super.onPostExecute(st);
        final Rpc.RpcTaskInformer callBack = mCallBack.get();
        if(callBack != null) {
            callBack.onRpcNewEvent(st);
        }
    }

    private String[] getState(state st) {
        String[] rsp = new String[]{api, instanceName, "UNKNOWN_STATE"};
        switch (st) {
            case IDLE: rsp[2] = "IDLE"; break;
            case DONE: rsp[2] = "DONE"; break;
            case RUNNING: rsp[2] = "RUNNING"; break;
            case CONNECTION_TIMEOUT: rsp[2] = "CONNECTION_TIMEOUT"; break;
            case CONNECTION_ERROR: rsp[2] = "CONNECTION_ERROR"; break;
            case CONNECTION_INTERRUPTED: rsp[2] = "CONNECTION_INTERRUPTED"; break;
            case CONNECTED: rsp[2] = "CONNECTED"; break;
            case EMPTY_RESPONSE: rsp[2] = "EMPTY_RESPONSE"; break;
            case ID_MISSMATCH: rsp[2] = "ID_MISSMATCH"; break;
            case COMPOSE_MESSAGE_ERROR: rsp[2] = "COMPOSE_MESSAGE_ERROR"; break;
            case FOUND_DISCONNECTED: rsp[2] = "FOUND_DISCONNECTED"; break;
            case ERROR_RECEIVED_JSON: rsp[2] = "ERROR_RECEIVED_JSON"; break;
        }
        return rsp;
    }

    private void sendState(state st) {
        final Rpc.RpcTaskInformer callBack = mCallBack.get();
        if(callBack != null) {
            callBack.onRpcNewEvent(getState(st));
        } else {
            System.out.println("Rpc.java: RPC new event callback = null");
        }
    }

    public state connect() {
        final Rpc.RpcTaskInformer callBack = mCallBack.get();
        int retryCnt = 10;
        for (; retryCnt > 0; retryCnt--) {
            socket = new RpcSocket(Global.getNodeAddress(), this);
            timeoutCount = Global.rpcConnectionTimeout;
            while (!socket.connected && timeoutCount != 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    sendState(state.CONNECTION_INTERRUPTED);
                    return state.CONNECTION_INTERRUPTED;
                }
                timeoutCount--;
            }
            if (socket.connected) {
                break;
            }
        }
        if (retryCnt == 0) {
            sendState(state.CONNECTION_TIMEOUT);
            return state.CONNECTION_TIMEOUT;
        }
        sendState(state.CONNECTED);
        return state.CONNECTED;
    }

    public state send(String api, List<String> args) {
        return sendResponse(api, args);
    }

    private state sendResponse(String api, List<String> args) {
        final Rpc.RpcTaskInformer callBack = mCallBack.get();
        if(internalState != state.IDLE) {
            sendState(state.RUNNING);
            return state.RUNNING;
        }
        this.api = api;
        internalState = state.RUNNING;
        state st = connect();
        if (st != state.CONNECTED) {
            return st;
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray ();
        try {
            jsonObject.put("id", Id);
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", api);
            for (String arg: args) {
                jsonArray.put(arg);
            }
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            sendState(state.COMPOSE_MESSAGE_ERROR);
            e.printStackTrace();
            return state.COMPOSE_MESSAGE_ERROR;
        }
        this.lastParameters = args;
        if(!socket.send(jsonObject.toString())) {
            sendState(state.FOUND_DISCONNECTED);
            return state.FOUND_DISCONNECTED;
        }
        sendState(state.RUNNING);
        return state.RUNNING;
    }

    private void retrySign() {
        internalState = state.IDLE;
        sendResponse(api, lastParameters);
    }

    private void composeSendResponse(String rsp) {
        final Rpc.RpcTaskInformer callBack = mCallBack.get();
        String[] r = new String[]{api, instanceName, rsp};
        if(callBack != null) {
            callBack.onRpcTaskDone(r);
        } else {
            System.out.println("Rpc.java: RPC task done callback = null");
        }
    }

    @Override
    public void onRpcSocketTaskDone(RpcSocket output) {
        final Rpc.RpcTaskInformer callBack = mCallBack.get();
        String rsp = output.getResponse();
        if(rsp.length() == 0) {
            sendState(state.EMPTY_RESPONSE);
            internalState = state.IDLE;
            return;
        }
        try {
            JSONObject obj = new JSONObject(rsp);
            int id = obj.getInt("id");
            double jsonRpc = obj.getDouble("jsonrpc");
            if(!obj.isNull("result")) {
                if(id != Id) {
                    sendState(state.ID_MISSMATCH);
                    internalState = state.IDLE;
                    return;
                }
                Id = id + 1;
                internalState = state.IDLE;
                if(api.equals("History")) {
                    JSONArray array = obj.getJSONArray("result");
                    composeSendResponse(array.toString());
                } else {
                    obj = obj.getJSONObject("result");
                    composeSendResponse(obj.toString());
                }
                password = "";
            } else if (!obj.isNull("method")) {
                String method = obj.getString("method");
                if (method.equals("Sign")) {
                    composeSendResponse("Sign");
                    JSONArray array = obj.getJSONArray("params");
                    String hash = array.getString(1);
                    String pK = Accounts.getPrivateKey(password);
                    if(pK == null) {
                        sendState(state.NOT_SIGNING);
                        internalState = state.IDLE;
                        return;
                    }
                    String sig = Signatures.GetSignature(pK, hash);
                    JSONObject jsonObject = new JSONObject();
                    JSONArray jsonArray = new JSONArray ();
                    jsonObject.put("id", id);
                    jsonObject.put("jsonrpc", "2.0");
                    jsonArray.put("p1393");
                    jsonArray.put(sig);
                    jsonObject.put("result", jsonArray);
                    if(!socket.send(jsonObject.toString())) {
                        sendState(state.FOUND_DISCONNECTED);
                        internalState = state.IDLE;
                    }
                }
            } else if (!obj.isNull("error")) {
                composeSendResponse("error");
                internalState = state.IDLE;
                obj = obj.getJSONObject("error");
                obj = obj.getJSONObject("data");
                String error = obj.getString("message");
                System.out.println("RPC ERROR: " + error); // BlockSignatureValidationFailed
                if(error.equals("BlockSignatureValidationFailed")) {
                    retrySign();
                }
            }
        } catch (JSONException e) {
            sendState(state.ERROR_RECEIVED_JSON);
            e.printStackTrace();
            internalState = state.IDLE;
        }
    }
}
