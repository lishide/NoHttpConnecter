package com.lishide.nohttpconnecter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.adapter.MainFuncAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private RecyclerView mRvStartFunc;
    private MainFuncAdapter mMainFuncAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
    }

    private void initView() {
        mRvStartFunc = (RecyclerView) findViewById(R.id.rv_start_func);
        mRvStartFunc.setLayoutManager(new LinearLayoutManager(this));
        mRvStartFunc.setItemAnimator(new DefaultItemAnimator());

        List<String> titles = Arrays.asList(getResources().getStringArray(R.array.main_item_title));
        List<String> descriptions = Arrays.asList(getResources().getStringArray(R.array.main_item_des));
        mMainFuncAdapter = new MainFuncAdapter(titles, descriptions);
        mRvStartFunc.setAdapter(mMainFuncAdapter);
        mMainFuncAdapter.setOnItemClickListener((v, position) -> goItemPager(position));
    }

    private void goItemPager(int position) {
        Intent intent = null;
        switch (position) {
            case 0:// 各种请求方法演示(GET, POST等等)
                intent = new Intent(mContext, MethodActivity.class);
                break;
            default:
                break;
        }
        if (intent != null)
            startActivity(intent);
    }

}
