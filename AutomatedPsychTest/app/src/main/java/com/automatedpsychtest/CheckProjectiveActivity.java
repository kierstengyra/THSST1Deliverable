package com.automatedpsychtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckProjectiveActivity extends AppCompatActivity {
    ArrayList<Integer> scores;
    String studentName;

    TextView txtHasProjective;
    Button btnYes;
    Button btnNo;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isrecordavailable);

        Intent intent = getIntent();
        this.scores = intent.getIntegerArrayListExtra("PartialScores");
        this.studentName = intent.getStringExtra("Name");

        this.txtHasProjective = (TextView) findViewById(R.id.txtHasRecord);
        this.txtHasProjective.setText(R.string.txtHasDrawing);

        this.btnYes = (Button) findViewById(R.id.btnYes);
        this.btnNo = (Button) findViewById(R.id.btnNo);
    }

    public void clickedYes(View view) {
        Intent intent = new Intent(this, RetakePhoto.class);
        intent.putExtra("Name", this.studentName);
        intent.putIntegerArrayListExtra("PartialScores", this.scores);
        startActivity(intent);
    }

    public void clickedNo(View view) {
        Intent intent = new Intent(this, CheckStudentsActivity.class);
        intent.putExtra("Name", this.studentName);
        intent.putIntegerArrayListExtra("PartialScores", this.scores);
        intent.putExtra("FigureDrawing", false);
        startActivity(intent);
    }
}
