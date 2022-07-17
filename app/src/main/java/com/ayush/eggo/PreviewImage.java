package com.ayush.eggo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

public class PreviewImage extends AppCompatActivity {

    Bitmap bitmap = null;
    ProgressDialog p;
    ImageView previewImg;
    TextView resulttv;
    final int DEFAULT_TIMEOUT = 20 * 1000;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        previewImg = findViewById(R.id.previewImg);
        resulttv = findViewById(R.id.result);
        Intent intent = getIntent();
        int option = intent.getIntExtra("option", -1);
        if (option == 0){
            bitmap = intent.getParcelableExtra("bitmap");
        }
        else if (option == 1){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), intent.getParcelableExtra("uri"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(PreviewImage.this, "Error", Toast.LENGTH_LONG).show();
        }
        previewImg.setImageBitmap(bitmap);
        resulttv.setText("");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        RequestParams params = new RequestParams();
        params.put("inputImg", new ByteArrayInputStream(byteArray), "newFile.jpeg");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);
        client.post(PreviewImage.this, "https://www.asthetish.com/eggo/script.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                p = new ProgressDialog(PreviewImage.this);
                p.setMessage("Please wait... Results are OTW!!");
                p.setIndeterminate(false);
                p.setCancelable(false);
                p.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("API","Failed: " + error.toString());
                p.hide();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("API", "inside onSuccess()");
                String x = "";
                for (int i = 0; i < responseBody.length; i++) {
                    x += String.valueOf((char)Integer.parseInt(String.valueOf(responseBody[i])));
                }
                resulttv.setText(x);
                p.hide();
            }
        });
    }
}