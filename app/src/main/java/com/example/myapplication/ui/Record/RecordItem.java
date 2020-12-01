package com.example.myapplication.ui.Record;

public class RecordItem {

    String date;
    String title;
    String score;
    int star;

    public RecordItem(String date, String title, String score, int star) {
        this.date = date;
        this.title = title;
        this.score = score;
        this.star = star;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }
}