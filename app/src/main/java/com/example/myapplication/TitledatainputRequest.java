package com.example.myapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class TitledatainputRequest extends StringRequest {

    final static private String URL = "http://jieun959.cafe24.com/titledatainput.php";
    private Map<String, String> parameters;

    public TitledatainputRequest(String title, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("title",title);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
