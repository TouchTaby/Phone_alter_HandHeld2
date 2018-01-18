package com.gsyong.ny.nyapp;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017-08-19.
 */

public class my_toptitle extends LinearLayout {

    public Button back;
    TextView title;

    public my_toptitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.my_toptitle, this);

        back = (Button) findViewById(R.id.btn_title_back);
        title = (TextView) findViewById(R.id.view_toptitle);
    }

    /**
     * 设置此控件的文本
     *
     * @param text
     */
    public void setText(String text) {
        title.setText(text);
    }

    //设置显示或者隐藏 后退按钮
    public void setbackVisibility(boolean bo) {
        if (bo)
            back.setVisibility(VISIBLE);
        else
            back.setVisibility(GONE);
    }

}
