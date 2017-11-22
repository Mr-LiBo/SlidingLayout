package com.example.libo.slidinglayout;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.libo.slidinglayout.libs.SlideListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{
    private SlideListAdapter adapter;
    
    List<String> data = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        for (int i = 0; i < 20; i++)
        {
            data.add("我是好人" + i);
        }
        initMainContent();
        
    }
    
    private void initMainContent()
    {
        ListView lv_sliding = (ListView) findViewById(R.id.listview);
        adapter = new SlideListAdapter(MainActivity.this);
        adapter.setData(data);
        lv_sliding.setAdapter(adapter);
        lv_sliding.setOnScrollListener(new OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                // 正在滑动，立马之前的已打开的视图关闭
                if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    adapter.getSlideManager().closeAllLayout();
                }
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount)
            {
                
            }
        });

        adapter.setmOnItemFunctionListener(new SlideListAdapter.onItemFunctionListener() {
            @Override
            public void onCancelCallListener(int position) {
                String title = (String) adapter.getItem(position);
                Toast.makeText(MainActivity.this, title, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onDeleteCallListener(int position) {
                String title = (String) adapter.getItem(position);
                Toast.makeText(MainActivity.this, title, Toast.LENGTH_LONG).show();
                adapter.getData().remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        lv_sliding.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

    }
    
}
