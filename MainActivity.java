package com.example.jujungin.imagescan;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;

    private Uri mImageCaptureUri;
    private ImageView iv_UserPhoto;
    private String absoultePath;

    private TessBaseAPI mTess;

    String datapath = "";
    Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = BitmapFactory.decodeResource(getResources(), R.drawable.test1);
        datapath = getFilesDir()+"/tesseract/";
        iv_UserPhoto = findViewById(R.id.image);
        checkFile(new File(datapath+"tessdata/"));
        String lang = "kor";
        mTess = new TessBaseAPI();
        mTess.init(datapath,lang);


        ImageButton album = findViewById(R.id.btn_file);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBUM);
            }
        });

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Start scan!!!",Toast.LENGTH_LONG).show();
                processImage(v);
            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if( resultCode != RESULT_OK) return;

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                try {
                    Uri uri = data.getData();
                    mImageCaptureUri = data.getData();
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
                    iv_UserPhoto.setImageBitmap(image_bitmap);
                    String f_path = getPath(uri);
                    String f_name = getName(uri);
                    Toast.makeText(getApplicationContext(),f_path,Toast.LENGTH_LONG).show();
                    image = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());



                    datapath = getFilesDir()+"/tesseract/";
                    checkFile(new File(datapath+"tessdata/"));
                    String lang = "kor";
                    mTess = new TessBaseAPI();
                    mTess.init(datapath,lang);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            case PICK_FROM_CAMERA: {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri,"image/*");

                intent.putExtra("outputX",200);
                intent.putExtra("outputY",200);
                intent.putExtra("aspectX",1);
                intent.putExtra("aspectY",1);
                intent.putExtra("scale",true);
                intent.putExtra("return-data",true);
                startActivityForResult(intent,CROP_FROM_iMAGE);

                break;
            }

        }
    }

    private void checkFile(File dir) {
        if(!dir.exists() && dir.mkdirs() ) {
            copyFiles();
        }

        if(dir.exists()) {
            String datafilepath = datapath + "/tessdata/kor.traineddata";
            File datafile = new File(datafilepath);
            if( !datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/kor.traineddata";

            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/kor.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while( (read = instream.read(buffer)) != -1) {
                outstream.write(buffer,0,read);
            }

            outstream.flush();
            outstream.close();
            instream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void processImage(View view) {
        String OCRresult = null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        TextView OCRTextView = findViewById(R.id.text);

        OCRTextView.setText(OCRresult);
    }
    private String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    private String getName(Uri uri)
    {
        String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
