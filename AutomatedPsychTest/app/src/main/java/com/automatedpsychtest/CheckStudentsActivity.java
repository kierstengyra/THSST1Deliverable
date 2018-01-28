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

/**
 * Created by gyra on 11/01/2017.
 */
public class CheckStudentsActivity extends AppCompatActivity {
    ArrayList<Integer> scores;
    String studentName;
    boolean hasFigureDrawing;

    TextView txtHasOtherStudents;
    Button btnYes;
    Button btnNo;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isrecordavailable);

        Intent intent = getIntent();
        this.studentName = intent.getStringExtra("Name");
        this.scores = intent.getIntegerArrayListExtra("PartialScores");
        this.hasFigureDrawing = intent.getBooleanExtra("FigureDrawing", false);

        this.txtHasOtherStudents = (TextView) findViewById(R.id.txtHasRecord);
        this.txtHasOtherStudents.setText(R.string.txtHasOtherStudents);

        this.btnYes = (Button) findViewById(R.id.btnYes);
        this.btnNo = (Button) findViewById(R.id.btnNo);
    }

    public void clickedYes(View view) {
        Intent intent = new Intent(this, IsRecordAvailableActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void clickedNo(View view) {
        //TODO: Send data to database

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Exit", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
