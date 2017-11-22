package com.example.libo.slidinglayout.libs;

import java.util.HashSet;

/**
 * Created by LiBo on 2016/11/2.
 */
public class SlideManager
{
    private HashSet<SlidingItemLayout> mUnClosedLayouts = new HashSet<>();

    private OnSlideItemListener onSlideItemListener = new OnSlideItemListener() {
        @Override
        public void onClose(SlidingItemLayout slidingItemLayout) {
           mUnClosedLayouts.remove(slidingItemLayout);
        }

        @Override
        public void onOpen(SlidingItemLayout slidingItemLayout) {
            mUnClosedLayouts.add(slidingItemLayout);
        }

        @Override
        public void onStartClose(SlidingItemLayout slidingItemLayout) {

        }

        @Override
        public void onStartOpen(SlidingItemLayout slidingItemLayout) {
            closeAllLayout();
            mUnClosedLayouts.add(slidingItemLayout);
        }
    };

    public OnSlideItemListener getOnSlideItemListener()
    {
        return  onSlideItemListener;
    }
    public int getUnCloseCount()
    {
        return mUnClosedLayouts.size();
    }

    public void closeAllLayout()
    {
        if (mUnClosedLayouts.size() == 0)
        {
            return;
        }
        for (SlidingItemLayout layout : mUnClosedLayouts)
        {
            layout.closeSlidingLayout(true,false);
        }
        mUnClosedLayouts.clear();
    }
}
