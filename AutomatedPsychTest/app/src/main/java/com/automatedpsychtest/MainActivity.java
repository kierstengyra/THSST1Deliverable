package com.automatedpsychtest;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    EditText enterPwd;
    Button submitPwd;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getBooleanExtra("Exit", false)) {
            this.finish();
        }
        else {
            setContentView(R.layout.activity_main);

            this.enterPwd = (EditText) findViewById(R.id.enterPwd);
            this.submitPwd = (Button) findViewById(R.id.submitPwd);
        }
    }

    public void askRecord(View view) {
        //TODO: Retrieve password from DB

        if(!this.enterPwd.getText().toString().equals("")) {
            Intent intent = new Intent(this, IsRecordAvailableActivity.class);
            startActivity(intent);
        }
    }
}
