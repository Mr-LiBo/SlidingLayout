package com.example.libo.slidinglayout.libs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.libo.slidinglayout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiBo on 2016/11/2.
 */

public class SlideListAdapter extends BaseAdapter
{
    List<String> data = new ArrayList<>();
    
    private Context mContext;
    
    private LayoutInflater mInflater;
    
    private SlideManager slideManager;
    
    public SlideListAdapter(Context mContext)
    {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.slideManager = new SlideManager();
    }
    
    public void setData(List<String> data)
    {
        this.data = data;
    }

    public List<String> getData()
    {
        return data;
    }
    
    public SlideManager getSlideManager()
    {
        return slideManager;
    }
    
    @Override
    public int getCount()
    {
        return data.size();
    }
    
    @Override
    public Object getItem(int position)
    {
        return data.get(position);
    }
    
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder mHolder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.adapter_slidling_item, parent, false);
            mHolder = new ViewHolder();
            mHolder.mCancelCall = (Button) convertView.findViewById(R.id.bt_call);
            mHolder.mDeleteCall = (Button) convertView.findViewById(R.id.bt_delete);
            mHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(mHolder);
        }
        else
        {
            mHolder = (ViewHolder) convertView.getTag();
        }
        
        final String title = data.get(position);
        mHolder.tv_title.setText(title);
        
        mHolder.mDeleteCall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                mOnItemFunctionListener.onDeleteCallListener(position);
                
            }
        });
        mHolder.mCancelCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemFunctionListener.onCancelCallListener(position);

                slideManager.closeAllLayout();
            }
        });

        
        SlidingItemLayout view = (SlidingItemLayout) convertView;
        // 默认关闭
        view.closeSlidingLayout(false, false);
        // 给我们的滑动实图绑定回调监听（监听生命周期）
        view.setOnSlideItemListener(slideManager.getOnSlideItemListener());
        // 一旦你点击了contentView 将关闭已打开的view
        view.getContentView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                slideManager.closeAllLayout();
            }
        });
        
        return view;
    }


    public interface  onItemFunctionListener
    {
        public void onCancelCallListener(int position);
        public void onDeleteCallListener(int position);
    }

    private onItemFunctionListener mOnItemFunctionListener;

    public void setmOnItemFunctionListener(onItemFunctionListener listener)
    {
        mOnItemFunctionListener = listener;
    }

    class ViewHolder
    {
        public Button mCancelCall;
        
        public Button mDeleteCall;
        
        public TextView tv_title;
    }
}
