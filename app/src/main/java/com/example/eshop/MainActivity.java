package com.example.eshop;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private EditText et_user;
    private EditText et_passwd;
    private SaveUtil saveUtil;
    private CheckBox cb_RbPasswd;
    private CheckBox cb_ALogin;
    private static final int MSG_UPDATE_DATA = 1;
    private Handler msgHandler ;
    private List<Goods> goodsList;
    private RecyclerView recyclerView;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private GoodsAdapter mAdapter;
    private final static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        verifyStoragePermissions(MainActivity.this);

        String admin_user = "admin";
        String admin_password = "admin";
        cb_RbPasswd = (CheckBox) findViewById(R.id.cb_RbPasswd);
        cb_ALogin = (CheckBox) findViewById(R.id.cb_autologin);

        Button bt_login = findViewById(R.id.bt_login);
        saveUtil = new SaveUtil();
        et_user = findViewById(R.id.et_user);
        et_passwd = findViewById(R.id.et_passwd);




        File SDPath = Environment.getExternalStorageDirectory();
        File file = new File(SDPath,"data.txt");
        FileInputStream fileInputStream  = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = null;
            while ((line = bufferedReader.readLine())!=null){
                cb_RbPasswd.setChecked(true);
                String name = line.split(" ")[0];
                String password = line.split(" ")[1];
                int auto = Integer.parseInt(line.split(" ")[2]);

                System.out.println("用户名"+name+password);
                et_user.setText(name);
                et_passwd.setText(password);
                if (auto==1){
                    cb_ALogin.setChecked(true);
                    Toast.makeText(MainActivity.this,"自动登录",Toast.LENGTH_SHORT).show();
                    System.out.println("自动登录");
                    index();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bt_login.setOnClickListener(view -> {

            String username = et_user.getText().toString();
            String passwd = et_passwd.getText().toString();
            if (username.equals(admin_user) && passwd.equals(admin_password)) {
                int auto = 0;
                if (cb_RbPasswd.isChecked()) {
                    if (cb_ALogin.isChecked()){
                        auto=1;
                    }
                    saveUtil.saveUser(username,passwd,auto);
//                    SharedPreferences.Editor editor = getSharedPreferences("users", MODE_PRIVATE).edit();
//                    editor.putString("username", username);
//                    editor.putString("passwd", passwd);
//                    editor.putBoolean("save", true);
//                    editor.putBoolean("auto", true);
//                    editor.apply();
                }
                System.out.println("成功");
                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                index();


            } else {
                System.out.println("登录失败");
                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void index() {
        setContentView(R.layout.activity_main);
        // 获取ListView实例
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 创建并设置适配器
        mAdapter = new GoodsAdapter(this,goodsList);
        recyclerView.setAdapter(mAdapter);
        msgHandler= new Handler(message -> {
            if (message.what == MSG_UPDATE_DATA){
                mAdapter.notifyDataSetChanged();
            }
            return true;
        });
        fetchGoodsData();
    }
    private void fetchGoodsData(){
        new Thread(() -> {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://10.254.31.49:8080/eshop/goods_list_data.json").build();
                Response response = okHttpClient.newCall(request).execute();
                if(response.isSuccessful()){
                    String jsonData = response.body().string();
                    Log.d("Network",jsonData);
                    System.out.println("网络"+jsonData);
                    Gson gson = new Gson();
                    Goods[] goods = gson.fromJson(jsonData,Goods[].class);

                    for (Goods good : goods) {
                        good.setImg("http://10.254.31.49:8080/eshop"+good.getImg());
                    }
                    goodsList = Arrays.asList(goods);
                    msgHandler.sendEmptyMessage(MSG_UPDATE_DATA);

                    runOnUiThread(() -> {
                        mAdapter = new GoodsAdapter(MainActivity.this,goodsList);
                        recyclerView.setAdapter(mAdapter);
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }).start();
    }


}

