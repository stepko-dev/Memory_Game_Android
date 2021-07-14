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

    // imageViews and array for iterating through them
    ImageView iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10, iv_11, iv_12;
    ImageView[] imageViews = {iv_1, iv_2, iv_3, iv_4, iv_5, iv_6, iv_7, iv_8, iv_9, iv_10, iv_11, iv_12};

    // array for the image references
    Integer[] cardsArray = {101, 102, 103, 104, 105, 106, 201, 202, 203, 204, 205, 206,};

    int firstCard, secondCard;
    int clickedFirst, clickedSecond;
    int cardNumber = 1;

    int playerPoints = 0;
    private Chronometer timer;

    // List to keep track of which cards have been matched (to exclude from being re-enabled/re-hidden)
    List<Integer> matchedCards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout per activity_game.xml
        setContentView(R.layout.activity_game);

        // Start timer and display player points
        timerStart();
        tv_p1 = findViewById(R.id.tv_p1);

        // Get all imageViews from images.xml and store them into imageViews array
        // At this stage they are all using the placeholder image
        for (int i = 1; i < 13; i++) {
            int id = getResources().getIdentifier("iv_"+i,"id", getPackageName());
            imageViews[i-1] = findViewById(id);
        }

        // Find app_images folder and instantiate file and bitmap arrays
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File folder = new File(String.valueOf(cw.getDir("image9Dir", Context.MODE_PRIVATE)));
        File[] allFiles = new File[0];
        ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

        // Extract all image files into allFiles array
        if (folder.exists()) {
            allFiles = folder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
                }
            });

            if (allFiles!=null) {

                // Decode each file in allFiles and add the resulting Bitmap to bitmapArray
                for(int i = 0; i < 6 ; i++) {
                    bitmapArray.add(BitmapFactory.decodeFile(allFiles[i].getAbsolutePath()));
                }
            }
        }

        // Shuffle the order of image references
        Collections.shuffle(Arrays.asList(cardsArray));

        // Call play() every time a card/imageView is clicked on
        for (int i = 1; i<=12; i++) {
            int id = getResources().getIdentifier("iv_"+i,"id", getPackageName());
            ImageView iv = findViewById(id);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // get index reference for cardsArray from imageView tags in images.xml
                    int cardIdx = Integer.parseInt((String) view.getTag());
                    play(iv, cardIdx, bitmapArray);
                }
            });
        }
    }


    // Generate timer start method
    public void timerStart(){
        timer = findViewById(R.id.timer);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
    }

    // Generate timer stop method
    public void timerStop(){
        timer = findViewById(R.id.timer);
        timer.stop();
    }

    private void play(ImageView iv, int cardIdx, ArrayList<Bitmap> bitmapArray) {
        // Assign and display the clicked card image
        iv.setImageBitmap(bitmapArray.get(cardsArray[cardIdx]%100-1));

        // Check whether the selected card is the first or second of the pair
        if (cardNumber == 1) {
            // If first, set cardNumber to 2 for next click
            cardNumber = 2;
            // Store the image reference for match checking
            firstCard = cardsArray[cardIdx];
            // Store the clicked card tag for passing to calculate() method
            clickedFirst = cardIdx;
            // Disable the card from being clickable
            iv.setEnabled(false);
        }
        else if (cardNumber == 2) {
            // If second, set cardNumber to 1 for next click
            cardNumber = 1;
            // Store the image reference for match checking
            secondCard = cardsArray[cardIdx];
            // Store the clicked card tag for passing to calculate() method
            clickedSecond = cardIdx;

            // Disable all cards from being clickable
            // This is to prevent fast players from clicking more than two cards during the calculation delay
            for (int i = 1; i<=12; i++) {
                int id = getResources().getIdentifier("iv_"+i,"id", getPackageName());
                findViewById(id).setEnabled(false);
            }

            // Play success sound on match success, assuming it is not the match that ends the game
            if (firstCard%100 == secondCard%100) {
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


            // Using thread to delay second card from disappearing immediately
            // So player has time to see and remember the second card
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

    // This method updates player points and re-hides unmatched cards
    private void calculate(int clickedFirst, int clickedSecond) {
        // Match success
        if (firstCard%100 == secondCard%100) {
                // Add points and update the display
                playerPoints++;
                tv_p1.setText(playerPoints + " out of 6 matches");

                // Add card tags to matchedCards list to be excluded from below
                matchedCards.add(clickedFirst);
                matchedCards.add(clickedSecond);

        }
        // Match failure
        // Hide the selected cards and display the placeholder
        else {
            int id = getResources().getIdentifier("iv_" + (clickedFirst+1), "id", getPackageName());
            ImageView iv1 = findViewById(id);
            iv1.setImageResource(R.drawable.cross);
            id = getResources().getIdentifier("iv_" + (clickedSecond+1), "id", getPackageName());
            ImageView iv2 = findViewById(id);
            iv2.setImageResource(R.drawable.cross);
//            for (int i = 1; i < 13; i++) {
//                if(clickedFirst == i - 1 || clickedSecond == i - 1) {
//                    int id = getResources().getIdentifier("iv_" + i, "id", getPackageName());
//                    ImageView iv = findViewById(id);
//                    iv.setImageResource(R.drawable.cross);
//                }
//            }
        }

        // Allow all unmatched cards to be re-selectable
        for (int i = 1; i<=12; i++) {
            int id = getResources().getIdentifier("iv_"+i,"id", getPackageName());
            if(!matchedCards.contains(i-1))
                findViewById(id).setEnabled(true);
        }

        // Check for win condition
        checkEnd();
    }

    private void checkEnd() {
        // Win condition (all 6 pairs matched)
        if (playerPoints == 6) {
            // Play success jingle on game completion
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

            // Stop timer
            timerStop();

            // Display end of game pop-up
            // Player can:
            //  start another round using the same 6 images reshuffled
            //  return to the main activity screen
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
            alertDialogBuilder
                    .setMessage("Game Over!\nWell Done!")
                    .setCancelable(false)
                    .setPositiveButton("Rematch", new DialogInterface.OnClickListener() {
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


