package net.jileniao.imageeditor.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

public class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();

    public static final int TARGET_SIZE = 960;

    public static Bitmap resizeBitmapByScale(String tag, Bitmap bitmap,
            float scale, boolean recycle) {
        if (bitmap == null)
            return bitmap;
        int width = Math.round(bitmap.getWidth() * scale);
        int height = Math.round(bitmap.getHeight() * scale);
        if (width == bitmap.getWidth() && height == bitmap.getHeight())
            return bitmap;
        Bitmap target = LogCreate(tag,
                Bitmap.createBitmap(width, height, getConfig(bitmap)));
        Canvas canvas = new Canvas(target);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle)
            recycleSilently(tag, bitmap);
        return target;
    }

    private static Bitmap.Config getConfig(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        return config;
    }

    public static Bitmap resizeDownIfTooBig(String tag, Bitmap bitmap,
            int targetSize, boolean recycle) {
        if (bitmap == null)
            return bitmap;
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scale = Math.max((float) targetSize / srcWidth,
                (float) targetSize / srcHeight);
        if (scale > 0.5f)
            return bitmap;
        return resizeBitmapByScale(tag, bitmap, scale, recycle);
    }

    public static void recycleSilently(String tag, Bitmap bitmap) {
        if (bitmap == null)
            return;
        try {
            Log.w(TAG, "[BMP_RELEASE][ID:" + getBitmapNativeId(bitmap) + "]["
                    + tag + "]");
            bitmap.recycle();
        } catch (Throwable t) {
            Log.w(TAG, "WARNING: unable recycle bitmap-" + tag, t);
        }
    }

    public static int getOrientation(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            cursor = context
                    .getContentResolver()
                    .query(uri,
                            new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
                            null, null, null);
            if (cursor == null) {
                return 0;
            }
            if (cursor.moveToNext()) {
                int ori = cursor.getInt(0);
                return ori;
            } else {
                return -1;
            }
        } catch (SQLiteException e) {
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Bitmap rotateBitmap(String tag, Bitmap source, int rotation,
            boolean recycle) {
        if (rotation == 0)
            return source;
        int w = source.getWidth();
        int h = source.getHeight();
        Matrix m = new Matrix();
        m.postRotate(rotation);
        Bitmap bitmap = LogCreate(tag,
                Bitmap.createBitmap(source, 0, 0, w, h, m, true));
        if (recycle)
            recycleSilently(tag, source);
        return bitmap;
    }

    public static Bitmap LogCreate(String tag, Bitmap bitmap) {
        Log.d("DecodeUtils", "[BMP_CREATE][ID:" + getBitmapNativeId(bitmap)
                + "][" + tag + "]");
        return bitmap;
    }

    private static Field FILELD_NATIVE_ID = null;

    public static long getBitmapNativeId(Bitmap bitmap) {
        if (bitmap == null)
            return 0l;
        if (FILELD_NATIVE_ID == null) {
            try {
                FILELD_NATIVE_ID = Bitmap.class
                        .getDeclaredField("mNativeBitmap");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        if (FILELD_NATIVE_ID == null)
            return 0l;
        try {
            return FILELD_NATIVE_ID.getLong(bitmap);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
            System.gc();
        }
    }

    /**
     * convert bitmap to byte[]
     * 
     * @param bitmap
     * @return byte[]
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * convert byte[] to bitmap
     * 
     * @param b
     * @return Bitmap
     */
    public static Bitmap bytes2Bitmap(byte[] b) {
        if (b == null || b.length == 0) {
            return null;
        } else {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
    }

    /**
     * ビットマップリサイズ
     * 
     * @param bmp
     *            the bitmap before resize
     * @param newWidth
     *            the target width
     * @param newHeight
     *            the target height
     * @return the bitmap after resize
     */
    private static Bitmap resizeBitmap(Bitmap bmp, float newWidth,
            float newHeight) {
        float scaleWidth = newWidth / bmp.getWidth();
        float scaleHeight = newHeight / bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // GCが発生する
        Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                bmp.getHeight(), matrix, true);
        bmp = null;

        return resizedBitmap;
    }

    /**
     * 从原始的byte[]得到Bitmap对象
     * 
     * @return Bitmap
     */
    public static Bitmap createBitmapFromOriginalData(byte[] data, int width,
            int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                options);

        // resize bitmap according to the selected picture size
        bitmap = resizeBitmap(bitmap, width, height);

        return bitmap;
    }

    /**
     * reversal bitmap at left-right
     * 
     * @param originalImage
     * @return the bitmap after reversal
     */
    public static Bitmap createReversal(Bitmap originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);

        return Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix,
                false);
    }

    /** saved image file name format */
    private static final String DATA_FORMAT = "'IMG_'yyyyMMdd_HHmmss'.jpg'";
    /** saved image file path */
    public static final String PIC_ROOT_PATH = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            .toString()
            + File.separator;

    /**
     * 保存Bitmap到文件
     * 
     * @param context
     * @param bmp
     * @param isRotate
     * @return
     */
    public static boolean storeImage(Context context, Bitmap bmp,
            boolean isRotate) {

        // use the current data&time for image file name
        String takenTime_YYMMDD_HHMMSS = new SimpleDateFormat(DATA_FORMAT)
                .format(new Date());

        // saved bitmap: full path
        String path = PIC_ROOT_PATH + takenTime_YYMMDD_HHMMSS;
        File f = new File(path);

        if (f != null && !f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        if (isRotate) {
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.postRotate(90);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), matrix, true);
        }

        try {
            FileOutputStream out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return updateGallery(context, bmp, takenTime_YYMMDD_HHMMSS);
    }

    @SuppressLint("InlinedApi")
    private static boolean updateGallery(Context context, Bitmap bmp,
            String fileNm) {
        // Insert into MediaStore.
        ContentValues values = new ContentValues();
        values.put(ImageColumns.TITLE, fileNm);
        values.put(ImageColumns.DISPLAY_NAME, fileNm);
        values.put(ImageColumns.DATE_TAKEN, System.currentTimeMillis());
        values.put(ImageColumns.MIME_TYPE, "image/jpeg");
        values.put(ImageColumns.ORIENTATION, 0);
        values.put(ImageColumns.DATA, PIC_ROOT_PATH + fileNm);
        values.put(ImageColumns.WIDTH, bmp.getWidth());
        values.put(ImageColumns.HEIGHT, bmp.getHeight());

        try {
            Uri uri = context.getContentResolver().insert(
                    Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                Log.e("storeImage", "Failed to insert MediaStore");
                return false;
            } else {
                context.sendBroadcast(new Intent(
                        "com.android.camera.NEW_PICTURE", uri));
            }
        } catch (Exception e) {
            Log.e("storeImage", "Failed to write MediaStore", e);
            return false;
        }

        return true;
    }

    /**
     * 根据图片在服务器上的地址加载图片
     * 
     * @param urlStr
     * @param reqWidth
     * @param reqHeight
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static Bitmap decodeSampledBitmapFromUrl(String urlStr,
            int reqWidth, int reqHeight) throws MalformedURLException,
            IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory
                .decodeStream(getInputStreamFromUrl(urlStr), null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        return BitmapFactory.decodeStream(getInputStreamFromUrl(urlStr), null,
                options);
    }

    /**
     * 从url中得到流
     * 
     * @param urlStr
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static InputStream getInputStreamFromUrl(String urlStr)
            throws MalformedURLException, IOException {
        URL url = new URL(urlStr);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        InputStream inputStream = urlConn.getInputStream();
        return inputStream;
    }

    /**
     * 计算inSampleSize
     * 
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static Bitmap getBitmapFromUri(Context context, Uri photoUri) {
        if (photoUri == null) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            ContentResolver conReslv = context.getContentResolver();
            // 得到选择图片的Bitmap对象
            bitmap = MediaStore.Images.Media.getBitmap(conReslv, photoUri);
        } catch (Exception e) {
            Log.e(TAG, "Media.getBitmap failed", e);
        }
        if (bitmap != null) {
            bitmap = bitmap.copy(Config.ARGB_8888, true);
        }

        return bitmap;
    }
}
