package com.easyapp.loopjhandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by easyapp_jim on 2015/10/10.
 */
public class ResponseHandler implements JResponseHandler, AsyncResponseHandler {
    @Override
    public void NoNetwork() {

    }

    @Override
    public void Success(int StatusCode, JSONObject response) {

    }

    @Override
    public void Success(int StatusCode, JSONArray response) {

    }



    @Override
    public void Success(int statusCode, Header[] headers, byte[] responseBody) {

    }

    @Override
    public void Failure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

    }
}
