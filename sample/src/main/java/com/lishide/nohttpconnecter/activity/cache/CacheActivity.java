package com.lishide.nohttpconnecter.activity.cache;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lishide.nohttpconnecter.R;
import com.lishide.nohttpconnecter.activity.BaseActivity;
import com.lishide.nohttpconnecter.adapter.RvMultiAdapter;
import com.lishide.nohttpconnecter.entity.ListItemInfo;
import com.lishide.nohttpconnecter.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存策略
 */
public class CacheActivity extends BaseActivity {
    private RecyclerView mRvCache;

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_cache);
    }

    @Override
    protected void initView() {
        List<ListItemInfo> listItems = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.activity_cache_entrance);
        String[] titlesDes = getResources().getStringArray(R.array.activity_cache_entrance_des);
        for (int i = 0; i < titles.length; i++) {
            listItems.add(new ListItemInfo(titles[i], titlesDes[i]));
        }
        mRvCache = (RecyclerView) findViewById(R.id.rv_cache_activity);
        mRvCache.setLayoutManager(new LinearLayoutManager(this));
        mRvCache.setItemAnimator(new DefaultItemAnimator());
        RvMultiAdapter listAdapter = new RvMultiAdapter(listItems, mItemClickListener);
        mRvCache.setAdapter(listAdapter);
    }

    @Override
    protected void initLogic() {
        mToolbar.setTitle(getString(R.string.title_cache_activity));
    }

    /**
     * list item 单击
     */
    private OnItemClickListener mItemClickListener = (v, position) -> {
        Intent intent = null;
        switch (position) {
            case 0:// Http标准协议的缓存。
                intent = new Intent(this, CacheHttpActivity.class);
                break;
            case 1:// 请求网络失败后返回上次的缓存。
                intent = new Intent(this, CacheRequestFailedReadCacheActivity.class);
                break;
            case 2:// 没有缓存才去请求网络。
                intent = new Intent(this, CacheNoneCacheRequestNetWorkActivity.class);
                break;
            case 3:// 仅仅请求缓存。
                intent = new Intent(this, CacheOnlyReadCacheActivity.class);
                break;
            case 4:// 仅仅请求网络。
                intent = new Intent(this, CacheOnlyRequestNetworkActivity.class);
                break;
            default:
                break;
        }
        if (intent != null)
            startActivity(intent);
    };
}
