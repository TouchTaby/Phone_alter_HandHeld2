package com.gsyong.ny.nyapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-12-03.
 */

public class act_rulicp_list extends AppCompatActivity {

    my_toptitle toptitle;
    List<act_rulicp_list.addGoods> gslist = new ArrayList<act_rulicp_list.addGoods>();
    ListView tableListView;
    act_rulicp_list.TableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_rulicp_list);
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        toptitle = (my_toptitle) findViewById(R.id.toptitle);
        toptitle.setText("入库列表");
        toptitle.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //设置表格标题的背景颜色
        ViewGroup tableTitle = (ViewGroup) findViewById(R.id.table_title);
        tableTitle.setBackgroundColor(Color.rgb(177, 173, 172));

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String uid = bundle.getString("uid");

        String sqlstr = "select daima,ylint2,ylint3,ylstr1 from dt_ruku_cp where listUid = '" + uid + "'";
        DBHelper db = new DBHelper(this);
        Cursor cursor = db.getWritableDatabase().rawQuery(sqlstr, null);
        if (cursor.moveToNext()) {
            act_rulicp_list.addGoods gs = new act_rulicp_list.addGoods();
            gs.mingzi = cursor.getString(3);
            gs.hanliang = cursor.getString(1);
            gs.jianshu = cursor.getString(2);
            gs.daima = cursor.getString(0);
            gs.xuhao = gslist.size() + 1;
            gslist.add(gs);
        }

        tableListView = (ListView) findViewById(R.id.list);

        adapter = new act_rulicp_list.TableAdapter(this, gslist);
        tableListView.setAdapter(adapter);
    }





    public class TableAdapter extends BaseAdapter {
        private List<act_rulicp_list.addGoods> datas;
        private LayoutInflater inflater;

        public TableAdapter(Context context, List<act_rulicp_list.addGoods> data) {
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

            act_rulicp_list.addGoods goods = (act_rulicp_list.addGoods) this.getItem(position);

            act_rulicp_list.ViewHolder viewHolder;

            if (convertView == null) {

                viewHolder = new act_rulicp_list.ViewHolder();

                convertView = inflater.inflate(R.layout.item_rukuliebiao, null);
                viewHolder.text_xuhao = (TextView) convertView.findViewById(R.id.text_xuhao);
                viewHolder.text_mingzi = (TextView) convertView.findViewById(R.id.text_mingzi);
                viewHolder.text_hanliang = (TextView) convertView.findViewById(R.id.text_hanliang);
                viewHolder.text_jianshu = (EditText) convertView.findViewById(R.id.text_jianshu);
                viewHolder.text_code = (TextView) convertView.findViewById(R.id.text_code);
                viewHolder.text_jianshu.setEnabled(true);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (act_rulicp_list.ViewHolder) convertView.getTag();
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

        public addGoods() {
        }

        public addGoods(Integer xuhao, String mingzi, String hanliang, String jianshu, String daima) {
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
}
