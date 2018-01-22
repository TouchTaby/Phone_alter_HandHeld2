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
import android.widget.EditText;
import android.widget.Toast;

import com.gsyong.ny.nyapp.Model.dt_qiye;
import com.gsyong.ny.nyapp.Model.dt_zhucema;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2017-11-23.
 */

public class act_zhuce extends AppCompatActivity {

    private Button btn1;
    Dialog mDialog;
    EditText codeEdit;
    private static String TAG = "TAG-act_zhuce";
    Barcode2DWithSoft barcode2DWithSoft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_zhuce);

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        btn1 = (Button) findViewById(R.id.button);
        codeEdit = (EditText) findViewById(R.id.txtcode);
        barcode2DWithSoft = Barcode2DWithSoft.getInstance();
        //监听button事件
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(codeEdit.getText().toString().length() != 10)
                {
                    Toast.makeText(act_zhuce.this, "注册码不符合规范", Toast.LENGTH_LONG).show();
                    return;
                }
                mDialog= WeiboDialogUtils.createLoadingDialog(act_zhuce.this, "注册中...");
                btn1.setEnabled(false);// 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
                new Thread(networkTask).start();
            }
        });

    }

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            // 在这里进行 http request.网络请求相关操作
            try {
                //获取URL地址
                String Url = getResources().getString(R.string.Webservice_Url_nongyao);
                String code = codeEdit.getText().toString().trim();
                //设置参数
                HashMap<String, String> var = new HashMap<String, String>();
//                var.put("action", "zhucemabangding");
//                var.put("pcmac", WebService.getMAC(getApplication()));
//                var.put("zhucema", code);
                var.put("action", "zhuce");
                var.put("code", code);
                //发送请求d
                String str = PostRequest.sendPostRequest(Url, var, null);

//                String str = WebService.zhucebangding(code, WebService.getMAC(getApplicationContext()));

                Message msg = new Message();
                msg.what = 1;
                msg.obj = str;//可以是基本类型，可以是对象，可以是List、map等；
                Log.e(TAG, "注册run: "+ str );
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
            if (jsonstr.equals("anyType{status=false; info=注册码不存在或者已经失效; }")){
                Toast.makeText(getApplicationContext(),"status=false; 注册码不存在或者已经失效",Toast.LENGTH_LONG).show();
            }
            JSONObject jsonObj = new JSONObject(jsonstr);
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
                helper.login_qiye._cityid =qy.getInt("_cityid");
                helper.login_qiye._countyid = qy.getInt("_countyid");
                helper.login_qiye._townshipid = qy.getInt("_townshipid");
                helper.login_qiye._villageid =qy.getInt("_villageid");
                helper.login_qiye._ylint1 = qy.getInt("_ylint1");
                helper.login_qiye._ylint2 = qy.getInt("_ylint2");
                helper.login_qiye._ylint3 = qy.getInt("_ylint3");
                helper.login_qiye._ylint4 = qy.getInt("_ylint4");
                helper.login_qiye._ylstr1 = qy.getString("_ylstr1");
                helper.login_qiye._ylstr2 =qy.getString("_ylstr2");
                helper.login_qiye._ylstr3 = qy.getString("_ylstr3");
                helper.login_qiye._ylstr4 = qy.getString("_ylstr4");

                JSONObject zcm = jsonObj.getJSONObject("zhucema");
                helper.login_zhucema = new dt_zhucema();
                helper.login_zhucema = new dt_zhucema();
                helper.login_zhucema._id = zcm.getInt("_id");
                helper.login_zhucema._qiyeid =  zcm.getInt("_qiyeid");
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

                try {
                //保存文件
                FileService fileService = new FileService(this);
                boolean bo = fileService.saveToRom(jsonstr, "zc.txt");
                    Log.i("写入注册:",bo?"成功":"失败"+" - "+jsonstr);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("写入异常:",e.getMessage());
                }
                Intent intent = new Intent(this, act_mian.class);
                startActivity(intent);
                Toast.makeText(getBaseContext(), "注册成功", Toast.LENGTH_LONG).show();
                finish();
                return;
            } else {
                Toast.makeText(getBaseContext(), jsonObj.getString("info"), Toast.LENGTH_LONG).show();
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
}
