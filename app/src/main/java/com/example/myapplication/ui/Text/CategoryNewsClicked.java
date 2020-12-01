package com.example.myapplication.ui.Text;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.PapagoAPI;
import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class CategoryNewsClicked extends Activity {

    private Button korean;
    private Button english;
    private String en="";
    private String ko="";
    private String url="";
    private String body="";
    private TextView context;
    private Button study;
    private String clientId,clientSecret,apiURL;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.news_item);


        Intent intent=getIntent();
        url=intent.getStringExtra("url");
        CategoryNewsClicked.JsoupAsyncTask jsoupAsyncTask = new CategoryNewsClicked.JsoupAsyncTask();
        jsoupAsyncTask.execute();

        TextView title =(TextView)findViewById(R.id.title);
        context =(TextView)findViewById(R.id.context);
        final String titleString =intent.getStringExtra("title");
        title.setText(titleString);


        korean = (Button) findViewById(R.id.korean);
        english = (Button) findViewById(R.id.english);

        //한글로 보기
        korean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ko.equals("")) {
                    try {
                        en=context.getText().toString();
                        ko = new PapagoAPI().execute(en).get();
                        context.setText(ko);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    context.setText(ko);
                }
            }
        });

        //영어로 보기
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                context.setText(en);
            }
        });

        study = (Button) findViewById(R.id.study);
        study.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), TextDoActivity.class);
                        intent.putExtra("title", titleString);
                        intent.putExtra("en", context.getText().toString());
                        intent.putExtra("CP", 1);
                        startActivity(intent);
                    }
                }
        );
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect("http://www.koreaherald.com"+url).get();
                Elements news= doc.select("div.view_con_t");
                for(Element e: news){
                    body += e.text().trim()+"\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            context.setText(body);
        }
    }
}
