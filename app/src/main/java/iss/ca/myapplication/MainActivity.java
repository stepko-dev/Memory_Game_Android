package iss.ca.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button mSearchBtn;
    ProgressBar mProgressBar;
    private AsyncTask mMyTask;
    List<Bitmap> bitmaplist = new ArrayList<>();
    File mTargetFile;

 List<String> listImages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBtn = (Button) findViewById(R.id.searchBtn);

        String filePath = "SampleFolder";
        String fileName = "SampleFile.jpg";
        mTargetFile = new File(getFilesDir(), filePath + "/" + fileName);


        Spinner spinner = (Spinner) findViewById(R.id.search);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pics_array, android.R.layout.simple_spinner_item);




        Drawable[] images = new Drawable[] {
                getDrawable(R.drawable.apple_10_resized), getDrawable(R.drawable.apple_11_resized),
                getDrawable(R.drawable.apple_12_resized), getDrawable(R.drawable.apple_13_resized),
                getDrawable(R.drawable.apple_17_resized), getDrawable(R.drawable.apple_18_resized),
                getDrawable(R.drawable.apple_19_resized), getDrawable(R.drawable.apple_1_resized),
                getDrawable(R.drawable.apple_20_resized), getDrawable(R.drawable.apple_21_resized),
                getDrawable(R.drawable.apple_25_resized), getDrawable(R.drawable.apple_26_resized),
                getDrawable(R.drawable.apple_27_resized), getDrawable(R.drawable.apple_28_resized),
                getDrawable(R.drawable.apple_29_resized), getDrawable(R.drawable.apple_2_resized),
                getDrawable(R.drawable.apple_3_resized), getDrawable(R.drawable.apple_4_resized),
                getDrawable(R.drawable.apple_5_resized), getDrawable(R.drawable.apple_9_resized)
        };

//        LinearLayout layout = (LinearLayout) findViewById(R.id.parentLayout);
//
//        TableLayout table = new TableLayout(this);
//        table.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 2.0f));
//        layout.addView(table);
//        for(int i=0;i<5;i++) {
//            TableRow row = new TableRow(this);
//            table.addView(row);
//            for(int j=0;j<4;j++) {
//                ImageView imageView = new ImageView(this);
//                imageView.setImageDrawable(new BitmapDrawable(getResources(), scaleDown(((BitmapDrawable)images[i*4 + j]).getBitmap(), 100, true)));
//                imageView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
//                row.addView(imageView);
//            }
//        }
//        setContentView(layout);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = spinner.getSelectedItem().toString();
                if(url!= null){
                   // try {
                        initImages();

//                        org.jsoup.nodes.Document document = Jsoup.connect(url).get();
//                        org.jsoup.select.Elements links = document.getElementsByClass("photo-grid-preview");
//                        int count = 0;
//                        for(org.jsoup.nodes.Element link : links) {
//                            count++;
//                            if(count<21){
//                                String linkHref = link.getElementsByTag("img").attr("src");
//                                listImages.add(linkHref);
//                                // publishProgress();
//                                //mProgressBar.setProgress((int) ((count / (float) count) * 100));
//
//                                //System.out.println(linkHref);
//                            }
//                            else{
//                                System.out.println(listImages.size());
//                                break;
//                            }
//
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                   new  retrieveImgUrl().execute(url);
                    for(String imgUrls : listImages) {
                        mMyTask = new theurlgrabber().execute(stringToURL(imgUrls));
                    }

                }
            }
        });


    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    // Custom method to convert string to url
    protected URL stringToURL(String urlString){
        try{
            URL url = new URL(urlString);
            return url;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    private class retrieveImgUrl extends AsyncTask<String,Void, List<String>>{

        @Override
        protected List<String> doInBackground(String... strings){
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(strings[0]).get();
                org.jsoup.select.Elements links = document.getElementsByClass("photo-grid-preview");
                int count = 0;
                for(org.jsoup.nodes.Element link : links) {
                    count++;
                    if(count<21){
                        String linkHref = link.getElementsByTag("img").attr("src");
                        listImages.add(linkHref);
                       // publishProgress();
                        //mProgressBar.setProgress((int) ((count / (float) count) * 100));

                        //System.out.println(linkHref);
                    }
                    else{
                        System.out.println(listImages.size());
                        break;
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return listImages;
        }

        @Override
        protected void onPostExecute(List<String>result){
            super.onPostExecute(result);
        }


    }

    private class theurlgrabber  extends AsyncTask<URL, Void, Bitmap> {
       // String url = "https://stocksnap.io/search/business";
        String url;
        Random random = new Random();
        Integer pic = random.nextInt(20);

//        public theurlgrabber(String url) {
//        this.url = url;
//        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected void onProgressUpdate(Integer... progress){
        }

        @Override
        protected Bitmap doInBackground(URL... urls) {
            URL url = urls[0];
            HttpURLConnection connection = null;

            try{
                // Initialize a new http url connection
                connection = (HttpURLConnection) url.openConnection();

                // Connect the http url connection
                connection.connect();

                // Get the input stream from http url connection
                InputStream inputStream = connection.getInputStream();

                /*
                    BufferedInputStream
                        A BufferedInputStream adds functionality to another input stream-namely,
                        the ability to buffer the input and to support the mark and reset methods.
                */
                /*
                    BufferedInputStream(InputStream in)
                        Creates a BufferedInputStream and saves its argument,
                        the input stream in, for later use.
                */
                // Initialize a new BufferedInputStream from InputStream
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                /*
                    decodeStream
                        Bitmap decodeStream (InputStream is)
                            Decode an input stream into a bitmap. If the input stream is null, or
                            cannot be used to decode a bitmap, the function returns null. The stream's
                            position will be where ever it was after the encoded data was read.

                        Parameters
                            is InputStream : The input stream that holds the raw data
                                              to be decoded into a bitmap.
                        Returns
                            Bitmap : The decoded bitmap, or null if the image data could not be decoded.
                */
                // Convert BufferedInputStream to Bitmap object
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                // Return the downloaded bitmap
                return bmp;

            }catch(IOException e){
                e.printStackTrace();
            }finally{
                // Disconnect the http url connection
                connection.disconnect();
            }
            return null;
        }

//            listImages.clear();
//            try {
//                org.jsoup.nodes.Document document = Jsoup.connect(url).get();
//                org.jsoup.select.Elements links = document.getElementsByClass("photo-grid-preview");
//                int count = 0;
//                for(org.jsoup.nodes.Element link : links) {
//                    count++;
//                    if(count<21){
//                        String linkHref = link.getElementsByTag("img").attr("src");
//                        listImages.add(linkHref);
//                       // publishProgress();
//                        //mProgressBar.setProgress((int) ((count / (float) count) * 100));
//
//                        //System.out.println(linkHref);
//                    }
//                    else{
//                        System.out.println(listImages.size());
//                        break;
//                    }
//
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//            return null;
//        }

        protected void onPostExecute(Bitmap result){
            if(result!=null){

                ImageView imageView = null;

                // Save bitmap to internal storage
                Uri imageInternalUri = saveImageToInternalStorage(result);

                for(int i = 1; i <= 20; i++)
                    imageView = findViewById(Integer.valueOf(i));
                    if(imageView.getDrawable() == getDrawable(R.drawable.image)){



                        // Display the downloaded image into ImageView
//                        imageView.setImageBitmap(result);


                        // Set the ImageView image from internal storage
                        imageView.setImageURI(imageInternalUri);

                    }

            }


        }
    }

    protected Uri saveImageToInternalStorage(Bitmap bitmap){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("image9Dir", Context.MODE_PRIVATE);

//        File parent = mTargetFile.getParentFile();
//        if(!parent.exists() && !parent.mkdirs()){
//            throw new IllegalStateException("couldn't create dir:" +parent);
//        }


            File file = new File(directory,  bitmap.toString() + ".jpg");
            //Bitmap bitmap = bitmaps.get(i);

            if (!file.exists()) {
                Log.d("path", file.toString());
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
            // Parse the gallery image url to uri
             Uri savedImageURI = Uri.parse(file.getAbsolutePath());

            // Return the saved image Uri
            return savedImageURI;
    }


    protected void initImages() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.parentLayout);

        // Remember to delete images from storage when delete the table
        if(findViewById(Integer.valueOf(99)) != null)
            layout.removeViewInLayout(findViewById(Integer.valueOf(99)));

        if(findViewById(Integer.valueOf(100)) != null)
            layout.removeViewInLayout(findViewById(Integer.valueOf(100)));

        //create the table again
        TableLayout table = new TableLayout(getApplicationContext());
        table.setId(Integer.valueOf(99));

        table.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f));
        layout.addView(table);

        for(int i=0;i<5;i++) {
            TableRow row = new TableRow(getApplicationContext());
            table.addView(row);
            for(int j=0;j<4;j++) {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setId(i*4 + j + 1);
                Picasso.with(MainActivity.this).load("drawable//" + R.drawable.image).resize(270,270).into(imageView);
//                    imageView.setImageDrawable(new BitmapDrawable(getResources(), scaleDown((Bitmap)(getDrawable(R.drawable.image)).getBitmap(), 100, true)));
                imageView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
                row.addView(imageView);
            }
        }

        ProgressBar progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setId(Integer.valueOf(100));
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        layout.addView(progressBar);

        setContentView(layout);
    }


//    Document document = Jsoup.parse(html);
//
//    Elements images = document.select("img");
//for (Element image : images) {
//        String imageUrl = image.attr("data-original");
//        System.out.println(imageUrl);
//    }
}