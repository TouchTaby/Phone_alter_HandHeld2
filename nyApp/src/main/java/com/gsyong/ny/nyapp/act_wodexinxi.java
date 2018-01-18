package com.gsyong.ny.nyapp;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by Administrator on 2017-12-02.
 */

public class act_wodexinxi  extends AppCompatActivity {

    my_toptitle toptitle;
    EditText vmingzi,vdizhi,vkezhenghao,vyouxiaoqi,vqixian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wodexinxi);
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        toptitle = (my_toptitle) findViewById(R.id.toptitle);
        toptitle.setText("我的信息");
        toptitle.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        vdizhi = (EditText) findViewById(R.id.view_xx_dizhi);
        vmingzi = (EditText) findViewById(R.id.view_xx_mingzi);
        vkezhenghao = (EditText) findViewById(R.id.view_xx_xukezheng);
        vyouxiaoqi = (EditText) findViewById(R.id.view_xx_youxiaoqi);
        vqixian = (EditText) findViewById(R.id.view_xx_qixian);

        vdizhi.setText(helper.login_qiye._dizhi);
        vkezhenghao.setText(helper.login_qiye._nyxukehao);
        vmingzi.setText(helper.login_qiye._mingzi);
        vyouxiaoqi.setText(helper.login_qiye._nyvld1.split(" ")[0] +" - "+helper.login_qiye._nyvld2.split(" ")[0]);
        vqixian.setText(helper.login_zhucema._valtime);
    }
}
