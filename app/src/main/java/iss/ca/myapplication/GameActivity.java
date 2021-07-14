package iss.ca.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    TextView tv_p1;


    ImageView iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10, iv_11, iv_12;
    ImageView[] IMGS = {iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10, iv_11, iv_12};


    //array for the images
    Integer[] cardsArray = {101, 102, 103, 104, 105, 106, 201, 202, 203, 204, 205, 206,};

    int firstCard, secondCard;
    int clickedFirst, clickedSecond;
    int cardNumber = 1;

    int playerPoints = 0;
    private Chronometer timer;

    List<Integer> matchedCards = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        timerStart();

        tv_p1 = findViewById(R.id.tv_p1);

        // Get all imageView from layout and store them into IMGS Array
        for (int i = 1; i < 13; i++) {
            int id = getResources().getIdentifier("iv_"+i,"id", getPackageName());
            IMGS[i-1] = findViewById(id);
        }

        //find all pictures
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File folder = new File(String.valueOf(cw.getDir("image9Dir", Context.MODE_PRIVATE)));
        File[] allFiles = new File[0];

        if(folder.exists()) {
            allFiles = folder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
                }
            });
        }

        // Transfer all files to bitmap and store them into bitmap Array
        ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
        for(int i = 0; i < 6 ; i++) {
            bitmapArray.add(BitmapFactory.decodeFile(allFiles[i].getAbsolutePath()));
        }


        //shuffle the images
        Collections.shuffle(Arrays.asList(cardsArray));


        // Set onClick listener to each image view then call play method inside the click listener
        for(int i = 1; i<=12; i++) {
            int id = getResources().getIdentifier("iv_"+i,"id", getPackageName());
            ImageView iv = findViewById(id);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int theCard = Integer.parseInt((String) view.getTag());
                    play(iv, theCard, bitmapArray);
                }
            });
        }
    }


    //Generate timer start method
    public void timerStart(){
        timer = findViewById(R.id.timer);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
    }

    //Generate timer stop method
    public void timerStop(){
        timer = findViewById(R.id.timer);
        timer.stop();
    }

    // Here use tag set in imageView to get image correctly
    private void play(ImageView iv, int card, ArrayList<Bitmap> bitmapArray) {
        //set the correct image to the imageview
        iv.setImageBitmap(bitmapArray.get(cardsArray[card]%100-1));

        // check which image is selected and save it to temporary variable
        if(cardNumber == 1) {
            firstCard = cardsArray[card];
            if(firstCard > 200) {
                firstCard = firstCard - 100;
            }
            cardNumber = 2;
            clickedFirst = card;
            iv.setEnabled(false);
        } else if(cardNumber == 2) {
            secondCard = cardsArray[card];
            if(secondCard> 200) {
                secondCard = secondCard - 100;
            }
            cardNumber = 1;
            clickedSecond = card;

            //disable all cards
            for(int i = 1; i<=12; i++) {
                int id = getResources().getIdentifier("iv_"+i,"id", getPackageName());
                findViewById(id).setEnabled(false);
            }

            //if match, play success sound
            if(firstCard == secondCard) {
                if (playerPoints < 6) {
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.success_bell);
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.stop();
                            mp.reset();
                            mp.release();
                        }

                    });
                    mp.start();
                }
            }


            //Using thread to delay execution so second card is not closed immediately
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(700);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            calculate(clickedFirst, clickedSecond);
                        }
                    });
                }
            }).start();

        }

    }

    // Here check for match
    private void calculate(int clickedFirst, int clickedSecond) {
        //if images are equal add points store them in matchedCards list
        if(firstCard == secondCard) {
                playerPoints++;
                tv_p1.setText(playerPoints + " out of 6 matches");

                //add matched cards(positions) to list
                matchedCards.add(clickedFirst);
                matchedCards.add(clickedSecond);

        }
        // display placeholder image
        //ie. rehide them
        else {
            for (int i = 1; i < 13; i++) {
                if(clickedFirst == i - 1 || clickedSecond == i - 1) {
                    int id = getResources().getIdentifier("iv_" + i, "id", getPackageName());
                    ImageView iv = findViewById(id);
                    iv.setImageResource(R.drawable.cross);
                }
            }
        }

        //set all cards that are not matched yet to true so they can be selected
        for(int i = 1; i<=12; i++) {
            int id = getResources().getIdentifier("iv_"+i,"id", getPackageName());
            if(!matchedCards.contains(i-1))
                findViewById(id).setEnabled(true);
        }



        //check if game is over
        checkEnd();

    }

    private void checkEnd() {
        // Game finish when player points reach 6
        if(playerPoints == 6) {
            // play finish music
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.success_jingle);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.reset();
                    mp.release();
                }

            });
            mp.start();

            // stop timer
            timerStop();

            // Display end of game pop-up
            // User able to select play another round with current images or return Main Activity
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
            alertDialogBuilder
                    .setMessage("Game Over!\nWell Done!")
                    .setCancelable(false)
                    .setPositiveButton("NEW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}


