package iss.ca.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    TextView tv_p1;

    ImageView iv_11, iv_12, iv_13, iv_14, iv_21, iv_22, iv_23, iv_24, iv_31, iv_32, iv_33, iv_34;


    //array for the images
    Integer[] cardsArray = {101, 102, 103, 104, 105, 106, 201, 202, 203, 204, 205, 206,};

    //actual images
    int image101, image102, image103, image104, image105, image106,
            image201, image202, image203, image204, image205, image206;

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

        tv_p1 = (TextView) findViewById(R.id.tv_p1);

        iv_11 = (ImageView) findViewById(R.id.iv_11);
        iv_12 = (ImageView) findViewById(R.id.iv_12);
        iv_13 = (ImageView) findViewById(R.id.iv_13);
        iv_14 = (ImageView) findViewById(R.id.iv_14);
        iv_21 = (ImageView) findViewById(R.id.iv_21);
        iv_22 = (ImageView) findViewById(R.id.iv_22);
        iv_23 = (ImageView) findViewById(R.id.iv_23);
        iv_24 = (ImageView) findViewById(R.id.iv_24);
        iv_31 = (ImageView) findViewById(R.id.iv_31);
        iv_32 = (ImageView) findViewById(R.id.iv_32);
        iv_33 = (ImageView) findViewById(R.id.iv_33);
        iv_34 = (ImageView) findViewById(R.id.iv_34);


//        iv_11.setTag("0");
//        iv_12.setTag("1");
//        iv_13.setTag("2");
//        iv_14.setTag("3");
//        iv_21.setTag("4");
//        iv_22.setTag("5");
//        iv_23.setTag("6");
//        iv_24.setTag("7");
//        iv_31.setTag("8");
//        iv_32.setTag("9");
//        iv_33.setTag("10");
//        iv_34.setTag("11");

        //load the car images
        frontOfCardsResources();

        //shuffle the images
        Collections.shuffle(Arrays.asList(cardsArray));


        iv_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_11, theCard);
            }
        });
        iv_12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_12, theCard);

            }
        });
        iv_13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_13, theCard);

            }
        });
        iv_14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_14, theCard);

            }
        });
        iv_21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_21, theCard);

            }
        });
        iv_22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_22, theCard);

            }
        });
        iv_23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_23, theCard);

            }
        });
        iv_24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_24, theCard);

            }
        });
        iv_31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_31, theCard);

            }
        });
        iv_32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_32, theCard);

            }
        });
        iv_33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_33, theCard);

            }
        });
        iv_34.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(iv_34, theCard);

            }
        });

    }

    private void frontOfCardsResources() {
        image101 = R.drawable.ic_image101;
        image102 = R.drawable.ic_image102;
        image103 = R.drawable.ic_image103;
        image104 = R.drawable.ic_image104;
        image105 = R.drawable.ic_image105;
        image106 = R.drawable.ic_image106;
        image201 = R.drawable.ic_image101;
        image202 = R.drawable.ic_image102;
        image203 = R.drawable.ic_image103;
        image204 = R.drawable.ic_image104;
        image205 = R.drawable.ic_image105;
        image206 = R.drawable.ic_image106;

    }

    public void timerStart(){
        timer = findViewById(R.id.timer);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
    }

    public void timerStop(){
        timer = findViewById(R.id.timer);
        timer.stop();
    }

    // Here use tag set in imageView to get image correctly
    private void doStuff(ImageView iv,int card) {
        //set the correct image to the imageview
        if(cardsArray[card] == 101) {
            iv.setImageResource(image101);
        } else if(cardsArray[card] == 102) {
            iv.setImageResource(image102);
        } else if(cardsArray[card] == 103) {
            iv.setImageResource(image103);
        } else if(cardsArray[card] == 104) {
            iv.setImageResource(image104);
        } else if(cardsArray[card] == 105) {
            iv.setImageResource(image105);
        } else if(cardsArray[card] == 106) {
            iv.setImageResource(image106);
        } else if(cardsArray[card] == 201) {
            iv.setImageResource(image201);
        } else if(cardsArray[card] == 202) {
            iv.setImageResource(image202);
        } else if(cardsArray[card] == 203) {
            iv.setImageResource(image203);
        } else if(cardsArray[card] == 204) {
            iv.setImageResource(image204);
        } else if(cardsArray[card] == 205) {
            iv.setImageResource(image205);
        } else if(cardsArray[card] == 206) {
            iv.setImageResource(image206);
        }

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


            iv_11.setEnabled(false);
            iv_12.setEnabled(false);
            iv_13.setEnabled(false);
            iv_14.setEnabled(false);
            iv_21.setEnabled(false);
            iv_22.setEnabled(false);
            iv_23.setEnabled(false);
            iv_24.setEnabled(false);
            iv_31.setEnabled(false);
            iv_32.setEnabled(false);
            iv_33.setEnabled(false);
            iv_34.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //check if the selected images are equal
                    calculate(clickedFirst, clickedSecond);
                }
            }, 800);

        }

    }

    private void calculate(int clickedFirst, int clickedSecond) {
        //if images are equal remove tgem and add points



        if(firstCard == secondCard) {
                playerPoints++;
                tv_p1.setText(playerPoints + " out of 6 matches");

                //add matched cards(positions) to list
                matchedCards.add(clickedFirst);
                matchedCards.add(clickedSecond);

        }
        else {
            //reset first card that was clicked
            if(clickedFirst == 0)
            iv_11.setImageResource(R.drawable.cross);
            else if(clickedFirst == 1)
            iv_12.setImageResource(R.drawable.cross);
            else if(clickedFirst == 2)
            iv_13.setImageResource(R.drawable.cross);
            else if(clickedFirst == 3)
            iv_14.setImageResource(R.drawable.cross);
            else if(clickedFirst == 4)
            iv_21.setImageResource(R.drawable.cross);
            else if(clickedFirst == 5)
            iv_22.setImageResource(R.drawable.cross);
            else if(clickedFirst == 6)
            iv_23.setImageResource(R.drawable.cross);
            else if(clickedFirst == 7)
            iv_24.setImageResource(R.drawable.cross);
            else if(clickedFirst == 8)
            iv_31.setImageResource(R.drawable.cross);
            else if(clickedFirst == 9)
            iv_32.setImageResource(R.drawable.cross);
            else if(clickedFirst == 10)
            iv_33.setImageResource(R.drawable.cross);
            else if(clickedFirst == 11)
            iv_34.setImageResource(R.drawable.cross);

            //reset second card that was clicked
            if(clickedSecond == 0)
                iv_11.setImageResource(R.drawable.cross);
            else if(clickedSecond == 1)
                iv_12.setImageResource(R.drawable.cross);
            else if(clickedSecond == 2)
                iv_13.setImageResource(R.drawable.cross);
            else if(clickedSecond == 3)
                iv_14.setImageResource(R.drawable.cross);
            else if(clickedSecond == 4)
                iv_21.setImageResource(R.drawable.cross);
            else if(clickedSecond == 5)
                iv_22.setImageResource(R.drawable.cross);
            else if(clickedSecond == 6)
                iv_23.setImageResource(R.drawable.cross);
            else if(clickedSecond == 7)
                iv_24.setImageResource(R.drawable.cross);
            else if(clickedSecond == 8)
                iv_31.setImageResource(R.drawable.cross);
            else if(clickedSecond == 9)
                iv_32.setImageResource(R.drawable.cross);
            else if(clickedSecond == 10)
                iv_33.setImageResource(R.drawable.cross);
            else if(clickedSecond == 11)
                iv_34.setImageResource(R.drawable.cross);

        }


          //unlock first card that was selected
        if(clickedFirst == 0)
            iv_11.setEnabled(true);
        else if(clickedFirst == 1)
            iv_12.setEnabled(true);
        else if(clickedFirst == 2)
            iv_13.setEnabled(true);
        else if(clickedFirst == 3)
            iv_14.setEnabled(true);
        else if(clickedFirst == 4)
            iv_21.setEnabled(true);
        else if(clickedFirst == 5)
            iv_22.setEnabled(true);
        else if(clickedFirst == 6)
            iv_23.setEnabled(true);
        else if(clickedFirst == 7)
            iv_24.setEnabled(true);
        else if(clickedFirst == 8)
            iv_31.setEnabled(true);
        else if(clickedFirst == 9)
            iv_32.setEnabled(true);
        else if(clickedFirst == 10)
            iv_33.setEnabled(true);
        else if(clickedFirst == 11)
            iv_34.setEnabled(true);



        iv_11.setEnabled(true);
        iv_12.setEnabled(true);
        iv_13.setEnabled(true);
        iv_14.setEnabled(true);
        iv_21.setEnabled(true);
        iv_22.setEnabled(true);
        iv_23.setEnabled(true);
        iv_24.setEnabled(true);
        iv_31.setEnabled(true);
        iv_32.setEnabled(true);
        iv_33.setEnabled(true);
        iv_34.setEnabled(true);

        //Store all matched cards

        //Reset all not matched cards to true


        for(Integer i : matchedCards) {
            if(i == 0)
                iv_11.setEnabled(false);
            else if(i == 1)
                iv_12.setEnabled(false);
            else if(i == 2)
                iv_13.setEnabled(false);
            else if(i == 3)
                iv_14.setEnabled(false);
            else if(i == 4)
                iv_21.setEnabled(false);
            else if(i == 5)
                iv_22.setEnabled(false);
            else if(i == 6)
                iv_23.setEnabled(false);
            else if(i == 7)
                iv_24.setEnabled(false);
            else if(i == 8)
                iv_31.setEnabled(false);
            else if(i == 9)
                iv_32.setEnabled(false);
            else if(i == 10)
                iv_33.setEnabled(false);
            else if(i == 11)
                iv_34.setEnabled(false);
        }
        //check if game is over
        checkEnd();

    }

    private void checkEnd() {
        if(playerPoints == 6) {
            timerStop();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
            alertDialogBuilder
                    .setMessage("Game Over!\nP1: " + playerPoints)
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