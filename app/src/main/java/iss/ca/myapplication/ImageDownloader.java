package iss.ca.myapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloader {
    protected boolean downloadImage(String imgURL, File destFile) {
        try{
            URL url = new URL(imgURL);
            URLConnection conn = url.openConnection();

            InputStream in = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(destFile);

            byte[] buf = new byte[1024];
            int bytesRead = -1;
            while((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
            Bitmap bitmap = BitmapFactory.decodeFile(destFile.getAbsolutePath());
            resizeAndSaveImage(bitmap,destFile);


            out.close();
            in.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    protected void resizeAndSaveImage(Bitmap bitmap, File input){
        // delete existing file
        if(input.exists()){
            input.delete();
        }

        // resize the file and store in the internal storage
        if (!input.exists()) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(input);
               ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap newbitmap = Bitmap.createScaledBitmap(bitmap, 270, 270, false);
                newbitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                 byte[] byteArray = stream.toByteArray();
                fos.write(byteArray);
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

    }


}
