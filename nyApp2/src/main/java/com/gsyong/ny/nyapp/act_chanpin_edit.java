package com.gsyong.ny.nyapp;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Administrator on 2017-11-30.
 */

public class act_chanpin_edit extends AppCompatActivity {

    my_toptitle toptitle;
    EditText vdaima,vmingzi,vzhengshu,vchangjia,vjixing,vhanliang,vjianshu;
    String uid = "",daima="";
    boolean isbendi = false;
    Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chanpin_edit);
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        toptitle = (my_toptitle) findViewById(R.id.toptitle);
        toptitle.setText("商品信息");
        toptitle.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        vdaima = (EditText) findViewById(R.id.view_cp_daima);
        vmingzi = (EditText) findViewById(R.id.view_cp_mingzi);
        vzhengshu = (EditText) findViewById(R.id.view_cp_dengjizheng);
        vchangjia = (EditText) findViewById(R.id.view_cp_chiyouren);
        vjixing = (EditText) findViewById(R.id.view_cp_jixing);
        vhanliang = (EditText) findViewById(R.id.view_cp_hanliang);
        vjianshu = (EditText) findViewById(R.id.view_cp_jianshu);
        btn1 = (Button)  findViewById(R.id.btnchaxun);

        vdaima.setEnabled(false);
        jiazaishangpin();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String daima, mingzi, zhengshu, changjia, jixing;
                Integer hanliang=0,jianshu = 0;
                try{
                    hanliang = Integer.parseInt(vhanliang.getText().toString());
                    jianshu = Integer.parseInt(vjianshu.getText().toString());
                }
                catch (Exception e){
                    Toast.makeText(getBaseContext(), "含量和件数必须是正整数", Toast.LENGTH_LONG).show();
                    return;
                }
                daima = vdaima.getText().toString();
                mingzi = vmingzi.getText().toString();
                zhengshu = vzhengshu.getText().toString();
                changjia = vchangjia.getText().toString();
                jixing = vjixing.getText().toString();

                if(mingzi.length() == 0){
                    Toast.makeText(getBaseContext(), "失败：农药名称不能为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if(zhengshu.length() == 0){
                    Toast.makeText(getBaseContext(), "失败：登记证号不能为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if(changjia.length() == 0){
                    Toast.makeText(getBaseContext(), "失败：登记证持有人不能为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!isbendi) {

                    if(uid.length() == 0){
                        UUID uuid1 = UUID.randomUUID();
                        uid = uuid1.toString().replaceAll("-","");
                    }

                    //写入本地数据库
                    String sqlstr = "insert into dt_chanpin (uid,cpmingzi,cpdaima,cpchangjia,ylstr1,ylstr2) values (?,?,?,?,?,?)";
                    Object[] obj = new Object[]{uid,mingzi,daima,changjia,jixing,zhengshu};
                    DBHelper db = new DBHelper(getBaseContext());
                    db.getWritableDatabase().execSQL(sqlstr,obj);
                    Log.d("写入数据","完成");
                }

                Intent intent = new Intent();
                intent.putExtra("daima", daima);
                intent.putExtra("mingzi", mingzi);
                intent.putExtra("hanliang", hanliang.toString());
                intent.putExtra("jianshu", jianshu.toString());
                //设置返回数据
                act_chanpin_edit.this.setResult(RESULT_OK, intent);
                //关闭Activity
                act_chanpin_edit.this.finish();
            }
        });
    }

    void jiazaishangpin(){
        //获取代码参数
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        daima = bundle.getString("daima");
        vdaima.setText(daima.substring(0,11));
        //首先查询本地数据库
        String sqlstr = "select uid,cpmingzi,cpdaima,cpchangjia,ylstr1,ylstr2 from dt_chanpin where cpdaima = '"+vdaima.getText().toString()+"'";
        Log.d("查询语句",sqlstr);
        DBHelper db = new DBHelper(this);
        Cursor cursor = db.getWritableDatabase().rawQuery(sqlstr,null);
        if(cursor.moveToNext()){
            isbendi = true;
            Log.d("获取产品","本地获取到信息");
            uid = cursor.getString(0);
            vdaima.setText(cursor.getString(2));
            vmingzi.setText(cursor.getString(1));
            vzhengshu.setText(cursor.getString(5));
            vchangjia.setText(cursor.getString(3));
            vjixing.setText(cursor.getString(4));
            vhanliang.setText("1");
            vjianshu.setText("1");
            jinyong();

        }
        cursor.close();

        //如果本地没有产品信息则向服务器查询
        if(uid.length() == 0){
            new Thread(networkTask_getchanpin).start();
        }
    }

    void result_data(String jsonstr){
        try {
            JSONObject jsonObj = new JSONObject(jsonstr);
            if(jsonObj.getString("status").equals("y")){
                JSONObject m = jsonObj.getJSONObject("model");
                uid = m.getString("_uid");
                vdaima.setText(m.getString("_cpdaima"));
                vmingzi.setText(m.getString("_cpmingzi"));
                vzhengshu.setText(m.getString("_ylstr2"));
                vchangjia.setText(m.getString("_cpchangjia"));
                vjixing.setText(m.getString("_cppici"));
                vhanliang.setText("1");
                vjianshu.setText("1");
                jinyong();
            }
            else
            {
                Toast.makeText(this, "失败："+jsonObj.getString("info"), Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException e) {

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    Runnable networkTask_getchanpin = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            try {

                String Url = getResources().getString(R.string.Webservice_Url_nongyao);
                HashMap<String, String> var = new HashMap<String, String>();
                var.put("action", "GetChanPin");
                var.put("daima", daima);
                String str = PostRequest.sendPostRequest(Url, var, null);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = str;//可以是基本类型，可以是对象，可以是List、map等；
                mHandler.sendMessage(msg);
            }
            catch (Exception ex)
            {
                //需要数据传递，用下面方法；
                Message msg =new Message();
                msg.what = 2;
                msg.obj = ex.getMessage();//可以是基本类型，可以是对象，可以是List、map等；
                mHandler.sendMessage(msg);
            }
        }
    };

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = (String)msg.obj;
            switch (msg.what) {
                case 1:
                    result_data(data);
                    break;
                case 2:
                    Toast.makeText(getBaseContext(), data, Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };

    void jinyong()
    {
        vmingzi.setEnabled(false);
        vzhengshu.setEnabled(false);
        vchangjia.setEnabled(false);
        vjixing.setEnabled(false);
    }
}
