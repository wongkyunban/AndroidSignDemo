package com.wong.sign;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    // 签名
    public static final int SIGN_REQUEST = 1000;

    private ImageView mIVSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIVSign = findViewById(R.id.iv_sign);
    }

    // 打开签名
    public void onSignClick(View view) {
        Intent intent = new Intent(this, SignActivity.class);
        startActivityForResult(intent, SIGN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 签名的回调
        if (requestCode == SIGN_REQUEST && resultCode == RESULT_OK && data != null) {
            byte[] byteArray = data.getByteArrayExtra("bitmap");
            final Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            mIVSign.post(new Runnable() {
                @Override
                public void run() {
                    int imageWidth = mIVSign.getWidth();// ImageView的宽
                    int imageHeight = mIVSign.getHeight();// ImageView的高
                    /**
                     *  BitmapFactory提供了多种方式根据不同的图片源来创建Bitmap对象：
                     *   （1）Bitmap BitmapFactory.decodeFile(String pathName, Options opts)： 读取SD卡上的图片。
                     *   （2）Bitmap BitmapFactory.decodeResource(Resources res, int id, Options opts) ： 读取网络上的图片。
                     *   （3）Bitmap BitmapFactory.decodeResource(Resources res, int id, Options opts) ： 读取资源文件中的图片。
                     *   这三个函数默认会直接为bitmap分配内存，我们通过第二个参数Options来控制，让它不分配内存，同时可以得到图片的相关信息。具体参考：
                     *   BitmapFactory.Options options = new BitmapFactory.Options();
                     *   options.inJustDecodeBounds = true;
                     *   BitmapFactory.decodeFile(pathName，options);
                     *   int imageHeight = options.outHeight;
                     *   int imageWidth = options.outWidth;
                     */
                    int bitmapWidth = bitmap.getWidth();// Bitmap的宽
                    int bitmapHeight = bitmap.getHeight();// Bitmap的高

                    mIVSign.setImageBitmap(BitmapUtils.createScaleBitmap(bitmap,imageWidth,imageHeight));


                }
            });
        }
    }
}