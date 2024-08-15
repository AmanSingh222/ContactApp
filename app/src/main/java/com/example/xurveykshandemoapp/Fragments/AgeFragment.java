package com.example.xurveykshandemoapp.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.xurveykshandemoapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AgeFragment extends Fragment {
    private EditText ageEditText;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 200;
    private AudioRecord audioRecorder;
    private boolean isRecording = false;
    private String audioFilePath;
    private Thread recordingThread;

    private static final int SAMPLE_RATE = 44100;
    private int bufferSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_age, container, false);

        ageEditText = view.findViewById(R.id.editTextAge);
        Button nextButton = view.findViewById(R.id.buttonNext);

        ageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAudioPermissionAndStartRecording();
            }
        });

        nextButton.setOnClickListener(v -> {
            String age = ageEditText.getText().toString().trim();
            stopRecordingAndSubmit();
            // Pass the age data to the next fragment
            Bundle bundle = new Bundle();
            bundle.putString("age", age);

            SelfieFragment selfieFragment = new SelfieFragment();
            selfieFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, selfieFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void checkAudioPermissionAndStartRecording() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RECORD_AUDIO_PERMISSION_CODE);
        } else {
            startRecording();
        }
    }

    private void startRecording() {
//        if (isRecording) return;
        Log.d("TAG", "startRecording: ");
        File audioDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "ContactFormAudio");
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        audioFilePath = audioDir.getAbsolutePath() + "/REC_" + timeStamp + ".wav";

        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        audioRecorder.startRecording();
        isRecording = true;

        recordingThread = new Thread(() -> writeAudioDataToFile());
        recordingThread.start();

        Toast.makeText(getContext(), "Recording started", Toast.LENGTH_SHORT).show();
    }

    private void writeAudioDataToFile() {
        byte[] audioBuffer = new byte[bufferSize];
        try (FileOutputStream os = new FileOutputStream(audioFilePath)) {
            writeWavHeader(os, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

            while (isRecording) {
                int read = audioRecorder.read(audioBuffer, 0, audioBuffer.length);
                if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                    os.write(audioBuffer, 0, read);
                }
            }

            updateWavHeader(os);
        } catch (IOException e) {
            Log.e("AudioRecording", "Error writing audio data: " + e.getMessage());
        }
    }

    private void stopRecordingAndSubmit() {
        if (isRecording) {
            isRecording = false;
            audioRecorder.stop();
            audioRecorder.release();
            audioRecorder = null;
            recordingThread = null;
            Toast.makeText(getContext(), "Recording stopped and saved as WAV", Toast.LENGTH_SHORT).show();
        }

        // Handle form submission logic here, including the audioFilePath.
    }

    private void writeWavHeader(FileOutputStream out, int sampleRate, int channels, int encoding) throws IOException {
        short format = 1; // PCM
        short numChannels = (short) 1;
        int byteRate = sampleRate * numChannels * 2;

        out.write(new byte[]{
                'R', 'I', 'F', 'F',  // ChunkID
                0, 0, 0, 0,  // ChunkSize (to be updated later)
                'W', 'A', 'V', 'E',  // Format
                'f', 'm', 't', ' ',  // Subchunk1ID
                16, 0, 0, 0,  // Subchunk1Size (16 for PCM)
                (byte) format, 0,  // AudioFormat (1 for PCM)
                (byte) numChannels, 0,  // NumChannels
                (byte) (sampleRate & 0xff), (byte) ((sampleRate >> 8) & 0xff), (byte) ((sampleRate >> 16) & 0xff), (byte) ((sampleRate >> 24) & 0xff),  // SampleRate
                (byte) (byteRate & 0xff), (byte) ((byteRate >> 8) & 0xff), (byte) ((byteRate >> 16) & 0xff), (byte) ((byteRate >> 24) & 0xff),  // ByteRate
                (byte) (numChannels * 2), 0,  // BlockAlign
                16, 0,  // BitsPerSample (16 bits)
                'd', 'a', 't', 'a',  // Subchunk2ID
                0, 0, 0, 0  // Subchunk2Size (to be updated later)
        });
    }

    private void updateWavHeader(FileOutputStream out) throws IOException {
        int fileSize = (int) new File(audioFilePath).length();
        try (RandomAccessFile wavFile = new RandomAccessFile(audioFilePath, "rw")) {
            wavFile.seek(4);
            wavFile.writeInt(fileSize - 8); // ChunkSize
            wavFile.seek(40);
            wavFile.writeInt(fileSize - 44); // Subchunk2Size
        }
    }
}
