package com.example.smartcampusrecruitment.Tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class ImageCompressor {
    public static String compressBase64(String base64, int maxWidth) {
        if (base64 == null || base64.isEmpty()) return "";

        try {
            byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
            Bitmap src = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            if (src == null) return base64;

            // 计算缩放比例
            float scale = (float) maxWidth / src.getWidth();
            int newWidth  = (int) (src.getWidth()  * scale);
            int newHeight = (int) (src.getHeight() * scale);
            Bitmap scaled = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);

            // JPEG 压缩，质量 80
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            byte[] newBytes = bos.toByteArray();

            // 回收原图
            if (!src.isRecycled()) src.recycle();
            if (!scaled.isRecycled()) scaled.recycle();
            String compressedBase64 = Base64.encodeToString(newBytes, Base64.NO_WRAP);
            Log.d("ImageCompressor", "压缩后的 Base64 前 100 字符: " + compressedBase64.substring(0, Math.min(100, compressedBase64.length())));

            return Base64.encodeToString(newBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e("ImageCompressor", "压缩失败", e);
            return base64;   // 失败时原样返回
        }
    }
}
