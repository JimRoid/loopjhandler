package com.jim.loophandler.app;

import android.app.Activity;
import android.os.Bundle;

import com.easyapp.loopjhandler.NetworkTool;
import com.easyapp.loopjhandler.ResponseHandler;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {


    private NetworkTool networkTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkTool = new NetworkTool(this, "https://jsonplaceholder.typicode.com/");
        networkTool.GET("posts", new ResponseHandler() {
            @Override
            public void Success(int StatusCode, JSONArray response) {
                Logger.e(new Gson().toJson(response));
            }

            @Override
            public void Success(int StatusCode, JSONObject response) {
                Logger.e(new Gson().toJson(response));
            }
        });
    }
}
