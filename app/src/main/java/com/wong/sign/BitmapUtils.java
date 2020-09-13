package com.wong.sign;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {



    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight);
    }

    /**
     * 缩放图片
     *
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    public static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
        if(dstHeight <= 0 || dstWidth <= 0) return null;
        /*如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false*/
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    /**
     * 计算图片压缩比例（采样率）
     * 通常如果图片是针对某种分辨率设计的，直接decode图片不会有什么问题。
     * 但是，如果图片不是特定设计，且比较大的话，容易造成OOM。
     * 假设，一个ImageView大小为512*384，有一张2048*1536的图片需要显示，
     * 那么如果加载整张图片，那就造成了浪费，这时候就需要采用不同的采样率对原图片进行一定的压缩。
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        /*源图片的高度和宽度*/
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            /*计算出实际宽高和目标宽高的比率*/
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


}
