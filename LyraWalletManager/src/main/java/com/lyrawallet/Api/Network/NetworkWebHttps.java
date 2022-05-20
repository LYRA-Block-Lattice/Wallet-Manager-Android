package com.lyrawallet.Api.Network;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class NetworkWebHttps extends AsyncTask<String, Void, NetworkWebHttps> {

    public static class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            //Log.i("RestUtilImpl", "Approving certificate for " + hostname);
            return true;
        }

    }
    WebHttpsTaskInformer mCallBack = null;
    private WebHttpsTaskListener listenerCallBack = null;

    public interface WebHttpsTaskInformer {
        void onWebHttpsTaskDone(NetworkWebHttps output);
    }
    public interface WebHttpsTaskListener {
        void onWebHttpsTaskFinished(NetworkWebHttps value);
    }

    public NetworkWebHttps(@Nullable WebHttpsTaskInformer callback) {
        this.mCallBack = callback;
    }

    public NetworkWebHttps() {
    }

    private int ResponseCode = -1;
    private String ResponseMessage = "";
    private int ContentLength = -1;
    private String Content = "";
    private boolean Done = false;
    private String InstanceName = "";

    public int getResponseCode() {
        return ResponseCode;
    }

    public String getResponseMessage() {
        return ResponseMessage;
    }

    public int getContentLength() {
        return ContentLength;
    }

    public String getContent() {
        return Content;
    }

    public boolean getDone() {
        boolean ret = Done;
        Done = false;
        return ret;
    }

    public String getInstanceName() {
        return InstanceName;
    }

    @Override
    protected void onPreExecute() {
        ResponseCode = -1;
        ResponseMessage = "";
        ContentLength = -1;
        Done = false;
    }

    @Override
    //params[0] Is the uri
    //params[1] Not mandatory, Instance name to be able to identify the request in callback function.
    protected NetworkWebHttps doInBackground(String... params) {
        ResponseCode = -1;
        ResponseMessage = "";
        ContentLength = -1;
        Done = false;
        InstanceName = "";
        try {
            List<String> paramList = Arrays.asList(params);
            if(paramList.size() > 0 && paramList.size() < 3 ) {
                if (paramList.size() == 2) {
                    InstanceName = params[1];
                }
                Content = get(params[0]);
            } else {
                throw new Exception("WebHttps.java: invalid arguments.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
    @Override
    protected void onPostExecute(NetworkWebHttps s) {
        super.onPostExecute(s);

        if(mCallBack != null) {
            mCallBack.onWebHttpsTaskDone(s);
        }
        if (listenerCallBack != null) {
            listenerCallBack.onWebHttpsTaskFinished(s);
        }
        this.cancel(true);
    }
    public void setListener(WebHttpsTaskListener listener) {
        this.listenerCallBack = listener;
    }

    public String get(String uri) throws IOException {
        Done = false;
        Content = "";
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                System.setProperty("java.net.preferIPv4Stack" , "true");
            }
            URL myUrl = new URL(uri);
            trustAllHosts(); // If not, most of the time will give exception KeyStoreException: BKS not found
            HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
            //conn.setSSLSocketFactory(new NetworkUnsecuredSSLSocketFactory()); // If not, most of the time will give exception KeyStoreException: BKS not found
            //conn.setSSLSocketFactory(getFactorySimple()); // If not, most of the time will give exception KeyStoreException: BKS not found
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                Content += inputLine;
            }
            br.close();
            ContentLength = Content.length();
            ResponseCode = conn.getResponseCode();
            ResponseMessage = conn.getResponseMessage();
        } catch (Exception e) {
            Content = e.getMessage();
        }
        Done = true;
        return Content;
    }

    private static SSLSocketFactory getFactorySimple() throws Exception {
        HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
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
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
