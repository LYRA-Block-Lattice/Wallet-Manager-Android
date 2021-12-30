package com.lyrawallet.Api.Network;

import android.os.AsyncTask;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class WebHttps extends AsyncTask<String, Void, WebHttps> {
    public interface WebHttpsTaskInformer {
        void onWebHttpsTaskDone(WebHttps output);
    }
    public interface WebHttpsTaskListener {
        void onWebHttpsTaskFinished(WebHttps value);
    }
    final WeakReference<WebHttpsTaskInformer> mCallBack;
    private WebHttpsTaskListener listenerCallBack = null;

    public WebHttps(@Nullable WebHttpsTaskInformer callback) {
        this.mCallBack = new WeakReference<>(callback);
    }

    private int responseCode = -1;
    private String responseMessage = "";
    private int contentLength = -1;
    private String content = "";
    private boolean done = false;
    private String instanceName = "";

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContent() {
        return content;
    }

    public boolean getDone() {
        boolean ret = done;
        done = false;
        return ret;
    }

    public String getInstanceName() {
        return instanceName;
    }

    @Override
    protected void onPreExecute() {
        responseCode = -1;
        responseMessage = "";
        contentLength = -1;
        done = false;
    }

    @Override
    //params[0] Is the uri
    //params[1] Not mandatory, Instance name to be able to indentify the request in callback function.
    protected WebHttps doInBackground(String... params) {
        responseCode = -1;
        responseMessage = "";
        contentLength = -1;
        done = false;
        instanceName = "";
        try {
            List<String> paramList = Arrays.asList(params);
            if(paramList.size() > 0 && paramList.size() < 3 ) {
                if (paramList.size() == 2) {
                    instanceName = params[1];
                }
                content = get(params[0]);
            } else {
                throw new Exception("WebHttps.java: invalid arguments.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
    @Override
    protected void onPostExecute(WebHttps s) {
        super.onPostExecute(s);
        final WebHttpsTaskInformer callBack = mCallBack.get();

        if(callBack != null) {
            callBack.onWebHttpsTaskDone(s);
        }
        if (listenerCallBack != null) {
            listenerCallBack.onWebHttpsTaskFinished(s);
        }
    }
    public void setListener(WebHttpsTaskListener listener) {
        this.listenerCallBack = listener;
    }

    public String get(String uri) throws IOException {
        done = false;
        content = "";
        try {
            URL myUrl = new URL(uri);
            //trustAllHosts(); // If not, most of the time will give exception KeyStoreException: BKS not found
            HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
            conn.setSSLSocketFactory(new UnsecuredSSLSocketFactory()); // If not, most of the time will give exception KeyStoreException: BKS not found
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                content += inputLine;
            }
            br.close();
            contentLength = content.length();
            responseCode = conn.getResponseCode();
            responseMessage = conn.getResponseMessage();
        } catch (Exception e) {
            content = e.getMessage();
        }
        done = true;
        return content;
    }

    private static SSLSocketFactory getFactorySimple() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        return context.getSocketFactory();
    }

    private static void trustAllHosts() {
        X509TrustManager easyTrustManager = new X509TrustManager() {

            public void checkClientTrusted(
                    X509Certificate[] chain,
                    String authType) {
            }
            public void checkServerTrusted(
                    X509Certificate[] chain,
                    String authType) throws CertificateException {
            }
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {easyTrustManager};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
