package com.easyapp.loopjhandler;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

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

    public void removeAllHeaders() {
        asyncHttpClient.removeAllHeaders();
    }

    public void removeHeaders(String header) {
        asyncHttpClient.removeHeader(header);
    }

    public void addHeader(String header, String value) {
        asyncHttpClient.addHeader(header, value);
    }

    public void GET(String route, RequestParams params, ResponseHandler responseHandler) {
        if (!route.startsWith("http")) {
            route = baseUrl + route;
        }

        Logger("route" + route);
        Logger("params: " + params);
        asyncHttpClient.get(context, route, params, DefaultJsonHttpResponseHandler(responseHandler));
    }

    public void GET(String route, ResponseHandler responseHandler) {
        if (!route.startsWith("http")) {
            route = baseUrl + route;
        }

        Logger("route: " + route);
        asyncHttpClient.get(context, route, DefaultJsonHttpResponseHandler(responseHandler));
    }

    public void GET(String route, HttpEntity httpEntity, ResponseHandler responseHandler) {
        if (!route.startsWith("http")) {
            route = baseUrl + route;
        }

        Logger("route: " + route);
        String content_type = "text/plain charset=utf-8";
        asyncHttpClient.get(context, route, httpEntity, content_type, DefaultHttpResponseHandler(responseHandler));
    }


    public void POST(String route, RequestParams params, ResponseHandler responseHandler) {
        if (!route.startsWith("http")) {
            route = baseUrl + route;
        }

        Logger("route: " + route);
        Logger("params: " + params);
        asyncHttpClient.post(context, route, params, DefaultJsonHttpResponseHandler(responseHandler));
    }

    public void POST(String route, StringEntity stringEntity, ResponseHandler responseHandler) {
        String content_type = "text/plain charset=utf-8";
        POST(route, stringEntity, content_type, responseHandler);
    }

    public void POST(String route, StringEntity stringEntity, String content_type, ResponseHandler responseHandler) {
        if (!route.startsWith("http")) {
            route = baseUrl + route;
        }

        Logger("route: " + route);
        asyncHttpClient.post(context, route, stringEntity, content_type, DefaultHttpResponseHandler(responseHandler));
    }

    public void DELETE(String route, ResponseHandler responseHandler) {
        if (!route.startsWith("http")) {
            route = baseUrl + route;
        }
        Logger("route: " + route);
        asyncHttpClient.delete(context, route, DefaultHttpResponseHandler(responseHandler));
    }

    public void PUT(String route, RequestParams params, ResponseHandler responseHandler) {
        if (!route.startsWith("http")) {
            route = baseUrl + route;
        }
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

    protected JsonHttpResponseHandler DefaultJsonHttpResponseHandler(final ResponseHandler responseHandler) {
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
            Log.d("log", message);
        }
    }


}
