package iss.ca.myapplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class HTMLDownloader {
    protected boolean downloadHTML(String link, File destFile) {
        try{
            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            InputStream in = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(destFile);

            byte[] buf = new byte[1024];
            int bytesRead = -1;
            while((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.close();
            in.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}