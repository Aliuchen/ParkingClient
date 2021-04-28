package com.example.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.parking.ManageActivity.SocketActivity;
import com.example.parking.com.example.style.com.example.util.ToastUtil;
import com.example.parking.db.DaoUtilsStore;
import com.example.parking.db.Order;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends SocketActivity {

    private final static String TAG = "OrderHistoryActivity";

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    private LinearLayoutManager mLayoutManager;
    private RefreshLayout refreshLayout;

    private int headNum = 0;
    private int endNum = 0;
    private ArrayList<String> totalList;
    private List<Order> totalRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //设置是否显示标题栏
        setShowTitle(false);
        //设置是否显示状态栏
        setShowStatusBar(true);
        //是否允许屏幕旋转
        setAllowScreenRoate(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_order_history;
    }

    @Override
    protected void initView() {


    }

    @Override
    protected void initData() {

        totalList = new ArrayList<>();
        String info = "info";
        for(int i =0 ;i < 30; i++) {

            totalList.add(info+i);
        }

        totalRecord = DaoUtilsStore.getInstance().getOrderDaoUtils().queryAll();
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MyAdapter(getData());
        mRecyclerView.setAdapter(mAdapter);

        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);

        // 头部 和 底部
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));

        refreshLayout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
               // mAdapter.refreshData(MoreDatas()); //下拉刷新，数据从上往下添加到界面上
                refreshLayout.finishRefresh(1000); //这个记得设置，否则一直转圈
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mAdapter.loadMore(MoreData());  //上滑刷新，数据从下往上添加到界面上
                refreshLayout.finishLoadMore(1000); //这个记得设置，否则一直转圈
            }

        });


    }

    //原始的recyclerView数据
    private ArrayList<String> getDatas() {
        ArrayList<String> data = new ArrayList<>();
        String temp = " item";
        for(int i = 0; i < 15; i++) {
            data.add(totalList.get(i));
            endNum ++;
        }

        return data;
    }

    // 从数据库读出所有的数据
    private List<Order> getData() {
        List<Order> myList = new ArrayList<>();

        int i = 0;
        for(; i < totalRecord.size()&& i<15; i++) {

            myList.add(totalRecord.get(i));
        }

        endNum = i;
        return myList;
    }

    //刷新得到的数据
    private ArrayList<String> MoreDatas() {
        ArrayList<String> data = new ArrayList<>();
        String temp = "新加数据 ";
        int i = endNum;
        for(; (i < 6+endNum) && (i < totalList.size()); i++) {
            data.add(temp + i);
        }
        if(i < totalList.size()) {
            endNum = i;
        }else  {
            endNum = totalList.size();
        }
        return data;
    }

    private List<Order> MoreData() {
        List<Order> data = new ArrayList<>();
        int i = endNum;
        for(;(i<6+endNum) && (i<totalRecord.size());i++) {
            data.add(totalRecord.get(i));
        }

        if(i < totalList.size()) {
            endNum = i;
        }else  {
            endNum = totalList.size();
        }

        return data;

    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        public ArrayList<String> datas = null;
        public List<Order> data = null;

        public MyAdapter(ArrayList<String> datas) {
            this.datas = datas;
        }
        public MyAdapter(List<Order> data) {
            this.data = data;
        }


        //创建新View，被LayoutManager所调用
        @NonNull
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;

        }

        //将数据与界面进行绑定的操作
        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, final int position) {

            //holder.mTextView.setText(datas.get(position));
            holder.location.setText(data.get(position).getLocation());
            holder.money.setText(String.valueOf(data.get(position).getConsume()));
            if(data.get(position).getPayTag() == 1) {
                holder.isPay.setText("已支付");
            }else {
                holder.isPay.setText("未支付");
            }
            holder.time.setText(data.get(position).getStartReserveTime());
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"点击成功"+position);

                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        //底部上拉刷新，数据直接在底部显示
        public void loadMore(List<Order> val) {
            data.addAll(val);
            notifyDataSetChanged();
        }

        //底部下拉刷新，数据直接从上往下添加数据，显示在顶部
        public void refreshData(List<Order> val) {
            data.addAll(0, val);
            notifyDataSetChanged();
//            notifyItemInserted(0); 一次只能加一项数据
        }

        //自定义的ViewHolder，持有每个Item的的所有界面元素
        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView location,money,isPay,time;
            public Button btn;

            public ViewHolder(View view){
                super(view);
                location = (TextView) view.findViewById(R.id.rcv_title);
                money = (TextView) view.findViewById(R.id.rcv_price);
                isPay = (TextView) view.findViewById(R.id.rcv_zt);
                time = (TextView) view.findViewById(R.id.rcv_data);
                btn = view.findViewById(R.id.rcv_dd);
            }
        }
    }
}