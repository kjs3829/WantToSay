package com.example.myapplication.ui.Record;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;
import com.example.myapplication.Userinfo;
import com.example.myapplication.ui.Text.News;
import com.example.myapplication.ui.Text.NewsListAdapter;
import com.example.myapplication.ui.profile.ProfileFragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

public class RecordFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private ListView listView;
    RecordAdapter adapter;
    TextView dateView;
    TextView titleView;
    TextView scoreView;
    RatingBar starView;
    phpdo task;
    String recording;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_record, container, false);
        super.onCreate(savedInstanceState);

        String id = "0007790A6F10";
        String name = "Red";
        task = new phpdo();
        task.execute(id, name);

        dateView = (TextView)root.findViewById(R.id.dateView);
        titleView = (TextView)root.findViewById(R.id.titleView);
        scoreView = (TextView)root.findViewById(R.id.scoreView);
        starView = (RatingBar)root.findViewById(R.id.starView);

        listView = (ListView) root.findViewById(R.id.listView);
        return root;
    }

    class RecordAdapter extends BaseAdapter {
        ArrayList<RecordItem> items = new ArrayList<RecordItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(RecordItem item){
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecorditemView view = null;
            if(convertView == null){
                view = new RecorditemView(getContext());
            } else {
                view = (RecorditemView) convertView;
            }

            RecordItem item = items.get(position);
            view.setDate(item.getDate());
            view.setTitle(item.getTitle());
            view.setScore(item.getScore());
            view.setStar(item.getStar());
            return view;
        }
    }

    private class phpdo extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){

        }
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String link = "http://jieun959.cafe24.com/record.php?ID=" + Userinfo.userID;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                in.close();
                recording=sb.toString();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){
            adapter = new RecordAdapter();

            try {
                JSONArray jsonArray=new JSONArray(recording);
                for(int i = 0 ; i<jsonArray.length(); i++) {
                    JSONObject resultObject = jsonArray.getJSONObject(i);
                    Log.d("@@@@@@",resultObject.toString());
                    String date = resultObject.getString("date");
                    String title = resultObject.getString("title");
                    String score = resultObject.getString("score");
                    int star = resultObject.getInt("star");
                    adapter.addItem(new RecordItem(date,title,score,star));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            listView.setAdapter(adapter);
        }
    }
}