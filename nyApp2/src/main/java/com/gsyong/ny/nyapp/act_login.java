package com.gsyong.ny.nyapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class act_login extends AppCompatActivity {


    private Button btn1,btn3;
    Dialog mDialog;
    EditText user;
    EditText pwd;
    CheckBox jz_yonghuming;
    CheckBox jz_mima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        Log.e("login", "onCreate: ");
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        btn1 = (Button) findViewById(R.id.button);
        btn3 = (Button) findViewById(R.id.button3);
        user = (EditText) findViewById(R.id.txtname);
        pwd = (EditText) findViewById(R.id.txtpwd);
        jz_yonghuming = (CheckBox)findViewById(R.id.checkBox3);
        jz_mima = (CheckBox)findViewById(R.id.checkBox2);

        //监听button事件
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(user.getText().toString().length() == 0)
                {
                    Toast.makeText(act_login.this, "登录名不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if(pwd.getText().toString().length() == 0)
                {
                    Toast.makeText(act_login.this, "密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                mDialog= WeiboDialogUtils.createLoadingDialog(act_login.this, "加载中...");
                btn1.setEnabled(false);// 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
                new Thread(networkTask).start();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(act_login.this,act_qiye_sousou.class);
                startActivityForResult(intent,1);
            }
        });

        try {
            FileService f = new FileService(this);
            String s = f.loadToRom("private.txt");
            if(s.length() > 0){
                String[] ss = s.split(":");
                if(ss[0].length() > 0)
                {
                    user.setText(ss[0].toCharArray(),0,ss[0].length());
                    jz_yonghuming.setChecked(true);
                }
                if(ss[1].length() > 0){
                    pwd.setText(ss[1].toCharArray(),0,ss[1].length());
                    jz_mima.setChecked(true);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "错误:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            try {
                //获取URL地址
                String Url = getResources().getString(R.string.Webservice_Url_nongyao);
                String username = user.getText().toString().trim();
                String userpwd = pwd.getText().toString().trim();
                //设置参数
                HashMap<String, String> var = new HashMap<String, String>();
                var.put("action", "app_qylogin");
                var.put("loginName", username);
                var.put("password", helper.getMD5(userpwd));
                //发送请求
                String str = PostRequest.sendPostRequest(Url, var, null);
                //获取响应的json字符串
                JSONObject jsonObj = new JSONObject(str);
                String status = jsonObj.getString("status");
                if (status.equals("y")){
                    //成功后执行
                }
                else
                {
                    //失败后执行
                }


                Message msg = new Message();
                msg.what = 1;
                msg.obj = str;//可以是基本类型，可以是对象，可以是List、map等；
                mHandler.sendMessage(msg);
            } catch (Exception ex) {
                //需要数据传递，用下面方法；
                Message msg = new Message();
                msg.what = 2;
                msg.obj = ex.getMessage();//可以是基本类型，可以是对象，可以是List、map等；
                mHandler.sendMessage(msg);
            }
        }
    };


    protected void resultNet(String jsonstr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonstr);
            String status = jsonObj.getString("status");
            if (status.equals("y")) {
                String msg = "尊敬的 ";
                JSONObject model = jsonObj.getJSONObject("model");

                //记住登录用户必要的信息
                msg += model.getString("_mingzi");
                msg += " 欢迎您使用本系统";

                //保存用户名跟密码
                String savestr = "";
                if(jz_yonghuming.isChecked()) {
                    savestr = user.getText().toString().trim() + ":";
                    if (jz_mima.isChecked())
                        savestr += pwd.getText().toString().trim();
                    try {
                        FileService fileService = new FileService(this);
                        boolean bol = fileService.saveToRom(savestr, "private.txt");

                    }catch (Exception e){
                        e.printStackTrace();

                    }
                }

                Toast.makeText(act_login.this, msg, Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(this, act_main.class);
//                startActivity(intent);

            } else {

                Toast.makeText(act_login.this, jsonObj.getString("info"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            System.out.println("Json parse error");
            e.printStackTrace();
        }
        mHandler.sendEmptyMessageDelayed(3, 500);
        btn1.setEnabled(true);
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = (String) msg.obj;
            switch (msg.what) {
                case 2:
                case 0:
                    //完成主界面更新,拿到数据
                    Toast tot = Toast.makeText(
                            act_login.this,
                            data,
                            Toast.LENGTH_LONG);
                    tot.show();
                    break;
                case 1:
                    resultNet(data);
                    break;
                case 3:
                    WeiboDialogUtils.closeDialog(mDialog);
                    break;
                default:
                    break;
            }
        }

    };


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
            user.setText(result);
        }
    }

}
