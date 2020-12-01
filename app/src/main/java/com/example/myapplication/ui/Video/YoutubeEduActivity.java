package com.example.myapplication.ui.Video;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.RecordRequest;
import com.example.myapplication.TextdatainputRequest;
import com.example.myapplication.TitledatainputRequest;
import com.example.myapplication.Userinfo;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YoutubeEduActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private String APIKEY = "AIzaSyDP0OOxTisP17z8MQCqJdmMNzD1wy8WukQ";
    static vSentenceAdapter adapter;
    static ListView vSentenceView;
    Button end;
    Button test;
    TextView tv;

    private String ID;
    private YouTubePlayer player;
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private ArrayList<String> captions;
    private int auto;
    private String autoURL;
    static ArrayList<vSentence> items = new ArrayList<vSentence>();
    public static final String PREFS_NAME = "prefs";
    private static final String MSG_KEY = "status";
    public static final String ENG_KEY = "english";
    public static final String POS_KEY = "position";
    public static final String TIME_KEY = "time";
    int maxLenSpeech = 16000 * 45;
    byte [] speechData = new byte [maxLenSpeech * 2];
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;
    MyHandler myHandler;
    static String result;
    static String result2;
    String title;
    RequestQueue queue;

    //총 점수 사용 변수
    static int TS = 0;
    static int TA = 0;
    static private TextView total_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_edu);

        Intent intent = getIntent();
        ID = intent.getStringExtra("Video_ID");
        auto = intent.getIntExtra("Auto",0);
        title = intent.getStringExtra("Video_title");
        tv = findViewById(R.id.tv);
        total_score = findViewById(R.id.total_score);
        if (auto == 1) autoURL = intent.getStringExtra("URL");
        captions = new ArrayList<String>();
        queue = Volley.newRequestQueue(YoutubeEduActivity.this);

        end = findViewById(R.id.end);
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                };
                RecordRequest recordRequest = new RecordRequest(title,Integer.toString(TA),responseListener);
                queue.add(recordRequest);
                Intent intent2 = new Intent(v.getContext(), MainActivity.class);
                intent2.putExtra("userID", Userinfo.userID);
                startActivity(intent2);
            }
        });

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(APIKEY, this);
        myHandler = new MyHandler();

        //total score
        TS=0;
        TA=0;
        for(int i = 0; i < items.size(); i++) {
            if(!items.get(i).getKorean().equals("Score")){
                TS += Integer.parseInt(items.get(i).getKorean());
            }
        }
        total_score.setText("Total Average : " + TS);


        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.player = player;
        if (!wasRestored) {
            player.cueVideo(ID);
        }
    }
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format("getString(R.string.player_error)", errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(APIKEY, this);
        }
    }
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... Params) {
            String captionURL;
            if(auto==0) {
                captionURL = "http://video.google.com/timedtext?lang=en&v=" + ID;
            }
            else {
                captionURL = autoURL;
            }
            Document doc = null;
            try {
                doc = Jsoup.connect(captionURL).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements caption = doc.select("text");

            for (Element e : caption) {
                String tmp = e.select("text").text().replaceAll("&#39;", "'");
                captions.add(tmp);
                captions.add(e.attr("start"));
                captions.add(e.attr("dur"));
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                        }
                    };
                    String context = "";
                    for(int i =0; i < captions.size(); i+=3) {
                        context += captions.get(i) + "^";
                    }
                    TitledatainputRequest titleinputRequest = new TitledatainputRequest(title,responseListener);
                    TextdatainputRequest textinputRequest = new TextdatainputRequest(title,context,responseListener);
                    queue.add(titleinputRequest);
                    queue.add(textinputRequest);
                }
            }).start();

            return null;
        }
        protected void onPostExecute(Void result) {
            vSentenceView = findViewById(R.id.listView);
            items.clear();
            adapter = new vSentenceAdapter();
            for(int i = 0; i < captions.size(); i+=3){
                adapter.addItem(new vSentence(captions.get(i),"Score",captions.get(i+1),i,"Pronunciation"));
            }
            vSentenceView.setAdapter(adapter);
        }
    }

    private String speak(final int position, final String eng, final String time) {

        //녹음동작이 작동중일때
        if (isRecording) {
            forceStop = true;
        }
        //녹음동작이 작동하고 있지 않을때
        else {
            try {
                new Thread(new Runnable() {
                    public void run() {
                        SendMessage("Recording...",eng,position,time, 1);
                        try {
                            recordSpeech();
                            SendMessage("Recognizing...",eng,position,time, 2);
                        } catch (RuntimeException e) {
                            SendMessage(e.getMessage(),eng,position,time, 3);
                            return;
                        }

                        Thread threadRecog = new Thread(new Runnable() {
                            public void run() {
                                result = sendDataAndGetResult(eng);
                                result2 = sendDataAndGetResult(null);
                            }
                        });
                        threadRecog.start();
                        try {
                            threadRecog.join(50000);
                            if (threadRecog.isAlive()) {
                                threadRecog.interrupt();
                                SendMessage("No response from server for 50 secs",eng,position,time, 4);
                            } else {
                                SendMessage("OK",eng,position,time, 5);
                            }
                        } catch (InterruptedException e) {
                            SendMessage("Interrupted",eng,position,time, 4);
                        }
                    }
                }).start();
            } catch (Throwable t) {
                SendMessage("ERROR: " + t.toString(),eng,position,time, 6);
                forceStop = false;
                isRecording = false;
            }
        }

        return result;
    }


    public class vSentenceView extends LinearLayout {
        TextView englisthView;
        TextView koreanView;
        TextView sttView;
        Button listen;
        Button test;

        public vSentenceView(Context context){
            super(context);
            init(context);
        }
        public vSentenceView(Context context, @Nullable AttributeSet attrs){
            super(context, attrs);
            init(context);
        }

        private void init(Context context){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.sentence,this,true);

            englisthView = (TextView)findViewById(R.id.englishView);
            koreanView = (TextView)findViewById(R.id.koreanView);
            listen = (Button)findViewById(R.id.listen);
            test = (Button)findViewById(R.id.test);
            sttView = (TextView)findViewById(R.id.sttView);
        }
        public void setEnglish(String english){
            englisthView.setText(english);
        }
        public void setKorean(String korean){
            koreanView.setText(korean);
        }
        public void setStt(String stt){
            sttView.setText(stt);
        }
    }

    class vSentenceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(vSentence item) {
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            vSentenceView view = null;
            if (convertView == null) {
                view = new vSentenceView(getApplicationContext());
            } else {
                view = (vSentenceView) convertView;
            }
            //convertView is for memory
            final vSentence item = items.get(position);
            view.setEnglish(item.getEnglish());
            view.setKorean(item.getKorean());
            view.setStt(item.getStt());

            //발음듣기 버튼 동작
            Button listen = (Button) view.findViewById(R.id.listen);
            listen.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    float tmp = Float.parseFloat(item.getTimed());
                    player.seekToMillis((int)tmp*1000);
                }
            });

            //발음평가 버튼 동작
            final TextView koreanView = (TextView) view.findViewById(R.id.koreanView);
            final TextView englishView = view.findViewById(R.id.englishView);
            TextView sttView = view.findViewById(R.id.sttView);
            test = (Button) view.findViewById(R.id.test);
            test.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    String score=speak(position,englishView.getText().toString(),item.getTimed());
                }
            });

            return view;
        }
    }

    public static class MyHandler extends Handler {
        @Override
        public synchronized void handleMessage(Message msg){
            Bundle bd = msg.getData();
            String v = bd.getString(MSG_KEY);
            String eng = bd.getString(ENG_KEY);
            String time = bd.getString(TIME_KEY);
            int pos = bd.getInt(POS_KEY);
            items.set(pos,new vSentence(eng,v,time,pos,"Pronunciation"));
            switch (msg.what) {
                // 녹음이 시작되었음(버튼)
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
                // 녹음이 정상적으로 종료되었음(버튼 또는 max time)
                case 2:
                    adapter.notifyDataSetChanged();
                    break;
                // 녹음이 비정상적으로 종료되었음(마이크 권한 등)
                case 3:
                    adapter.notifyDataSetChanged();
                    break;
                // 인식이 비정상적으로 종료되었음(timeout 등)
                case 4:
                    adapter.notifyDataSetChanged();
                    break;
                // 인식이 정상적으로 종료되었음 (thread내에서 exception포함)
                case 5:
                    int fs = (int)Normalization(Float.parseFloat(result)) * agreement(eng, result2) / 100;
                    items.set(pos,new vSentence(eng,Integer.toString(fs),time,pos,result2));
                    adapter.notifyDataSetChanged();
                    TS = 0;
                    int cnt = 0;
                    for(int i = 0; i < items.size(); i++) {
                        if(!items.get(i).getKorean().equals("Score")){
                            TS += Integer.parseInt(items.get(i).getKorean());
                            cnt++;
                        }
                    }
                    TA = TS/cnt;
                    total_score.setText("Total Average : " + TA);
                    break;
                case 6:
                    adapter.notifyDataSetChanged();
            }
            super.handleMessage(msg);
        }
    }

    public void SendMessage(String str, String eng, int pos, String time, int id) {
        Message msg = myHandler.obtainMessage();
        Bundle bd = new Bundle();
        bd.putString(MSG_KEY, str);
        bd.putString(ENG_KEY, eng);
        bd.putString(TIME_KEY,time);
        bd.putInt(POS_KEY, pos);
        msg.what = id;
        msg.setData(bd);
        myHandler.sendMessage(msg);
    }

    public void recordSpeech() throws RuntimeException {
        try {
            int bufferSize = AudioRecord.getMinBufferSize(
                    16000, // sampling frequency
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord audio = new AudioRecord(
                    MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    16000, // sampling frequency
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);
            lenSpeech = 0;
            if (audio.getState() != AudioRecord.STATE_INITIALIZED) {
                throw new RuntimeException("ERROR: Failed to initialize audio device. Allow app to access microphone");
            }
            else {
                short [] inBuffer = new short [bufferSize];
                forceStop = false;
                isRecording = true;
                audio.startRecording();
                while (!forceStop) {
                    int ret = audio.read(inBuffer, 0, bufferSize);
                    for (int i = 0; i < ret ; i++ ) {
                        if (lenSpeech >= maxLenSpeech) {
                            forceStop = true;
                            break;
                        }
                        speechData[lenSpeech*2] = (byte)(inBuffer[i] & 0x00FF);
                        speechData[lenSpeech*2+1] = (byte)((inBuffer[i] & 0xFF00) >> 8);
                        lenSpeech++;
                    }
                }
                audio.stop();
                audio.release();
                isRecording = false;
            }
        } catch(Throwable t) {
            throw new RuntimeException(t.toString());
        }
    }

    public String sendDataAndGetResult (String eng) {
        String openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Recognition";
        String accessKey = "62e49bb1-4ab7-410e-97de-caf53c5008f3";
        String languageCode;
        String audioContents;
        String script = eng;

        Gson gson = new Gson();

        languageCode = "english";
        openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Pronunciation";

        Map<String, Object> request = new HashMap<>();
        Map<String, String> argument = new HashMap<>();

        audioContents = Base64.encodeToString(
                speechData, 0, lenSpeech*2, Base64.NO_WRAP);

        argument.put("language_code", languageCode);
        if (script!=null) {
            argument.put("script", script);
        }
        argument.put("audio", audioContents);

        request.put("access_key", accessKey);
        request.put("argument", argument);

        URL url;
        Integer responseCode;
        String responBody;
        try {
            url = new URL(openApiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(gson.toJson(request).getBytes("UTF-8"));
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            String s1="";
            if ( responseCode == 200 ) {
                InputStream is = new BufferedInputStream(con.getInputStream());
                responBody = readStream(is);

                try {
                    JSONObject jObject = new JSONObject(responBody);
                    JSONObject resultObject = jObject.getJSONObject("return_object");
                    if (script!=null) {
                        s1=resultObject.getString("score");
                    }
                    else {
                        s1 = resultObject.getString("recognized");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return s1;
            }
            else
                return "ERROR: " + Integer.toString(responseCode);
        } catch (Throwable t) {
            return "ERROR: " + t.toString();
        }
    }

    public static String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    public static int agreement(String s1,String s2){
        int percent;
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        s1 = s1.replaceAll("[^a-zA-Z]+", " ");
        s2 = s2.replaceAll("[^a-zA-Z]+", " ");
        String[] script = s1.split(" ");
        String[] speak = s2.split(" ");

        ArrayList<String> script_list = new ArrayList<String>();
        ArrayList<String> speak_list = new ArrayList<String>();
        for(int i = 0; i < script.length;i++){
            if(!script[i].equals("")) {
                script_list.add(script[i]);
            }
        }
        for(int i = 0; i < speak.length;i++){
            speak_list.add(speak[i]);
        }
        int a=0; // 일치 단어 수

        for(int i = 0; i < script_list.size();i++){
            if(speak_list.contains(script_list.get(i))){
                speak_list.remove(speak_list.indexOf(script_list.get(i)));
                a++;
            }
        }

        percent=a*100/script.length;

        return percent;
    }

    //min-max normalization
    public static float Normalization(float v){
        int min = 0;
        int max = 5;
        int new_max = 100;
        int new_min = 0;
        return ((v - min) / (max - min)) * (new_max - new_min) + new_min;
    }
}
