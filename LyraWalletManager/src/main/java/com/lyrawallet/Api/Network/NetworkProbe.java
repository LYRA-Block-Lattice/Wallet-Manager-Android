package com.lyrawallet.Api.Network;

public class NetworkProbe {
    public static void rpc() {
        NetworkRpcSocket socket = new NetworkRpcSocket("wss://161.97.166.188:4504/api/v1/socket", null);
        while(!socket.getConnected());
        socket.send("{\"id\":1,\"jsonrpc\":\"2.0\",\"method\":\"History\",\"params\":[\"LF6LpgcrANQWrErPcLREAbKzJg9DLeuXXa45cz5hKsUng7aJ2zCrAgHbtkSXv5dXiEfUB8ypN8i3daUkmiJwcX8cbXSv5U\",\"0\",\"1639860329843\",\"0\"]}");
        String rsp;
        do {
            rsp = socket.getResponse();
        } while(rsp.length() == 0);
    }

    public static void httpsSatic() {
        NetworkWebHttps webHttpsTask = new NetworkWebHttps(null);
        webHttpsTask.setListener(new NetworkWebHttps.WebHttpsTaskListener() {
            @Override
            public void onWebHttpsTaskFinished(NetworkWebHttps instance) {
                System.out.println("Static function thread: " + instance.getContent());
            }
        });
        webHttpsTask.execute("https://api.latoken.com/v2/ticker");
     }
}