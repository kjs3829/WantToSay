package com.example.myapplication.ui.Text;

import android.content.Intent;
import android.icu.util.ULocale;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TextFragment extends Fragment {
    private ListView newsListView;
    private List<News> newsList;
    private EditText et_search;
    private Button search;
    private Button input;
    private Button category1;
    private Button category2;
    private Button category3;
    private Button category4;
    private Button category5;
    private Button category6;
    private Button category7;
    private Button category8;
    public String htmlPageUrl;
    NewsListAdapter adapter;

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_text, container, false);

        et_search = root.findViewById(R.id.et_search);
        newsListView = (ListView) root.findViewById(R.id.NewsListView);
        newsListView.setVerticalScrollBarEnabled(false);
        setListViewHeightBasedOnChildren(newsListView);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CategoryNewsClicked.class);

                intent.putExtra("title",newsList.get(position).getTitle());
                intent.putExtra("url",newsList.get(position).getContext());
                intent.putExtra("date",newsList.get(position).getDate());
                startActivity(intent);
            }
        });

        //신문사버튼 활성
        category1 = root.findViewById(R.id.category1);
        category2 = root.findViewById(R.id.category2);
        category3 = root.findViewById(R.id.category3);
        category4 = root.findViewById(R.id.category4);
        category5 = root.findViewById(R.id.category5);
        category6 = root.findViewById(R.id.category6);
        category7 = root.findViewById(R.id.category7);
        category8 = root.findViewById(R.id.category8);

        category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryClicked.class);
                intent.putExtra("url", "http://www.koreaherald.com/list.php?ct=020100000000");
                startActivity(intent);
            }
        });
        category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryClicked.class);
                intent.putExtra("url", "http://www.koreaherald.com/list.php?ct=020200000000");
                startActivity(intent);
            }
        });
        category3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryClicked.class);
                intent.putExtra("url", "http://www.koreaherald.com/list.php?ct=021900000000");
                startActivity(intent);
            }
        });
        category4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryClicked.class);
                intent.putExtra("url", "http://www.koreaherald.com/list.php?ct=020300000000");
                startActivity(intent);
            }
        });
        category5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryClicked.class);
                intent.putExtra("url", "http://www.koreaherald.com/list.php?ct=020400000000");
                startActivity(intent);
            }
        });
        category6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryClicked.class);
                intent.putExtra("url", "http://www.koreaherald.com/list.php?ct=020500000000");
                startActivity(intent);
            }
        });
        category7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryClicked.class);
                intent.putExtra("url", "http://www.koreaherald.com/list.php?ct=021200000000");
                startActivity(intent);
            }
        });
        category8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CategoryClicked.class);
                intent.putExtra("url", "http://www.koreaherald.com/list.php?ct=020600000000");
                startActivity(intent);
            }
        });

        //검색버튼 활성
        search = (Button) root.findViewById(R.id.search);

        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String text = et_search.getText().toString();
                Intent intent = new Intent(getContext(), SearchNews.class);
                intent.putExtra("url",  "http://www.koreaherald.com/search/index.php?q="+text+"&sort=1&mode=list&kr=0");
                startActivity(intent);
                }
            });

        //직접입력버튼 활성
        input = (Button) root.findViewById(R.id.input);
        input.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getContext(), SelfInput.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        htmlPageUrl = "http://www.koreaherald.com/index.php";
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

                String title = "";
                String url = "";
                String resId = "";

                Document doc = Jsoup.connect(htmlPageUrl).get();
                newsList = new ArrayList<News>();
                int i=1;
                Elements news = doc.select("ul[class=main_r_li1]").select("li a");
                for (Element e : news) {
                    resId = e.select("div[class=main_r_li1_img]").select("img").attr("src");
                    title = e.select("div.main_r_li1_t1").text();
                    url = e.select("a").attr("href");
                    newsList.add(new News(title, "   # "+(i++), "", url, resId));
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final NewsListAdapter adapter = new NewsListAdapter(getActivity(), newsList);
            newsListView.setAdapter(adapter);
            newsListView.setVerticalScrollBarEnabled(false);
            setListViewHeightBasedOnChildren(newsListView);

        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        double totalHeight = 0;

        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight()+listView.getDividerHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        totalHeight*=1.3;
        params.height = (int) Math.ceil(totalHeight);
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}