package com.example.xurveykshandemoapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;



@Database(entities = {Model.class}, version = 1, exportSchema = false)
public abstract class SubmissionDatabase extends RoomDatabase {

    private static SubmissionDatabase instance;

    public abstract SubmissionDao submissionDao();

    public static synchronized SubmissionDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            SubmissionDatabase.class, "submission_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    @Override
    public void clearAllTables() {
        // Optional: You can implement custom behavior here if needed.
//        super.clearAllTables();
    }
}
