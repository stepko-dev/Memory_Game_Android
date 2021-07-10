package iss.ca.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                getDrawable(R.drawable.apple_5_resized), getDrawable(R.drawable.apple_9_resized),
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
                imageView.setImageDrawable(new BitmapDrawable(getResources(), scaleDown(((BitmapDrawable)images[i*4 + j]).getBitmap(), 100, true)));
                imageView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,1.0f));
                row.addView(imageView);
            }
        }
        setContentView(layout);
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
}