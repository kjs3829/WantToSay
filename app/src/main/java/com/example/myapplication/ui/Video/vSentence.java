package com.example.myapplication.ui.Video;

public class vSentence {
    String english;
    String korean;
    String timed;
    String stt;
    int number;

    public vSentence(String english, String korean, String timed, int number, String stt) {
        this.english = english;
        this.korean = korean;
        this.timed = timed;
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

    public String getTimed() {
        return timed;
    }

    public void setTimed(String timed) {
        this.timed = timed;
    }

    public String getStt() {
        return stt;
    }

    public void setStt(String stt) {
        this.stt = stt;
    }
}
