package iss.ca.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    Button mSearchBtn;
    ProgressBar mProgressBar;
    TextView tv;
    boolean finishedDl = false;
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
                finishedDl = false;
                selectedUrl = spinner.getSelectedItem().toString();

                urls.clear();
                if(dlThread !=null){
                    if(dlThread.isAlive()) {
                        interrupted = true;
                        startDownloadingHTML(selectedUrl);
                    }
                    else
                    {
                        deleteAllFiles();
                        startDownloadingHTML(selectedUrl);
                    }
                }
                else
                {
                    deleteAllFiles();
                    startDownloadingHTML(selectedUrl);
                }
                initImages();

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

    protected void initImages() {
        LinearLayout parent = (LinearLayout) findViewById(R.id.parentLayout);
        LinearLayout layout = (LinearLayout) findViewById(R.id.pic_stabilizer);
        LinearLayout progress = (LinearLayout) findViewById(R.id.progress_stabilizer);

        // Remember to delete images from storage when delete the table
        if (findViewById(Integer.valueOf(99)) != null)
            layout.removeViewInLayout(findViewById(Integer.valueOf(99)));

        if (findViewById(Integer.valueOf(100)) != null)
            progress.removeViewInLayout(findViewById(Integer.valueOf(100)));

        if (findViewById(Integer.valueOf(101)) != null)
            progress.removeViewInLayout(findViewById(Integer.valueOf(101)));

        //create the table again
        TableLayout table = new TableLayout(getApplicationContext());
        table.setId(Integer.valueOf(99));

        table.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f));
        layout.addView(table);

        for (int i = 0; i < 5; i++) {
            TableRow row = new TableRow(getApplicationContext());
            table.addView(row);
            for (int j = 0; j < 4; j++) {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setId(i * 4 + j + 1);
                imageView.setImageDrawable(new BitmapDrawable(getResources(), scaleDown(((BitmapDrawable) getDrawable(R.drawable.image)).getBitmap(), 100, true)));
                imageView.setTag(R.string.none, null);
                imageView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                row.addView(imageView);
            }
        }

        ProgressBar progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setId(Integer.valueOf(100));
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(875, 100, 1.0f));
        progressBar.setScaleY(8.0f);
        progressBar.setPadding(200, 0, 0, 0);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(20);

        progress.addView(progressBar);

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
        File directory = getDir(getString(R.string.folder), Context.MODE_PRIVATE);

        List<File> fileList = new ArrayList<File>();
        if (directory.exists()) {
            for (File child : directory.listFiles()) {
                for (Bitmap bm : bitmapList) {
                    if (child.toString().contains(bm.toString())) {
                        fileList.add(child);

                    }
                }
            }
            deleteAllFiles();

            try {
                for (File orphanFile : fileList) {
                    if (!orphanFile.exists()) {
                        Log.d("path", orphanFile.toString());
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(orphanFile);
                        for (Bitmap bm : bitmapList) {
                            if (orphanFile.toString().contains(bm.toString())) {
                                File file = new File(directory, bm.toString() + ".jpg");
                                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            }
                        }
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
                                    if (urls.size() <= 20) {
                                        String destFilename = word.substring(word.lastIndexOf("/") + 1);
                                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                                        File dir = cw.getDir(getString(R.string.folder), Context.MODE_PRIVATE);
                                        File destFile = new File(dir, destFilename);
                                        ImageDownloader imgDL = new ImageDownloader();
                                        if(interrupted)
                                        {
                                            urls.clear();
                                            scannedFile.close();
                                            deleteAllFiles();
                                            interrupted = false;
                                            return;
                                        }
                                        if (imgDL.downloadImage(word, destFile)) {
                                            Bitmap bitmap = BitmapFactory.decodeFile(destFile.getAbsolutePath());
                                            ImageView imageView = findViewById(Integer.valueOf(urls.size()));
                                            if(bitmap != null && imageView !=null) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        imageView.setImageBitmap(bitmap);
                                                        imageView.setTag(R.string.none,bitmap);
                                                        mProgressBar = findViewById(Integer.valueOf(100));
                                                        mProgressBar.setProgress(dir.list().length);
                                                        tv = findViewById(Integer.valueOf(101));
                                                        tv.setGravity(Gravity.CENTER | Gravity.TOP);
                                                        tv.setText("Downloading " + dir.list().length + " of 20 images..." );


                                                        imageView.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                final Bitmap bitMapFile = (Bitmap) v.getTag(R.string.none);
                                                                if (selectedImage.size() < 6) {
                                                                    v.setAlpha(0.2f);
                                                                    v.setEnabled(false);
                                                                    selectedImage.add(bitMapFile);
                                                                }
                                                                if (selectedImage.size() == 6) {
                                                                    deleteRemainingFiles(selectedImage);
                                                                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
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
        finishedDl = true;
    }

}