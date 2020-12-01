package com.example.myapplication.ui.Text;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SearchNews extends AppCompatActivity {

    private ListView newsListView;
    private List<News> newsList;

    public String htmlPageUrl; //파싱할 홈페이지의 URL주소

    int cnt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company);

        Intent intent=getIntent();
        htmlPageUrl=intent.getStringExtra("url");
        Log.d("@@@@@",htmlPageUrl);
        //리스트뷰 클
        newsListView = (ListView)findViewById(R.id.NewsListView);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), CategoryNewsClicked.class);

                intent.putExtra("title",newsList.get(position).getTitle());
                intent.putExtra("url",newsList.get(position).getContext());
                intent.putExtra("date",newsList.get(position).getDate());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                String title="";
                String url="";
                String date = "";
                String resId="";
                String type="";

                Document doc = Jsoup.connect(htmlPageUrl).get();
                newsList = new ArrayList<News>();

                Elements news= doc.select("div[class=sch] div[class=site_width] div[class=sch_result] ul[class=main_sec_li]").select("li a");
                for(Element e: news){
                    resId = e.select("div[class=main_l_img]").select("img").attr("src");
                    title = e.select("div.main_l_t1").text();
                    date = e.select("div[class=main_l_t2]").select("span").text();
                    type = e.select("div[class=main_l_t2]").text();
                    type = type.replace(date,"");
                    String on = "on ";
                    on = on.concat(date);
                    url=e.attr("href");
                    newsList.add(new News(title,type,on,url,resId));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final NewsListAdapter adapter = new NewsListAdapter(getApplicationContext(),newsList);
            newsListView.setAdapter(adapter);
        }
    }


}