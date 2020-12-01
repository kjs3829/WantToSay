package com.example.myapplication.ui.Text;

public class Sentence {
    String english;
    String korean;
    String stt;
    int number;

    public Sentence(String english, String korean,int number,String stt) {
        this.english = english;
        this.korean = korean;
        this.number = number;
        this.stt = stt;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getKorean() {
        return korean;
    }

    public void setKorean(String korean) {
        this.korean = korean;
    }

    public String getStt() {
        return stt;
    }

    public void setStt(String stt) {
        this.stt = stt;
    }

}
