package com.lyrawallet.Api.Network;


import androidx.annotation.Nullable;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;

public class NetworkRpcSocket {
    public interface RpcSocketTaskInformer {
        void onRpcSocketTaskDone(NetworkRpcSocket output);
    }
    final WeakReference<NetworkRpcSocket.RpcSocketTaskInformer> mCallBack;
    private boolean connected = false;
    private String response = "";
    private WebSocketClient mWebSocketClient;
    private NetworkRpcSocket thisInstance = this;
    private String instanceName = "";
    private String serverAddress = "";

    public boolean getConnected() {
        return connected;
    }

    public NetworkRpcSocket(String url, @Nullable RpcSocketTaskInformer callback) {
        this.mCallBack = new WeakReference<>(callback);
        //WebSocketImpl.DEBUG = true;
        serverAddress = url;
        connect();
    }

    private boolean connect() {
        URI uri;
        try {
            uri = new URI(serverAddress);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        mWebSocketClient = new WebSocketClient(uri, new Draft_6455()) {
            @OnOpen
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("WEB_SOCKET:  OPENED.");
                connected = true;
            }
            @OnMessage
            public void onMessage(String s) {
                // Prevent concurrent action if a response is get after a timeout is declared.
                if (mWebSocketClient != null) {
                    response = s;
                    final RpcSocketTaskInformer callBack = mCallBack.get();
                    if (callBack != null) {
                        callBack.onRpcSocketTaskDone(thisInstance);
                    }
                }
            }
            @OnMessage
            public void onMessage(ByteBuffer bytes) {
                // Prevent concurrent action if a response is get after a timeout is declared.
                if (mWebSocketClient != null) {
                    String s = new String(bytes.array());
                    response = s;
                    final RpcSocketTaskInformer callBack = mCallBack.get();
                    if (callBack != null) {
                        callBack.onRpcSocketTaskDone(thisInstance);
                    }
                }
            }
            @OnClose
            public void onClose(int i, String s, boolean b) {
                if (mWebSocketClient != null) {
                    mWebSocketClient.close();
                    mWebSocketClient = null;
                }
                System.out.println("WEB_SOCKET: CONNECTION CLOSED.");
                connected = false;
                this.close();
            }
            @OnError
            public void onError(Exception e) {
                if (mWebSocketClient != null) {
                    mWebSocketClient.close();
                    mWebSocketClient = null;
                }
                System.out.println("WEB_SOCKET: ERROR: " + e.getMessage());
                this.close();
            }
        };
        NetworkUnsecuredSSLSocketFactory factory = new NetworkUnsecuredSSLSocketFactory();
        try {
            mWebSocketClient.setSocketFactory(factory);
            mWebSocketClient.setReuseAddr(true);
            mWebSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean openConnection() {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
            mWebSocketClient = null;
        }
        return connect();
    }

    public void closeConnection() {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
            mWebSocketClient = null;
        }
    }

    public boolean send(String s, @Nullable String instName) {
        instanceName = instName;
        try {
            if (connected) {
                mWebSocketClient.send(s);
                return true;
            } else {
                throw new Exception("RpcSocket.java: Not yet connected.");
            }
        } catch (Exception e) {
            System.out.println("WEB_SOCKET: ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean send(String s) {
        return send(s, "");
    }

    public String getResponse() {
        String ret = response;
        response = "";
        return  ret;
    }

    public String getInstanceName() {
        return instanceName;
    }
}
