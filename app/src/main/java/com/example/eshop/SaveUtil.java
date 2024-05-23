package com.example.eshop;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class SaveUtil {

    private String state = Environment.getExternalStorageState();

    public void saveUser(String username,String password,int auto){
        if(state.equals(Environment.MEDIA_MOUNTED)){
            File SDPath = Environment.getExternalStorageDirectory();
            File file = new File(SDPath,"data.txt");
            FileOutputStream fos = null;
            String data = username + " " + password + " " + auto;
            try{
                fos = new FileOutputStream(file);
                fos.write(data.getBytes());
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try{
                    if (fos!= null){
                        fos.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
