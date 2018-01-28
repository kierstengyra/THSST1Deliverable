package com.automatedpsychtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChecklistActivity extends AppCompatActivity {

    TextView question;
    RadioGroup btnGroup;
    RadioButton selected;
    Button btnNext;

    String name;
    ArrayList<Integer> scores;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        Intent intent = getIntent();
        this.name = intent.getStringExtra("Name");

        this.question = (TextView) findViewById(R.id.txtQuestion);
        this.btnGroup = (RadioGroup) findViewById(R.id.btnGroup);
        this.btnNext = (Button) findViewById(R.id.btnNext);

        this.scores = new ArrayList<>();
    }

    public void next(View view) {
        if(this.btnGroup.getCheckedRadioButtonId() != -1) {
            int selectedId = this.btnGroup.getCheckedRadioButtonId();
            this.selected = (RadioButton) findViewById(selectedId);
            this.scores.add(Integer.parseInt(selected.getTag().toString()));

            if(this.scores.size() < 35) {
                this.btnGroup.clearCheck();
                String questionNum = "Q"+(this.scores.size()+1);
                this.question.setText(ChecklistActivity.getQuestion(this, questionNum));
            }
            else {
                Intent intent = new Intent(this, CheckProjectiveActivity.class);
                intent.putExtra("Name", this.name);
                intent.putIntegerArrayListExtra("PartialScores", this.scores);
                startActivity(intent);
            }
        }
    }

    public static int getQuestion(Context context, String name) {
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
    }
}
