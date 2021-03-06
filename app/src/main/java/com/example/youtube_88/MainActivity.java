package com.example.youtube_88;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ImageView imv_anh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imv_anh = findViewById(R.id.imv_anh);
    }

    public void open_camera(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File file = create_imageFIile();
            if (file != null){
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        "com.example.youtube_88.fileprovider",
                        file
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT , photoURI );
                startActivityForResult(intent , 999);
            }
        }catch (ActivityNotFoundException e) {
            Toast.makeText(this , "ko thay phan mem chup anh nao",
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String currenPhotoPATH;
    public File create_imageFIile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String file_Name = "JPGE_" + timeStamp + "_";
        File storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                file_Name,
                ".jpg",
                storage
        );
        currenPhotoPATH = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == RESULT_OK){
//            preview image voi chat luong thap
//            Bundle bundle = data.getExtras();
//            Bitmap bitmap = (Bitmap) bundle.get("data");
//            img = findViewById(R.id.imv_cam);
//            img.setImageBitmap(bitmap);
//            luu anh vao may
            set_Pic();
//            quet lai thu vien
            gallery_Add_img();
        }
    }

    private void gallery_Add_img(){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(currenPhotoPATH);
        Uri uri_Content = Uri.fromFile(file);
        intent.setData(uri_Content);
        this.sendBroadcast(intent);
    }

    private void set_Pic(){
        int targetW = imv_anh.getWidth();
        int targetH = imv_anh.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currenPhotoPATH);

        int photoW = options.outWidth;
        int photoH = options.outHeight;

        int scaleFactor = Math.max(1 , Math.min(photoW/targetW , photoH/targetH ));

        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currenPhotoPATH , options);
        imv_anh.setImageBitmap(bitmap);
    }
}