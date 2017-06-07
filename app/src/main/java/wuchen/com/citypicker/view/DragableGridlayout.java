package wuchen.com.citypicker.view;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;

import wuchen.com.citypicker.R;


/**
 * Created by 巫晨 on 2017/6/2.
 */
public class DragableGridlayout extends GridLayout {
    private static final int columnCountNum = 4;
    private View mDragView;
    private ArrayList<Rect> mRects;
    private int margin = 5;
    private boolean hasAnimation;


    public DragableGridlayout(Context context) {
        this(context, null);
    }

    public DragableGridlayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragableGridlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setColumnCount(4);
        //设置增加时候的动画效果
        setLayoutTransition(new LayoutTransition());
    }


    /**
     * 设置我们的展示的面目
     */
    public void setItems(ArrayList<String> items) {

        for (int j = 0; j < items.size(); j++) {
            addItem(items.get(j));
        }

    }


    /**
     * 设置是否带有拖动切换的动画
     * 此方法需要在setItems之前调用
     *
     * @param hasAnimation
     */
    public void setHasAnimation(boolean hasAnimation) {
        this.hasAnimation = hasAnimation;
        if (this.hasAnimation) {
            setOnDragListener(mOnDragListener);
        } else {
            setOnDragListener(null);
        }
    }

    /**
     * 清楚所有的子控件
     */
    public void clearChilds() {

        for (int j = 0; j < getChildCount(); j++) {
            removeViewAt(j);
        }

    }

    private OnDragListener mOnDragListener = new OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            //当textView被长按。我们就会走到这里的拖拽监听

            switch (event.getAction()) {

                case DragEvent.ACTION_DRAG_STARTED://开始
                    Log.d("tag", "onDrag开始");
                    mDragView.setEnabled(false);
                    createRects();
                    break;

                case DragEvent.ACTION_DRAG_LOCATION://正在
                    Log.d("tag", "ACTION_DRAG_ENTERED");
                    int changeRect = getChangeRect(event);
                    if (changeRect != -1 && mDragView != getChildAt(changeRect)) {
                        //要是知道了你进到那个矩形里面并且不是当前正在拖拽的那个矩形热热热
                        removeView(mDragView);
                        addView(mDragView, changeRect);
                    }
                    break;

                case DragEvent.ACTION_DRAG_ENDED://松手
                    Log.d("tag", "onDrag松手");
                    mDragView.setEnabled(true);
                    break;

                default:
                    break;
            }
            return true;
        }
    };

    private void createRects() {
        //拿到所有的你添加的条目的矩形框范围
        if (mRects == null) {
            mRects = new ArrayList<>();
        } else {
            mRects.clear();
        }
        for (int j = 0; j < getChildCount(); j++) {
            mRects.add(new Rect(getChildAt(j).getLeft()
                    , getChildAt(j).getTop()
                    , getChildAt(j).getRight()
                    , getChildAt(j).getBottom()));
        }
    }


    public void addItem(String item) {

        TextView textView = new TextView(getContext());
        textView.setText(item);
        LayoutParams params = new LayoutParams();
        //设置外边距
        params.setMargins(margin, margin, margin, margin);
        textView.setPadding(0, margin, 0, margin);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.tvbg);
        params.height = LayoutParams.WRAP_CONTENT;
        //条目设置了外边距之后，那么左右两边的距离就大了。应该减去
        params.width = getResources().getDisplayMetrics().widthPixels / 4 - 2 * margin;
        addView(textView, params);
        if (hasAnimation) {
            //要是长按就需要去产生拖拽的效果
            textView.setOnLongClickListener(mOnLongClickListener);
        }else {
            textView.setOnLongClickListener(null);
        }

        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(DragableGridlayout.this, (TextView) v);
                }
            }
        });

    }

    private onItemClickListener onItemClickListener;

    public interface onItemClickListener {
        void onItemClick(View parent, TextView child);
    }

    public void setOnItemClickListener(DragableGridlayout.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //这里开始走了拖拽，那么首先这个textView是在gridLayout中的 所以我们监听这个gridLayout拖拽监听
            v.startDrag(null, new DragShadowBuilder(v), null, 0);
            //            v.setOnDragListener(mOnDragListener);
            //不要瞎设置dragView因为你不能保证你拖的那个影子是你那个textView
            mDragView = v;
            return true;
        }
    };

    public int getChangeRect(DragEvent event) {
        //获取到拖拽的view在哪个矩形范围内
        for (int j = 0; j < mRects.size(); j++) {
            if (mRects.get(j).contains((int) event.getX(), (int) event.getY())) {
                return j;
            }
        }
        return -1;
    }

}
