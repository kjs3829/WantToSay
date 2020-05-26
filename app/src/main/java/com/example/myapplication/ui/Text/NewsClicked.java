package com.example.myapplication.ui.Text;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.Study;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class NewsClicked extends Activity {

    private Button korean;
    private Button english;
    private Button study;
    private String en="";
    private String ko="";
    private TextView title;
    private TextView context;
    private String clientId,clientSecret,apiURL;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.news_item);

        Intent intent=getIntent();

        title =(TextView)findViewById(R.id.title);
        context =(TextView)findViewById(R.id.context);
        title.setText(intent.getStringExtra("title"));
        context.setText(intent.getStringExtra("context"));

        korean = (Button) findViewById(R.id.korean);
        english = (Button) findViewById(R.id.english);
        study = (Button) findViewById(R.id.study);

        en=context.getText().toString();

        //한글로 보기
        korean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ko.equals("")) {
                    //파파고 연결
//                    new BackgroundTask().execute();
                    context.setText(ko);
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

        //학습 시
        study.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        //SubActivity로 가는 인텐트를 생성
                        Intent intent = new Intent(getApplicationContext(), TextDoActivity.class);
                        //액티비티 시작!
//                        String s1 = en;
//                        ArrayList a = new ArrayList();
//                        StringTokenizer tokens = new StringTokenizer(s1, ".");
//                        while (tokens.countTokens() != 0) {
//                            a.add(tokens.nextToken() + ".");
//                        }
//                        a.remove(a.size() - 1);
                        intent.putExtra("en", en);

//                        if(!ko.equals("")){
//                            String s2 = ko;
//                            ArrayList b = new ArrayList();
//                            StringTokenizer token = new StringTokenizer(s2, ".");
//                            while (token.countTokens() != 0) {
//                                b.add(token.nextToken() + ".");
//                            }
//                            b.remove(b.size() - 1);
//                            intent.putExtra("klist", b);
//                        }

                        startActivity(intent);
                    }
                }
        );
    }
    // 백 그라운드에서 파파고 API와 연결하여 번역 결과를 가져옵니다.
    class BackgroundTask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
            clientId = "WWeL1BOEEoGS8Kc363jI"; // 애플리케이션 클라이언트 아이디 값";
            clientSecret = "xma865zeJo"; // 애플리케이션 클라이언트 시크릿 값";
            apiURL = "https://openapi.naver.com/v1/papago/n2mt";
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            StringBuilder output = new StringBuilder();

            try {
                // 번역문을 UTF-8으로 인코딩합니다.
                String text = URLEncoder.encode(en, "UTF-8");
                // 파파고 API와 연결을 수행합니다.
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                // 번역할 문장을 파라미터로 전송합니다.
                String postParams = "source=en&target=ko&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();

                // 번역 결과를 받아옵니다.
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    output.append(inputLine);
                }
                br.close();
                String responseBody=output.toString();
                try {
                    JSONObject jObject = new JSONObject(responseBody);
                    JSONObject messageObject = jObject.getJSONObject("message");
                    JSONObject resultObject = messageObject.getJSONObject("result");
                    String s1 = resultObject.getString("translatedText");
                    context.setText(s1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch(Exception ex) {
                Log.e("SampleHTTP", "Exception in processing response.", ex);
                ex.printStackTrace();
            }
            return null;
        }

    }
}