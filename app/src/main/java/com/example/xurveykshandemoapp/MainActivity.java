package com.example.xurveykshandemoapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.xurveykshandemoapp.Fragments.AgeFragment;
import com.example.xurveykshandemoapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=  ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new AgeFragment())
                    .commit();
        }
    }
}