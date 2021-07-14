package iss.ca.myapplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class HTMLDownloader {

    private InputStream in;
    private FileOutputStream out;

    protected boolean downloadHTML(String link, File destFile) throws IOException {
        try{
            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            in = conn.getInputStream();
            out = new FileOutputStream(destFile);

            byte[] buf = new byte[1024];
            int bytesRead = -1;
            while((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }


            return true;
        } catch (Exception e) {
            return false;
        }
        finally {
            if(out != null)
                out.close();
            if(in != null)
                in.close();
        }
    }


}