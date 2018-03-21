package com.guijunbai.alphaweather;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.guijunbai.app.MyApplication;
import com.guijunbai.bean.City;
import com.guijunbai.bean.TodayWeather;
import com.guijunbai.location.LocationService;
import com.guijunbai.util.NetUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static com.guijunbai.alphaweather.R.layout.main;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView updateBtn,weatherStateImg,pmStateImg;
    private TextView temperatureT1,climateT1,windT1,date1;
    private TextView temperatureT2,climateT2,windT2,date2;
    private TextView temperatureT3,climateT3,windT3,date3;
    private TextView humidityT,pmDataT,pmQualityT,temperatureT,climateT,windT,cityT,timeT;
    private TodayWeather todayWeather;
    private ImageView selectCtiyImg;
    private String updateCityCode;
    private LocationService locationService;
    private String locate;
    private List<City> cities;
    private ImageView locateImg;
    private ImageView shareImg;
    private GifImageView mainBackGroundImg;
    void initView()
    {
        //today weather
        cityT = (TextView)findViewById(R.id.todayinfo1_cityName);
        timeT = (TextView)findViewById(R.id.todayinfo1_updateTime);
        humidityT = (TextView)findViewById(R.id.todayinfo1_humidity);
        pmDataT = (TextView)findViewById(R.id.todayinfo1_pm25);
        pmQualityT = (TextView)findViewById(R.id.todayinfo1_pm25status);
        temperatureT = (TextView)findViewById(R.id.todayinfo2_temperature);
        climateT = (TextView)findViewById(R.id.todayinfo2_weatherState);
        windT = (TextView)findViewById(R.id.todayinfo2_wind);
        weatherStateImg = (ImageView)findViewById(R.id.todayinfo2_weatherStatusImg);
        pmStateImg = (ImageView)findViewById(R.id.todayinfo1_pm25img);

        date1 = (TextView) findViewById(R.id.date1);
        temperatureT1 = (TextView) findViewById(R.id.temperatureT1);
        climateT1 = (TextView) findViewById(R.id.climateT1);
        windT1 = (TextView) findViewById(R.id.windT1);

        date2 = (TextView) findViewById(R.id.date2);
        temperatureT2 = (TextView) findViewById(R.id.temperatureT2);
        climateT2 = (TextView) findViewById(R.id.climateT2);
        windT2 = (TextView) findViewById(R.id.windT2);

        date3 = (TextView) findViewById(R.id.date3);
        temperatureT3 = (TextView) findViewById(R.id.temperatureT3);
        climateT3 = (TextView) findViewById(R.id.climateT3);
        windT3 = (TextView) findViewById(R.id.windT3);
        /*
        cityT.setText("N/A");
        timeT.setText("N/A");
        humidityT.setText("N/A");
        pmDataT.setText("N/A");
        pmQualityT.setText("N/A");
        temperatureT.setText("N/A");
        climateT.setText("N/A");
        windT.setText("N/A");

        //明天
        date1.setText("N/A");
        temperatureT1.setText("N/A");
        climateT1.setText("N/A");
        windT1.setText("N/A");
        //后天
        date2.setText("N/A");
        temperatureT2.setText("N/A");
        climateT2.setText("N/A");
        windT2.setText("N/A");
        //大后天
        date3.setText("N/A");
        temperatureT3.setText("N/A");
        climateT3.setText("N/A");
        windT3.setText("N/A");
        */
        //百度SDK获取当前所在城市
        locationService = ((MyApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }

    private Handler mHandler = new Handler() {
      public void handleMessage(Message message) {
          switch (message.what) {
              case 1 : updateTodayWeather((TodayWeather) message.obj);
                  break;
              default:
                  break;
          }
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(main);
        if (!NetUtil.getNetworkState(this)) {//测试
            Toast.makeText(this, "请连接网络,更新最新天气信息", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "网络正常！", Toast.LENGTH_SHORT).show();
        }

        //数据更新
        updateBtn = (ImageView) findViewById(R.id.title_update);
        updateBtn.setOnClickListener(this);

        //城市选择
        selectCtiyImg = (ImageView) findViewById(R.id.title_city);
        selectCtiyImg.setOnClickListener(this);

        //定位当前城市
        locateImg = (ImageView) findViewById(R.id.title_locate);
        locateImg.setOnClickListener(this);

        //分享
        shareImg = (ImageView) findViewById(R.id.title_share);
        shareImg.setOnClickListener(this);
        initView();

        mainBackGroundImg = (GifImageView) findViewById(R.id.background_main);
        mainBackGroundImg.setVisibility(View.INVISIBLE);
        updateCityCode = getIntent().getStringExtra("cityCode");
        if (!"".equals(updateCityCode) && updateCityCode != null) {
            Log.d("@@@@@@@@@@@@@@@@@", updateCityCode);
            getWeatherDataFromNet(updateCityCode);
            locationService.unregisterListener(mListener);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_city) {
            Intent intent = new Intent(this, SelectCityActivity.class);
            //第一个参数：一个Intent对象，用于携带将跳转至下一个界面中使用的数据，使用putExtra(A,B)方法，此处存储的数据类型特别多，基本类型全部支持。
            //第二个参数：如果> = 0,当Activity结束时requestCode将归还在onActivityResult()中。以便确定返回的数据是从哪个Activity中返回，用来标识目标activity。
            startActivityForResult(intent, 1);
            onActivityResult(RESULT_OK, 1, intent);
        }
        if (v.getId() == R.id.title_update || v.getId() == R.id.title_locate) {
            //百度SDK获取当前所在城市
            locationService = ((MyApplication) getApplication()).locationService;
            //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
            locationService.registerListener(mListener);
            //注册监听
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
            locationService.start();
        }
        if (v.getId() == R.id.title_share) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            // 指定发送内容的类型
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, "share to"));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为" + newCityCode);
            if (!NetUtil.getNetworkState(this)) {//测试
                Toast.makeText(this, "请连接网络,更新最新天气信息", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "网络正常！", Toast.LENGTH_SHORT).show();
                getWeatherDataFromNet(newCityCode);
            }

        }
    }

    /**
     * 从网络获取天气数据
     * @param cityCode
     */
    private void getWeatherDataFromNet(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(address);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(8000);
                    urlConnection.setReadTimeout(8000);
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer sb = new StringBuffer();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        sb.append(str);
                    }
                    String response = sb.toString();
                    Log.d("网络数据:", response);
                    todayWeather = parseXML(response);
                    if (todayWeather != null) {
                        Message message = new Message();
                        message.what = 1;
                        message.obj = todayWeather;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 天数数据XML解析
     * @param xmlData
     */
    private TodayWeather parseXML(String xmlData)
    {
        TodayWeather todayWeather = null;

        int fengliCount = 0;
        int fengxiangCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));

            int eventType = xmlPullParser.getEventType();
            Log.d("MWeater", "start parse xml");

            while (eventType != xmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    //文档开始位置
                    case XmlPullParser.START_DOCUMENT:
                        Log.d("parse", "start doc");
                        break;
                    //标签元素开始位置
                    case XmlPullParser.START_TAG:
                        if ("resp".equals(xmlPullParser.getName())) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if ("city".equals(xmlPullParser.getName())) {
                                eventType = xmlPullParser.next();
                                Log.d("city", xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if ("updatetime".equals(xmlPullParser.getName())) {
                                eventType = xmlPullParser.next();
                                Log.d("updatetime", xmlPullParser.getText());
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if ("wendu".equals(xmlPullParser.getName())) {
                                eventType = xmlPullParser.next();
                                Log.d("wendu", xmlPullParser.getText());
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if ("fengli".equals(xmlPullParser.getName()) && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("fengli", xmlPullParser.getText());
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            }  else if ("fengli".equals(xmlPullParser.getName()) && fengliCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("fengli1", xmlPullParser.getText());
                                todayWeather.setFengli1(xmlPullParser.getText());
                                fengliCount++;
                            } else if ("fengli".equals(xmlPullParser.getName()) && fengliCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("fengli2", xmlPullParser.getText());
                                todayWeather.setFengli2(xmlPullParser.getText());
                                fengliCount++;
                            } else if ("fengli".equals(xmlPullParser.getName()) && fengliCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("fengli3", xmlPullParser.getText());
                                todayWeather.setFengli3(xmlPullParser.getText());
                                fengliCount++;
                            } else if ("shidu".equals(xmlPullParser.getName())) {
                                eventType = xmlPullParser.next();
                                Log.d("shidu", xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if ("fengxiang".equals(xmlPullParser.getName()) && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("fengxiang", xmlPullParser.getText());
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if ("pm25".equals(xmlPullParser.getName())) {
                                eventType = xmlPullParser.next();
                                Log.d("pm25", xmlPullParser.getText());
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if ("quality".equals(xmlPullParser.getName())) {
                                eventType = xmlPullParser.next();
                                Log.d("quelity", xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if ("date".equals(xmlPullParser.getName()) && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("date", xmlPullParser.getText());
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            }  else if ("date".equals(xmlPullParser.getName()) && dateCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("date", xmlPullParser.getText());
                                todayWeather.setDate1(xmlPullParser.getText());
                                dateCount++;
                            }  else if ("date".equals(xmlPullParser.getName()) && dateCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("date", xmlPullParser.getText());
                                todayWeather.setDate2(xmlPullParser.getText());
                                dateCount++;
                            }  else if ("date".equals(xmlPullParser.getName()) && dateCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("date", xmlPullParser.getText());
                                todayWeather.setDate3(xmlPullParser.getText());
                                dateCount++;
                            } else if ("high".equals(xmlPullParser.getName()) && highCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("high", xmlPullParser.getText());
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            }  else if ("high".equals(xmlPullParser.getName()) && highCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("high", xmlPullParser.getText());
                                todayWeather.setHigh1(xmlPullParser.getText());
                                highCount++;
                            }  else if ("high".equals(xmlPullParser.getName()) && highCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("high", xmlPullParser.getText());
                                todayWeather.setHigh2(xmlPullParser.getText());
                                highCount++;
                            }  else if ("high".equals(xmlPullParser.getName()) && highCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("high", xmlPullParser.getText());
                                todayWeather.setHigh3(xmlPullParser.getText());
                                highCount++;
                            } else if ("low".equals(xmlPullParser.getName()) && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("low", xmlPullParser.getText());
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            }  else if ("low".equals(xmlPullParser.getName()) && lowCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("low1", xmlPullParser.getText());
                                todayWeather.setLow1(xmlPullParser.getText());
                                lowCount++;
                            }  else if ("low".equals(xmlPullParser.getName()) && lowCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("low2", xmlPullParser.getText());
                                todayWeather.setLow2(xmlPullParser.getText());
                                lowCount++;
                            }  else if ("low".equals(xmlPullParser.getName()) && lowCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("low3", xmlPullParser.getText());
                                todayWeather.setLow3(xmlPullParser.getText());
                                lowCount++;
                            } else if ("type".equals(xmlPullParser.getName()) && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("type", xmlPullParser.getText());
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            } else if ("type".equals(xmlPullParser.getName()) && typeCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("type", xmlPullParser.getText());
                                todayWeather.setType1(xmlPullParser.getText());
                                typeCount++;
                            } else if ("type".equals(xmlPullParser.getName()) && typeCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("type", xmlPullParser.getText());
                                todayWeather.setType2(xmlPullParser.getText());
                                typeCount++;
                            } else if ("type".equals(xmlPullParser.getName()) && typeCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("type", xmlPullParser.getText());
                                todayWeather.setType3(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType=xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    void updateTodayWeather(TodayWeather todayWeather)
    {
        Log.d("updateTodayWeather:", "updateTodayWeather:");
        cityT.setText(todayWeather.getCity());
        timeT.setText(todayWeather.getUpdatetime());
        humidityT.setText("湿度:"+todayWeather.getShidu());
        pmDataT.setText(todayWeather.getPm25());
        pmQualityT.setText(todayWeather.getQuality());
        temperatureT.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        climateT.setText(todayWeather.getType());
        windT.setText("风力:" + todayWeather.getFengli());

        date1.setText(todayWeather.getDate1());
        temperatureT1.setText(todayWeather.getHigh1() + "~" + todayWeather.getLow1());
        climateT1.setText(todayWeather.getType1());
        windT1.setText("风力:" + todayWeather.getFengli1());

        date2.setText(todayWeather.getDate2());
        temperatureT2.setText(todayWeather.getHigh2() + "~" + todayWeather.getLow2());
        climateT2.setText(todayWeather.getType2());
        windT2.setText("风力:" + todayWeather.getFengli2());

        date3.setText(todayWeather.getDate3());
        temperatureT3.setText(todayWeather.getHigh3() + "~" + todayWeather.getLow3());
        climateT3.setText(todayWeather.getType3());
        windT3.setText("风力:" + todayWeather.getFengli3());

        if (todayWeather.getPm25() != null) {
            int pm25 = Integer.parseInt(todayWeather.getPm25());
            if (pm25 <= 50) {
                pmStateImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            } else if (pm25 >= 51 && pm25 <= 100) {
                pmStateImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            } else if (pm25 >= 101 && pm25 <= 150) {
                pmStateImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            } else if (pm25 >= 151 && pm25 <= 200) {
                pmStateImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            } else if (pm25 >= 201 && pm25 <= 300) {
                pmStateImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }
        }
        if (todayWeather.getType() != null) {
            Log.d("type", todayWeather.getType());
            mainBackGroundImg.setVisibility(View.VISIBLE);
            switch (todayWeather.getType()) {
                case "晴":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                    mainBackGroundImg.setBackgroundResource(R.drawable.sunny);
                    break;
                case "阴":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                    mainBackGroundImg.setBackgroundResource(R.drawable.overcast);
                    break;
                case "雾":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                    mainBackGroundImg.setBackgroundResource(R.drawable.foggy);
                    break;
                case "多云":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    mainBackGroundImg.setBackgroundResource(R.drawable.cloudy);
                    break;
                case "小雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                    mainBackGroundImg.setBackgroundResource(R.drawable.lightrain);
                    break;
                case "中雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    mainBackGroundImg.setBackgroundResource(R.drawable.rain);
                    break;
                case "大雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    mainBackGroundImg.setBackgroundResource(R.drawable.heavyrain);
                    break;
                case "阵雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    mainBackGroundImg.setBackgroundResource(R.drawable.showerrain);
                    break;
                case "雷阵雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    mainBackGroundImg.setBackgroundResource(R.drawable.showerrain);
                    break;
                case "雷阵雨加暴":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "暴雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "特大暴雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "阵雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                    break;
                case "暴雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "大雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "小雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "雨夹雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "中雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                    break;
                case "沙尘暴":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                    break;
                default:
                    break;
            }
        }
        Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                Log.d("@@@@@@@", sb.toString());
                locate = location.getCity();
                Log.d("location.getCity()", locate);
                if (locate != null && !"".equals(locate)) {
                    Log.d("locate:", locate);
                    locationService.unregisterListener(mListener);
                    locationService.stop();
                    cities = ((MyApplication) getApplication()).getAllCity();
                    for (City city : cities) {
                        if (locate.indexOf(city.getCity()) > -1) {
                            Log.d("还没更新", "大爷的");
                            getWeatherDataFromNet(city.getNumber());
                            break;
                        }
                    }
                } else {
                    Log.d("百度说:", "大爷我还没开始定位");
                }
            }
        }

    };

}
