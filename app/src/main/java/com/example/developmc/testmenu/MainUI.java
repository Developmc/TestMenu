package com.example.developmc.testmenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created by developmc on 15/10/25.
 */
public class MainUI extends RelativeLayout {
    private Context context ;
    private FrameLayout leftMenu ;
    private FrameLayout middleMenu ;
    private FrameLayout bottomMenu ;
    //声明ID
    public static final int LEFT_ID=0xaabbcc;
    public static final int MIDDLE_ID=0xaaccbb;
    public static final int BOTTOM_ID=0xccbbaa;
    //滑动动画
    private Scroller mScroller ;
    public MainUI(Context context) {
        super(context);
        initView(context);
    }

    public MainUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    private void initView(Context context){
        this.context = context ;
        //第二个参数是动画渲染器
        mScroller = new Scroller(context,new DecelerateInterpolator()) ;
        leftMenu = new FrameLayout(context) ;
        middleMenu=new FrameLayout(context) ;
        bottomMenu = new FrameLayout(context) ;
        leftMenu.setBackgroundColor(Color.RED);
        middleMenu.setBackgroundColor(Color.GREEN);
        bottomMenu.setBackgroundColor(Color.GRAY);
        leftMenu.setId(LEFT_ID);
        middleMenu.setId(MIDDLE_ID);
        bottomMenu.setId(BOTTOM_ID);
        //将view 添加进来
        addView(leftMenu);
        addView(middleMenu);
        addView(bottomMenu);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //先测量中间view的高度和宽度
        middleMenu.measure(widthMeasureSpec, heightMeasureSpec);
        //获取屏幕整体的宽度
        int realWidth = MeasureSpec.getSize(widthMeasureSpec);
        int realHeight = MeasureSpec.getSize(heightMeasureSpec);
        //设置左部布局的宽高
        int tempWidthMeasure = MeasureSpec.makeMeasureSpec(
                (int)(realWidth*0.8f),MeasureSpec.EXACTLY
        );
        leftMenu.measure(tempWidthMeasure, heightMeasureSpec);
        //设置底部布局的宽高
        int tempHeightMeasure = MeasureSpec.makeMeasureSpec(
                (int)(realHeight*0.5f),MeasureSpec.EXACTLY
        );
        bottomMenu.measure(widthMeasureSpec,tempHeightMeasure);
    }

    //放置布局
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //把view填充进去
        middleMenu.layout(l,t,r,b);
        leftMenu.layout(l-leftMenu.getMeasuredWidth(),t,r,b);

        bottomMenu.layout(l,t+middleMenu.getMeasuredHeight(),r,t+middleMenu.getMeasuredHeight()+bottomMenu.getMeasuredHeight());
    }

    //判断是怎样的滑动手势
    private boolean isTestCompete ;
    private boolean isLeftRightEvent ;
    private boolean isUpBottomEvent ;
    //触摸事件分发
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(!isTestCompete){
            //判断是何种手势
            getEventType(ev) ;
            return true ;
        }
        //如果是左右滑动
        if(isLeftRightEvent){
            switch (ev.getActionMasked()){
                case MotionEvent.ACTION_MOVE:
                    //滚动的距离
                    int curScrollX = getScrollX();
                    //滑动的距离
                    int dis_x = (int)(ev.getX()-point.x);
                    //判断是向左还是向右
                    int expectX = -dis_x + curScrollX ;
                    //记录最终的距离
                    int finalX = 0;
                    //如果是向左
                    if(expectX<0){
                        finalX = Math.max(expectX,-leftMenu.getMeasuredWidth());
                    }
                    //向右滑动
                    else{
//                        finalX = Math.min(expectX,leftMenu.getMeasuredWidth()) ;
                    }
                    //移动到当前的位置,上下没有滑动，传0
                    scrollTo(finalX,0);
                    //保证每次滑动都是正常的
                    point.x = (int)ev.getX();
                    break ;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

        /*****************************************************/
                    //添加动画（滑动不够一半时缩回去，反之全弹出）
                    //获得滚动的距离
                    curScrollX = getScrollX() ;
                    //有可能为负数（滑动的方向导致的）,当超过一半宽度时
                    if(Math.abs(curScrollX)>leftMenu.getMeasuredWidth()>>1){
                        //判断是向左还是向右
                        if(curScrollX<0){
                            //手指向右滑动，出现左菜单
                            //让view可以滑动
                            mScroller.startScroll(curScrollX,0,
                            -leftMenu.getMeasuredWidth()-curScrollX,0);
                        }
                        else{
                            //向左滑动 ,200是动画执行的时间
//                            mScroller.startScroll(curScrollX,0,
//                                    leftMenu.getMeasuredWidth()-curScrollX,0,200);

                        }
                    }
                    //当长度不足一半时
                    else{
                        mScroller.startScroll(curScrollX,0,-curScrollX,0,200);
                    }
                    //刷新视图
                    invalidate();
        /*****************************************************/

                    //手指抬起后，初始化数据
                    isLeftRightEvent = false ;
                    isTestCompete = false ;

                    isUpBottomEvent = false ;
                    break ;
            }
        }
        //上下滑动
        else if(isUpBottomEvent){
            switch (ev.getActionMasked()){
                case MotionEvent.ACTION_MOVE:
                    //滚动的距离
                    int curScrollY = getScrollY();
                    //滑动的距离
                    int dis_y = (int)(ev.getY()-point.y) ;
                    //判断是向上还是向下
                    int expectY = -dis_y+curScrollY;
                    //记录最终的距离
                    int finalY = 0;
                    //如果是向下
                    if(expectY<0){
//                        finalY = Math.max(expectY,-bottomMenu.getMeasuredHeight());
                    }
                    //如果向上
                    else{
                        finalY = Math.min(expectY,bottomMenu.getMeasuredHeight()) ;
                    }
                    //移动到当前的位置
                    scrollTo(0,finalY);
                    //保证每次滑动都是正常的
                    point.y = (int)ev.getY();
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
        /*****************************************************/
                    //添加上下滑动的动画
                    //获得滚动的距离
                    curScrollY = getScrollY() ;
                    //当滑动的距离超过底部视图的一半时
                    if(Math.abs(curScrollY)>bottomMenu.getMeasuredHeight()>>1)
                    {
                        //判断是向上还是向下
                        //手指向下滑动
                        if(curScrollY<0){
                            //do nothing

                        }
                        //手指向上滑动
                        else{
                            mScroller.startScroll(0,curScrollY,0,
                                    bottomMenu.getMeasuredHeight()-curScrollY,200);
                        }
                    }
                    //滑动距离不足一半时
                    else{
                        mScroller.startScroll(0,curScrollY,0,-curScrollY);
                    }
                    //刷新视图（重写computeScroll）
                    invalidate();
        /*****************************************************/
                    isUpBottomEvent = false ;

                    isLeftRightEvent = false ;
                    isTestCompete = false ;
                    break ;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    //滑动的回调方法
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(!mScroller.computeScrollOffset()){
            return ;
        }
        //判断滑动手势（水平还是垂直）

        int tempX = mScroller.getCurrX();
        int tempY = mScroller.getCurrY();
        scrollTo(tempX,tempY);
//        else if(isUpBottomEvent){
//            int tempY = mScroller.getCurrY();
//            scrollTo(0,tempY);
//        }
    }

    //屏幕的点
    private Point point = new Point() ;
    //定义一个值，判定是否发生了滑动
    private static final int TEST_DIS=20;
    private void getEventType(MotionEvent ev) {
        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                //手指按下
                //获取按下这个点的坐标
                point.x = (int)ev.getX();
                point.y = (int)ev.getY();

                //把事件处理交还系统
                super.dispatchTouchEvent(ev);
                break ;
            case MotionEvent.ACTION_MOVE:
                //手指移动
                //记录在x,y上活动的距离
                int dX = Math.abs((int)ev.getX()-point.x) ;
                int dY = Math.abs((int)ev.getY()-point.y) ;
                if(dX>=TEST_DIS&& dX>dY){
                    //左右滑动
                    isLeftRightEvent = true ;
                    isUpBottomEvent = false ;
                    isTestCompete = true ;
                    //为了滑动之后还能继续滑动
                    point.x = (int)ev.getX();
                    point.y = (int)ev.getY();
                }
                else if(dY>=TEST_DIS && dY>dX){
                    //上下滑动
                    isUpBottomEvent = true ;
                    isLeftRightEvent = false ;
                    isTestCompete = true ;
                    //为了滑动之后还能继续滑动
                    point.x = (int)ev.getX();
                    point.y = (int)ev.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起手指
            case MotionEvent.ACTION_CANCEL:
                //屏幕边缘

                //将点击事件返回给系统处理
                super.dispatchTouchEvent(ev);
                //初始化操作
                isLeftRightEvent = false ;
                isUpBottomEvent = false ;
                isTestCompete = false ;


                break;
        }
    }
}
