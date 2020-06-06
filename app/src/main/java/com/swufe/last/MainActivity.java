package com.swufe.last;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements Runnable {
    EditText school;
    ListView result;
    private final String TAG = "SchoolName";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        school = (EditText)findViewById(R.id.school);
        result = (ListView) findViewById(R.id.result);

        Thread t = new Thread(this);
        t.start();

        Log.i(TAG, "onClick: ");
        String str = school.getText().toString();
        Log.i(TAG, "onClick: get str=" + str);




        if(str.length()>0){
          //return str;//有问题
        }else{
            //用户没有输入内容
            Toast.makeText(this, "请输入大学全称", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "onClick: str=" + str);
    }


    @Override
    public void run() {

        Log.i(TAG,"run:run()....");

        try {

            Thread.sleep(1000);

        } catch (InterruptedException e) {

            e.printStackTrace();

        }


//获取网络数据，放入 List 带回到主线程中

        Document doc = null;

        Vector<String> list=new Vector<String>();


        try {
            doc = Jsoup.connect("http://www.chinakaoyan.com/info/list/ClassID/22/pagenum/1.shtml").get(); Log.i(TAG,"run:"+doc.title());
            //获取 a 中的数据
            int count=0;
            Elements tds = doc.getElementsByTag("li");
            Log.i(TAG,"run:"+doc.title());

            for(int i=0;i<tds.size();i+=8){
                Element td1 = tds.get(i);
                //Element td2 = tds.get(i+5);
                //String str1 = td1.text();
                //String val = td2.text();
                list.add(count,td1.text()+"#http://www.chinakaoyan.com"+(td1.attr("href")));
                Log.i(TAG,"run:"+list.get(count));
                count++;
            }

            } catch (IOException ex) {
            ex.printStackTrace();
            Log.i(TAG,"run:请检查网络，如果网络没问题则说明网页已改变，那么请修改解析网页源代码 ");
        }



/*
//获取 Message 对象，用于返回主线程

        Message msg=handler.obtainMessage(7);

        msg.what=5;

//msg.obj="Hello from run()";

        msg.obj=list;

        handler.sendMessage(msg);


    }

    处理子线程传回的数据

            handler=new Handler(){

        @Override

        public void handleMessage(@NonNull Message msg) {

            if(msg.what==5){

                Vector<String> data_list=(Vector<String>) msg.obj;


//将新的汇率值保存到 SP 中

                SharedPreferences sp=getSharedPreferences("mySwufeData", Activity.MODE_PRIVATE); SharedPreferences.Editor editor=sp.edit(); data=new String[data_list.size()];

                for(int m=0;m<data_list.size();m++){

                    data[m]=data_list.get(m);

                    editor.putString(""+m,data_list.get(m));

                }

                editor.putInt("recordCount",data_list.size());

                editor.putString("updateTime",OSTime);

                editor.commit();


                Log.i(Tag,"onActivityResult:handlerMessage:updateTime="+OSTime);

                Log.i(Tag,"onActivityResult:handlerMessage:committing of rate finished");


                Toast.makeText(SwufeInfoActivity.this,"Data has

                        updated",Toast.LENGTH_SHORT).show();

            }

            super.handleMessage(msg);
*/

        }



}
