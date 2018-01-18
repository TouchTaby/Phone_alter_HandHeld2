package com.gsyong.ny.nyapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by Administrator on 2017-11-24.
 */

public class act_mian extends AppCompatActivity {

    TextView rukucaozuo,rukuchaxun,wodexinxi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mian);

        iniActivity();
    }

    void iniActivity(){
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        rukucaozuo = (TextView) findViewById(R.id.mian_rukucaozuo);
        rukuchaxun = (TextView) findViewById(R.id.mian_rukuchaxun);
        wodexinxi = (TextView) findViewById(R.id.mian_wodexinxi);

        //入库操作
        rukucaozuo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),act_rukucaozuo.class);
                startActivity(intent);
            }
        });

        //入库查询
        rukuchaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),act_rukuchaxun.class);
                startActivity(intent);
            }
        });

        //我的隐患
        wodexinxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),act_wodexinxi.class);
                startActivity(intent);
            }
        });

        //主窗体启动后开始上传数据线程
        new Thread(networkTask_upload).start();

    }


    Runnable networkTask_upload = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            try {
              while (true) {
                  String Url = getResources().getString(R.string.Webservice_Url_nongyao);
                  String sqlstr = "";
                  Cursor cursor = null;
                  DBHelper db = new DBHelper(getBaseContext());
                  //首先上传入库商品详情表 商品详情表一定在先于 入库列表表上传
                  sqlstr = "select uid,listUid,daima,ylint1,ylint2,ylint3,ylstr1 from dt_ruku_cp where isserver = 0";
                  cursor = db.getReadableDatabase().rawQuery(sqlstr,null);
                  while (cursor.moveToNext()) {
                      HashMap<String, String> var = new HashMap<String, String>();
                      var.put("action", "upload_rukucp");
                      var.put("daima", cursor.getString(2));
                      var.put("listUid", cursor.getString(1));
                      var.put("uid", cursor.getString(0));
                      var.put("ylint1", cursor.getString(3));
                      var.put("ylint2", cursor.getString(4));
                      var.put("ylint3", cursor.getString(5));
                      var.put("ylstr1", cursor.getString(6));

                      String str = PostRequest.sendPostRequest(Url, var, null);

                      try {
                          Log.d("上传入库商品",cursor.getString(0)+" "+str);
                          JSONObject jsonObj = new JSONObject(str);
                          if(jsonObj.getString("status").equals("y")){
                                sqlstr = "update dt_ruku_cp set isserver = 1 where uid = '"+cursor.getString(0)+"'";
                                db.getWritableDatabase().execSQL(sqlstr);
                          }

                      }
                      catch (JSONException e) {
                          Log.d("上传入库商品错误:",e.getMessage());
                      }
                      Thread.sleep(500);
                  }

                  //上传入库列表
                  sqlstr = "select uid,addtime,bianhao,qiyeid,shuliang from dt_ruku_list where isserver = 0";

                  cursor = db.getReadableDatabase().rawQuery(sqlstr,null);
                  while (cursor.moveToNext()) {
                      HashMap<String, String> var = new HashMap<String, String>();
                      var.put("action", "upload_rukudan");
                      var.put("addtime", cursor.getString(1));
                      var.put("bianhao", cursor.getString(2));
                      var.put("qiyeid", cursor.getString(3));
                      var.put("shuliang", cursor.getString(4));
                      var.put("uid", cursor.getString(0));


                      String str = PostRequest.sendPostRequest(Url, var, null);

                      try {
                          Log.d("上传入库列表",cursor.getString(0)+" "+str);
                          JSONObject jsonObj = new JSONObject(str);
                          if(jsonObj.getString("status").equals("y")){
                              sqlstr = "update dt_ruku_list set isserver = 1 where uid = '"+cursor.getString(0)+"'";
                              db.getWritableDatabase().execSQL(sqlstr);
                          }

                      }
                      catch (JSONException e) {
                          Log.d("上传入库列表错误:",e.getMessage());
                      }
                      Thread.sleep(500);
                  }

                  for (Integer i = 0;i<10;i++){
                      Log.d("上传数据线程暂停",i.toString());
                      Thread.sleep(6000);
                  }

              }
            }
            catch (Exception ex)
            {
                Log.d("上传数据线程结束",ex.getMessage());
            }
        }
    };
    private long exitTime = 0;
    // 用来计算返回键的点击间隔时间
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
