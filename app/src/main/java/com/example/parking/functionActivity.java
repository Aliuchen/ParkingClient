package com.example.parking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.parking.ManageActivity.BaseActivity;
import com.example.parking.Util.NodeUtils;
import com.example.parking.Util.RoutePlanUtil;
import com.example.parking.Util.WalkingRouteOverlay;
import com.example.parking.Util.WayPointUtil;
import com.example.parking.baiduSearch.OverlayManager;
import com.example.parking.baiduSearch.RouteLineAdapter;
import com.example.parking.baiduSearch.SelectRouteDialog;
import com.example.parking.interfaces.OnGetDrivingResultListener;
import com.example.parking.baiduSearch.SelectRouteDialog.OnItemInDlgClickListener;

import java.util.ArrayList;
import java.util.List;

public class functionActivity extends BaseActivity implements BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener {

    private MapView mMapView = null;
    private BaiduMap mBaiduMap;

    // ????????????????????????
    private Button mBtnPre = null; // ???????????????
    private Button mBtnNext = null; // ???????????????
    private RouteLine mRouteLine = null;
    private OverlayManager mRouteOverlay = null;
    private boolean mUseDefaultIcon = false;
    private NodeUtils mNodeUtils;

    private RoutePlanSearch mSearch = null;
    private WalkingRouteResult mWalkingRouteResult = null;
    private boolean hasShowDialog = false;


    // ???????????????????????????????????????
    private Polyline mPolyline;

    // ???????????????????????????????????????
    private Marker mMoveMarker;

    // ??????????????????????????????
    private Handler mHandler;

    // ???????????????????????????
    private static final int TIME_INTERVAL = 1000;

    // ?????????????????????????????????
    private ArrayList<LatLng> latLngs = new ArrayList<>();

    // ??????????????????????????????????????????
    private List<BitmapDescriptor> mTrafficTextureList = new ArrayList<>();
    // ??????????????????????????????????????????
    private List<Integer> mTrafficTextureIndexList = new ArrayList<>();
    // ??????????????????????????????????????????????????????
    private List<Integer> mNewTrafficTextureIndexList = new ArrayList<>();


    // ????????????????????????
    private int mIndex = 0;
    // ??????????????????
    private Thread moveThread = null;
    // ???????????????????????????
    private volatile boolean exit = false;

    // ????????????
    private LatLng startLatLng;
    private double startLat = 34.385288;
    private double startLng = 108.996068;
    // ????????????
    private LatLng endLatLng;
    private double endLat = 34.383584;
    private double endLng = 108.993399;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //???????????????????????????
        setShowTitle(false);
        //???????????????????????????
        setShowStatusBar(true);
        //????????????????????????
        setAllowScreenRoate(true);



        super.onCreate(savedInstanceState);

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_function;
    }

    @Override
    protected void initView() {

        //addRouteLine(startLatLng,endLatLng);

    }

    @Override
    protected void initData() {

        //????????????????????????
        mMapView = (MapView) findViewById(R.id.map);
        mBaiduMap = mMapView.getMap();
        mMapView.showZoomControls(false);
        mHandler = new Handler(Looper.getMainLooper());

        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);

        // ??????button ????????????????????????????????????
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);


        // ????????????????????????
        mBaiduMap.setOnMapClickListener(this);

        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        mNodeUtils = new NodeUtils(this,  mBaiduMap);


        Intent intent = getIntent();
        startLat = intent.getDoubleExtra("startLat",0.00);
        startLng = intent.getDoubleExtra("startLon",0.00);

        endLat = intent.getDoubleExtra("endLat",0.00);
        endLng = intent.getDoubleExtra("endLon",0.00);


        startLatLng = new LatLng(startLat, startLng);
        endLatLng = new LatLng(endLat, endLng);

    }

    /**
     * ??????????????????
     */
    public void nodeClick(View view) {
        if (null != mRouteLine) {
            mNodeUtils.browseRoutNode(view,mRouteLine);
        }
    }

    public void searchButtonProcess(View v) {

        addRouteLine(startLatLng,endLatLng);

    }


    private void NoCarMove() {

        // ?????????????????????????????????
        mRouteLine = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);

        // ????????????????????????
        mBaiduMap.clear();


        PlanNode s = PlanNode.withLocation(startLatLng);
        PlanNode e = PlanNode.withLocation(endLatLng);

        // ????????????????????????????????????????????????????????????
        mSearch.walkingSearch((new WalkingRoutePlanOption())
                .from(s) // ??????
                .to(e)); // ??????
    }

    private void addRouteLine(final LatLng startLatLng, final LatLng endLatLng) {
        // ??????????????????
        RoutePlanUtil routePlanUtil = new RoutePlanUtil(new OnGetDrivingResultListener() {
            @Override
            public void onSuccess(DrivingRouteResult drivingRouteResult) {
                List<DrivingRouteLine> lines = drivingRouteResult.getRouteLines();
                if (lines == null) {
                   showToast("??????????????????");
                    return;
                }
                DrivingRouteLine selectedRouteLine = lines.get(0);
                // ???????????????????????????
                for (int i = 1; i < lines.size(); i++) {
                    if (selectedRouteLine.getCongestionDistance() > lines.get(i).getCongestionDistance()) {
                        selectedRouteLine = lines.get(i);
                    }
                }

                // ??????????????????????????????
                List<DrivingRouteLine.DrivingStep> allStep = selectedRouteLine.getAllStep();
                mTrafficTextureIndexList.clear();
                for (int j = 0; j < allStep.size(); j++) {
                    if (allStep.get(j).getTrafficList() != null && allStep.get(j).getTrafficList().length > 0) {
                        for (int k = 0; k < allStep.get(j).getTrafficList().length; k++) {
                            mTrafficTextureIndexList.add(allStep.get(j).getTrafficList()[k]);
                        }
                    }
                }
                if(!mTrafficTextureIndexList.isEmpty()) {
                    // ???????????????????????????
                    latLngs = WayPointUtil.getWayPointLatLng(selectedRouteLine);
                    // ????????????????????????????????????0.00009??????9??????0.00008??????8???, ???????????????
                    ArrayList<LatLng> temp = divideRouteLine(latLngs, 0.00009);
                    latLngs = temp;

                    drawPolyLine();
                    moveLooper();
                    // ??????????????????????????????
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(startLatLng).include(endLatLng);
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build(), 100, 100, 100, 100));


                } else {
                    NoCarMove();
                }

            }
        });

        // ??????????????????
        PlanNode startNode = PlanNode.withLocation(startLatLng);
        PlanNode endNode = PlanNode.withLocation(endLatLng);
        routePlanUtil.routePlan(startNode, endNode);
    }

    /**
     * ????????????????????????????????????
     * ??????????????????????????????????????????demo???????????????????????????????????????
     * @param routeLine
     * @param distance
     * @return
     */
    private ArrayList<LatLng> divideRouteLine(ArrayList<LatLng> routeLine, double distance) {
        // ?????????????????????????????????
        ArrayList<LatLng> result = new ArrayList<>();

        mNewTrafficTextureIndexList.clear();
        for (int i = 0; i < routeLine.size() - 1; i++) {
            final LatLng startPoint = routeLine.get(i);
            final LatLng endPoint = routeLine.get(i + 1);

            double slope = getSlope(startPoint, endPoint);
            // ????????????????????????
            boolean isYReverse = (startPoint.latitude > endPoint.latitude);
            boolean isXReverse = (startPoint.longitude > endPoint.longitude);

            double intercept = getInterception(slope, startPoint);

            double xMoveDistance = isXReverse ? getXMoveDistance(slope, distance) :
                    -1 * getXMoveDistance(slope, distance);

            double yMoveDistance = isYReverse ? getYMoveDistance(slope, distance) :
                    -1 * getYMoveDistance(slope, distance);

            ArrayList<LatLng> temp1 = new ArrayList<>();

            for (double j = startPoint.latitude, k = startPoint.longitude;
                 !((j > endPoint.latitude) ^ isYReverse) && !((k > endPoint.longitude) ^ isXReverse); ) {
                LatLng latLng = null;

                if (slope == Double.MAX_VALUE) {
                    latLng = new LatLng(j, k);
                    j = j - yMoveDistance;
                } else if (slope == 0.0) {
                    latLng = new LatLng(j, k - xMoveDistance);
                    k = k - xMoveDistance;
                } else {
                    latLng = new LatLng(j, (j - intercept) / slope);
                    j = j - yMoveDistance;
                }


                final LatLng finalLatLng = latLng;
                if (finalLatLng.latitude == 0 && finalLatLng.longitude == 0) {
                    continue;
                }

                if(!mTrafficTextureIndexList.isEmpty()) {
                    mNewTrafficTextureIndexList.add(mTrafficTextureIndexList.get(i));
                }

                temp1.add(finalLatLng);
            }
            result.addAll(temp1);
            if (i == routeLine.size() - 2) {
                result.add(endPoint); // ??????
            }
        }
        return result;
    }

    /**
     * ??????????????????????????????
     * <p>
     * ???????????????0????????? 1????????? 2????????? 3????????? 4???????????????
     *
     * @return ????????????
     */
    public List<BitmapDescriptor> getTrafficTextureList() {
        ArrayList<BitmapDescriptor> list = new ArrayList<BitmapDescriptor>();
        list.add(BitmapDescriptorFactory.fromAsset("Icon_road_blue_arrow.png"));
        list.add(BitmapDescriptorFactory.fromAsset("Icon_road_green_arrow.png"));
        list.add(BitmapDescriptorFactory.fromAsset("Icon_road_yellow_arrow.png"));
        list.add(BitmapDescriptorFactory.fromAsset("Icon_road_red_arrow.png"));
        list.add(BitmapDescriptorFactory.fromAsset("Icon_road_nofocus.png"));
        return list;
    }

    private void drawPolyLine() {
        if (mTrafficTextureList.isEmpty()) {
            mTrafficTextureList.addAll(getTrafficTextureList());
        }

        // ?????????
        PolylineOptions polylineOptions = new PolylineOptions()
                .points(latLngs)
                .dottedLine(true)
                .width(10)
                .focus(true)
                .textureIndex(mNewTrafficTextureIndexList)
                .customTextureList(mTrafficTextureList)
                .zIndex(0);

        mPolyline = (Polyline) mBaiduMap.addOverlay(polylineOptions);


        // ?????????
        OverlayOptions markerOptions;
        markerOptions = new MarkerOptions().flat(true).anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromAsset("arrow.png")).position(latLngs.get(0))
                .rotate((float) getAngle(0));
        mMoveMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
    }

    /**
     * ?????????????????????????????????
     */
    private double getAngle(int startIndex) {
        if ((startIndex + 1) >= mPolyline.getPoints().size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = mPolyline.getPoints().get(startIndex);
        LatLng endPoint = mPolyline.getPoints().get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }

    /**
     * ????????????????????????????????????
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        } else if (slope == 0.0) {
            if (toPoint.longitude > fromPoint.longitude) {
                return -90;
            } else {
                return 90;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    // ??????????????????
    private void stopMoveThread() {
        exit = true;
    }

    /**
     * ????????????????????????
     */
    public void moveLooper() {
        moveThread = new Thread() {
            public void run() {
                Thread thisThread = Thread.currentThread();
                while (!exit) {
                    for (int i = 0; i < latLngs.size() - 1; ) {
                        if (exit) {
                            break;
                        }
                        for (int p = 0; p < latLngs.size() - 1; p++) {
                            // ?????????????????????????????????????????????true
                            // ??????????????????????????????????????????5??? DistanceUtil.getDistance(mCurrentLatLng, latLngs.get(p)) <= 5???
                            // mCurrentLatLng ????????????????????????????????????????????????
                            if (true) {
//                              ??????????????????????????? mIndex = p;
                                mIndex++; // ?????????????????????1
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("????????????"+mIndex);
                                    }
                                });
                                break;
                            }
                        }

                        // ??????????????????
                        i = mIndex + 1;

                        if (mIndex >= latLngs.size() - 1) {
                            exit = true;
                            break;
                        }

                        // ?????????????????????
                        int len = mNewTrafficTextureIndexList.subList(mIndex, mNewTrafficTextureIndexList.size()).size();
                        Integer[] integers = mNewTrafficTextureIndexList.subList(mIndex, mNewTrafficTextureIndexList.size()).toArray(new Integer[len]);
                        int[] index = new int[integers.length];
                        for (int x = 0; x < integers.length; x++) {
                            index[x] = integers[x];
                        }
                        if (index.length > 0) {
                            mPolyline.setIndexs(index);
                            mPolyline.setPoints(latLngs.subList(mIndex, latLngs.size()));
                        }

                        // ?????????????????????????????????????????????????????????????????????
                        final LatLng startPoint = latLngs.get(mIndex);
                        final LatLng endPoint = latLngs.get(mIndex + 1);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // ???????????????????????????????????????
                                if (mMapView == null) {
                                    return;
                                }
                                mMoveMarker.setPosition(startPoint);
                                mMoveMarker.setRotate((float) getAngle(startPoint,
                                        endPoint));
                            }
                        });

                        try {
                            // ??????????????????????????????
                            thisThread.sleep(TIME_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        // ????????????
        moveThread.start();
    }

    /**
     * ??????x???????????????????????????
     */
    private double getXMoveDistance(double slope, double distance) {
        if (slope == Double.MAX_VALUE || slope == 0.0) {
            return distance;
        }
        return Math.abs((distance * 1 / slope) / Math.sqrt(1 + 1 / (slope * slope)));
    }

    /**
     * ??????y???????????????????????????
     */
    private double getYMoveDistance(double slope, double distance) {
        if (slope == Double.MAX_VALUE || slope == 0.0) {
            return distance;
        }
        return Math.abs((distance * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * ??????????????????????????????
     */
    private double getInterception(double slope, LatLng point) {
        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ????????????????????????????????????
     * ????????????????????????????????????????????????
     * ????????????X?????????????????????Y?????????
     */
    private double getSlope(LatLng startPoint, LatLng endPoint) {
        /**
         * ????????????????????????????????????????????????Double.MAX_VALUE
         */
        if (endPoint.longitude == startPoint.longitude) {
            return Double.MAX_VALUE;
        }
        return (endPoint.latitude - startPoint.latitude) / (endPoint.longitude - startPoint.longitude);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mBaiduMap.clear();
        // ????????????
        stopMoveThread();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {

        if (null == result) {
            return;
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // ?????????????????????????????????????????????????????????????????????????????????
            // result.getSuggestAddrInfo()
            AlertDialog.Builder builder = new AlertDialog.Builder(functionActivity.this);
            builder.setTitle("??????");
            builder.setMessage("??????????????????????????????????????????\n?????????getSuggestAddrInfo()??????????????????????????????");
            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }

        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            showToast("??????????????????");
        }else {
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);

            if (result.getRouteLines().size() > 1) {
                mWalkingRouteResult = result;
                if (!hasShowDialog) {
                    SelectRouteDialog selectRouteDialog = new SelectRouteDialog(functionActivity.this,
                            result.getRouteLines(), RouteLineAdapter.Type.WALKING_ROUTE);
                    selectRouteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShowDialog = false;
                        }
                    });
                    selectRouteDialog.setOnItemInDlgClickLinster(new SelectRouteDialog.OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            mRouteLine = mWalkingRouteResult.getRouteLines().get(position);
                            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
                            mBaiduMap.setOnMarkerClickListener(overlay);
                            mRouteOverlay = overlay;
                            overlay.setData(mWalkingRouteResult.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    selectRouteDialog.show();
                    hasShowDialog = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                // ????????????
                mRouteLine = result.getRouteLines().get(0);
                WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                mRouteOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else {
                Log.d("route result", "?????????<0");
            }
        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        private MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

}
