package com.gsyong.ny.nyapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
/**
 * Created by Administrator on 2017-11-24.
 */

public class act_rukucaozuo extends AppCompatActivity {

    Button btn1,btn2,btn3;
    Integer leixing = 0;//0 未入库 1已入库
    ListView tableListView;
    List<addGoods> gslist = new ArrayList<addGoods>();
    TableAdapter adapter;
    long mLastTime=0,mCurTime=0;
    int hangshu = -1;
    Handler handler = new Handler();
    String _uid = "";

    /******************************************
     添加摩托二维头读取
     添加处：line:69
            line:500~600
     添加lib cw-deviceapi20171026.jar和jniLibs 依赖为扫描头驱动
     */
    static String TAG = "TAG";
    private Barcode2DWithSoft barcode2DWithSoft = null;
    boolean scanning = false;


    /***********************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_rukucaozuo);

        new InitTask().execute();//异步初始化
        iniActivity();
    }

    private void permission() {
        if (Build.VERSION.SDK_INT>21){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(act_rukucaozuo.this,
                        new String[]{android.Manifest.permission.CAMERA},1);
                Log.e(TAG, "permission: 添加权限" );
            }else {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(act_rukucaozuo.this,
                        new String[]{android.Manifest.permission.CAMERA},1);
                //说明已经获取到摄像头权限了 想干嘛干嘛
                Log.e(TAG, "permission: 已经添加权限" );
            }
            //读写内存权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                ActivityCompat
                        .requestPermissions(
                                this,
                                new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                                1);
            }

            int checkCallPhonePermission = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, }, 1);
                return;
            } else {
                // 上面已经写好的拨号方法

            }

        }else {
//这个说明系统版本在6.0之下，不需要动态获取权限。
            Log.e(TAG, "permission:这个说明系统版本在6.0之下，不需要动态获取权限。 " );
        }
    }

    void iniActivity() {
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);

        my_toptitle toptitle = (my_toptitle) findViewById(R.id.toptitle);
        toptitle.setText("企业入库");
        toptitle.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //设置表格标题的背景颜色
        ViewGroup tableTitle = (ViewGroup) findViewById(R.id.table_title);
        tableTitle.setBackgroundColor(Color.rgb(177, 173, 172));
        try{
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            _uid = bundle.getString("uid");
            Log.d("入库uid",_uid);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(_uid.length() > 0){
            //如果是之前保存的加载之前的商品信息
            String sqlstr = "select daima,ylint2,ylint3,ylstr1 from dt_ruku_cp where listUid = '"+_uid+"'";
            DBHelper db = new DBHelper(this);
            Cursor cursor = db.getWritableDatabase().rawQuery(sqlstr,null);
            if(cursor.moveToNext()){
                addGoods gs = new addGoods();
                gs.mingzi = cursor.getString(3);
                gs.hanliang= cursor.getString(1);
                gs.jianshu = cursor.getString(2);
                gs.daima = cursor.getString(0);
                gs.xuhao = gslist.size() + 1;
                gslist.add(gs);
            }

        }

//        gslist.add(new addGoods(1,"名字1","1","1","aa1111111"));
//        gslist.add(new addGoods(2,"名字2","1","1","aa1111111"));
//        gslist.add(new addGoods(3,"名字3","1","1","aa1111111"));


        tableListView = (ListView) findViewById(R.id.list);

        adapter = new TableAdapter(this, gslist);
        tableListView.setAdapter(adapter);

        btn1.setFocusable(true);
        btn1.setFocusableInTouchMode(true);
        btn1.requestFocus();
        btn1.requestFocusFromTouch();

        tableListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            //list点击事
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                Log.d("onItemClick","OK");
                mCurTime = System.currentTimeMillis();
                if(mCurTime - mLastTime < 500 && hangshu == p3){
                    final int fp3 = p3;
                    AlertDialog.Builder builder = new AlertDialog.Builder(act_rukucaozuo.this);
                    builder.setTitle("删除确认");
                    builder.setMessage("您真的要删除这个记录吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gslist.remove(fp3);
                            for(int i = 0;i<gslist.size();i++){
                                gslist.get(i).xuhao = i+1;
                            }
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getBaseContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //    设置一个NegativeButton
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();

                    mLastTime = mCurTime - 500;
                } else
                {
                    hangshu = p3;
                    isSingClick(mCurTime,p3);
                    mLastTime = mCurTime;
                }
            }

            private void isSingClick(final long times,final int index) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mCurTime == times){
                            if(gslist.get(index).daima.length() < 7)
                            {
                                Toast.makeText(getBaseContext(), "产品代码不正确", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(getBaseContext(),act_chanpin_edit.class);
                            intent.putExtra("daima", gslist.get(index).daima);
                            startActivityForResult(intent, 16);
                        }
                    }
                }, 500);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcode2DWithSoft.close();//读头和摄像头不能同时打开
                IntentIntegrator integrator = new IntentIntegrator(act_rukucaozuo.this);
                // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setCaptureActivity(act_saoyisao.class);
                integrator.setPrompt("请扫描二维码"); //底部的提示文字，设为""可以置空
                integrator.setCameraId(0); //前置或者后置摄像头
                integrator.setBeepEnabled(true); //扫描成功的「哔哔」声，默认开启
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });

        //监听button事件 保存
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leixing = 0;
                baocun();
//                Intent intent = new Intent(v.getContext(),act_chanpin_edit.class);
//                startActivity(intent);
            }
            });
        //监听button事件 提交
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leixing = 1;
                baocun();
            }
        });

    }


    void baocun(){
        Integer count = tableListView.getCount();
        if(count == 0){
            Toast.makeText(getBaseContext(), "没有可以入库的数据", Toast.LENGTH_LONG).show();
            return;
        }
        //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        AlertDialog.Builder builder = new AlertDialog.Builder(act_rukucaozuo.this);
        builder.setTitle("入库确认");
        builder.setMessage("您确定要将这些数据入库吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //开始入库操作
                List<String> sqlstr = new ArrayList<String>();
                List<String> sqldel = new ArrayList<String>();
                List<Object[]> sqlsp = new ArrayList<Object[]>();

                if(_uid.length() > 0){
                    //如果有UID代表是之前保存过，全部删除掉 重新进行入库操作
                    String delete = "delete from dt_ruku_list where uid = '"+_uid+"'";
                    sqldel.add(delete);
                    delete =  "delete from dt_ruku_cp where listUid = '"+_uid+"'";
                    sqldel.add(delete);
                }



                UUID uuid = UUID.randomUUID();
                String uid = uuid.toString().replaceAll("-","");
                Integer zongshu = 0;//总数
                int isserver = leixing==0?1:0;



                for (int i = 0;i<tableListView.getCount();i++){
                    View vi = tableListView.getChildAt(i);
                    ViewHolder vh = new ViewHolder();
                    vh.text_xuhao = (TextView) vi.findViewById(R.id.text_xuhao);
                    vh.text_mingzi = (TextView) vi.findViewById(R.id.text_mingzi);
                    vh.text_hanliang = (TextView) vi.findViewById(R.id.text_hanliang);
                    vh.text_jianshu = (EditText) vi.findViewById(R.id.text_jianshu);
                    vh.text_code = (TextView) vi.findViewById(R.id.text_code);
                    //生成入库商品详情语句
                    String sql = "insert into dt_ruku_cp (uid,listUid,daima,ylint1,ylint2,ylint3,isserver,ylstr1) values (?,?,?,?,?,?,"+isserver+",?)";
                    sqlstr.add(sql);

                    UUID uuid1 = UUID.randomUUID();
                    String uid1 = uuid1.toString().replaceAll("-","");

                    Integer j,h;
                    h = Integer.parseInt(vh.text_hanliang.getText().toString());
                    try{
                        j = Integer.parseInt(vh.text_jianshu.getText().toString());
                    }
                    catch (Exception e){
                        j = 1;
                    }

                    Object[] objs = new Object[]{uid1.toString(),uid,vh.text_code.getText().toString(),h*j,h,j,vh.text_mingzi.getText().toString()};
                    sqlsp.add(objs);

                    zongshu += h*j;
//                            msg += vh.text_xuhao.getText()+"-"+vh.text_mingzi.getText()+"-"+vh.text_hanliang.getText()+"-"+vh.text_jianshu.getText()+"-"+vh.text_code.getText();
//                            Log.d("输出内容",msg);
                }
                sqlstr.add("insert into dt_ruku_list (uid,addtime,bianhao,qiyeid,shuliang,ylint1,isserver) values (?,?,?,?,?,"+leixing+","+isserver+")");
                sqlsp.add(new Object[]{uid,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()),helper.login_qiye._id,zongshu});

                DBHelper db = new DBHelper(getBaseContext());

                for(int i = 0;i<sqldel.size();i++){
                    //删除
                    db.getWritableDatabase().execSQL(sqldel.get(i));
                }
                for(int i = 0;i<sqlstr.size();i++){
                    String msg = sqlstr.get(i)+" - ";
                    for (int z = 0;z < sqlsp.get(i).length;z++){
                        msg += sqlsp.get(i)[z].toString()+",";
                    }
                    Log.d("sql",msg);

                    db.getWritableDatabase().execSQL(sqlstr.get(i),sqlsp.get(i));

                }

                gslist.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), "操作成功", Toast.LENGTH_SHORT).show();
                if(_uid.length() > 0)
                    finish();
            }
        });
        //    设置一个NegativeButton
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "您取消了入库", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 16){
            if(data != null) {
                //得到新Activity 关闭后返回的数据
                for (int i = 0;i < gslist.size();i++){
                    if(gslist.get(i).daima.equals(data.getExtras().getString("daima"))){
                        gslist.get(i).jianshu = data.getExtras().getString("jianshu");
                        gslist.get(i).hanliang= data.getExtras().getString("hanliang");
                        adapter.notifyDataSetChanged();
//                        TableAdapter adapter = new TableAdapter(this, gslist);
//                        tableListView.setAdapter(adapter
                        return;
                    }
                }
                addGoods gs = new addGoods();
                gs.daima = data.getExtras().getString("daima");
                gs.jianshu = data.getExtras().getString("jianshu");
                gs.hanliang= data.getExtras().getString("hanliang");
                gs.mingzi = data.getExtras().getString("mingzi");
                gs.xuhao = gslist.size() + 1;
                gslist.add(gs);
                adapter.notifyDataSetChanged();
            }
        } else {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null) {
                String result = scanResult.getContents();
                if (result != null) {
                    if (result.indexOf("http://") == 0 || result.indexOf("https://") == 0) {
                        Log.d("二维码", result);
                        if (result.length() < 32) {
                            Toast.makeText(getBaseContext(), result + "\n\n结果不符合二维码规格", Toast.LENGTH_LONG).show();
                        } else {
                            String daima = result.substring(result.length() - 32);
                            Log.d("二维码解析", daima);

                            if(daima.length() < 11)
                            {
                                Toast.makeText(getBaseContext(), "产品代码不正确", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(this, act_chanpin_edit.class);
                            intent.putExtra("daima", daima);
                            startActivityForResult(intent, 16);
                        }
                    } else {
                        Toast.makeText(getBaseContext(), result + "\n\n结果不符合规范", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }

    public class TableAdapter extends BaseAdapter  {
        private List<addGoods> datas;
        private LayoutInflater inflater;

        public TableAdapter(Context context, List<addGoods> data) {
            super();
            inflater = LayoutInflater.from(context);
            datas = data;
        }

        @Override
        public int getCount()  {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            addGoods goods = (addGoods) this.getItem(position);
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_rukuliebiao, null);
                viewHolder.text_xuhao = (TextView) convertView.findViewById(R.id.text_xuhao);
                viewHolder.text_mingzi = (TextView) convertView.findViewById(R.id.text_mingzi);
                viewHolder.text_hanliang = (TextView) convertView.findViewById(R.id.text_hanliang);
                viewHolder.text_jianshu = (EditText) convertView.findViewById(R.id.text_jianshu);
                viewHolder.text_code = (TextView) convertView.findViewById(R.id.text_code);
                viewHolder.text_jianshu.setEnabled(true);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.text_xuhao.setText(goods.xuhao.toString());
            viewHolder.text_mingzi.setText(goods.mingzi);
            viewHolder.text_hanliang.setText(goods.hanliang);
            viewHolder.text_jianshu.setText(goods.jianshu);
            viewHolder.text_code.setText(goods.daima);
            return convertView;
        }
    }

    public class ViewHolder {
        TextView text_xuhao;
        TextView text_mingzi;
        TextView text_hanliang;
        EditText text_jianshu;
        TextView text_code;
    }
    public class addGoods {

        public addGoods(){}

        public addGoods(Integer xuhao,String mingzi,String hanliang,String jianshu,String daima){
            this.xuhao = xuhao;
            this.mingzi = mingzi;
            this.hanliang = hanliang;
            this.jianshu = jianshu;
            this.daima = daima;
        }

        public Integer xuhao;
        public String mingzi;
        public String hanliang;
        public String jianshu;
        public String daima;
    }
    /**
     * start
     * ***************************************************************************
     */
    private class ScanBack implements Barcode2DWithSoft.ScanCallback {
        @Override
        public void onScanComplete(int i, int length, byte[] bytes) {
            String barCode = new String(bytes, 0, length);
            scanning = false;
            barcode2DWithSoft.stopScan();
            Log.e(TAG, "onScanComplete:-------------- " + barCode);
            if (barCode != null) {
                if (barCode.indexOf("http://") == 0 || barCode.indexOf("https://") == 0) {
                    Log.d("二维码", barCode);
                    if (barCode.length() < 32) {
                        Toast.makeText(getBaseContext(), barCode + "\n\n结果不符合二维码规格", Toast.LENGTH_LONG).show();
                    } else {
                        String daima = barCode.substring(barCode.length() - 32);
                        Log.d("二维码解析", daima);
                        if (daima.length() < 7) {
                            Toast.makeText(getBaseContext(), "产品代码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //跳转到产品详细信息界面
                        Intent intent = new Intent(getApplicationContext(), act_chanpin_edit.class);
                        intent.putExtra("daima", daima);
                        startActivityForResult(intent, 16);
                    }
                } else {
                    Toast.makeText(getBaseContext(), barCode + "\n\n结果不符合规范", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            if (barcode2DWithSoft == null) {
                barcode2DWithSoft = Barcode2DWithSoft.getInstance();
            }
            boolean reuslt = false;
            if (barcode2DWithSoft != null) {
                reuslt = barcode2DWithSoft.open(act_rukucaozuo.this);
                barcode2DWithSoft.setScanCallback(new ScanBack());
            }
            return reuslt;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(act_rukucaozuo.this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(act_rukucaozuo.this, "fail", Toast.LENGTH_SHORT).show();
            }
            mypDialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mypDialog = new ProgressDialog(act_rukucaozuo.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }

    }
    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        if (barcode2DWithSoft != null) {
            barcode2DWithSoft.stopScan();
            barcode2DWithSoft.close();
        }
        super.onDestroy();
    }
    private void ScanBarcode() {
        if (barcode2DWithSoft != null && !scanning) {
            Log.e(TAG, "ScanBarcode");
            scanning = true;
            barcode2DWithSoft.scan();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (barcode2DWithSoft != null) {
            barcode2DWithSoft.close();//屏幕熄灭
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new InitTask().execute();//屏幕重新启动的时候初始化读头
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139) {
            if (event.getRepeatCount() == 0) {
                ScanBarcode();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * end
     * *********************************************************************
     */
}
