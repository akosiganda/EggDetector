package com.example.eggdetector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eggdetector.databinding.ActivityScannerBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ScannerActivity extends AppCompatActivity {

    ActivityScannerBinding binding;
    private static final int CAMERA_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Scanner");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_scanner);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.bottom_records) {
                startActivity(new Intent(getApplicationContext(), RecordsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.bottom_scanner) {
                return true;
            } else if (itemId == R.id.bottom_info) {
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;

        });

        Button button = findViewById(R.id.Camera);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(ScannerActivity.this, ObjectDetectionActivity.class);
            startActivity(intent);
        });

//        button.setOnClickListener(view -> {
//            // Launch camera if we have permission
//            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, 1);
//            }
//            else {
//                //Request camera permission if we don't have it.
//                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap capturedImage = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent = new Intent(this, CameraActivity.class);
            intent.putExtra("image", byteArray);
            startActivity(intent);
        }
    }
}