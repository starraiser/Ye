package com.example.administrator.sqlite;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * 带一键清除的edittext
 * Created by Administrator on 2015/11/6.
 */
public class ClearEditText extends EditText {
    public ClearEditText(Context context) {
        super(context);
        setClearIcon();
    }


    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClearIcon();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClearIcon();
    }

    /**
     * 创建默认的清除图标
     */
    private void setClearIcon() {
        //  if (getCompoundDrawables().length == 0) {
        setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ico_clear), null);
        //    }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final Drawable[] compoundDrawables = getCompoundDrawables();
        if (compoundDrawables.length < 1) {
            return super.onTouchEvent(event);
        }
        //获得清除按钮。一键清除内容
        final Drawable del_ico = compoundDrawables[2];
        if (del_ico != null && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();

            if (x > (getWidth() - getTotalPaddingRight()) && x < (getWidth() - getPaddingRight())) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }
}

