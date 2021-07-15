package iss.ca.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    Button mSearchBtn;
    ProgressBar mProgressBar;
    TextView tv;
    String selectedUrl = null;
    private Thread dlThread;
    boolean interrupted = false;
    List<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBtn = (Button) findViewById(R.id.searchBtn);
        Spinner spinner = (Spinner) findViewById(R.id.search);

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUrl = spinner.getSelectedItem().toString();

                urls.clear();
                if(dlThread != null){
                    if(dlThread.isAlive()) {
                        // dlThread is not null and is alive
                        // If onclick comes in during this period, it is interrupting the download
                        // Set interrupted to true
                        interrupted = true;
                        // Introduce button delay to prevent malicious spam fetching
                        mSearchBtn.setEnabled(false);
                        mSearchBtn.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mSearchBtn.setEnabled(true);
                            }
                        }, 800);
                    }
                    // Clear existing files
                    // Restart download
                    deleteAllFiles();
                    startDownloadingHTML(selectedUrl);
                }
                else
                {
                    // dlThread is null, user has just clicked for the first time and dlThread has not been launched yet
                    // Clear existing files
                    // Restart download
                    deleteAllFiles();
                    startDownloadingHTML(selectedUrl);
                }
                // Always initialize the following:
                // -Table and rows to hold images
                // -Default images in the imageView before the actual image loads in
                // -ProgressBar and TextView under progress bar
                init();
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

    protected void init() {
        // Get outermost LinearLayout
        LinearLayout parent = (LinearLayout) findViewById(R.id.parentLayout);

        // Get the LinearLayout which holds the table and all the pictures within it
        LinearLayout picGrid = (LinearLayout) findViewById(R.id.pic_stabilizer);

        // Get the LinearLayout which holds the progressbar and textview within in
        LinearLayout progress = (LinearLayout) findViewById(R.id.progress_stabilizer);

        // If table exists, destroy table
        // If doesn't exist, proceed to create it as per normal
        if (findViewById(Integer.valueOf(99)) != null)
            picGrid.removeViewInLayout(findViewById(Integer.valueOf(99)));

        // If progress bar exists, destroy progress bar
        // If doesn't exist, proceed to create it as per normal
        if (findViewById(Integer.valueOf(100)) != null)
            progress.removeViewInLayout(findViewById(Integer.valueOf(100)));

        // If text view exists, destroy text view
        // If doesn't exist, proceed to create it as per normal
        if (findViewById(Integer.valueOf(101)) != null)
            progress.removeViewInLayout(findViewById(Integer.valueOf(101)));

        //create the table again
        TableLayout table = new TableLayout(getApplicationContext());
        table.setId(Integer.valueOf(99));

        table.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f));
        picGrid.addView(table);

        // Create 5 rows and add it to the table
        for (int i = 0; i < 5; i++) {
            TableRow row = new TableRow(getApplicationContext());
            table.addView(row);
            // For each row, create 4 imageViews
            // Set their ids to be from 1 to 20
            // Put in default image into the imageView
            // Set tag for attaching information to imageView later on
            // Set imageView layout parameters and add to the row
            for (int j = 0; j < 4; j++) {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setId(i * 4 + j + 1);
                // scaleDown for resizing image so that it fits nicely
                imageView.setImageDrawable(new BitmapDrawable(getResources(), scaleDown(((BitmapDrawable) getDrawable(R.drawable.image)).getBitmap(), 98, true)));
                imageView.setTag(R.string.none, null);
                imageView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                row.addView(imageView);
            }
        }

        // create progress bar again
        ProgressBar progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setId(Integer.valueOf(100));
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(875, 100, 1.0f));
        progressBar.setScaleY(8.0f);
        progressBar.setPadding(200, 0, 0, 0);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(20);
        progress.addView(progressBar);

        // create text view again
        TextView tv = new TextView(getApplicationContext());
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        tv.setId(Integer.valueOf(101));
        progress.addView(tv);

        setContentView(parent);
    }

    public void deleteAllFiles() {

        try {
            File directory = getDir(getString(R.string.folder), Context.MODE_PRIVATE);
            if (directory.exists()) {
                for (File child : directory.listFiles()) {
                    child.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteRemainingFiles(List<Bitmap> bitmapList) {

        // find the selected 6 file images from internal storage
        File directory = getDir(getString(R.string.folder), Context.MODE_PRIVATE);
        Map<File,Bitmap> fileList = new HashMap<File, Bitmap>();
        if (directory.exists()) {
            for (File child : directory.listFiles()) {
                for (Bitmap bm : bitmapList) {
                    Bitmap childBM = BitmapFactory.decodeFile(child.getPath());
                    if (childBM.sameAs(bm)) {
                        fileList.put(child,childBM);
                    }
                }
            }

            deleteAllFiles();

            // reconstruct the 6 File objects using the bitmaps and save into internal storage
            try {
                for (File orphanFile : fileList.keySet()) {
                    if (!orphanFile.exists()) {
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(orphanFile);
                        fileList.get(orphanFile).compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void startDownloadingHTML(String link) {
        String destFilename = "htmlFile";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File dir = cw.getDir("html", Context.MODE_PRIVATE);
        File destFile = new File(dir, destFilename + ".txt");
        List<Bitmap> selectedImage = new ArrayList<>();

        // creating a background thread
        // This background thread downloads the html given a link and
        // downloads certain images with a particular structure to their links
        dlThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HTMLDownloader htmlDL = new HTMLDownloader();
                try {
                    if (htmlDL.downloadHTML(link, destFile)) {
                        File html = dir.listFiles()[0];
                        Scanner scannedFile;
                        try {
                            scannedFile = new Scanner(html);
                            while (scannedFile.hasNext()) {
                                String word = scannedFile.next();
                                if (word.contains("data-cfsrc=" + '"' + "https://cdn.stocksnap.io/img-thumbs")) {
                                    word = word.replaceAll("data-cfsrc=", "");
                                    word = word.replace('"', ' ').trim();
                                    urls.add(word);
                                    // if urls contains less than or equals to 20 image links, continue download
                                    // else break while loop and stop download
                                    if (urls.size() <= 20) {
                                        String destFilename = word.substring(word.lastIndexOf("/") + 1);
                                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                                        File dir = cw.getDir(getString(R.string.folder), Context.MODE_PRIVATE);
                                        File destFile = new File(dir, destFilename);
                                        ImageDownloader imgDL = new ImageDownloader();
                                        // If user interrupts download, clear the existing list of urls, close the scannedFile,
                                        // remove all existing image files, and reset interrupted to false
                                        if(interrupted)
                                        {
                                            urls.clear();
                                            scannedFile.close();
                                            deleteAllFiles();
                                            interrupted = false;
                                            return;
                                        }
                                        // If download image successful, get the bitmap from the downloaded file
                                        // and get the imageView to put it in
                                        // If bitmap and imageView are both not null, put the bitmap into the imageView
                                        // Set onClickListener for each imageView
                                        // Update the progess bar along with the text whenever a new image is displayed
                                        if (imgDL.downloadImage(word, destFile)) {
                                            Bitmap bitmap = BitmapFactory.decodeFile(destFile.getAbsolutePath());
                                            ImageView imageView = findViewById(Integer.valueOf(urls.size()));
                                            if(bitmap != null && imageView !=null) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        imageView.setImageBitmap(bitmap);
                                                        // Attach bitmap to imageView using tag
                                                        imageView.setTag(R.string.none,bitmap);
                                                        mProgressBar = findViewById(Integer.valueOf(100));
                                                        mProgressBar.setProgress(dir.list().length);
                                                        tv = findViewById(Integer.valueOf(101));
                                                        tv.setGravity(Gravity.CENTER | Gravity.TOP);
                                                        tv.setText("Downloading " + dir.list().length + " of 20 images..." );

                                                        // User selects the 6 images that he wants to use for the game
                                                        imageView.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                // When the user clicks on an image, retrieve the bitmap using tag
                                                                final Bitmap bitMapFile = (Bitmap) v.getTag(R.string.none);
                                                                // If user has not selected 6 images yet, reflect user's selection
                                                                // by increasing selected image's transparency
                                                                // Clicking a selected image again is also disallowed
                                                                // Put the bitmap into a list of bitmaps called selectedImage
                                                                if (selectedImage.size() < 6) {
                                                                    v.setAlpha(0.2f);
                                                                    v.setEnabled(false);
                                                                    selectedImage.add(bitMapFile);
                                                                }
                                                                // once 6 images have been selected, go to second Activity
                                                                if (selectedImage.size() == 6) {
                                                                    // Clear all images that were not selected from the internal storage
                                                                    deleteRemainingFiles(selectedImage);
                                                                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    } else {
                                        break;
                                    }

                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        dlThread.start();
    }

}