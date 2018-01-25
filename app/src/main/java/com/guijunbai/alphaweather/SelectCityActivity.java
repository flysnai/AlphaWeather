package com.guijunbai.alphaweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by guijunbai on 2018/1/25.
 */

public class SelectCityActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        backBtn = (ImageView) findViewById(R.id.title_back);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.title_back) {
            Intent intent = new Intent();
            intent.putExtra("cityCode", "101160101");
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
