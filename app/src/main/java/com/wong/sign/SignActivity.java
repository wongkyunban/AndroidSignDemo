package com.wong.sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignActivity extends AppCompatActivity {

    private HandwritingBoardView tuya;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
//        LinearLayout linearLayout = findViewById(R.id.ll_sign_panel);
//        tuya = new HandwritingBoardView(this);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        tuya.setLayoutParams(params);
//        linearLayout.addView(tuya);
        tuya = findViewById(R.id.ll_sign_panel);
//        tuya.setPenSize(20);
//        tuya.setPanelColor(Color.GREEN);
//        tuya.setPenColor(Color.BLUE);

    }

    // 确定签名
    public void onConfirmClick(View view){
        if(tuya.getBitmap() != null){
            Intent intent = new Intent();

            Bitmap bitmap = tuya.getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            /*下面方法表示压缩图片，中间的值越小，压缩比例越大，失真也约厉害，100表示不压缩*/
            bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
            byte[] byteArray = baos.toByteArray();
            intent.putExtra("bitmap",byteArray);
            setResult(RESULT_OK,intent);
            finish();
        }

    }

    // 取消签名
    public void onCancelClick(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    // 撤销
    public void undo(View view){
        tuya.undo();
    }
    // 重写
    public void redo(View view){
        tuya.redo();
    }

    public void SaveImage(){
        /*在这里保存图片纯粹是为了方便,保存图片进行验证*/
        String fileUrl = Environment.getExternalStorageDirectory()
                .toString() + "/android/data/test.png";
        try {
            if(tuya.getBitmap() == null) return;
            Bitmap mBitmap = tuya.getBitmap();
            FileOutputStream fos = new FileOutputStream(new File(fileUrl));
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}