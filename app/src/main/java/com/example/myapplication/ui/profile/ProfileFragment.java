package com.example.myapplication.ui.profile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.R;
import com.example.myapplication.Userinfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class ProfileFragment extends Fragment {

    TextView nameview;
    TextView idview;
    TextView timeview;
    TextView starview;
    TextView rankview;
    TextView scntview;

    phpdo task;
    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        super.onCreate(savedInstanceState);

        nameview = (TextView)root.findViewById(R.id.name);
        idview = (TextView)root.findViewById(R.id.ID);
        timeview = (TextView)root.findViewById(R.id.time);
        starview = (TextView)root.findViewById(R.id.star);
        rankview = (TextView)root.findViewById(R.id.rank);
        scntview = (TextView)root.findViewById(R.id.scnt);

        String id = "0007790A6F10";
        String name = "Red";
        task = new phpdo();
        task.execute(id, name);
        Log.v("userid : @@@@@@",Userinfo.userID);
        return root;
    }

    private class phpdo extends AsyncTask<String,Void,String> {

        protected void onPreExecute(){

        }
        @Override
        protected String doInBackground(String... arg0) {
            try {
                String link = "http://jieun959.cafe24.com/profile.php?ID=" + Userinfo.userID;
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
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){
            String name="";
            String ID="";
            String time="";
            String star="";
            String rank="";
            String scnt="";
            try {
//                JSONObject jObject = new JSONObject(result);
                JSONArray jsonArray=new JSONArray(result);
                for(int i = 0 ; i<jsonArray.length(); i++) {
                    JSONObject resultObject = jsonArray.getJSONObject(i);
                    name = resultObject.getString("name");
                    ID = resultObject.getString("id");
                    time = resultObject.getString("time");
                    star = Integer.toString(resultObject.getInt("star"));
                    rank = resultObject.getString("Rank");
                    scnt = Integer.toString(resultObject.getInt("Scnt"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            nameview.setText(name);
            idview.setText(ID);
            timeview.setText(time);
            starview.setText(star);
            rankview.setText(rank);
            scntview.setText(scnt);
        }
    }

}