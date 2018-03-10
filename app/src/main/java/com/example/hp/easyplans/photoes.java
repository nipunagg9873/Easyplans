package com.example.hp.easyplans;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class photoes extends AppCompatActivity {

    View main;
    boolean inmain;
    View photo;
    GridView photoesgrid;
    long plan_id;
    final int TAKE_PHOTO = 100;
    final int CHOOSE_PHOTO = 101;
    String mcurrentphotopath;
    private ArrayList<Bitmap> imagelist = new ArrayList<>();
    private File directory;
    ArrayAdapter<Bitmap> imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        main = getLayoutInflater().inflate(R.layout.photos, null);
        setContentView(main);
        inmain = true;
        plan_id = getIntent().getLongExtra(PlansContract.plans.COLUMN_PLAN_ID, 0);
        photoesgrid = (GridView) main.findViewById(R.id.photos_grid_view);
        directory= getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/"+plan_id);
        imageAdapter = new ArrayAdapter<Bitmap>(this, R.layout.grid_item_photos, imagelist) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater();
                ImageView imageview = (ImageView) new ImageView(photoes.this);
                Bitmap image = getItem(position);
                imageview.setImageBitmap(image);
                int width= (int) getResources().getDisplayMetrics().density;

                imageview.setLayoutParams(new ViewGroup.LayoutParams(120*width,120*width));
                imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return imageview;
            }
        };
        new LoadImages(this,imageAdapter,directory,imagelist).execute();
        photoesgrid.setAdapter(imageAdapter);
        photoesgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                show_image(position);
            }
        });
    }
    void show_image(int position)
    {
        photo=getLayoutInflater().inflate(R.layout.grid_item_photos,null);
        setContentView(photo);
        File[] files=directory.listFiles();
        Bitmap image= BitmapFactory.decodeFile(files[position].getAbsolutePath());
        ImageView imageView=(ImageView)photo.findViewById(R.id.grid_item_photo_image_view);
        imageView.setImageBitmap(image);
        imageView.setBackgroundColor(Color.BLACK);
        inmain=false;
    }

    public void add_image_clicked(View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] dialogoptions = {"Take photo", "Choose from galary"};
        builder.setItems(dialogoptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        Intent take_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (take_photo.resolveActivity(getPackageManager()) != null) {
                            File photofile = null;
                            try {
                                photofile = createPhotoFile();
                            } catch (IOException ex) {
                                Log.v("easyplans", "io eroor occured");
                            }
                            if (photofile != null) {
                                Uri photouri = FileProvider.getUriForFile(photoes.this, "com.example.hp.easyplans", photofile);
                                take_photo.putExtra(MediaStore.EXTRA_OUTPUT, photouri);
                                startActivityForResult(take_photo, TAKE_PHOTO);
                            }
                        }
                        break;
                    }
                    case 1: {
                        Intent choose_photo = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        choose_photo.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                        startActivityForResult(choose_photo, CHOOSE_PHOTO);
                        break;
                    }
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if(!inmain)
        {
            setContentView(main);
            inmain=true;
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            imageAdapter.clear();
            new LoadImages(this,imageAdapter,directory,imagelist).execute();
        }
        if(requestCode==CHOOSE_PHOTO&&resultCode==RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            File newfile=null;
            try {
                newfile=createPhotoFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (newfile != null) {
                try {
                    FileUtils.copyFile(new File(picturePath),newfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            imageAdapter.clear();
            new LoadImages(this,imageAdapter,directory,imagelist).execute();
        }
    }

    private File createPhotoFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
        String imagefilename = plan_id + "_" + timestamp + "_";
        File storagedirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/"+plan_id);
        File image = File.createTempFile(imagefilename, ".jpg", storagedirectory);
        mcurrentphotopath = image.getAbsolutePath();
        return image;

    }

    void gallaryAddPic() {
        Intent mediascanintent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mcurrentphotopath);
        Uri contentUri = Uri.fromFile(f);
        mediascanintent.setData(contentUri);
        sendBroadcast(mediascanintent);
    }
}
