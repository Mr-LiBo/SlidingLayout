package com.example.libo.slidinglayout.libs;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 1.自定义组件
 */
public class SlidingItemLayout extends FrameLayout implements ISlidingLayout
{
    private int contentViewWidth;

    // 2.定义滑动视图摆放方向（采用枚举定义）
    public enum SlidingType
    {
        Left, Right;
    }

    // 3.定义滑动视图状态(采用枚举)
    public enum SlidingStatus
    {
        Close, Open, Sliding;
    }

    // 内容视图
    private View contentView;

    // 功能视图
    private View functionView;

    private int horizontalDX;

    // 6.1.1 计算布局摆放位置(矩形：left top right buttom) （功能 在左边）
    private SlidingType slidingType = SlidingType.Right;

    // 6.1.1 计算布局摆放位置(矩形：left top right buttom)
    private SlidingStatus slidingStaus = SlidingStatus.Close;

    // 8.1设置手势方向（水平方向 ：左 右）
    private GestureDetectorCompat detectorCompat;

    // 8.2添加手势拖拽与视图之间回调接口
    private ViewDragHelper viewDrageHelper;

    // 10 解决listView 显示 问题
    private OnSlideItemListener onSlideItemListener;

    public OnSlideItemListener getOnSlideItemListener()
    {
        return onSlideItemListener;
    }

    public void setOnSlideItemListener(OnSlideItemListener onSlideItemListener)
    {
        this.onSlideItemListener = onSlideItemListener;
    }

    public View getContentView()
    {
        return contentView;
    }

    public SlidingItemLayout(Context context)
    {
        super(context);

    }

    public SlidingItemLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    // 布局加载器
    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        initView();

        initGesture();
    }

    // 4.初始化当前SlidingItemLayout条目布局
    private void initView()
    {
        if (getChildCount() != 2)
        {
            throw new IllegalArgumentException("你的子视图只允许有两个");
        }
        contentView = getChildAt(0);
        functionView = getChildAt(1);
        initContentView();
    }

    // 5.滑动视图测量
    // 目的：计算视图 - 滑动偏移量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 默认:functionView 有多宽 ，偏移量有多大
        //
        horizontalDX = functionView.getMeasuredWidth();
        contentViewWidth = contentView.getMeasuredWidth();
    }

    // 6.滑动视图摆放
    // 注意 ：滑动视处于关闭状态
    // 6.1 摆放内容视图
    // 6.2 摆放功能视图
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        layoutView(false);
    }

    /**
     * @param isOpen true 打开 false 关闭 默认
     */
    private void layoutView(boolean isOpen)
    {
        // 6.1 摆放内容视图
        // 6.1.1 计算布局摆放位置
        Rect contentRect = layoutContentView(isOpen);
        // 6.1.2摆放视图
        contentView.layout(contentRect.left, contentRect.top, contentRect.right, contentRect.bottom);

        // 6.2 摆放功能视图
        // 6.2.1 计算功能摆放位置
        Rect functionRect = layoutFunctionView(contentRect, isOpen);
        // 6.2.2 摆放视图
        functionView.layout(functionRect.left, functionRect.top, functionRect.right, functionRect.bottom);
    }

    /**
     * 6.1.1 计算布局摆放位置(矩形：left top right buttom)
     *
     * @param isOpen
     * @return
     */
    private Rect layoutContentView(boolean isOpen)
    {
        int left = 0;
        if (isOpen)//功能视图打开 可见 计算内容视图的左边距
        {
            if (slidingType == SlidingType.Left)//功能视图在左边 从不可见  到可见 功能视图向右滑动
            {
                // 功能视图摆放方向 左边
                left = horizontalDX;
            }
            else if (slidingType == SlidingType.Right)
            {
                // 功能视图摆放方向 右边
                left = -horizontalDX;
            }
        }

        // 首先摆放默认情况 --- false 状态
        return new Rect(left, 0, left + getMeasuredWidth(), getMeasuredHeight());
    }

    /**
     * 6.2 摆放功能视图 6.2.1 计算功能摆放位置
     *
     * @param rect
     * @param isOpen 代表功能视图是否打开
     * @return
     */
    private Rect layoutFunctionView(Rect rect, boolean isOpen)
    {
        int left = 0;
        // 这个判断目地：关闭状态
        if (isOpen)
        {
            // 根据类型摆放
            if (slidingType == SlidingType.Right)
            {
                // 功能视图摆放在 右边
                left = getMeasuredWidth() - horizontalDX;
            }
            else if (slidingType == SlidingType.Left)
            {
                // 功能视图摆放在 左边
                left = 0;
            }
        }
        else
        {
            // 根据类型摆放
            if (slidingType == SlidingType.Right)
            {
                // 功能视图摆放在 右边
                left = rect.right;
            }
            else if (slidingType == SlidingType.Left)
            {
                // 功能视图摆放在 左边
                left = -horizontalDX;
            }
        }

        return new Rect(left, 0, left + horizontalDX, functionView.getMeasuredHeight());
    }

    // 8 手势处理
    private void initGesture()
    {
        // 8.1设置手势方向（水平方向 ：左 右）
        detectorCompat = new GestureDetectorCompat(getContext(), onGestureListener);

        // 8.2添加手势拖拽与视图之间回调接口
        // 通过系统源码分析得知 系统默认最小值 ： 8
        // private static final int TOUCH_SLOP =8
        viewDrageHelper = ViewDragHelper.create(this, callback);
    }

    // 注意 将来写接口的时候，记得要给一个默认适配接口类
    private OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        /**
         * @param e1
         * @param e2
         * @param distanceX :x方向偏移理
         * @param distanceY :y方向偏移量
         * @return //处理方向 true 水平滑动 false 垂直滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            return Math.abs(distanceX) >= Math.abs(distanceY);
        }
    };

    // 8.2添加手势拖拽与视图之间回调接口
    // 该类目地：为了确定手势滑动方向
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback()
    {
        // 8.2.1重写tryCaptureView
        // 目地：绑定拖拽视图（一个内容视图 一个功能视图）

        /**
         * @param view :当前拖拽的View
         * @param pointerId: 扩展知识 （当前单点触控手指的ID）
         * @return
         */
        @Override
        public boolean tryCaptureView(View view, int pointerId)
        {

            return view == contentView || view == functionView;
        }

        // 8.2.2 重写 getViewHorizontalDragRange
        // 目地：设置滑动偏移量（不可无限滑
        // ）
        @Override
        public int getViewHorizontalDragRange(View child)
        {
            return horizontalDX;
        }

        // 8.2.3 重写 clampViewPositionHorizontal
        // 目地： 控制滚动的范围

        /**
         *目地 控制滚动的范围
         * @param child 拖拽的视图
         * @param left 距离父控件容器左边的距离
         * @param dx 当前手提视图偏移量
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx)
        {
            int newLeft = left;
            // 从效果来看：contentView 和 functionView 都 可以滑动
            // 所以要分开处理各自滑动范围
            // 8.2.3.1控制contentView滑动视图范围
            if (child == contentView)  //计算 cotentView 的左边 滑动
            {
                switch (slidingType)
                {
                    case Left:// 左边：代表funciotn 摆放在左边
                        // contentView 滑动的范围（0 - horizontalDX）
                        if (newLeft < 0)
                        {
                            newLeft = 0;
                        }
                        else if (newLeft > horizontalDX)
                        {
                            newLeft = horizontalDX;
                        }
                        break;
                    case Right:// 右边 代表functionView 摆放在右边
                        /// contentView 滑动范围 （-horizontalDX - 0）
                        if (newLeft < -horizontalDX)
                        {
                            newLeft = -horizontalDX;
                        }
                        else if (newLeft > 0)
                        {
                            newLeft = 0;
                        }
                        break;
                }
            }
            else if (child == functionView)
            {
                switch (slidingType)
                {
                    case Left:// 左边：代表funciotn 摆放在左边
                        // contentView 滑动的范围（- horizontalDX - 0）
                        if (newLeft < -horizontalDX)
                        {
                            newLeft = -horizontalDX;
                        }
                        else if (newLeft > 0)
                        {
                            newLeft = 0;
                        }
                        break;
                    case Right:// 右边 代表functionView 摆放在右边
                        /// contentView 滑动范围 （ 屏幕宽度 -horizontalDX - 屏幕宽度）
                        if (newLeft < contentViewWidth - horizontalDX)
                        {
                            newLeft = contentViewWidth - horizontalDX;
                        }
                        else if (newLeft > contentViewWidth)
                        {
                            newLeft = contentViewWidth;
                        }
                        break;
                }
            }
            return newLeft;
        }

        /**
         * 8.2.4 重写 onViewPositionChanged 目地拖拽视图的时候，希望能够同时干一些其他事情 （拖拽contentView 希望functionView 也要跟着动）
         *
         * @param changedView ：当前拖拽视图
         * @param left 当前拖拽视图距离父容器左边距离
         * @param top 当前拖拽视图距离父容器顶部距离
         * @param dx X方向偏移量
         * @param dy Y方向偏移量
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy)
        {
            // 8.2.4.1
            // 第一种情况 拖拽contentView functionView 跟着动
            if (changedView == contentView)
            {
                functionView.offsetLeftAndRight(dx);
            }
            // 8.2.4.2
            // 第二种情况 拖拽funciontView contentView跟着动
            if (changedView == functionView)
            {
                contentView.offsetLeftAndRight(dx);
            }

            // 8.2.4.3
            // 随时随刻更新视图状态
            updateSlidingStatus();

            // 8.2.4.4
            // 更新视图
            invalidate();

        }

        // 8.2.4.5
        // 目的 当手势弹起，需要处理一些逻辑
        /**
         *
         * @param releasedChild 当前拖拽释放的视图
         * @param xvel 拖拽 X方向速度
         * @param yvel 拖拽Y方向速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel)
        {

            // 第一种情况： 拖拽contentView 释放后对 contenview 和function 处理
            if (releasedChild == contentView)
            {
                // 8.2.4.5.1
                onContentViewonViewReleased(xvel, yvel);
            }
            // 第二种情况： 和function 释放 和后对 function 和function 处理
            if (releasedChild == functionView)
            {
                onFunctionViewReleased(xvel, yvel);
            }
            // 更新视图
            invalidate();
        }

        @Override
        public void onViewDragStateChanged(int state)
        {
            // 视图状态发生改变回调

        }
    };

    //
    private void updateSlidingStatus()
    {
        updateSlidingStatus(true);
    }

    /**
     * @param isNotify :是否更新状态
     */
    private void updateSlidingStatus(boolean isNotify)
    {
        SlidingStatus status = getCurrentSlidingStatus();
        if (status != slidingStaus)
        {
            if (!isNotify && onSlideItemListener == null)
            {
                return;
            }
            if (status == SlidingStatus.Open)
            {
                this.onSlideItemListener.onOpen(this);
            }
            else if (status == SlidingStatus.Close)
            {
                this.onSlideItemListener.onClose(this);
            }
            else if (status == SlidingStatus.Sliding)
            {
                if (slidingStaus == SlidingStatus.Close)
                {
                    this.onSlideItemListener.onStartOpen(this);
                }
                else if (slidingStaus == SlidingStatus.Open)
                {
                    this.onSlideItemListener.onStartClose(this);
                }
            }
            // 这个里面你可以做一些外部回调
        }
        slidingStaus = status;
    }

    /**
     * 获取当前视图状态 通过滑动偏移量控制（也可以通过left判断获取）
     *
     * @return
     */
    private SlidingStatus getCurrentSlidingStatus()
    {
        int left = contentView.getLeft();
        if (left == 0)
        {
            return SlidingStatus.Close;
        }

        if (left == horizontalDX // function摆放在左边
                || left == -horizontalDX) // function摆放在右边
        {
            return SlidingStatus.Open;
        }
        return SlidingStatus.Sliding;
    }

    // 8.2.4.5.1
    private void onContentViewonViewReleased(float xvel, float yvel)
    {
        // 第一步判断 摆放方向
        switch (slidingType)
        {
            case Left: // functionView 摆放在左边
                // 根据 速度取判断
                if (xvel == 0)
                {
                    // 当前拖拽 停止（需要判断拖拽停止之后，偏移量范围）
                    // 有一个拖拽范围（假设horizontalDx = 100）
                    // 如果 你只拖拽20 距离回弹 超过20，去到指定100
                    // horizontalDX * 0.5f自己定义，只要合理
                    if (contentView.getLeft() > horizontalDX * 0.5f)
                    {
                        // 打开状态 - - - > 打开滑动视图
                        openSlidingLayout(true);
                    }
                    else
                    {
                        closeSlidingLayout(true);
                    }
                }
                else if (xvel > 0)
                {
                    openSlidingLayout(true);
                }
                else
                {
                    // 实现close关闭滑动视图
                    closeSlidingLayout(true);
                }
                break;

            case Right: // functionView 摆放在右边
                if (xvel == 0)
                {
                    // 右边 的偏移量怎么计算（和左边相反）
                    if (contentView.getLeft() < -horizontalDX * 0.5f)
                    {
                        openSlidingLayout(true);
                    }
                    else
                    {
                        closeSlidingLayout(true);
                    }
                }
                else if (xvel < 0)
                {
                    openSlidingLayout(true);
                }
                else
                {
                    closeSlidingLayout(true);
                }

                break;
        }
    }

    // 8.2.5.2 第二种情况：拖拽functionView，释放之后要对functionView和contentView做处理
    private void onFunctionViewReleased(float xvel, float yvel)
    {
        // 第一步判断 摆放方向
        switch (slidingType)
        {
            case Left: // functionView 摆放在左边
                // 根据 速度取判断
                if (xvel == 0)
                {
                    // 当前拖拽 停止（需要判断拖拽停止之后，偏移量范围）
                    // 有一个拖拽范围（假设contenvView.width - horizontalDX 至
                    // contenvView.width）
                    // 偏移凶多少我就打菜单（常量 contentViewWidth - horizontalDX *0.5f ）
                    // horizontalDX * 0.5f自己定义，只要合理
                    if (functionView.getLeft() > (-horizontalDX * 0.4f))
                    {
                        // 打开状态 - - - > 打开滑动视图
                        openSlidingLayout(true);
                    }
                    else
                    {
                        closeSlidingLayout(true);
                    }
                }
                else if (xvel > 0)
                {// 方向X轴正方向
                    openSlidingLayout(true);
                }
                else
                {
                    // 实现close关闭滑动视图
                    closeSlidingLayout(true);
                }
                break;

            case Right: // functionView 摆放在右边
                if (xvel == 0)
                {
                    // 右边 的偏移量怎么计算（和左边相反）
                    if (functionView.getLeft() < contentViewWidth - horizontalDX * 0.4f)
                    {
                        openSlidingLayout(true);
                    }
                    else if (xvel < 0)
                    {
                        openSlidingLayout(true);
                    }
                    else
                    {
                        closeSlidingLayout(true);
                    }
                }
                break;
        }
    }

    /**
     * 打开滑动视图
     *
     * @param isSmooth :是否在滑动的时候有动画
     */
    private void openSlidingLayout(boolean isSmooth)
    {

        openSlidingLayout(isSmooth, true);
    }

    /**
     * 注意：当我们的手势弹起时，要更新状态
     *
     * @param isSmooth:是否在滑动的时候有动画
     * @param isNotify：是否更新视图状态
     */
    private void openSlidingLayout(boolean isSmooth, boolean isNotify)
    {
        if (isSmooth)
        {
            // 计算contentView left right 值
            // 目标位置
            Rect contentRect = layoutContentView(true);
            // smoothSlideViewTo 帮助我们自动滚动视图
            /**
             * contentView 需要滚动的视图 left X方向目标位置 top Y方向目标位置 return : true 滑动完成 false 滑动失败
             */
            if (viewDrageHelper.smoothSlideViewTo(contentView, contentRect.left, contentRect.top))
            {
                // invalidate --- 刷新视图
                // 扩展知识 （系统版本兼容）
                // 一般情况调用invalidate
                // 别一种情况 : ViewCompat.postInvalidateOnAnimation(this);
                // 区别：版本兼容
                // 目地：来个动画过程
                // 问题：如果版本小于16是什么情况，如果大于等于16是什么情况
                // 低于16 view.invalidate();//版本低会报错
                // 大于等于16 view.postInvalidateOnAnimation()
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
        else
        {
            // 重写摆放
            layoutView(true);
            updateSlidingStatus(isNotify);
        }

    }

    /**
     * 关闭滑动视图
     *
     * @param isSmooth :是否在滑动的时候有动画
     */
    public void closeSlidingLayout(boolean isSmooth)
    {

        closeSlidingLayout(isSmooth, true);
    }

    /**
     * 注意：当我们的手势弹起时，要更新状态
     *
     * @param isSmooth:是否在滑动的时候有动画
     * @param isNotify：是否更新视图状态
     */
    public void closeSlidingLayout(boolean isSmooth, boolean isNotify)
    {
        if (isSmooth)
        {
            // 计算contentView left right 值
            // 目标位置
            Rect contentRect = layoutContentView(false);
            // smoothSlideViewTo 帮助我们自动滚动视图
            /**
             * contentView 需要滚动的视图 left X方向目标位置 top Y方向目标位置 return : true 滑动完成 false 滑动失败
             */
            if (viewDrageHelper.smoothSlideViewTo(contentView, contentRect.left, contentRect.top))
            {
                // invalidate --- 刷新视图
                // 扩展知识 （系统版本兼容）
                // 一般情况调用invalidate
                // 别一种情况 : ViewCompat.postInvalidateOnAnimation(this);
                // 区别：版本兼容
                // 目地：来个动画过程
                // 问题：如果版本小于16是什么情况，如果大于等于16是什么情况
                // 低于16 view.invalidate();//版本低会报错
                // 大于等于16 view.postInvalidateOnAnimation()
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
        else
        {
            // 重写摆放(关闭视图)
            layoutView(false);
            updateSlidingStatus(isNotify);
        }

    }

    // 9.2 初始化内容视图,绑定监听，SlidingItemLayout 实现该接口
    private void initContentView()
    {
        if (contentView instanceof SlidingContentView)
        {
            SlidingContentView slidingContentView = (SlidingContentView) contentView;
            slidingContentView.setSlidingLayout(this);
        }
    }

    @Override
    public SlidingStatus getCurrentStaus()
    {
        return getCurrentSlidingStatus();
    }

    @Override
    public void close()
    {
        closeSlidingLayout(true);
    }

    @Override
    public void open()
    {
        openSlidingLayout(true);
    }

    // 9.3事件分发给我们的contentView
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return viewDrageHelper.shouldInterceptTouchEvent(ev) & detectorCompat.onTouchEvent(ev);
    }

    // 9.3事件分发给我们的ContentView （触摸事件）
    private float downX;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // 处理按下 移动 弹起
        switch (MotionEventCompat.getActionMasked(event))
        {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX() - downX;
                // 什么时候拦截
                // 这个getTouchSlop（）是默认滑动最小距离
                if (x > viewDrageHelper.getTouchSlop())
                {
                    // 父容器不要拦截我的事件，我自己处理
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                downX = 0;
                break;

        }
        // 执行触摸事件
        try
        {
            viewDrageHelper.processTouchEvent(event);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void computeScroll()
    {
        // 以下代码是什么意思？
        // viewDragHelper.continueSettling(true)：控制是否滑动结束
        // true：代表可以滑动（说白了继续滑） false：滑动结束
        // 老师你怎么知道？---答案：源码解析得出结论
        // 再来一个问题？
        // 为什么传true？不能传false？
        // 有什么区别？
        // 根据源码得出
        // 设置为true：代表回调onViewDragStateChanged方法
        // 设置false：回调onViewDragStateChanged方法不被执行
        if (viewDrageHelper.continueSettling(true))
        {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
