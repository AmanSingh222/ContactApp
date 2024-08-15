package com.example.xurveykshandemoapp.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.xurveykshandemoapp.R;

import java.util.List;

public class SelfieFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private ImageView imageView;
    private Bitmap capturedImage;
    private double latitude, longitude;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selfie, container, false);

        imageView = view.findViewById(R.id.imageViewSelfie);
        Button captureButton = view.findViewById(R.id.buttonCapture);
        Button submitButton = view.findViewById(R.id.buttonSubmit);
        fetchLocation();
        captureButton.setOnClickListener(v -> checkCameraPermission());
        submitButton.setOnClickListener(v -> {
            if (capturedImage != null) {
                // Handle form submission logic here
                Toast.makeText(getActivity(), "lat "+ latitude+ "log"+ longitude, Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), "Selfie captured!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please capture a selfie first.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request camera permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            // If permission is already granted, open the camera
            openFrontCamera();
        }
    }

    private void openFrontCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if the front camera exists
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1); // Open front camera
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(getContext(), "Front camera not available!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            capturedImage = (Bitmap) extras.get("data");
            imageView.setImageBitmap(capturedImage);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFrontCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission is required to capture a selfie.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);
            Location location = getLastKnownLocation(locationManager);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(getContext(), "Latitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Unable to fetch location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Location getLastKnownLocation(LocationManager locationManager) {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = location;
                }
            }
        }
        return bestLocation;
    }

}
