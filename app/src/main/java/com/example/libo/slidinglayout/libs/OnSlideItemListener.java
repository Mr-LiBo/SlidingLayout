package com.example.libo.slidinglayout.libs;

/**
 * Created by LiBo on 2016/11/2.
 * 10 定义回调监听
 */

public interface OnSlideItemListener
{
    /**
     * 关闭
     * 
     * @param slidingItemLayout
     */
    public void onClose(SlidingItemLayout slidingItemLayout);
    
    /**
     * 打开
     * 
     * @param slidingItemLayout
     */
    public void onOpen(SlidingItemLayout slidingItemLayout);
    
    /**
     * 开始关闭
     * 
     * @param slidingItemLayout
     */
    public void onStartClose(SlidingItemLayout slidingItemLayout);
    
    /**
     * 关闭
     * 
     * @param slidingItemLayout
     */
    public void onStartOpen(SlidingItemLayout slidingItemLayout);
    
}
