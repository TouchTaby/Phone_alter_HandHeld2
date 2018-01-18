package com.gsyong.ny.nyapp.utlis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gsyong.ny.nyapp.common.Constant;
import com.thoughtworks.xstream.XStream;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by administrator on 2018-01-16.
 */

public class WebService {
    public static String TAG = "TAG-WebService";

    public static String zhucebangding(String zhucema, String pcmac) {
        //注册码绑定
        SoapObject rpc = new SoapObject(Constant.serverNAMESPACE, "zhucemabangding");
        rpc.addProperty("zhucema", zhucema);
        rpc.addProperty("pcmac", pcmac);
        Log.e(TAG, "zhucebangding:  IMEI  " + pcmac + "注册码：" + zhucema);
        return sendAndRequestInfo("zhucemabangding", rpc);
    }

    public static String getMAC(Context context) {
        /**
         * 获取 IMEI号
         */
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        assert tm != null;
        @SuppressLint({"MissingPermission", "HardwareIds"}) String imei = tm.getDeviceId();
        Log.e("TAG", "getMAC: " + imei);
        return imei;
    }

    public static boolean syn_rukucp(String dt_ruku_cp) {
        //入库商品信息
        SoapObject rpc = new SoapObject(Constant.serverNAMESPACE, "Syn_rukudan");
        rpc.addProperty("var_Syn_rukucp", dt_ruku_cp);
        return upLoadCp("Syn_rukudan", rpc);
    }

    private static boolean upLoadCp(String methodName, SoapObject rpc) {
        boolean result = false;
        try {
            final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(Constant.serverURL);
            ht.debug = true;
            ht.call(Constant.serverNAMESPACE + methodName, envelope);
//            SoapObject soapObject = (SoapObject) envelope.bodyIn;
            String d = (String) envelope.getResponse();
//            Log.e(TAG, "run: 上传结果：" + soapObject.getProperty(0).equals("true"));
//            Log.e(TAG, "run: 上传结果```：" + soapObject.getProperty(0).toString());
            Log.e(TAG, "run: 上传结果`d`：" + d);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static String sendAndRequestInfo(String methodName, SoapObject rpc) {
        String detail = null;
        try {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(rpc);
            HttpTransportSE ht = new HttpTransportSE(Constant.serverURL);
            ht.debug = true;
            ht.call(Constant.serverNAMESPACE + methodName, envelope);
            SoapObject soapObject = (SoapObject) envelope.bodyIn;
            Log.e(TAG, "sendAndRequestInfo:注册结果： "+soapObject.getProperty(0).toString() );
            detail = soapObject.getProperty(0).toString();
            XStream xStream = new XStream();

        } catch (Exception e) {
            Log.i("Tag", e.toString());
        }
        return detail;
    }

}
