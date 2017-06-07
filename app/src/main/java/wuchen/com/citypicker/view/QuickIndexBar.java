package wuchen.com.citypicker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 巫晨 on 2017/6/3.
 */

public class QuickIndexBar extends View {

    private static final String[] LETTERS = new String[]{
            "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X",
            "Y", "Z"
    };

    private Paint mPaint;
    private float mMeasuredHeight;
    private float mMeasuredWidth;
    private String TAG = "tag";

    public QuickIndexBar(Context context) {
        this(context, null);
    }

    public QuickIndexBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //就来直接去创建画笔只创建一次就行了
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        float dip = 14 * context.getResources().getDisplayMetrics().density;
        mPaint.setTextSize(dip);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //我们要画字上去。首先我们要去得到字符的左下角的那个点的左边
        //字体的长度和宽度
        for (int i = 0; i < LETTERS.length; i++) {
            Log.d("qaz", "bhjkbkj");
            String letter = LETTERS[i];
            Rect rect = new Rect();
            //注意，这里传只需要传进去你这一次想画的字符的长度就行了。
            mPaint.getTextBounds(letter, 0, letter.length(), rect);
            float x = mMeasuredWidth / 2 - rect.width() / 2;
            float y = mMeasuredHeight * i + mMeasuredHeight / 2 + rect.height() / 2;
            canvas.drawText(letter, x, y, mPaint);
        }
    }


    private int lastIndex = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                index = (int) (event.getY() / mMeasuredHeight);
                if (lastIndex != index && index <= LETTERS.length) {
                    Log.d(TAG, "onTouchEvent: " + LETTERS[index]);
                    if (mOnLetterChangeLIstener != null) {
                        mOnLetterChangeLIstener.onLetterChange(LETTERS[index]);
                    }
                }
                lastIndex = index;
                break;

            case MotionEvent.ACTION_MOVE:
                index = (int) (event.getY() / mMeasuredHeight);
                if (lastIndex != index && index <= LETTERS.length) {
                    Log.d(TAG, "onTouchEvent: " + LETTERS[index]);
                    if (mOnLetterChangeLIstener != null) {
                        mOnLetterChangeLIstener.onLetterChange(LETTERS[index]);
                    }
                }
                lastIndex = index;
                break;

            case MotionEvent.ACTION_UP:
                lastIndex = -1;
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //这是你这个控件的长度和宽度
        mMeasuredHeight = getMeasuredHeight() * 1.0f / LETTERS.length;
        mMeasuredWidth = getMeasuredWidth();
    }

    private onLetterChangeLIstener mOnLetterChangeLIstener;

    public interface onLetterChangeLIstener {
        void onLetterChange(String letter);
    }

    public void setOnLetterChangeLIstener(onLetterChangeLIstener onLetterChangeLIstener) {
        mOnLetterChangeLIstener = onLetterChangeLIstener;
    }
}
