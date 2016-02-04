package com.easyapp.loopjhandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public interface JResponseHandler {
    void Success(int StatusCode, JSONObject response);

    void Success(int StatusCode, JSONArray response);

    void NoNetwork();

    void Failure(int statusCode, Header[] headers, byte[] responseBody, Throwable error);
}