package com.djhs16.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class ImageUtils {

    public static String getPackageName(Context c) {
        return c.getPackageName();
    }

    public static File createImageFile(String folderInExternalDirectory) {
        String imageFileName = "IMG_" + System.currentTimeMillis();
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + folderInExternalDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
//        String result;
//        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            result = contentUri.getPath();
//        } else {
//            cursor.moveToFirst();
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            result = cursor.getString(idx);
//            cursor.close();
//        }
//        return result;
        return FileUtils.getPath(context, contentUri);
    }

    public static Bitmap getBitmapFromFile(String filePath, int width, int height) {
       /* BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, width, height);

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);*/


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        //options.inSampleSize = calculateInSampleSize(options, width, height);

        int thumbnailSize = (height > width) ? height : width;
        int originalSize = (options.outHeight > options.outWidth) ? options.outHeight : options.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / thumbnailSize;

        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 && reqHeight == 0)
            return 1;

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static String getSmallImageFromSDCard(String folderInExternalStorage, String originalImage, int width, int height) {
        try {
            Bitmap b = BitmapFactory.decodeFile(originalImage);
            Bitmap out = Bitmap.createScaledBitmap(b, width, height, false);

            File file = createImageFile(folderInExternalStorage);
            FileOutputStream fOut;
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
            fOut.flush();
            fOut.close();

            b.recycle();
            out.recycle();

            return file.getAbsolutePath();
        } catch (Exception e) { // TODO
            return originalImage;
        }
    }

    public static String getRightAngleImage(String photoPath) {
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree;
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }
            return rotateImage(degree, photoPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoPath;
    }

    public static String rotateImage(int degree, String imagePath) {

        if (degree <= 0) {
            return imagePath;
        }
        try {

            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 8;

            Bitmap b = BitmapFactory.decodeFile(imagePath, options);

            Matrix matrix = new Matrix();
            if (b.getWidth() > b.getHeight()) {
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 80, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }
}