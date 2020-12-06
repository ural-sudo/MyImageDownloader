package com.example.myimagedownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {


    EditText txtURL;
    Button btnDownload;
    ImageView imgView;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static  String[] PERMISSION_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtURL = findViewById(R.id.txtURL);
        btnDownload = findViewById(R.id.btnDownload);
        imgView= findViewById(R.id.imgView);


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if( permission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                        PERMISSION_STORAGE,REQUEST_EXTERNAL_STORAGE);

                }else{
                    AsyncTask backgroundTask = new DownLoadTask();
                    String[] urls = new String[1];
                    urls[0] = txtURL.getText().toString();
                    backgroundTask.execute(urls);
                }





            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_EXTERNAL_STORAGE){
            if(grantResults.length ==2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
            grantResults[1] ==PackageManager.PERMISSION_GRANTED ){
                AsyncTask backgroundTask = new DownLoadTask();
                String[] urls = new String[1];
                urls[0] = txtURL.getText().toString();
                backgroundTask.execute(urls);

            }
        }
    }

    private void downloadFile(String urlStr, String imagePath){
        try {
            URL url = new URL(urlStr);
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream is = new BufferedInputStream(url.openStream(), 8192);
            OutputStream os = new FileOutputStream(imagePath);
            byte data [] = new byte[1024];
            int count;
            while((count = is.read(data)) == -1){
                os.write(data, 0,count);
            }
            os.flush();
            os.close();
            is.close();

        }catch (Exception ex){
            Log.d("downloadFile", ex.getMessage());
            ex.printStackTrace();
        }


    }
    class DownLoadTask extends AsyncTask<String,Integer,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strs) {
            Log.d("Download Task", strs[0]);
            String imagePath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).
                    toString() +"/temp.jpg";
            downloadFile(strs[0], imagePath);
            Log.d("Download Task", "download completed");
            Bitmap image = BitmapFactory.decodeFile(imagePath);
            Log.d("Download Task", "file to image");
            float w = image.getWidth();
            float h = image.getHeight();
            int W = 400;
            int H = (int) ((h*W) / w);
            Bitmap b = Bitmap.createScaledBitmap(image, W, H, false);
            return b;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d("onPostExecute", "settin image");
            super.onPostExecute(bitmap);
            Log.d("Download Task", "before imageView");
            imgView.setImageBitmap(bitmap);
            Log.d("Download Task", "after imageView");

        }
    }

}