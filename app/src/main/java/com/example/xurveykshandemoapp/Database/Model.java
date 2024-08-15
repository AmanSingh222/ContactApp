package com.example.xurveykshandemoapp.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "submission_table")
public class Model {
    @PrimaryKey(autoGenerate = true)
    private int id;
    String Age, recording, Sbmit_Time;

    public String getAge() {
        return Age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getRecording() {
        return recording;
    }

    public void setRecording(String recording) {
        this.recording = recording;
    }

    public String getSbmit_Time() {
        return Sbmit_Time;
    }

    public void setSbmit_Time(String sbmit_Time) {
        Sbmit_Time = sbmit_Time;
    }
}
