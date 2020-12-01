package com.example.myapplication.ui.Text;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class SentenceView extends LinearLayout {
    TextView englisthView;
    TextView koreanView;
    TextView sttView;
    Button listen;
    Button test;

    public SentenceView(Context context){
        super(context);
        init(context);
    }
    public SentenceView(Context context, @Nullable AttributeSet attrs){
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
