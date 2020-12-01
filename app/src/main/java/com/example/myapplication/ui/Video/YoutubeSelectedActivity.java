package com.example.myapplication.ui.Video;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class YoutubeSelectedActivity extends YouTubeBaseActivity {
    private String APIKEY = "AIzaSyDP0OOxTisP17z8MQCqJdmMNzD1wy8WukQ";
    private String htmlPageUrl;
    private String ID;
    private String autoURL;
    private TextView captionTV;
    private EditText etDownloadDirectory;
    private int auto = 0;
    private String title;

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_selected);

        Intent intent = getIntent();
        ID = intent.getStringExtra("video_id");
        title = intent.getStringExtra("video_title");
        htmlPageUrl = "http://video.google.com/timedtext?lang=en&v="+ID;
        youTubePlayerView = findViewById(R.id.youtubeView);
        Button study = (Button) findViewById(R.id.study);
        captionTV = findViewById(R.id.captionTV);
        captionTV.setMovementMethod(new ScrollingMovementMethod());
        etDownloadDirectory = (EditText)findViewById(R.id.etDownloadDirectory);
        etDownloadDirectory.setText(getDefaultDownloadDirectory());


        listener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(ID);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView.initialize(APIKEY, listener);
        study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(v.getContext(), YoutubeEduActivity.class);
                intent1.putExtra("Video_ID",ID);
                intent1.putExtra("Auto",auto);
                intent1.putExtra("Video_title",title);
                if(auto == 1) intent1.putExtra("URL",autoURL);
                startActivity(intent1);
            }
        });
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... Params) {
            try {
                String test="";
                Document doc = Jsoup.connect(htmlPageUrl).get();

                Elements caption = doc.select("transcript");

                for(Element e: caption){
                    test = e.select("text").text();
                }

                if(test.length() == 0) {  //자막이 없으면
                    File file = new File(getDefaultDownloadDirectory()+"get_video_info?video_id="+ID);
                    if(!file.exists()) {    //비디오 정보 파일이 없을 경우
                        requestFileDownload("https://youtube.com/get_video_info?video_id="+ID);
                    }
                    else {  //비디오 정보 파일이 있을 경우
                        autoURL = getTimedTextURL();
                        if(autoURL == "%NotSupport") {
                            return "영어 동영상이 아니거나 자막을 지원하지 않는 동영상입니다.";
                        }
                        else {
                            Document doc2 = null;
                            try {
                                doc2 = Jsoup.connect(autoURL).get();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String autoCaption = doc2.text();
                            autoCaption = autoCaption.replaceAll("&#39;","'");
                            return autoCaption;
                        }

                    }

                }
                else {  //자막이 있을경우
                    test = test.replaceAll("&#39;","'");
                    return test;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Loading...";
        }
        protected void onPostExecute(String result) {
            result = result.replaceAll("&amp;","&").replaceAll("&quot;","\"").replaceAll("&#39;","'");
            result = result.replaceAll("</font>"," ").replaceAll("<font color=\"\\S+\">"," ");
            captionTV.setText(result);
        }
    }

    private String getTimedTextURL() {
        File file = new File(getDefaultDownloadDirectory()+"get_video_info?video_id="+ID);
        FileInputStream fis = null ;
        BufferedInputStream bufis = null ;
        StringBuilder sb = new StringBuilder();
        int data = 0 ;
        try {
            // open file.
            fis = new FileInputStream(file);
            bufis = new BufferedInputStream(fis);

            // read data from bufis's buffer.
            while ((data = bufis.read()) != -1) {
                // TODO : use data
                sb.append((char) data);
            }

            StringBuilder sb2 = new StringBuilder();
            Pattern pattern = Pattern.compile("captionTracks.*\"name\"");
            String tmp = URLDecoder.decode(sb.toString(), "utf-8");
            Matcher matcher = pattern.matcher(tmp);
            int s = 0;
            int e = 0;
            while (matcher.find()) {
                sb2.append(s = matcher.start());
                sb2.append(" and ");
                sb2.append(e = matcher.end());
            }

            if(s == 0) return "%NotSupport";    //자막을 지원하지 않는 동영상
            String timedTextURL = tmp.substring(s, e);

            timedTextURL = timedTextURL.replace("\\u0026", "&");
            timedTextURL = timedTextURL.replaceAll(",", "%2C");
            timedTextURL = timedTextURL.substring(timedTextURL.indexOf("https"),timedTextURL.indexOf("\"%2C\"name\""));
            timedTextURL = timedTextURL.replaceAll("lang=[a-z]{2}","lang=en");

            // close file.
            bufis.close();
            fis.close();
            auto = 1;
            return timedTextURL;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }

    private String getDefaultDownloadDirectory() {
        return this.getExternalFilesDir(null) + "/download/";
    }

    private void requestFileDownload(String fileUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(fileUrl)
                .build();
        CallbackToDownloadFile cbToDownloadFile = new CallbackToDownloadFile(
                this.etDownloadDirectory.getText().toString(),
                getFileNameFrom(fileUrl)
        );
        client.newCall(request).enqueue(cbToDownloadFile);
    }

    private String getFileNameFrom(String url) {
        int lastIndexOfSlash = url.lastIndexOf('/') + 1;
        return url.substring(lastIndexOfSlash, url.length());
    }

    private class CallbackToDownloadFile implements Callback {

        private File directory;
        private File fileToBeDownloaded;

        public CallbackToDownloadFile(String directory, String fileName) {
            this.directory = new File(directory);
            this.fileToBeDownloaded = new File(this.directory.getAbsolutePath() + "/" + fileName);
        }

        @Override
        public void onFailure(Request request, IOException e) {
            openOnUiThread(
                    "Cannot download the file. Check if your device is connected to the internet."
            );
        }

        @Override
        public void onResponse(Response response) throws IOException {
            if (!this.directory.exists()) {
                this.directory.mkdirs();
            }

            if (this.fileToBeDownloaded.exists()) {
                this.fileToBeDownloaded.delete();
            }

            try {
                this.fileToBeDownloaded.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                openOnUiThread("Cannot create the download file. Check the write permission.");
                return;
            }

            InputStream is = response.body().byteStream();
            OutputStream os = new FileOutputStream(this.fileToBeDownloaded);

            final int BUFFER_SIZE = 2048;
            byte[] data = new byte[BUFFER_SIZE];

            int count;
            long total = 0;

            while ((count = is.read(data)) != -1) {
                total += count;
                os.write(data, 0, count);
            }

            os.flush();
            os.close();
            is.close();
            autoURL = getTimedTextURL();
            if (autoURL == "%NotSupport") {
                openOnUiThread("영어 동영상이 아니거나 자막을 지원하지 않는 동영상입니다.");
            }
            else {
                Document doc2 = Jsoup.connect(autoURL).get();
                String autoCaption = doc2.text();
                autoCaption = autoCaption.replaceAll("&#39;","'");
                openOnUiThread(autoCaption);
            }
        }
    }

    private void openOnUiThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                captionTV.setText(message);
            }
        });
    }
}

