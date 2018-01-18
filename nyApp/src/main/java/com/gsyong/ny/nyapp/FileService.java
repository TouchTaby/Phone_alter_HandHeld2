package com.gsyong.ny.nyapp;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class FileService {
    private Context context;

    public FileService(Context context){
        this.context = context;
    }

    public boolean saveToRom(String str,String filename) throws Exception{
        //以私有的方式打开一个文件
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        fos.write(str.getBytes());
        fos.flush();
        fos.close();
        return true;
    }


    public String loadToRom(String filename)throws Exception {
        try {
            FileInputStream inStream= context.openFileInput(filename);
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            byte[] buffer=new byte[1024];
            int length=-1;
            while((length=inStream.read(buffer))!=-1)   {
                stream.write(buffer,0,length);
            }
            stream.close();
            inStream.close();
            return stream.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";

        }
        catch (IOException e){
            return "";
        }
    }
}
