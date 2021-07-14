package iss.ca.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageDownloader {

    private InputStream in;
    private FileOutputStream out;
    private FileOutputStream fos;

    protected boolean downloadImage(String imgURL, File destFile) throws IOException {
        try{
            URL url = new URL(imgURL);
            URLConnection conn = url.openConnection();

            in = conn.getInputStream();
            out = new FileOutputStream(destFile);

            byte[] buf = new byte[1024];
            int bytesRead = -1;
            while((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
            Bitmap bitmap = BitmapFactory.decodeFile(destFile.getAbsolutePath());
            resizeAndSaveImage(bitmap,destFile);

            return true;
        } catch (Exception e) {
            return false;
        }
        finally{
            if(out != null)
                out.close();
            if(in != null)
                in.close();
        }
    }


    protected void resizeAndSaveImage(Bitmap bitmap, File input) throws IOException {
        // delete existing file
        if(input.exists()){
            input.delete();
        }

        // resize the file and store in the internal storage
        if (!input.exists()) {
            try {
                fos = new FileOutputStream(input);
               ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap newbitmap = Bitmap.createScaledBitmap(bitmap, 270, 270, false);
                newbitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                 byte[] byteArray = stream.toByteArray();
                fos.write(byteArray);
                fos.flush();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            finally {
                if(fos != null)
                    fos.close();
            }
        }

    }



}
