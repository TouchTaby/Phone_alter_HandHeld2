package com.gsyong.ny.nyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.gsyong.ny.nyapp.Model.item_loginqiyelist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017-11-22.
 */

public class act_qiye_list extends AppCompatActivity {

    my_toptitle toptitle;
    PtrClassicFrameLayout ptrClassicFrameLayout;
    RecyclerView mRecyclerView;
    private RecyclerAdapter adapter;
    private RecyclerAdapterWithHF mAdapter;
    Handler handler = new Handler();

    Integer ProvinceID;
    Integer CityID;
    Integer DistrictID;
    Integer TownshipID;

    Integer recordCount = 0;
    Integer pageIndex = 0;
    Integer pageSize = 15;

    String qiye;

    List<item_loginqiyelist> alist = new ArrayList<item_loginqiyelist>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_qiye_list);
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        mRecyclerView =(RecyclerView)findViewById(R.id.recyclerView1);
        ptrClassicFrameLayout = (PtrClassicFrameLayout) this.findViewById(R.id.test_recycler_view_frame);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        //设置LayoutMananger
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));


        toptitle = (my_toptitle) findViewById(R.id.toptitle);
        toptitle.setText("企业列表");
        toptitle.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        ProvinceID = bundle.getInt("ProvinceID");
        CityID = bundle.getInt("CityID");
        DistrictID = bundle.getInt("DistrictID");
        TownshipID = bundle.getInt("TownshipID");
        qiye = bundle.getString("qiye");
        init();
    }


    private void init() {
        adapter = new RecyclerAdapter(this, alist);
        mAdapter = new RecyclerAdapterWithHF(adapter);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerAdapterWithHF.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerAdapterWithHF adapter, RecyclerView.ViewHolder vh, int position) {
                ChildViewHolder holder = (ChildViewHolder) vh;
                //Toast.makeText(act_qiye_list.this, "点击了" + position + " - " + holder.id.getText() + " - " + holder.names.getText(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("result", holder.names.getText());
                //设置返回数据
                act_qiye_list.this.setResult(RESULT_OK, intent);
                //关闭Activity
                act_qiye_list.this.finish();
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

                        new Thread(networkTask_qiyelist).start();
                    }
                }, 1500);
            }
        });

        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        new Thread(networkTask_qiyelist).start();

                    }
                }, 1000);
            }
        });
    }

    Runnable networkTask_qiyelist = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            try {
                pageIndex++;
                String Url = getResources().getString(R.string.Webservice_Url_nongyao);
                HashMap<String, String> var = new HashMap<String, String>();
                var.put("action", "GetQiYeList");
                var.put("hi_Province", ProvinceID.toString());
                var.put("hi_City", CityID.toString());
                var.put("hi_County", DistrictID.toString());
                var.put("hi_Township", TownshipID.toString());
                var.put("keyWord", qiye);

                var.put("pageIndex", pageIndex.toString());
                var.put("pageSize", pageSize.toString());


                String str = PostRequest.sendPostRequest(Url, var, null);
                Message msg = new Message();
                msg.what = 9;
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

    private void result_data(String jsonstr)
    {
        try {
            JSONObject jsonObj = new JSONObject(jsonstr);
            JSONArray jsonArray = jsonObj.getJSONArray("models");

            for (int i = 0; i < jsonArray.length(); i++) {
                item_loginqiyelist qy = new item_loginqiyelist();

                JSONObject jsonObj1 = ((JSONObject) jsonArray.opt(i));
                qy.setNames(jsonObj1.getString("_mingzi"));
                qy.setId(jsonObj1.getString("_id"));
                alist.add(qy);
            }

            pageIndex = jsonObj.getInt("pageIndex");
            pageSize = jsonObj.getInt("pageSize");
            recordCount = jsonObj.getInt("recordCount");

            mAdapter.notifyDataSetChanged();
            if(pageIndex == 1)
                ptrClassicFrameLayout.refreshComplete();
            else
                ptrClassicFrameLayout.loadMoreComplete(true);

            if(pageIndex * pageSize < recordCount)
                ptrClassicFrameLayout.setLoadMoreEnable(true);
            else
                ptrClassicFrameLayout.setLoadMoreEnable(false);

        }
        catch (JSONException e) {
            mAdapter.notifyDataSetChanged();
            ptrClassicFrameLayout.refreshComplete();
            if(pageIndex == 1)
                ptrClassicFrameLayout.refreshComplete();
            else
                ptrClassicFrameLayout.loadMoreComplete(true);

            Toast.makeText(this, "无符合要求的数据", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String data = (String)msg.obj;
            switch (msg.what) {
                case 9:
                    result_data(data);
                    break;
                default:
                    break;
            }
        }

    };

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<item_loginqiyelist> datas;
        private LayoutInflater inflater;

        public RecyclerAdapter(Context context, List<item_loginqiyelist> data) {
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
            ChildViewHolder holder = (ChildViewHolder) viewHolder;
            holder.names.setText(datas.get(position).getNames());
            holder.id.setText(datas.get(position).getId());


        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewHolder, int position) {
            View view = inflater.inflate(R.layout.item_loginqiyelist, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ChildViewHolder(view);
        }



    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView names;
        TextView id;

        public ChildViewHolder(View view) {
            super(view);
            names = (TextView) view.findViewById(R.id.names);
            id = (TextView) view.findViewById(R.id.qyid);
        }



    }



}
