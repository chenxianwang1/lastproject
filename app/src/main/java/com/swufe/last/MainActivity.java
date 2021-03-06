package com.swufe.last;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener {
    private EditText school;
    private ListView result;
    private Handler handler;
    private String data[];
    private final String TAG = "SchoolName";
    private SimpleAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        school = (EditText) findViewById(R.id.school);
        result = (ListView) findViewById(R.id.result);

        Thread t = new Thread(this);
        t.start();

        SharedPreferences sp = getSharedPreferences("kaoyanData", Activity.MODE_PRIVATE);
        int count = sp.getInt("recordCount", 0);//获取公告数
        data = new String[count];


        for (int i = 0; i < count; i++) {
            data[i] = sp.getString("" + i, "");
            Log.i(TAG, "onCreate:data:" + data[i].toString());
        }

        handler = new Handler() {




            public void handleMessage(@NonNull Message msg) {

                if (msg.what == 5) {
                    Vector<String> data_list = (Vector<String>) msg.obj;
                    SharedPreferences sp = getSharedPreferences("kaoyanData", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    data = new String[data_list.size()];
                    for (int m = 0; m < data_list.size(); m++) {
                        data[m] = data_list.get(m);
                        editor.putString("" + m, data_list.get(m));
                    }
                    editor.putInt("recordCount", data_list.size());
                    editor.commit();

                    Log.i(TAG, "onActivityResult:handlerMessage:committing of rate finished");


                    Toast.makeText(MainActivity.this, "数据已经更新", Toast.LENGTH_SHORT).show();

                }

                super.handleMessage(msg);

            }

        };

        school.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                List<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
                int flag = 0;
                for (int j = 0; j < data.length; j++) {
                    if (data[j].indexOf(s.toString()) != -1) {
                        flag = 1;
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("ItemTitle", data[j].substring(0, data[j].indexOf("#")));
                        map.put("ItemDetail", data[j].substring(data[j].indexOf("#") + 1));
                        dataList.add(map);
                    }
                }
                if (flag == 1) {
                    listAdapter = new SimpleAdapter(MainActivity.this, dataList,
                            R.layout.list_item,
                            new String[]{"ItemTitle", "ItemDetail"},
                            new int[]{R.id.itemTitle, R.id.itemDetail}
                    );
                    result.setAdapter(listAdapter);
                    result.setOnItemClickListener(MainActivity.this);

                } else {
                    Toast.makeText(MainActivity.this, "请输入正确的关键词 ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memorial,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            Intent config = new Intent(this,memorialActivity.class);
            startActivity(config);
        }else if(item.getItemId()==R.id.menu_set2){
            Intent config = new Intent(this,BookActivity.class);
            startActivity(config);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run() {
        Log.i(TAG, "run:run()....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //获取网络数据，放入 List 带回到主线程中
        Document doc = null;
        Vector<String> list = new Vector<String>();
        try {
            doc = Jsoup.connect("https://yz.chsi.com.cn/kyzx/zcdh/").get();
            Log.i(TAG, "run:" + doc.title());
            //获取网络中的数据
            int count = 0;
            Elements tds = doc.getElementsByTag("li");
            Log.i(TAG, "run:" + doc.title());

            for (int i = 0; i < tds.size(); i +=1) {
                Element td1 = tds.get(i);
                list.add(count, td1.text() + "#https://yz.chsi.com.cn/kyzx/zcdh/" + (td1.attr("href")));
                Log.i(TAG, "run:" + list.get(count));
                count++;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            Log.i(TAG, "run:请检查网络 ");
        }
        Message msg = handler.obtainMessage(7);
        msg.what = 5;
        msg.obj = list;
        handler.sendMessage(msg);

    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        {
            HashMap<String, String> map = (HashMap<String, String>) result.getItemAtPosition(position);
            String site = map.get("ItemDetail");
            Intent intent = new Intent();
            intent.setData(Uri.parse(site));
            intent.setAction(Intent.ACTION_VIEW);
            this.startActivity(intent);
        }

    }

}
