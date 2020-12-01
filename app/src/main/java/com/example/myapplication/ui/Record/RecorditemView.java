package com.example.myapplication.ui.Record;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class RecorditemView extends LinearLayout {
    TextView dateView;
    TextView titleView;
    TextView scoreView;
    RatingBar starView;

    public RecorditemView(Context context) {
        super(context);
        init(context);
    }

    public RecorditemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.record_item, this, true);

        dateView = (TextView)findViewById(R.id.dateView);
        titleView = (TextView)findViewById(R.id.titleView);
        scoreView = (TextView)findViewById(R.id.scoreView);
        starView = (RatingBar) findViewById(R.id.starView);
    }

    public void setDate(String date) {
        dateView.setText(date);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setScore(String score) {
        scoreView.setText("Score : "+score);
    }

    public void setStar(int star) {
        starView.setRating(star);
    }
}