package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{

    public Integer level, mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        clickListener();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.d("Main", String.valueOf(metrics.heightPixels));
        Log.d("Main", String.valueOf(metrics.widthPixels));
        showScore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("test","resume");
        showScore();
    }

    //get score from the shared preferences
    private void showScore()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        Integer highScore;
        highScore = Integer.parseInt(sharedPreferences.getString("highScore","0")) ;
        TextView score = (TextView) findViewById(R.id.txtScore);
        score.setText(String.valueOf(highScore));
    }


    private void clickListener()
    {
        ImageButton b1 = (ImageButton) findViewById(R.id.btn1);
        ImageButton b2 = (ImageButton) findViewById(R.id.btnHelp);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getLevel();
                Intent intentCanva = new Intent(MainActivity.this, Canva.class);
                intentCanva.putExtra("level", level);
                intentCanva.putExtra("mode", mode);
                startActivity(intentCanva);
            }


        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder alertText1 = new StringBuilder(50);
                alertText1.append("Scoring Criteria:\n\n");
                alertText1.append("One Player mode: +2 points per hit \nCom vs Player mode: +2 points per hit, \n\t\t+5 for when computer misses");
                //alertText1.append(Double.toString(lorentzFactor));
                alertShow(alertText1);
            }
        });
    }


    public void getLevel()
    {
        RadioGroup rg = (RadioGroup) findViewById(R.id.rg1);
        int selected = rg.getCheckedRadioButtonId();
        RadioButton selectedBtn = (RadioButton) findViewById(selected);
        String strSelected = selectedBtn.getText().toString();

        if(strSelected.equals("One Player"))
            mode = 1;
        else
            mode = 2;

        rg = (RadioGroup) findViewById(R.id.rg2);
        selected = rg.getCheckedRadioButtonId();
        selectedBtn = (RadioButton) findViewById(selected);
        strSelected = selectedBtn.getText().toString();

        if(strSelected.equals("Easy"))
            level = 1;
        else
            level = 3;
    }

    private void alertShow(StringBuilder alertText)
    {
        AlertDialog.Builder alertAnswer = new AlertDialog.Builder(MainActivity.this);

        alertAnswer.setMessage(alertText)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
        AlertDialog createAlert = alertAnswer.create();
        createAlert.show();
    }

}