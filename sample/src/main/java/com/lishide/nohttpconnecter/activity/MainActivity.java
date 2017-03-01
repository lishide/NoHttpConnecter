package com.lishide.nohttpconnecter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.lishide.nohttpconnecter.R;

public class MainActivity extends AppCompatActivity {
    private Button mBtnGetPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnGetPost = (Button) findViewById(R.id.btn_getPost);

        mBtnGetPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GetPostActivity.class);
                startActivity(intent);
            }
        });

    }

}
