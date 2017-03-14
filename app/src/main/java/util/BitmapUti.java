package util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by wilbert on 2016/12/1.
 */
public class BitmapUti {
    public static Bitmap getBitmapFromUri(Context context, Uri uri, float heightPx){
        InputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
//                    options.inSampleSize = 8;
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            float bitmapHeight = options.outHeight;
            Resources resources = context.getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            float px = heightPx * (displayMetrics.densityDpi / 160f);
            int sampleSize = (int) (bitmapHeight / px);
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            inputStream = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public static Uri getCommonUri(String fileName) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "TravelAlbum");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                fileName);
        Uri result = Uri.fromFile(mediaFile);
        LogUti.d(result.toString());
        return result;
    }
}
