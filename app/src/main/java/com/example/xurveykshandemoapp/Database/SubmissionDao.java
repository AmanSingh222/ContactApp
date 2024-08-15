package com.example.xurveykshandemoapp.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SubmissionDao {
    @Insert
    void insert(Model submission);

    @Query("SELECT * FROM submission_table")
    List<Model> getAllSubmissions();
}
