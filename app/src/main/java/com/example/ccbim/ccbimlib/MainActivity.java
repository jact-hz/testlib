package com.example.ccbim.ccbimlib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btOpenModel;
    private Button btOpenCad;
    private Button btFindState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btOpenModel = findViewById(R.id.bt_open_model);
        btOpenModel.setOnClickListener(this);
        btOpenCad = findViewById(R.id.bt_open_cad);
        btOpenCad.setOnClickListener(this);
        btFindState = findViewById(R.id.bt_find_state);
        btFindState.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_open_model:
                //CCBimSdkUtil.openModel(this,"a9dd00a0-76a6-2c91-8083-001f60951fc9");
                break;

            case R.id.bt_find_state:
//                Toast.makeText(this, CCBimSdkUtil.findConvertInfo(this, "a9dd00a0-76a6-2c91-8083-001f60951fc9"), Toast.LENGTH_LONG).show();
                break;
        }
    }
}
