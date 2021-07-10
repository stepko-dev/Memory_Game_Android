package iss.ca.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Drawable[] images = new Drawable[] {
                getDrawable(R.drawable.apple_1_100by100), getDrawable(R.drawable.apple_42_100by100),
                getDrawable(R.drawable.apple_68_100by100), getDrawable(R.drawable.apple_78_100by100),
                getDrawable(R.drawable.apple_79_100by100), getDrawable(R.drawable.apple_87_100by100),
                getDrawable(R.drawable.apple_88_100by100), getDrawable(R.drawable.apple_92_100by100),
                getDrawable(R.drawable.banana_78_100by100), getDrawable(R.drawable.banana_80_100by100),
                getDrawable(R.drawable.banana_89_100by100), getDrawable(R.drawable.banana_93_100by100),
                getDrawable(R.drawable.mixed_22_100by100), getDrawable(R.drawable.orange_4_100by100),
                getDrawable(R.drawable.orange_26_100by100), getDrawable(R.drawable.orange_28_100by100),
                getDrawable(R.drawable.orange_82_100by100), getDrawable(R.drawable.orange_83_100by100),
                getDrawable(R.drawable.orange_95_100by100), getDrawable(R.drawable.orange_14_100by100),
        };

        LinearLayout layout = (LinearLayout) findViewById(R.id.parentLayout);

        TableLayout table = new TableLayout(this);
        table.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 2.0f));
        layout.addView(table);
        for(int i=0;i<5;i++) {
            TableRow row = new TableRow(this);
            table.addView(row);
            for(int j=0;j<4;j++) {
                ImageView imageView = new ImageView(this);
                imageView.setImageDrawable(images[i*4 + j]);
                imageView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
                row.addView(imageView);
            }
        }
        setContentView(layout);
    }
}