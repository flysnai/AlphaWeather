package com.guijunbai.alphaweather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.guijunbai.util.NetUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Context context = (Context) getApplicationContext();
        if (!NetUtil.getNetworkState(this)) {//测试
            Toast.makeText(MainActivity.this, "请连接网络,更新最新天气信息", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(MainActivity.this, "网络正常", Toast.LENGTH_SHORT);
        }
    }
}
