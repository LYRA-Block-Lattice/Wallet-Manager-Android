package com.lyrawallet.Api.Network;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Crypto.CryptoSignatures;
import com.lyrawallet.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NetworkRpc extends AsyncTask<String, Void, String[]> implements NetworkRpcSocket.RpcSocketTaskInformer {
    private RpcTaskListener listenerCallBack = null;

    public interface RpcTaskListener {
        void onRpcTaskFinished(String[] output);
    }

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

    RpcTaskInformer mCallBack = null;
    private NetworkRpcSocket Socket = null;
    private state InternalState = state.IDLE;
    private int Id = 1;
    int TimeoutCount = 0;
    private String Api = "";
    private String InstanceName = "";
    private List<String> LastParameters = null;
    private String Password = null;
    private String UrlDst = "";

    public NetworkRpc(String urlDst, @Nullable RpcTaskInformer callback) {
        UrlDst = urlDst;
        this.mCallBack = callback;
    }

    public NetworkRpc(String urlDst) {
        UrlDst = urlDst;
    }

    public NetworkRpc(String urlDst, @Nullable RpcTaskInformer callback, String password) {
        UrlDst = urlDst;
        this.mCallBack = callback;
        this.Password = password;
    }

    public NetworkRpc(String urlDst, String password) {
        UrlDst = urlDst;
        this.Password = password;
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
                InstanceName = params[0];
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
        if(mCallBack != null) {
            NetworkRpc.RpcTaskInformer callBack = mCallBack;
            callBack.onRpcNewEvent(st);
        }
    }

    public void setListener(RpcTaskListener listener) {
        this.listenerCallBack = listener;
    }

    private String[] getState(state st) {
        String[] rsp = new String[]{Api, InstanceName, "UNKNOWN_STATE"};
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
        if(mCallBack != null) {
            mCallBack.onRpcNewEvent(getState(st));
        } else {
            System.out.println("Rpc.java: RPC new event callback = null");
        }
    }

    public state connect() {
        int retryCnt = 0;
        for (; retryCnt < Global.getMaxRpcConnectRetry(); retryCnt++) {
            for (int retryCntToNode = 0; retryCntToNode < Global.getMaxRpcConnectToNodeRetry(); retryCntToNode++) {
                String node = "wss://" + Global.getNodeAddress() + UrlDst;
                System.out.println("Retry: " + retryCntToNode + "; on node: " + "wss://" + node);
                Socket = new NetworkRpcSocket(node, this);
                TimeoutCount = Global.getRpcConnectionTimeout();
                while (!Socket.getConnected() && TimeoutCount != 0) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        sendState(state.CONNECTION_INTERRUPTED);
                        return state.CONNECTION_INTERRUPTED;
                    }
                    TimeoutCount--;
                }
                if (Socket.getConnected()) {
                    break;
                } else {
                    Socket.closeConnection();
                }
            }
            if (Socket.getConnected()) {
                break;
            } else {
                Socket.closeConnection();
            }
            Global.incrementNodeNumber();
        }
        if (retryCnt == Global.getMaxRpcConnectRetry()) {
            sendState(state.CONNECTION_TIMEOUT);
            Global.incrementNodeNumber();
            return state.CONNECTION_TIMEOUT;
        }
        sendState(state.CONNECTED);
        return state.CONNECTED;
    }

    public state send(String api, List<String> args) {
        return sendResponse(api, args);
    }

    private state sendResponse(String api, List<String> args) {
        if(InternalState != state.IDLE) {
            sendState(state.RUNNING);
            return state.RUNNING;
        }
        this.Api = api;
        InternalState = state.RUNNING;
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
                if(arg != null && !arg.equals("")) {
                    jsonArray.put(arg);
                }
            }
            jsonObject.put("params", jsonArray);
        } catch (JSONException e) {
            sendState(state.COMPOSE_MESSAGE_ERROR);
            e.printStackTrace();
            return state.COMPOSE_MESSAGE_ERROR;
        }
        this.LastParameters = args;
        if(!Socket.send(jsonObject.toString().replace("\\", ""))) {
            sendState(state.FOUND_DISCONNECTED);
            return state.FOUND_DISCONNECTED;
        }
        sendState(state.RUNNING);
        return state.RUNNING;
    }

    private void retrySign() {
        InternalState = state.IDLE;
        sendResponse(Api, LastParameters);
    }

    private void composeSendResponse(String rsp) {
        String[] r = new String[]{Api, InstanceName, rsp};
        if(mCallBack != null) {
            mCallBack.onRpcTaskDone(r);
        } else {
            System.out.println("Rpc.java: RPC task done callback = null");
        }
        if (listenerCallBack != null) {
            listenerCallBack.onRpcTaskFinished(r);
        }
    }

    @Override
    public void onRpcSocketTaskDone(NetworkRpcSocket output) {
        String rsp = output.getResponse();
        if(rsp.length() == 0) {
            sendState(state.EMPTY_RESPONSE);
            InternalState = state.IDLE;
            Socket.closeConnection();
            this.cancel(false);
            return;
        }
        try {
            JSONObject obj = new JSONObject(rsp);
            int id = obj.getInt("id");
            double jsonRpc = obj.getDouble("jsonrpc");
            if(!obj.isNull("result")) {
                if(id != Id) {
                    sendState(state.ID_MISSMATCH);
                    InternalState = state.IDLE;
                    return;
                }
                Id = id + 1;
                InternalState = state.IDLE;
                if(Api.equals("History")) {
                    JSONArray array = obj.getJSONArray("result");
                    composeSendResponse(array.toString());
                } else {
                    obj = obj.getJSONObject("result");
                    composeSendResponse(obj.toString());
                }
                Password = "";
                Socket.closeConnection();
                this.cancel(false);
            } else if (!obj.isNull("method")) {
                String method = obj.getString("method");
                if (method.equals("Sign")) {
                    //composeSendResponse("Sign");
                    JSONArray array = obj.getJSONArray("params");
                    String hash = array.getString(1);
                    String pK = Accounts.getPrivateKey(Password);
                    if(pK == null) {
                        sendState(state.NOT_SIGNING);
                        InternalState = state.IDLE;
                        Socket.closeConnection();
                        this.cancel(true);
                        return;
                    }
                    String sig = CryptoSignatures.getSignature(pK, hash);
                    JSONObject jsonObject = new JSONObject();
                    JSONArray jsonArray = new JSONArray ();
                    jsonObject.put("id", id);
                    jsonObject.put("jsonrpc", "2.0");
                    jsonArray.put("p1393");
                    jsonArray.put(sig);
                    jsonObject.put("result", jsonArray);
                    if(!Socket.send(jsonObject.toString())) {
                        sendState(state.FOUND_DISCONNECTED);
                        InternalState = state.IDLE;
                    }
                }
            } else if (!obj.isNull("error")) {
                InternalState = state.IDLE;
                obj = obj.getJSONObject("error");
                if(!obj.isNull("data")) {
                    obj = obj.getJSONObject("data");
                } else if(!obj.isNull("message")) {
                    obj = obj.getJSONObject("message");
                }
                String error = obj.getString("message");
                System.out.println("RPC ERROR: " + error); // BlockSignatureValidationFailed
                InstanceName = error;
                composeSendResponse("error");
                if(error.equals("BlockSignatureValidationFailed")) {
                    retrySign();
                }
                Socket.closeConnection();
                this.cancel(true);
            }
        } catch (JSONException e) {
            sendState(state.ERROR_RECEIVED_JSON);
            e.printStackTrace();
            InternalState = state.IDLE;
            Socket.closeConnection();
            this.cancel(true);
        }
    }
}
