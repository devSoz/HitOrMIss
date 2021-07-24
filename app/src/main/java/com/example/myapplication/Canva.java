package com.example.myapplication;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Canva extends AppCompatActivity
{
    private static  final int MENU_NEW = 1;
    private static  final int MENU_SETTING = 2;
    private static  final int MENU_QUIT = 3;
    private customView myCanvas;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();

        // Load the resolution into a Point object
        Point size= new Point();


        display.getSize(size); //get size of the window

        myCanvas= new customView(this);

       // myCanvas = new customView(this, size.x, size.y);
        //setContentView(R.layout.canva);
        setContentView(myCanvas);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //myCanvas.onStop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(this,MainActivity.class);// New activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_NEW, 0, R.string.menu_new_game);
        menu.add(0, MENU_SETTING, 0, R.string.menu_setting);
        menu.add(0, MENU_QUIT, 0, R.string.menu_quit);

        return true;
    }

    //Menu settings
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_QUIT:
                this.onStop();
                return true;
            case MENU_SETTING:
                Toast.makeText(Canva.this, "Next version is coming soon..", Toast.LENGTH_SHORT).show();
                return true;
            case MENU_NEW:
                Toast.makeText(Canva.this, "Coming soon", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }*/


}
