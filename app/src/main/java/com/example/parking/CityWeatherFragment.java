package com.example.parking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parking.ManageActivity.BaseFragment;
import com.example.parking.bean.WeatherBean;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CityWeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CityWeatherFragment extends BaseFragment  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";


    // 温度  城市  当天天气  风向  温度范围  时间
    private TextView tempTv,cityTv,conditionTv,windTv,tempRangeTv,dateTv;
    // 天气图标
    private ImageView dayIv ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String city;

    private String url1 = "http://api.map.baidu.com/telematics/v3/weather?location=";
    private String url2 = "&output=json&ak=v3mVu1BW6ROKH7Xl0Gp6sDNAyhpQ1PU2";

    public CityWeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CityWeatherFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static CityWeatherFragment newInstance(String param1, String param2) {
        CityWeatherFragment fragment = new CityWeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_weather, container, false);
        initView(view);
        Bundle bundle = getArguments();
        city = bundle.getString("city");
        String url = url1+city+url2;
        // 调用父类方法
        loadData(url);


        return view;
    }

    @Override
    public void onSuccess(String result) {
        parseShowData(result);

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        
    }


    private void parseShowData(String result) {

        WeatherBean weatherBean = new Gson().fromJson(result,WeatherBean.class);
        WeatherBean.ResultsBean resultsBean = weatherBean.getResults().get(0);

        // 设置textview
        dateTv.setText(weatherBean.getDate());
        cityTv.setText(resultsBean.getCurrentCity());

        // 设置今天的天气情况
        WeatherBean.ResultsBean.WeatherDataBean today = resultsBean.getWeather_data().get(0);
        windTv.setText(today.getWind());
        tempRangeTv.setText(today.getTemperature());
        conditionTv.setText(today.getWeather());

        // 获取实时天气温度
        String [] split = today.getDate().split("：");
        String todayTemp = split[1].replace(")","");
        tempTv.setText(todayTemp+"℃");

        // 设置天气图片
        Picasso.with(getActivity()).load(today.getDayPictureUrl()).into(dayIv);

    }
    private void initView(View view) {
//        用于初始化控件操作
        tempTv = view.findViewById(R.id.frag_tv_currenttemp);
        cityTv = view.findViewById(R.id.frag_tv_city);
        conditionTv = view.findViewById(R.id.frag_tv_condition);
        windTv = view.findViewById(R.id.frag_tv_wind);
        tempRangeTv = view.findViewById(R.id.frag_tv_temprange);
        dateTv = view.findViewById(R.id.frag_tv_date);
        dayIv = view.findViewById(R.id.frag_iv_today);

    }




}