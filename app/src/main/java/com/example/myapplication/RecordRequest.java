package com.example.myapplication;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RecordRequest extends StringRequest {
    final static private String URL = "http://jieun959.cafe24.com/recordinput.php";
    private Map<String,String> parameters;

    public RecordRequest(String title,String score, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userid",Userinfo.userID);
        parameters.put("title",title);
        parameters.put("score",score);
        int score_int=Integer.parseInt(score);
        if(score_int>90){
            parameters.put("star","3");
        }
        else if (score_int>75){
            parameters.put("star","2");
        }
        else{
            parameters.put("star","1");
        }
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}