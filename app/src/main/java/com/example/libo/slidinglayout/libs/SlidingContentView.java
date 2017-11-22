package com.example.libo.slidinglayout.libs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 9自定义内容视图 目地：拦截事件（有条件：滑动拦截） 提供接口
 */

public class SlidingContentView extends LinearLayout
{
    private ISlidingLayout slidingLayout;
    
    public void setSlidingLayout(ISlidingLayout slidingLayout)
    {
        this.slidingLayout = slidingLayout;
    }
    
    public SlidingContentView(Context context)
    {
        super(context);
    }
    
    public SlidingContentView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    // 拦截触摸事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (slidingLayout.getCurrentStaus() == SlidingItemLayout.SlidingStatus.Close)
        {
            // 不需要拦截
            return super.onInterceptTouchEvent(ev);
        }
        return true;
    }
    
    // 触摸事件传递
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (slidingLayout.getCurrentStaus() == SlidingItemLayout.SlidingStatus.Close)
        {
            return super.onTouchEvent(event);
        }
        else
        {
            if (event.getActionMasked() == MotionEvent.ACTION_UP)
            {
                slidingLayout.close();
            }
            // 注意：
            return true;
        }
    }
}
