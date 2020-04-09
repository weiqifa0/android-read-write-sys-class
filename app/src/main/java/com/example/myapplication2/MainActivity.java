package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final String EM20918_PATH_PS = "/sys/class/em20918/control/ps";
    private static final String EM20918_PATH_CONFIG = "/sys/class/em20918/control/config";
    private static final String TAG = "EM20918";
    private int TIME = 200;
    private TextView txtShow;
    private TextView txtShow2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtShow=(TextView)findViewById(R.id.textview01);
        txtShow2=(TextView)findViewById(R.id.textview02);
        findViewById(R.id.btn_click_one).setOnClickListener(onClickListener);
        Log.w(TAG, "Start...");

        /*启动定时器*/
        handler.postDelayed(runnable, TIME); //每隔1s执行
    }

    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v){
            //Toast.makeText(MainActivity.this,"Button点击事件1",Toast.LENGTH_LONG).show();
            String readt = "";
            writeSysFile(EM20918_PATH_CONFIG,"0xB8");
            readt = readFile(EM20918_PATH_CONFIG);
            txtShow2.setText(readt);
        }
    };

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                handler.postDelayed(this, TIME);
                System.out.println("do...");

                //Log.w(MainActivity.TAG, " time---");
                String readt = "";
                readt = readFile(EM20918_PATH_PS);
                txtShow.setText(readt);
                readt = readFile(EM20918_PATH_CONFIG);
                txtShow2.setText(readt);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("exception...");
            }
        }
    };

    public static void writeSysFile(String sys_path,String str){

        Process p = null;
        DataOutputStream os = null;
        try {
            p = Runtime.getRuntime().exec("sh");
            os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("echo 0x80 > "+sys_path + "\n");
            os.writeBytes("exit\n");
            os.flush();
            Log.e(MainActivity.TAG, " write success" + sys_path);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(MainActivity.TAG, " can't write " + sys_path+e.getMessage());
        } finally {
            if(p != null){
                p.destroy();
            }
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //sys_path 为节点映射到的实际路径
    public static String readFile(String sys_path) {
        String prop = "waiting";// 默认值
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(sys_path));
            prop = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(MainActivity.TAG, " ***ERROR*** Here is what I know: " + e.getMessage());
        } finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //Log.w(MainActivity.TAG, "readFile cmd from"+sys_path + "data"+" -> prop = "+prop);
        return prop;
    }

    //sys_path 为节点映射到的实际路径
    public static String read(String sys_path){

        Log.w(TAG, "read Start..." + sys_path);
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("cat " + sys_path); // 此处进行读操作
            InputStream is = process.getInputStream();
            Log.w(TAG, "read try Start...");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            Log.w(TAG, "read try2 Start...");
            String line ;
            while (null != (line = br.readLine())) {
                Log.w(TAG, "read readLine Start...");
                Log.w(TAG, "read data ---> " + line);
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "*** ERROR *** Here is what I know: " + e.getMessage());
        }
        return null;
    }
}
