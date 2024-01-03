package com.example.eggdetector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eggdetector.Helper.Constants;
import com.example.eggdetector.Helper.Helper;
import com.example.eggdetector.Helper.MainViewModel;
import com.example.eggdetector.Helper.Transaction;
import com.example.eggdetector.databinding.ActivityCameraBinding;
import com.example.eggdetector.ml.ModelUnquant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CameraActivity extends AppCompatActivity {

    ActivityCameraBinding binding;
    TextView result, confidence;
    ImageView imageView;
    int imageSize = 224;
    MainViewModel viewModel;
    Transaction transaction;
    private final int totalGoodCount = 0;
    Calendar calendar = Calendar.getInstance();


    private String[] classes = {"Good Egg", "Crack Egg", "Dirty Egg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        transaction = new Transaction();

        Button buttonOk = findViewById(R.id.OK);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = binding.result.getText().toString();
                Calendar calendar = Calendar.getInstance();
                String dateToShow = Helper.formatDate(calendar.getTime());
                binding.Date.setText(dateToShow);

                Transaction transaction = new Transaction();

                if ("Good Egg".equals(type)) {
                    transaction.setType(Constants.GOOD);
                } else if ("Crack Egg".equals(type)) {
                    transaction.setType(Constants.CRACKED);
                } else if ("Dirty Egg".equals(type)) {
                    transaction.setType(Constants.DIRTY);
                } else if ("Bloodspot Egg".equals(type)) {
                    transaction.setType(Constants.BLOOD_SPOT);
                }  else if ("No Bloodspot".equals(type)) {
                    transaction.setType(Constants.NO_BLOOD_SPOT);
                }
                int count = 1;  // Set the count to 1
                transaction.setCount(count);

                transaction.setDate(new Date());
                transaction.setDate(calendar.getTime());
                transaction.setId(calendar.getTime().getTime());

                viewModel.addTransaction(transaction);
                viewModel.getTransaction(calendar);

                Intent intent = new Intent(CameraActivity.this, RecordsActivity.class);
                startActivity(intent);
                finish();
            }
        });


        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("image")) {
            byte[] byteArray = intent.getByteArrayExtra("image");
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            binding.imageView.setImageBitmap(imageBitmap);
            saveImageToGallery(imageBitmap);


            classifyImage(imageBitmap);
        }
    }

    private void classifyImage(Bitmap imageBitmap) {
        try {
            Calendar calendar = Calendar.getInstance();
            String dateToShow = Helper.formatDate(calendar.getTime());
            binding.Date.setText(dateToShow);

            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            imageBitmap.getPixels(intValues, 0, imageBitmap.getWidth(), 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();

            String[] classes = {"Good Egg", "Crack Egg", "Dirty Egg", "No Bloodspot", "Bloodspot"};
            int maxPos = calculateMaxPosition(confidences);

            String resultText = classes[maxPos];
            binding.result.setText(resultText);

            if (confidences[maxPos] * 100 >= 74) {
                String s = "";
                for (int i = 0; i < classes.length; i++) {
                    s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
                }
                binding.confidence.setText(s);
            } else {
                // Confidence is below 74%, so do not display it
                binding.confidence.setText("");
            }

            model.close();

        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    private int calculateMaxPosition(float[] confidences) {
        int maxPosition = 0;
        float maxConfidence = 0;
        for (int i = 0; i < confidences.length; i++) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i];
                maxPosition = i;
            }
        }
        return maxPosition;
    }
    private void saveImageToGallery(Bitmap imageBitmap){
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";

        File imagefile = new File(storageDir,fileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(imagefile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imagefile));
            sendBroadcast(mediaScanIntent);

            Toast.makeText(this, "Image Saved To Gallery", Toast.LENGTH_LONG).show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}