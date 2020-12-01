package com.example.myapplication.ui.Video;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class YoutubeListActivity extends AppCompatActivity {
    private ImageView iv_thum1;
    private ImageView iv_thum2;
    private ImageView iv_thum3;
    private ImageView iv_thum4;
    private ImageView iv_thum5;
    private TextView tv_title1;
    private TextView tv_title2;
    private TextView tv_title3;
    private TextView tv_title4;
    private TextView tv_title5;
    private TextView tv_channel1;
    private TextView tv_channel2;
    private TextView tv_channel3;
    private TextView tv_channel4;
    private TextView tv_channel5;
    private LinearLayout btn_list1;
    private LinearLayout btn_list2;
    private LinearLayout btn_list3;
    private LinearLayout btn_list4;
    private LinearLayout btn_list5;
    private ArrayList<String> str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_list);

        final ArrayList<Bitmap> thumList = new ArrayList<Bitmap>();
        init_activity();
        Intent intent = getIntent();
        str = intent.getStringArrayListExtra("videoList");

        tv_title1.setText(str.get(1).replaceAll("&amp;","&").replaceAll("&quot;","\"").replaceAll("&#39;","'"));
        tv_channel1.setText(str.get(3));
        tv_title2.setText(str.get(5).replaceAll("&amp;","&").replaceAll("&quot;","\"").replaceAll("&#39;","'"));
        tv_channel2.setText(str.get(7));
        tv_title3.setText(str.get(9).replaceAll("&amp;","&").replaceAll("&quot;","\"").replaceAll("&#39;","'"));
        tv_channel3.setText(str.get(11));
        tv_title4.setText(str.get(13).replaceAll("&amp;","&").replaceAll("&quot;","\"").replaceAll("&#39;","'"));
        tv_channel4.setText(str.get(15));
        tv_title5.setText(str.get(17).replaceAll("&amp;","&").replaceAll("&quot;","\"").replaceAll("&#39;","'"));
        tv_channel5.setText(str.get(19));
        Thread ThumnailLoder = new Thread() {
            @Override
            public void run() {
                try{
                    for(int i = 2; i < str.size(); i+=4) {
                        URL url = new URL(str.get(i));
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Bitmap result = Bitmap.createScaledBitmap(bitmap,180,100,false);
                        thumList.add(result);
                    }
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        ThumnailLoder.start();
        try{
            ThumnailLoder.join();
            iv_thum1.setImageBitmap(thumList.get(0));
            iv_thum2.setImageBitmap(thumList.get(1));
            iv_thum3.setImageBitmap(thumList.get(2));
            iv_thum4.setImageBitmap(thumList.get(3));
            iv_thum5.setImageBitmap(thumList.get(4));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        btn_list1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video_id = str.get(0);
                String video_title = str.get(1);
                Intent intent1 = new Intent(v.getContext(), YoutubeSelectedActivity.class);
                intent1.putExtra("video_id",video_id);
                intent1.putExtra("video_title",video_title);
                startActivity(intent1);
            }
        });
        btn_list2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video_id = str.get(4);
                String video_title = str.get(5);
                Intent intent1 = new Intent(v.getContext(), YoutubeSelectedActivity.class);
                intent1.putExtra("video_id",video_id);
                intent1.putExtra("video_title",video_title);
                startActivity(intent1);
            }
        });
        btn_list3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video_id = str.get(8);
                String video_title = str.get(9);
                Intent intent1 = new Intent(v.getContext(), YoutubeSelectedActivity.class);
                intent1.putExtra("video_id",video_id);
                intent1.putExtra("video_title",video_title);
                startActivity(intent1);
            }
        });
        btn_list4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video_id = str.get(12);
                String video_title = str.get(13);
                Intent intent1 = new Intent(v.getContext(), YoutubeSelectedActivity.class);
                intent1.putExtra("video_id",video_id);
                intent1.putExtra("video_title",video_title);
                startActivity(intent1);
            }
        });
        btn_list5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video_id = str.get(16);
                String video_title = str.get(17);
                Intent intent1 = new Intent(v.getContext(), YoutubeSelectedActivity.class);
                intent1.putExtra("video_id",video_id);
                intent1.putExtra("video_title",video_title);
                startActivity(intent1);
            }
        });
    }
    private void init_activity(){
        iv_thum1 = findViewById(R.id.iv_thum1);
        iv_thum2 = findViewById(R.id.iv_thum2);
        iv_thum3 = findViewById(R.id.iv_thum3);
        iv_thum4 = findViewById(R.id.iv_thum4);
        iv_thum5 = findViewById(R.id.iv_thum5);
        tv_title1 = findViewById(R.id.tv_title1);
        tv_title2 = findViewById(R.id.tv_title2);
        tv_title3 = findViewById(R.id.tv_title3);
        tv_title4 = findViewById(R.id.tv_title4);
        tv_title5 = findViewById(R.id.tv_title5);
        tv_channel1 = findViewById(R.id.tv_channel1);
        tv_channel2 = findViewById(R.id.tv_channel2);
        tv_channel3 = findViewById(R.id.tv_channel3);
        tv_channel4 = findViewById(R.id.tv_channel4);
        tv_channel5 = findViewById(R.id.tv_channel5);
        btn_list1 = findViewById(R.id.btn_list1);
        btn_list2 = findViewById(R.id.btn_list2);
        btn_list3 = findViewById(R.id.btn_list3);
        btn_list4 = findViewById(R.id.btn_list4);
        btn_list5 = findViewById(R.id.btn_list5);
    }
}
