package com.gsyong.ny.nyapp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.gsyong.ny.nyapp.Model.dt_zhucema;
import com.gsyong.ny.nyapp.Model.dt_qiye;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2017-11-23.
 */

public class act_qidong extends AppCompatActivity {



    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_qidong);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                apprun();
            }
        }, 1000);

    }

    void apprun(){
        //尝试加载配置文件
        try {
            //实例化数据库对象
            DBHelper db = new DBHelper(this);
            db.getWritableDatabase();
            FileService f = new FileService(this);
            String s = f.loadToRom("zc.txt");
            Log.i("读取注册:",s);
            if(s.length() > 0) {
                JSONObject jsonObj = new JSONObject(s);
                boolean status = jsonObj.getBoolean("status");
                if (status) {

                    JSONObject qy = jsonObj.getJSONObject("qiye");
                    //赋值企业
                    helper.login_qiye = new dt_qiye();
                    helper.login_qiye._id = qy.getInt("_id");
                    helper.login_qiye._mingzi = qy.getString("_mingzi");
                    helper.login_qiye._dizhi = qy.getString("_dizhi");
                    helper.login_qiye._leixing = qy.getString("_leixing");
                    helper.login_qiye._fuzeren = qy.getString("_fuzeren");
                    helper.login_qiye._fzrshenfenzheng = qy.getString("_fzrshenfenzheng");
                    helper.login_qiye._fzrdianhua = qy.getString("_fzrdianhua");
                    helper.login_qiye._yingyezhizhao = qy.getString("_yingyezhizhao");
                    helper.login_qiye._yingyevld1 = qy.getString("_yingyevld1");
                    helper.login_qiye._yingyevld2 = qy.getString("_yingyevld2");
                    helper.login_qiye._nyxukehao = qy.getString("_nyxukehao");
                    helper.login_qiye._nyvld1 = qy.getString("_nyvld1");
                    helper.login_qiye._nyvld2 = qy.getString("_nyvld2");
                    helper.login_qiye._addtime = qy.getString("_addtime");
                    helper.login_qiye._status = qy.getInt("_status");
                    helper.login_qiye._provinceid = qy.getInt("_provinceid");
                    helper.login_qiye._cityid = qy.getInt("_cityid");
                    helper.login_qiye._countyid = qy.getInt("_countyid");
                    helper.login_qiye._townshipid = qy.getInt("_townshipid");
                    helper.login_qiye._villageid = qy.getInt("_villageid");
                    helper.login_qiye._ylint1 = qy.getInt("_ylint1");
                    helper.login_qiye._ylint2 = qy.getInt("_ylint2");
                    helper.login_qiye._ylint3 = qy.getInt("_ylint3");
                    helper.login_qiye._ylint4 = qy.getInt("_ylint4");
                    helper.login_qiye._ylstr1 = qy.getString("_ylstr1");
                    helper.login_qiye._ylstr2 = qy.getString("_ylstr2");
                    helper.login_qiye._ylstr3 = qy.getString("_ylstr3");
                    helper.login_qiye._ylstr4 = qy.getString("_ylstr4");

                    JSONObject zcm = jsonObj.getJSONObject("zhucema");
                    helper.login_zhucema = new dt_zhucema();

                    helper.login_zhucema = new dt_zhucema();
                    helper.login_zhucema._id = zcm.getInt("_id");
                    helper.login_zhucema._qiyeid = zcm.getInt("_qiyeid");
                    helper.login_zhucema._zhucema = zcm.getString("_zhucema");
                    helper.login_zhucema._addtime = zcm.getString("_addtime");
                    helper.login_zhucema._valtime = zcm.getString("_valtime");
                    helper.login_zhucema._pcmac = zcm.getString("_pcmac");
                    helper.login_zhucema._fangshi = zcm.getString("_fangshi");
                    helper.login_zhucema._zhuangtai = zcm.getInt("_zhuangtai");
                    helper.login_zhucema._ylint1 = zcm.getInt("_ylint1");
                    helper.login_zhucema._ylint2 = zcm.getInt("_ylint2");
                    helper.login_zhucema._ylint3 = zcm.getInt("_ylint3");
                    helper.login_zhucema._ylstr1 = zcm.getString("_ylstr1");
                    helper.login_zhucema._ylstr2 = zcm.getString("_ylstr2");
                    helper.login_zhucema._ylstr3 = zcm.getString("_ylstr3");
                    helper.login_zhucema._jine = zcm.getString("_jine");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "错误:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }



        if(helper.login_zhucema == null){
            Toast.makeText(getApplicationContext(), "请注册", Toast.LENGTH_SHORT).show();
            //如果没有注册文件 代表没有注册 启动注册页面
            Intent intent = new Intent(this, act_zhuce.class);
            startActivity(intent);
            finish();
        }
        else
        {
            //判断是否过期
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {

                java.util.Date date=sdf.parse(helper.login_zhucema._valtime);
                Date curDate = new Date(System.currentTimeMillis());
                if(curDate.getTime() > date.getTime()){
                    Toast.makeText(getApplicationContext(), "注册信息已经过期,请重新注册", Toast.LENGTH_SHORT).show();
                    //如果没有注册文件 代表没有注册 启动注册页面
                    Intent intent = new Intent(this, act_zhuce.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(this, act_mian.class);
                    startActivity(intent);
                    finish();
                }



            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "注册日期异常", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }
}
