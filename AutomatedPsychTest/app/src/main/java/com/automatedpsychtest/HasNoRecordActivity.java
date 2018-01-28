package com.automatedpsychtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HasNoRecordActivity extends AppCompatActivity {

    ImageButton goBack;

    EditText inputLastName;
    EditText inputFirstName;
    EditText inputMiddleName;

    EditText inputAge;
    EditText inputGrade;
    EditText inputSection;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasnorecord);

        this.goBack = (ImageButton) findViewById(R.id.goBack);

        this.inputLastName = (EditText) findViewById(R.id.inputLastName);
        this.inputFirstName = (EditText) findViewById(R.id.inputFirstName);
        this.inputMiddleName = (EditText) findViewById(R.id.inputMiddleName);

        this.inputAge = (EditText) findViewById(R.id.inputAge);
        this.inputGrade = (EditText) findViewById(R.id.inputGrade);
        this.inputSection = (EditText) findViewById(R.id.inputSection);
    }

    public void prev(View view) {
        this.finish();
    }

    public void takePSC(View view) {
        //TODO: Add student profile to DB
        //TODO: Validate input values

        if(!this.inputLastName.getText().toString().equals("") &&
                !this.inputFirstName.getText().toString().equals("") &&
                !this.inputMiddleName.getText().toString().equals("") &&
                !this.inputAge.getText().toString().equals("") &&
                !this.inputGrade.getText().toString().equals("") &&
                !this.inputSection.getText().toString().equals("")) {

            Intent intent = new Intent(this, ConfirmStudentActivity.class);
            intent.putExtra("Name", this.inputFirstName.getText()+" "+this.inputLastName.getText());
            startActivity(intent);
        }
    }
}
