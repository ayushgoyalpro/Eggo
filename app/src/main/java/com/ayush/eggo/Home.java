package com.ayush.eggo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SELECT_PICTURE = 200;
    private static final int CAMERA_PERM_CODE = 200;

    Uri selectedImageUri;
    Bitmap bitmap = null;
    String currentPhotoPath;

    ImageView pickbtn, cambtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pickbtn = findViewById(R.id.pickbtn);
        cambtn = findViewById(R.id.cambtn);

        pickbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        cambtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {dispatchTakePictureIntent();}
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(Home.this, "Error Opening Camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            bitmapDispatcher(0);
        }
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            bitmapDispatcher(1);
        }
    }

    private void bitmapDispatcher(int option) {
        Intent intent = new Intent(Home.this, PreviewImage.class);
        intent.putExtra("option", option);
        if (option == 0) {
            intent.putExtra("bitmap", bitmap);
        }
        if (option == 1) {
            intent.putExtra("uri", selectedImageUri);
        }
        startActivity(intent);
    }

}