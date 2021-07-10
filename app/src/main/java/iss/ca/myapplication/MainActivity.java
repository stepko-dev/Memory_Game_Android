package iss.ca.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
 Button mSearchBtn;
 List<String> listImages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBtn = (Button) findViewById(R.id.searchBtn);


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
                    theurlgrabber grab = new theurlgrabber(url);
                    grab.execute();


                    LinearLayout layout = (LinearLayout) findViewById(R.id.parentLayout);

                    TableLayout table = new TableLayout(getApplicationContext());
                    table.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 2.0f));
                    layout.addView(table);

                        for(int i=0;i<5;i++) {
                            TableRow row = new TableRow(getApplicationContext());
                            table.addView(row);
                            for(int j=0;j<4;j++) {
                                ImageView imageView = new ImageView(getApplicationContext());

                                try {
                                    imageView.setImageBitmap(BitmapFactory.decodeStream(new URL(listImages.get(i*4 + j)).openConnection().getInputStream()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //imageView.setImageDrawable(new BitmapDrawable(getResources(), scaleDown(((BitmapDrawable)images[i*4 + j]).getBitmap(), 100, true)));
                                imageView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
                                row.addView(imageView);
                            }
                        }


                    setContentView(layout);
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

    private class theurlgrabber  extends AsyncTask<Void, Void, Void> {
       // String url = "https://stocksnap.io/search/business";
        String url;
        Random random = new Random();
        Integer pic = random.nextInt(20);

        public theurlgrabber(String url) {
        this.url = url;
        }

        @Override
        protected void onPreExecute() {super.onPreExecute();}

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                org.jsoup.nodes.Document document = Jsoup.connect(url).get();
                org.jsoup.select.Elements links = document.getElementsByClass("photo-grid-preview");
                int count = 0;
                for(org.jsoup.nodes.Element link : links) {
                    count++;
                    if(count<21){
                        String linkHref = link.getElementsByTag("img").attr("src");
                        listImages.add(linkHref);

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

            return null;
        }
    }


//    Document document = Jsoup.parse(html);
//
//    Elements images = document.select("img");
//for (Element image : images) {
//        String imageUrl = image.attr("data-original");
//        System.out.println(imageUrl);
//    }
}