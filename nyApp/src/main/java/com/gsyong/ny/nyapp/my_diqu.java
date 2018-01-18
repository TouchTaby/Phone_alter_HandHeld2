package com.gsyong.ny.nyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017-08-19.
 */

public class my_diqu extends LinearLayout {

    Spinner spinner1 = null;
    Spinner spinner2 = null;
    Spinner spinner3 = null;
    Spinner spinner4 = null;


    List<String> list_Pro = null;
    List<Integer> list_ProID=null;
    List<String> list_City = null;
    List<Integer> list_CityID=null;
    List<String> list_Dis = null;
    List<String> list_Tow = null;
    List<Integer> list_DisID=null;
    List<Integer> list_TowID=null;

    public Integer getProvinceID()
    {
        try{
            Integer id = spinner1.getSelectedItemPosition();
            id = list_ProID.get(id);
            return id;
        }catch (Exception e) {
            Log.d("getProvinceID",e.getMessage());
            return helper._loginProvinceID;
        }
    }

    public Integer getCityID()
    {
        try{
            Integer id = spinner2.getSelectedItemPosition();
            id = list_CityID.get(id);
            return id;
        }catch (Exception e) {
            Log.d("getCityID",e.getMessage());
            return helper._loginCountyID;
        }

    }

    public Integer getDistrictID()
    {
        try{
            Integer id = spinner3.getSelectedItemPosition();
            id = list_DisID.get(id);
            return id;
        }catch (Exception e) {
            Log.d("getDistrictID",e.getMessage());
            return helper._loginCountyID;
        }


    }

    public Integer getTownshipID()
    {
        try{
            Integer id = spinner4.getSelectedItemPosition();
            id = list_TowID.get(id);
            return id;
        }catch (Exception e) {
            Log.d("getTownshipID",e.getMessage());
            return helper._loginTownshipID;
        }


    }

    public my_diqu(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.my_diqu, this);
        list_Pro = new ArrayList<String>();
        list_ProID = new ArrayList<Integer>();
        list_City = new ArrayList<String>();
        list_CityID=new ArrayList<Integer>();
        list_Dis = new ArrayList<String>();
        list_DisID=new ArrayList<Integer>();
        list_Tow = new ArrayList<String>();
        list_TowID=new ArrayList<Integer>();


        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        spinner4 = (Spinner) findViewById(R.id.spinner4);


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if(position == 0)
                {
                    chongzhi_1();
                    chongzhi_2();
                    chongzhi_3();
                    return;
                }

                new Thread(networkTask_City).start();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if(position == 0)
                {
                    chongzhi_2();
                    chongzhi_3();
                    return;
                }
                //Toast.makeText(getContext(), list_CityID.get(position).toString()+" - "+list_City.get(position), Toast.LENGTH_LONG).show();
                new Thread(networkTask_District).start();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                //Toast.makeText(getContext(), "spinner3", Toast.LENGTH_LONG).show();
                if(position == 0)
                {

                    chongzhi_3();
                    return;
                }
                new Thread(networkTask_Township).start();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        new Thread(networkTask_Province).start();
    }

    private void chongzhi_1()
    {
        list_City.clear();
        list_CityID.clear();
        list_City.add("城市");
        list_CityID.add(0);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, list_City);
        spinner2.setAdapter(aa);
    }

    private void chongzhi_2()
    {
        list_Dis.clear();
        list_DisID.clear();
        list_Dis.add("区县");
        list_DisID.add(0);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, list_Dis);
        spinner3.setAdapter(aa);
    }

    private void chongzhi_3()
    {
        list_Tow.clear();
        list_TowID.clear();
        list_Tow.add("乡镇街道");
        list_TowID.add(0);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, list_Tow);
        spinner4.setAdapter(aa);
    }

    protected void resultProvince(String jsonstr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonstr);
            String status = jsonObj.getString("status");
            if(status.equals("y")) {
                list_Pro.clear();
                list_ProID.clear();

                JSONArray provincelist = jsonObj.getJSONArray("provincelist");
                Integer index = 0;
                for (int i = 0;i<provincelist.length();i++)
                {
                    JSONObject jsonObj1 = ((JSONObject)provincelist.opt(i));
                    String s = jsonObj1.getString("_provincename");
                    list_Pro.add(s);
                    list_ProID.add(jsonObj1.getInt("_provinceid"));
                    if(helper._loginProvinceID != 0)
                        if(jsonObj1.getInt("_provinceid") == helper._loginProvinceID){
                            index = i;
                        }

                }

                ArrayAdapter<String> aa = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, list_Pro);
                spinner1.setAdapter(aa);
                spinner1.setSelection(index);
                if(index != 0)
                    spinner1.setEnabled(false);
            }
            else
            {
                Toast.makeText(getContext(), "数据解析失败", Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException e) {
            System.out.println("Json parse error");
            e.printStackTrace();
        }
    }

    protected void resultCity(String jsonstr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonstr);
            String status = jsonObj.getString("status");
            if(status.equals("y")) {
                list_City.clear();
                list_CityID.clear();

                JSONArray provincelist = jsonObj.getJSONArray("citylist");
                Integer index = 0;
                for (int i = 0;i<provincelist.length();i++)
                {
                    JSONObject jsonObj1 = ((JSONObject)provincelist.opt(i));
                    String s = jsonObj1.getString("_cityname");
                    list_City.add(s);
                    list_CityID.add( jsonObj1.getInt("_cityid"));
                    if(helper._loginCityID != 0)
                        if(jsonObj1.getInt("_cityid") == helper._loginCityID){
                            index = i;
                        }
                }

                ArrayAdapter<String> aa = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, list_City);
                spinner2.setAdapter(aa);
                spinner2.setSelection(index);
                if(index != 0)
                    spinner2.setEnabled(false);
            }
            else
            {
                Toast.makeText(getContext(), "数据解析失败", Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException e) {
            System.out.println("Json parse error");
            e.printStackTrace();
        }
    }

    protected void resultDistrict(String jsonstr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonstr);
            String status = jsonObj.getString("status");
            if(status.equals("y")) {
                list_Dis.clear();
                list_DisID.clear();

                JSONArray provincelist = jsonObj.getJSONArray("districtlist");
                Integer index= 0;
                for (int i = 0;i<provincelist.length();i++)
                {
                    JSONObject jsonObj1 = ((JSONObject)provincelist.opt(i));
                    String s = jsonObj1.getString("_districtname");
                    list_Dis.add(s);
                    list_DisID.add( jsonObj1.getInt("_districtid"));
                    if(helper._loginCountyID != 0)
                        if(jsonObj1.getInt("_districtid") == helper._loginCountyID){
                            index = i;
                        }
                }

                ArrayAdapter<String> aa = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, list_Dis);
                spinner3.setAdapter(aa);
                spinner3.setSelection(index);
                if(index != 0)
                    spinner3.setEnabled(false);
            }
            else
            {
                Toast.makeText(getContext(), "数据解析失败", Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException e) {
            Toast.makeText(getContext(), "异常:"+e.getMessage(), Toast.LENGTH_LONG).show();
            System.out.println("Json parse error");
            e.printStackTrace();
        }
    }

    protected void resultTownship(String jsonstr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonstr);
            String status = jsonObj.getString("status");
            if(status.equals("y")) {
                list_Tow.clear();
                list_TowID.clear();
                Integer index = 0;
                JSONArray provincelist = jsonObj.getJSONArray("townshiplist");
                for (int i = 0;i<provincelist.length();i++)
                {
                    JSONObject jsonObj1 = ((JSONObject)provincelist.opt(i));
                    String s = jsonObj1.getString("_townshipname");
                    list_Tow.add(s);
                    list_TowID.add( jsonObj1.getInt("_townshipid"));
                    if(helper._loginTownshipID != 0)
                        if(jsonObj1.getInt("_townshipid") == helper._loginTownshipID){
                            index = i;
                        }
                }

                ArrayAdapter<String> aa = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, list_Tow);
                spinner4.setAdapter(aa);
                spinner4.setSelection(index);
                if(index != 0)
                    spinner4.setEnabled(false);
            }
            else
            {
                Toast.makeText(getContext(), "数据解析失败", Toast.LENGTH_LONG).show();
            }
        }
        catch (JSONException e) {
            System.out.println("Json parse error");
            e.printStackTrace();
        }
    }

    protected void resultError(String jsonstr)
    {
        Toast.makeText(getContext(), jsonstr, Toast.LENGTH_LONG).show();

    }

    /**
     * 网络操作相关的子线程 获取省份数据
     */
    Runnable networkTask_Province = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            try {

                String Url = getResources().getString(R.string.Webservice_Url_mobileinterface);
                HashMap<String, String> var = new HashMap<String, String>();
                var.put("action", "getProvince");
                String str = PostRequest.sendPostRequest(Url, var, null);
                Message msg = new Message();
                msg.what = 11;
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

    /**
     * 网络操作相关的子线程 获取城市数据
     */
    Runnable networkTask_City = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            try {
                Integer pro_id = spinner1.getSelectedItemPosition();
                pro_id = list_ProID.get(pro_id);

                String Url = getResources().getString(R.string.Webservice_Url_mobileinterface);
                HashMap<String, String> var = new HashMap<String, String>();
                var.put("action", "getCity");
                var.put("ProvinceID", pro_id.toString());
                String str = PostRequest.sendPostRequest(Url, var, null);
                Message msg = new Message();
                msg.what = 12;
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

    Runnable networkTask_District = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            try {
                Integer pro_id = spinner2.getSelectedItemPosition();
                pro_id = list_CityID.get(pro_id);

                String Url = getResources().getString(R.string.Webservice_Url_mobileinterface);
                HashMap<String, String> var = new HashMap<String, String>();
                var.put("action", "getDistrict");
                var.put("CityID", pro_id.toString());
                String str = PostRequest.sendPostRequest(Url, var, null);
                Message msg = new Message();
                msg.what = 13;
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

    Runnable networkTask_Township = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            try {
                Integer pro_id = spinner3.getSelectedItemPosition();
                pro_id = list_DisID.get(pro_id);

                String Url = getResources().getString(R.string.Webservice_Url_mobileinterface);
                HashMap<String, String> var = new HashMap<String, String>();
                var.put("action", "getTownship");
                var.put("DistrictID", pro_id.toString());
                String str = PostRequest.sendPostRequest(Url, var, null);
                Message msg = new Message();
                msg.what = 14;
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
                case 11:
                    resultProvince(data);
                    break;
                case 12:
                    resultCity(data);
                    break;
                case 13:
                    resultDistrict(data);
                    break;
                case 14:
                    resultTownship(data);
                    break;
                case 2:
                    resultError(data);
                    break;
                default:
                    break;
            }
        }

    };
}
