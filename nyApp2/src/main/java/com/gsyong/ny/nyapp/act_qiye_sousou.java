package com.gsyong.ny.nyapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Administrator on 2017-11-22.
 */

public class act_qiye_sousou extends AppCompatActivity {

    my_toptitle toptitle;
    my_diqu mydiqu;
    private EditText editText1;
    private Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_qiye_sousou);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        btn1 = (Button) findViewById(R.id.button2);
        editText1 = (EditText) findViewById(R.id.editText);

        mydiqu = (my_diqu) findViewById(R.id.diqu);

        toptitle = (my_toptitle) findViewById(R.id.toptitle);
        toptitle.setText("选择企业");
        toptitle.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //监听button事件
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer ProvinceID = mydiqu.getProvinceID();
                Integer CityID = mydiqu.getCityID();
                Integer DistrictID = mydiqu.getDistrictID();
                Integer TownshipID = mydiqu.getTownshipID();
                String qiye = editText1.getText().toString();
                Intent intent;

                    intent = new Intent(v.getContext(), act_qiye_list.class);
                    intent.putExtra("ProvinceID", ProvinceID);
                    intent.putExtra("CityID", CityID);
                    intent.putExtra("DistrictID", DistrictID);
                    intent.putExtra("TownshipID", TownshipID);
                    intent.putExtra("qiye", qiye);
                    startActivityForResult(intent,1);

            }
        });


    }
    /**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
         *
                 * requestCode 请求码，即调用startActivityForResult()传递过去的值
         * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
         */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(data != null) {
           String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
           Log.i("参数", result);
           Intent intent = new Intent();
           //把返回数据存入Intent
           intent.putExtra("result", result);
           //设置返回数据
           act_qiye_sousou.this.setResult(RESULT_OK, intent);
           //关闭Activity
           act_qiye_sousou.this.finish();
       }
    }
}
