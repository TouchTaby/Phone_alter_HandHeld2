package com.gsyong.ny.nyapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.gsyong.ny.nyapp.R;
import com.gsyong.ny.nyapp.act_chanpin_edit;
import com.gsyong.ny.nyapp.act_saoyisao;
import com.gsyong.ny.nyapp.my_toptitle;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.util.ArrayList;
import java.util.List;

public class OutStorageActivity extends AppCompatActivity {
    Button bt_scan_system, bt_save_db, bt_commit;
    List<AddGoods> goodlist = new ArrayList<>();
    ListView out_listView;
    OutTableAdapter adapter;
    static String TAG = "TAG";
    private Barcode2DWithSoft barcode2DWithSoft = null;
    boolean scanning = false;
    Handler outhandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_storage);
        initView();
        new InitTask().execute();//异步初始化

    }

    private void initView() {
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        my_toptitle toptitle = (my_toptitle) findViewById(R.id.toptitle);
        toptitle.setText("企业出库");
        toptitle.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //设置表格标题的背景颜色
        ViewGroup tableTitle = (ViewGroup) findViewById(R.id.table_title);
        tableTitle.setBackgroundColor(Color.rgb(177, 173, 172));
        bt_commit = (Button) findViewById(R.id.bt_commit);
        bt_save_db = (Button) findViewById(R.id.bt_save_db);
        bt_scan_system = (Button) findViewById(R.id.bt_system_scan);
        bt_scan_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(OutStorageActivity.this);
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
        out_listView = (ListView) findViewById(R.id.out_listView);
        adapter = new OutTableAdapter(this, goodlist);
        out_listView.setAdapter(adapter);
        //跳转详细编辑
        out_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                outhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (goodlist.get(position).daima.length() < 7) {
                            Toast.makeText(getBaseContext(), "产品代码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(getBaseContext(), act_chanpin_edit.class);
                        intent.putExtra("daima", goodlist.get(position).daima);
                        startActivityForResult(intent, 16);
                    }
                }, 500);
            }
        });
        //长按删除
        out_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final int fp3 = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(OutStorageActivity.this);
                builder.setTitle("删除确认");
                builder.setMessage("您真的要删除这个记录吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goodlist.remove(fp3);
                        for (int i = 0; i < goodlist.size(); i++) {
                            goodlist.get(i).xuhao = i + 1;
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

                return false;
            }
        });
        bt_save_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        bt_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

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
                reuslt = barcode2DWithSoft.open(OutStorageActivity.this);
                barcode2DWithSoft.setScanCallback(new ScanBack());
            }
            return reuslt;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Toast.makeText(OutStorageActivity.this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(OutStorageActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
            mypDialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mypDialog = new ProgressDialog(OutStorageActivity.this);
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

    public class OutTableAdapter extends BaseAdapter {
        private List<AddGoods> datas;
        private LayoutInflater inflater;

        public OutTableAdapter(Context context, List<AddGoods> data) {
            super();
            inflater = LayoutInflater.from(context);
            datas = data;
        }

        @Override
        public int getCount() {
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
            AddGoods goods = (AddGoods) this.getItem(position);
            OutViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new OutViewHolder();
                convertView = inflater.inflate(R.layout.item_rukuliebiao, null);
                viewHolder.text_xuhao = (TextView) convertView.findViewById(R.id.text_xuhao);
                viewHolder.text_mingzi = (TextView) convertView.findViewById(R.id.text_mingzi);
                viewHolder.text_hanliang = (TextView) convertView.findViewById(R.id.text_hanliang);
                viewHolder.text_jianshu = (EditText) convertView.findViewById(R.id.text_jianshu);
                viewHolder.text_code = (TextView) convertView.findViewById(R.id.text_code);
                viewHolder.text_jianshu.setEnabled(true);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (OutStorageActivity.OutViewHolder) convertView.getTag();
            }
            viewHolder.text_xuhao.setText(goods.xuhao.toString());
            viewHolder.text_mingzi.setText(goods.mingzi);
            viewHolder.text_hanliang.setText(goods.hanliang);
            viewHolder.text_jianshu.setText(goods.jianshu);
            viewHolder.text_code.setText(goods.daima);
            return convertView;
        }
    }

    public class OutViewHolder {
        TextView text_xuhao;
        TextView text_mingzi;
        TextView text_hanliang;
        EditText text_jianshu;
        TextView text_code;
    }

    public class AddGoods {

        public AddGoods() {
        }

        public AddGoods(Integer xuhao, String mingzi, String hanliang, String jianshu, String daima) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 16) {
            if (data != null) {
                //得到新Activity 关闭后返回的数据
                for (int i = 0; i < goodlist.size(); i++) {
                    if (goodlist.get(i).daima.equals(data.getExtras().getString("daima"))) {
                        goodlist.get(i).jianshu = data.getExtras().getString("jianshu");
                        goodlist.get(i).hanliang = data.getExtras().getString("hanliang");
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
                AddGoods addGoods = new AddGoods();
                addGoods.daima = data.getExtras().getString("daima");
                addGoods.jianshu = data.getExtras().getString("jianshu");
                addGoods.hanliang = data.getExtras().getString("hanliang");
                addGoods.mingzi = data.getExtras().getString("mingzi");
                addGoods.xuhao = goodlist.size() + 1;
                goodlist.add(addGoods);
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

                            if (daima.length() < 11) {
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
}
