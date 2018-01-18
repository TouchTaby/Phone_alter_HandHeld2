package com.gsyong.ny.nyapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.gsyong.ny.nyapp.Model.item_rukuchaxun;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Administrator on 2017-11-30.
 */

public class act_rukuchaxun extends AppCompatActivity {

    EditText strattime,endtime,zhuangtai;
    Button btn1;

    Integer riqi=0;//告诉选择器是赋值给哪个日期

    PtrClassicFrameLayout ptrClassicFrameLayout;
    RecyclerView mRecyclerView;
    private act_rukuchaxun.RecyclerAdapter adapter;
    private RecyclerAdapterWithHF mAdapter;
    Handler handler = new Handler();
    List<item_rukuchaxun> alist = new ArrayList<item_rukuchaxun>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_rukuchaxun);

        iniActivity();
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

        my_toptitle toptitle = (my_toptitle) findViewById(R.id.toptitle);
        toptitle.setText("入库查询");
        toptitle.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn1 = (Button)findViewById(R.id.btnchaxun);
        strattime = (EditText) findViewById(R.id.startTime);
        endtime = (EditText) findViewById(R.id.endTime);
        zhuangtai = (EditText) findViewById(R.id.zhuangtai);
        strattime.setInputType(InputType.TYPE_NULL); //不显示系统输入键盘</span>
        endtime.setInputType(InputType.TYPE_NULL); //不显示系统输入键盘</span>
        zhuangtai.setInputType(InputType.TYPE_NULL); //不显示系统输入键盘</span>

        btn1.setFocusable(true);
        btn1.setFocusableInTouchMode(true);
        btn1.requestFocus();
        btn1.requestFocusFromTouch();
        //绑定事件
        strattime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (hasFocus) {
                    riqi = 0;
                    showDatePickerDialog();
                }
            }
        });

        strattime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                riqi = 0;
                showDatePickerDialog();
            }
        });
        //绑定事件
        endtime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (hasFocus) {
                    riqi = 1;
                    showDatePickerDialog();
                }
            }
        });

        endtime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                riqi = 1;
                showDatePickerDialog();
            }
        });

        //绑定事件
        zhuangtai.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (hasFocus) {
                    showWeiZhangDialog();
                }
            }
        });

        zhuangtai.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showWeiZhangDialog();
            }
        });

        //入库操作
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetData();
            }
        });

        init();
    }


    private void init() {

        mRecyclerView =(RecyclerView)findViewById(R.id.recyclerView1);
        ptrClassicFrameLayout = (PtrClassicFrameLayout) this.findViewById(R.id.test_recycler_view_frame);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        //设置LayoutMananger
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        adapter = new act_rukuchaxun.RecyclerAdapter(this, alist);
        mAdapter = new RecyclerAdapterWithHF(adapter);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerAdapterWithHF.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerAdapterWithHF adapter, RecyclerView.ViewHolder vh, int position) {
                act_rukuchaxun.ChildViewHolder holder = (act_rukuchaxun.ChildViewHolder) vh;
                if(holder.tview3.getText().equals("未入库")){
                    Intent intent = new Intent(getBaseContext(),act_rukucaozuo.class);
                    intent.putExtra("uid",holder.tview4.getText().toString());
                    startActivityForResult(intent, 1);
                }
                else
                {
                    Intent intent = new Intent(getBaseContext(),act_rulicp_list.class);
                    intent.putExtra("uid",holder.tview4.getText().toString());
                    startActivity(intent);
                }


            }
        });
        ptrClassicFrameLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                ptrClassicFrameLayout.autoRefresh(true);
            }
        }, 150);

        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetData();
                    }
                }, 500);
            }
        });

        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                       // GetData();
                    }
                }, 500);
            }
        });
    }

    void GetData(){
        String t1 = "",t2 = "",zt="";
        t1 = strattime.getText().toString();
        t2 =endtime.getText().toString();
        zt = zhuangtai.getText().toString();
        String sqlstr = "select uid,addtime,shuliang,ylint1 from dt_ruku_list where 1=1";
        List<String> obj = new ArrayList<String>();
        obj.add("1");
        if(t1.length() > 0) {
            obj.add(t1+" 00:00:00");
            sqlstr += " and addtime > ?";
        }

        if(t2.length() > 0) {
            obj.add(t2+" 23:59:59");
            sqlstr += " and addtime < ?";
        }

        if(zt.length() > 0){
            switch (zt){
                case "已入库":
                    sqlstr += " and ylint1 = 1";
                    break;
                case "未入库":
                    sqlstr += " and ylint1 = 0";
                    break;
                default:break;
            }
        }
        sqlstr += " order by addtime desc";
        DBHelper db = new DBHelper(this);
//        Cursor cursor = db.getWritableDatabase().rawQuery(sqlstr,obj.toArray(new String[obj.size()]));
        Cursor cursor = db.getWritableDatabase().rawQuery(sqlstr,null);
        Log.d("sql",sqlstr);
        alist.clear();
        while (cursor.moveToNext()) {

            item_rukuchaxun item = new item_rukuchaxun();
            Log.d("sql",cursor.getString(0));
            item.id =  cursor.getString(0) == null ?"":cursor.getString(0);
            item.shijian = cursor.getString(1) == null ?"":cursor.getString(1);
            item.shuliang = cursor.getString(2) == null ?"":cursor.getString(2);
            Integer zt1 =  cursor.getInt(3);
            item.zhuangtai = zt1==0?"未入库":"已入库";
            Integer sl = alist.size();
            sl++;
            item.xuhao =sl.toString();
            alist.add(item);
        }

        mAdapter.notifyDataSetChanged();
        ptrClassicFrameLayout.refreshComplete();
        ptrClassicFrameLayout.setLoadMoreEnable(false);
    }

    /**
     * 展示日期选择对话框
     */
    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(act_rukuchaxun.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                if(riqi == 0)
                    strattime.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                else
                    endtime.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void showWeiZhangDialog() {
        final String[] liststr = new String[]{"全部","已入库","未入库"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("选择入库状态");//设置标题
        //builder.setIcon(R.drawable.ic_launcher);//设置图标
        builder.setItems(liststr,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(),"我点击了"+liststr[which],Toast.LENGTH_SHORT).show();
                zhuangtai.setText(liststr[which]);
            }
        });
        AlertDialog dialog=builder.create();//获取dialog
        dialog.show();//显示对话框
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        GetData();
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<item_rukuchaxun> datas;
        private LayoutInflater inflater;

        public RecyclerAdapter(Context context, List<item_rukuchaxun> data) {
            super();
            inflater = LayoutInflater.from(context);
            datas = data;
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            act_rukuchaxun.ChildViewHolder holder = (act_rukuchaxun.ChildViewHolder) viewHolder;
            holder.tview.setText(datas.get(position).xuhao.toString());
            holder.tview1.setText(datas.get(position).shijian);
            holder.tview2.setText(datas.get(position).shuliang);
            holder.tview3.setText(datas.get(position).zhuangtai);
            holder.tview4.setText(datas.get(position).id);

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewHolder, int position) {
            View view = inflater.inflate(R.layout.item_rukuchaxun, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new act_rukuchaxun.ChildViewHolder(view);
        }



    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tview,tview1,tview2,tview3,tview4;


        public ChildViewHolder(View view) {
            super(view);
            tview = (TextView) view.findViewById(R.id.rkcx_View);
            tview1 = (TextView) view.findViewById(R.id.rkcx_View1);
            tview2 = (TextView) view.findViewById(R.id.rkcx_View2);
            tview3 = (TextView) view.findViewById(R.id.rkcx_View3);
            tview4 = (TextView) view.findViewById(R.id.rkcx_View4);
        }



    }
}
