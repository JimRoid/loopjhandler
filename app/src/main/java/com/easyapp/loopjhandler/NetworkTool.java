package com.easyapp.loopjhandler;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.KeyStore;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by easyapp_jim on 15/3/19.
 */
public class NetworkTool {
    protected Context context;
    protected static AsyncHttpClient asyncHttpClient;
    protected boolean isShowLog = true;
    protected String baseUrl = "";
    private AlertDialog alertDialog;

    static {
        asyncHttpClient = new AsyncHttpClient();
        AsyncHttpClient.allowRetryExceptionClass(javax.net.ssl.SSLException.class);
    }


    public NetworkTool(Context context, String baseUrl) {
        this.context = context;
        this.baseUrl = baseUrl;
        if (baseUrl.contains("https")) {
            ssl_enable();
        }
    }

    public NetworkTool(Activity context, String baseUrl) {
        this.context = context;
        this.baseUrl = baseUrl;

        if (baseUrl.contains("https")) {
            ssl_enable();
        }
    }

    private void ssl_enable() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            asyncHttpClient.setSSLSocketFactory(sf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDialog(AlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }


    public void GetRandomChinese(int limit, int n, ResponseHandler responseHandler) {
        String route = "http://more.handlino.com/sentences.json?limit=" + limit + "&n=" + n;
        GET(route, responseHandler);
    }

    protected void removeAllHeaders() {
        asyncHttpClient.removeAllHeaders();
    }

    protected void removeHeaders(String header) {
        asyncHttpClient.removeHeader(header);
    }

    protected void addHeader(String header, String value) {
        asyncHttpClient.addHeader(header, value);
    }

    protected void GET(String route, RequestParams params, ResponseHandler responseHandler) {
        if (!isNetworkConnected(context)) {
            responseHandler.NoNetwork();
            showNetworkCheck();
            return;
        }

        if (!route.startsWith("http"))
            route = baseUrl + route;

        Logger("route" + route);
        Logger("params: " + params);
        asyncHttpClient.get(context, route, params, Default_jsonHttpResponseHandler(responseHandler));
    }

    protected void GET(String route, ResponseHandler responseHandler) {
        if (!isNetworkConnected(context)) {
            responseHandler.NoNetwork();
            showNetworkCheck();
            return;
        }

        if (!route.startsWith("http"))
            route = baseUrl + route;

        Logger("route: " + route);
        asyncHttpClient.get(context, route, Default_jsonHttpResponseHandler(responseHandler));
    }

    protected void GET(String route, HttpEntity httpEntity, ResponseHandler responseHandler) {
        if (!isNetworkConnected(context)) {
            responseHandler.NoNetwork();
            showNetworkCheck();
            return;
        }

        if (!route.startsWith("http"))
            route = baseUrl + route;

        Logger("route: " + route);
        String content_type = "text/plain charset=utf-8";
        asyncHttpClient.get(context, route, httpEntity, content_type, DefaultHttpResponseHandler(responseHandler));
    }

    protected void POST(String route, RequestParams params, ResponseHandler responseHandler) {
        POST(route, params, false, responseHandler);
    }

    protected void POST(String route, RequestParams params, boolean isLogin, ResponseHandler responseHandler) {
        if (!isNetworkConnected(context)) {
            responseHandler.NoNetwork();
            showNetworkCheck();
            return;
        }

        if (!route.startsWith("http"))
            route = baseUrl + route;

        Logger("route: " + route);
        Logger("params: " + params);
        asyncHttpClient.post(context, route, params, Default_jsonHttpResponseHandler(responseHandler));
    }

    protected void POST(String route, StringEntity stringEntity, ResponseHandler responseHandler) {
        String content_type = "text/plain charset=utf-8";
        POST(route, stringEntity, content_type, responseHandler);
    }

    protected void POST(String route, StringEntity stringEntity, String content_type, ResponseHandler responseHandler) {
        if (!isNetworkConnected(context)) {
            responseHandler.NoNetwork();
            showNetworkCheck();
            return;
        }

        if (!route.startsWith("http"))
            route = baseUrl + route;


        Logger("route: " + route);
        asyncHttpClient.post(context, route, stringEntity, content_type, DefaultHttpResponseHandler(responseHandler));
    }

    protected void DELETE(String route, ResponseHandler responseHandler) {
        if (!isNetworkConnected(context)) {
            responseHandler.NoNetwork();
            showNetworkCheck();
            return;
        }

        if (!route.startsWith("http"))
            route = baseUrl + route;

        Logger("route: " + route);
        asyncHttpClient.delete(context, route, DefaultHttpResponseHandler(responseHandler));
    }

    protected void PUT(String route, RequestParams params, ResponseHandler responseHandler) {
        if (!isNetworkConnected(context)) {
            responseHandler.NoNetwork();
            showNetworkCheck();
            return;
        }

        if (!route.startsWith("http"))
            route = baseUrl + route;

        Logger("route: " + route);
        Logger("params: " + params);
        asyncHttpClient.put(context, route, params, DefaultHttpResponseHandler(responseHandler));
    }

    protected AsyncHttpResponseHandler DefaultHttpResponseHandler(final ResponseHandler responseHandler) {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Logger(response);
                responseHandler.Success(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    if (responseBody.length > 0) {
                        String response = new String(responseBody);
                        Logger(response);
                        responseHandler.Failure(statusCode, headers, responseBody, error);
                    }
                }
            }
        };
    }

    protected JsonHttpResponseHandler Default_jsonHttpResponseHandler(final ResponseHandler responseHandler) {
        return new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                responseHandler.Success(statusCode, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Logger(response.toString());
                responseHandler.Success(statusCode, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                responseHandler.Failure(statusCode, headers, responseString.getBytes(), throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                responseHandler.Failure(statusCode, headers, null, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                responseHandler.Failure(statusCode, headers, null, throwable);
            }
        };
    }


    protected void Logger(String message) {
        if (isShowLog) {
            Logger.d(message);
        }
    }

    protected void showNetworkCheck() {
        if (alertDialog != null) {
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    public boolean isWifi() {
        //WIFI
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }


        return false;
    }

    public boolean isMobile() {
        //MOBILE
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();

        if (network == null || !network.isConnected()) {
            return false;
        } else {
            return network.isAvailable();
        }
    }

    public static void support_check_network_with_dialog(Activity activity, final Dialog_call_cancel dialog_call_cancel) {
        if (!isNetworkConnected(activity)) {
            new AlertDialog.Builder(activity).setTitle(R.string.hint_network_title).setMessage(R.string.hint_network_error).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dialog_call_cancel.onDismiss();
                }
            }).show();
        }
    }

    public interface Dialog_call_cancel {
        void onDismiss();
    }
}
