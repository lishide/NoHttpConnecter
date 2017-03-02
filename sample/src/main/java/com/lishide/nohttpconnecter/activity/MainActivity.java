package com.lishide.nohttpconnecter.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.adapter.RvMultiAdapter;
import com.lishide.nohttpconnecter.entity.ListItemInfo;
import com.lishide.nohttpconnecter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private RecyclerView mRvStartFunc;

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

        List<ListItemInfo> listItems = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.main_item_title);
        String[] titlesDes = getResources().getStringArray(R.array.main_item_des);
        for (int i = 0; i < titles.length; i++) {
            listItems.add(new ListItemInfo(titles[i], titlesDes[i]));
        }
        RvMultiAdapter mRvMultiAdapter = new RvMultiAdapter(listItems, mItemClickListener);
        mRvStartFunc.setAdapter(mRvMultiAdapter);
    }

    /**
     * list item 单击
     */
    private OnItemClickListener mItemClickListener = (v, position) -> {
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
    };

}
