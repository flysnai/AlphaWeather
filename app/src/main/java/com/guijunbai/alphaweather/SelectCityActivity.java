package com.guijunbai.alphaweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.guijunbai.app.MyApplication;
import com.guijunbai.bean.City;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guijunbai on 2018/1/25.
 */

public class SelectCityActivity extends Activity implements View.OnClickListener {
    private ImageView backBtn;
    private ListView selectCityLv;
    private MyApplication myApplication = MyApplication.getInstance();
    private List<City> cities = new ArrayList<>();
    private EditText searchText;
    private ImageView searchImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        //backBtn = (ImageView) findViewById(R.id.title_back);
        //backBtn.setOnClickListener(this);
        cities = myApplication.getAllCity();
        List<String> cityNames = new ArrayList<>();
        for (int i = 0;i < cities.size();i++) {
            cityNames.add(cities.get(i).getCity());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityNames);
        selectCityLv = (ListView) findViewById(R.id.select_city_lv);
        selectCityLv.setAdapter(adapter);
        selectCityLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCityActivity.this, "当前城市:" + (String)((TextView)view).getText(), Toast.LENGTH_SHORT).show();
                Log.d("当前Item的值", (String)((TextView)view).getText());
                //String updateCityCode = cities.get(position).getNumber();
                String updateCityCode = "101010100";
                for (City city:cities) {
                    if (city.getCity().equals((String)((TextView)view).getText())) {
                        updateCityCode = city.getNumber();
                        break;
                    }
                }
                Intent intent = new Intent(SelectCityActivity.this, MainActivity.class);
                intent.putExtra("cityCode", updateCityCode);
                startActivity(intent);
            }
        });

        searchImg = (ImageView) findViewById(R.id.selectcity_search_button);
        searchImg.setOnClickListener(this);
        searchText = (EditText) findViewById(R.id.selectcity_search);
    }

    @Override
    public void onClick(View v) {
        /*if(v.getId() == R.id.title_back) {
            Intent intent = new Intent();
            intent.putExtra("cityCode", "101160101");
            setResult(RESULT_OK, intent);
            finish();
        }*/
        if (v.getId() == R.id.selectcity_search_button) {
            Toast.makeText(SelectCityActivity.this, searchText.getText(), Toast.LENGTH_SHORT).show();
            List<String> cityNames = new ArrayList<>();
            cityNames.add(searchText.getText().toString());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityNames);
            selectCityLv = (ListView) findViewById(R.id.select_city_lv);
            selectCityLv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
