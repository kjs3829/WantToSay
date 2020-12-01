package com.example.myapplication;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class TextdatainputRequest extends StringRequest {
    final static private String URL = "http://jieun959.cafe24.com/textdatainput.php";
    private Map<String, String> parameters;

    public TextdatainputRequest(String title,String context, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("title",title);
        parameters.put("context",context);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
