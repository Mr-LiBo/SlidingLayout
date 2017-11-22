package com.example.libo.slidinglayout.libs;

/**
 *  9自定义内容视图
 *  目地：（该接口给contentView 使用 回调到SlidingItemLayout）
 *  9.1提供接口
 */

public interface ISlidingLayout
{
    /**
     *当前视图状态
     */
    public  SlidingItemLayout.SlidingStatus getCurrentStaus();

    /**
     * 关闭
     */
    public void close();

    /**
     * 打开
     */
    public void open();
}
