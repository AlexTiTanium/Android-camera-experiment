package com.promo.nestea;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends FragmentActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_CONTENT_RESOLVER = 101;

    private Uri previewImageView;
    private ImageView photoUri;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    //Content Resolver
    private Uri mPhotoUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button;
        photoUri = (ImageView) findViewById(R.id.fotoPreview);

        button = (Button) findViewById(R.id.takeFotoBtn);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // Alternative strategy of generating a content URI using the MediaStore
                // This way seems to work very reliably

                mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_CONTENT_RESOLVER);
            }
        }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        photoUri.setImageBitmap(null);

            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_CONTENT_RESOLVER && resultCode == RESULT_OK) {
                // Image saved to a generated MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                String[] projection = {
                        MediaStore.MediaColumns._ID,
                        MediaStore.Images.ImageColumns.ORIENTATION,
                        MediaStore.Images.Media.DATA
                };
                Cursor c = getContentResolver().query(mPhotoUri, projection, null, null, null);
                c.moveToFirst();
                String photoFileName = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                Bitmap bitmap = BitmapFactory.decodeFile(photoFileName);
                photoUri.setImageBitmap(bitmap);

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
    }

}
