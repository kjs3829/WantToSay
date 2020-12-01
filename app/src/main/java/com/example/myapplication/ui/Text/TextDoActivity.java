package com.example.myapplication.ui.Text;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.RecordRequest;
import com.example.myapplication.TextdatainputRequest;
import com.example.myapplication.TitledatainputRequest;
import com.example.myapplication.Userinfo;
import com.google.gson.Gson;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TextDoActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    static SentenceAdapter adapter;
    static ListView sentenceView;
    private TextToSpeech tts;

    //총점수 사용 변수
    static private TextView total_score;
    static int TS = 0;    //total_score
    static int TA = 0;

    //발음평가 사용 변수
    private static final String MSG_KEY = "status";
    public static final String ENG_KEY = "english";
    public static final String POS_KEY = "position";
    int maxLenSpeech = 16000 * 45;
    byte [] speechData = new byte [maxLenSpeech * 2];
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;
    static String result;
    static String result2;
    TextView textResult;
    Button test;
    MyHandler mHandler;
    String title;
    RequestQueue queue;
    Button end;
    String[] text;

    static ArrayList<Sentence> items = new ArrayList<Sentence>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_do);

        Intent intent = getIntent();
        title=intent.getStringExtra("title");
        String str = intent.getStringExtra("en");
        text = create_text(str);

        queue = Volley.newRequestQueue(TextDoActivity.this);
        //tts 객체 생성
        tts = new TextToSpeech(this, this);
        mHandler = new MyHandler();
        //리스트 뷰 생성
        sentenceView = findViewById(R.id.sentenceView);
        total_score = findViewById(R.id.total_score);
        items.clear();
        adapter = new SentenceAdapter();
        adapter.addItem(new Sentence(title,"Score",0,"Pronunciation"));
        for(int i=0;i<text.length-1;i++){
            if(!text[i].equals("")){
                adapter.addItem(new Sentence(text[i],"Score",i+1,"Pronunciation"));
            }
        }
        sentenceView.setAdapter(adapter);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        };

        String context="";
        for(int i=0;i<text.length-1;i++){
            context=context+text[i]+"^";
        }



        TitledatainputRequest titleinputRequest = new TitledatainputRequest(title,responseListener);
        TextdatainputRequest textinputRequest = new TextdatainputRequest(title,context,responseListener);
        queue.add(titleinputRequest);
        queue.add(textinputRequest);

        end=(Button) findViewById(R.id.end);
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

        TS=0;
        TA=0;
        //total score
        for(int i = 0; i < items.size(); i++) {
            if(!items.get(i).getKorean().equals("Score")){
                TS += Integer.parseInt(items.get(i).getKorean());
            }
        }
        total_score.setText("Total Average : " + TS);
    }

    public String[] create_text(String a) {
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        String source = a;
        ArrayList<String> tmp = new ArrayList<String>();
        iterator.setText(source);
        int start = iterator.first();
        iterator.next();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
            tmp.add(source.substring(start,end));
        }
        String[] text = new String[tmp.size()];
        text = tmp.toArray(text);
        for(int i = 0; i<text.length-1;i++){
            text[i] = text[i].replaceAll(System.getProperty("line.separator"),"");
        }
        return text;
    }


    class SentenceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Sentence item) {
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
            SentenceView view = null;
            if (convertView == null) {
                view = new SentenceView(getApplicationContext());
            } else {
                view = (SentenceView) convertView;
            }
            //convertView is for memory
            final Sentence item = items.get(position);
            view.setEnglish(item.getEnglish());
            view.setKorean(item.getKorean());
            view.setStt(item.getStt());

            //발음듣기 버튼 동작
            Button listen = (Button) view.findViewById(R.id.listen);
            listen.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    listen(item.getEnglish());
                }
            });

            //발음평가 버튼 동작
            final TextView koreanView = (TextView) view.findViewById(R.id.koreanView);
            final TextView englishView = view.findViewById(R.id.englishView);
            TextView sttView = view.findViewById(R.id.sttView);
            test = (Button) view.findViewById(R.id.test);
            test.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    speak(position,englishView.getText().toString());
                }
            });

            return view;
        }
    }

    private void speak(final int position, final String eng) {

        //녹음동작이 작동중일때
        if (isRecording) {
            forceStop = true;
        }
        //녹음동작이 작동하고 있지 않을때
        else {
            try {
                new Thread(new Runnable() {
                    public void run() {
                        SendMessage("Recording...",eng,position, 1);
                        try {
                            recordSpeech();
                            SendMessage("Recognizing...",eng,position, 2);
                        } catch (RuntimeException e) {
                            SendMessage(e.getMessage(),eng,position, 3);
                            return;
                        }

                        Thread threadRecog = new Thread(new Runnable() {
                            public void run() {
                                result = sendDataAndGetResult(eng);
                                result2 = sendDataAndGetResult(null);   //STT
                            }
                        });
                        threadRecog.start();
                        try {
                            threadRecog.join(50000);
                            if (threadRecog.isAlive()) {
                                threadRecog.interrupt();
                                SendMessage("No response from server for 50 secs",eng,position, 4);
                            } else {
                                SendMessage("OK",eng,position, 5);
                            }
                        } catch (InterruptedException e) {
                            SendMessage("Interrupted",eng,position, 4);
                        }
                    }
                }).start();
            } catch (Throwable t) {
                textResult.setText("ERROR: " + t.toString());
                forceStop = false;
                isRecording = false;
            }
        }
    }

    public static class MyHandler extends Handler{
        @Override
        public synchronized void handleMessage(Message msg){
            Bundle bd = msg.getData();
            String v = bd.getString(MSG_KEY);
            String eng = bd.getString(ENG_KEY);
            int pos = bd.getInt(POS_KEY);
            items.set(pos,new Sentence(eng,v,pos,"Pronunciation"));
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
                    items.set(pos,new Sentence(eng,Integer.toString(fs),pos,result2));
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
            }
            super.handleMessage(msg);
        }
    }

    public void SendMessage(String str, String eng, int pos, int id) {
        Message msg = mHandler.obtainMessage();
        Bundle bd = new Bundle();
        bd.putString(MSG_KEY, str);
        bd.putString(ENG_KEY, eng);
        bd.putInt(POS_KEY, pos);
        msg.what = id;
        msg.setData(bd);
        mHandler.sendMessage(msg);
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
        String openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Pronunciation";
        String accessKey = "62e49bb1-4ab7-410e-97de-caf53c5008f3";
        String languageCode;
        String audioContents;
        String script = eng;

        Gson gson = new Gson();

        languageCode = "english";

        Map<String, Object> request = new HashMap<>();
        Map<String, String> argument = new HashMap<>();

        audioContents = Base64.encodeToString(
                speechData, 0, lenSpeech*2, Base64.NO_WRAP);

        argument.put("language_code", languageCode);
        if(script != null){
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
                    if(script != null){
                        s1 = resultObject.getString("score");
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

    //TTS
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
    private void listen(String text) {
        //음색 낮을수록 저음(디폴트 1)
        tts.setPitch(1);
//        tts.setPitch((float)0.1);

        //속도 (디폴트 1)
        tts.setSpeechRate(1);
//        tts.setSpeechRate((float) 0.7);

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
