package io.hackharvard.emotification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by mdislam on 10/24/16.
 */
public class InstructionsActivity extends AppCompatActivity {

    private ArrayList<ImageView> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        images = new ArrayList<>();

        final ImageView image1 = (ImageView) findViewById(R.id.image1);
        final ImageView image2 = (ImageView) findViewById(R.id.image2);
        final ImageView image3 = (ImageView) findViewById(R.id.image3);
        final ImageView image4 = (ImageView) findViewById(R.id.image4);

        images.add(image1);
        images.add(image2);
        images.add(image3);
        images.add(image4);


        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllImages();
                image1.setImageResource(R.drawable.instruc1);
            }
        });


        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllImages();
                image2.setImageResource(R.drawable.instruc2);
            }
        });


        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllImages();
                image3.setImageResource(R.drawable.instruc3);
            }
        });



        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllImages();
                image4.setImageResource(R.drawable.instruc4);
            }
        });

        image1.setImageResource(R.drawable.instruc1);

    }


    private void closeAllImages(){
        for(ImageView image : images){
            image.setImageResource(R.drawable.screenshotbtn);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
