package com.lyrawallet.Api.Network;

public class ProbeNetwork {
    public static void Rpc() {
        RpcSocket socket = new RpcSocket("wss://161.97.166.188:4504/api/v1/socket", null);
        while(!socket.connected);
        socket.send("{\"id\":1,\"jsonrpc\":\"2.0\",\"method\":\"History\",\"params\":[\"LF6LpgcrANQWrErPcLREAbKzJg9DLeuXXa45cz5hKsUng7aJ2zCrAgHbtkSXv5dXiEfUB8ypN8i3daUkmiJwcX8cbXSv5U\",\"0\",\"1639860329843\",\"0\"]}");
        String rsp;
        do {
            rsp = socket.getResponse();
        } while(rsp.length() == 0);
    }

    public static void HttpsSatic() {
        WebHttps webHttpsTask = new WebHttps(null);
        webHttpsTask.setListener(new WebHttps.WebHttpsTaskListener() {
            @Override
            public void onWebHttpsTaskFinished(WebHttps instance) {
                System.out.println("Static function thread: " + instance.getContent());
            }
        });
        webHttpsTask.execute("https://api.latoken.com/v2/ticker");
     }
}
