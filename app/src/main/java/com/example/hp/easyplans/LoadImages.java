package com.example.hp.easyplans;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by HP on 7/30/2017.
 */

public class LoadImages extends AsyncTask<Void, Void, Void> {
    private File[] listFile;
    Context mContext;
    ArrayAdapter<Bitmap> imageAdapter;
    ArrayList<Bitmap> imagelist;

    public LoadImages(Context context, ArrayAdapter<Bitmap> adapter, File directory, ArrayList<Bitmap> imagelist) {
        listFile = directory.listFiles();
        imageAdapter = adapter;
        mContext = context;
        this.imagelist = imagelist;
    }

    private Bitmap decodeFile(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        try {
            FileInputStream fis = new FileInputStream(f);

            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > 100 * mContext.getResources().getDisplayMetrics().density || o.outWidth > 100 * mContext.getResources().getDisplayMetrics().density) {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(100 * mContext.getResources().getDisplayMetrics().density /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));

            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.outHeight= (int) (100 * mContext.getResources().getDisplayMetrics().density);
            o2.outWidth= (int) (100 * mContext.getResources().getDisplayMetrics().density);
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return b;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                imagelist.add(decodeFile(listFile[i]));
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        imageAdapter.notifyDataSetChanged();

    }
}
