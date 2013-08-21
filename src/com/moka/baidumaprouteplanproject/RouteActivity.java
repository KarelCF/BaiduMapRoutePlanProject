package com.moka.baidumaprouteplanproject;

import java.lang.ref.WeakReference;
import java.util.List;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.moka.baidumaprouteplanproject.application.MainApplication;
import com.moka.baidumaprouteplanproject.entity.Poi;
import com.moka.baidumaprouteplanproject.listener.MyLocationListener;
import com.moka.baidumaprouteplanproject.listener.RouteSearchListener;
import com.moka.baidumaprouteplanproject.util.PoiListUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RouteActivity extends Activity {
	
	private List<Poi> pois;
	private int poiIndex;
	private Poi targetPoi;
	private RouteActivityHandler routeActivityHandler = new RouteActivityHandler(this);
	private DrawRouteHandler drawRouteHandler = new DrawRouteHandler(this);
	
	
	private TextView poiNameTextView = null;
	private TextView poiAddressTextView = null;
	private Button choosePoiBtn = null;
	private MapView mapView = null;
	
	private MapController mapController = null;
	private BMapManager mapManager = null;
	private MKSearch mkSearch = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapManager = MainApplication.getBMapManager();
		super.setContentView(R.layout.layout_routeactivity);
		getInfoFromMainActivity();
		initRouteAivityView();
		initLocationService();
		searchRoute();
//		drawTargetPoint();
		fillInTargetPointInfo();
	}
	
	private void getInfoFromMainActivity() {
		Intent intent = this.getIntent();
		poiIndex = intent.getIntExtra("PoiIndex", 0);
		pois = PoiListUtil.getSortedPois();
		targetPoi = pois.get(poiIndex);
	}
	
	private void initRouteAivityView() {
		poiNameTextView = (TextView) findViewById(R.id.poiNameTextView); 
		poiAddressTextView = (TextView) findViewById(R.id.poiAddressTextView); 
		choosePoiBtn = (Button) findViewById(R.id.choosePoiBtn); 
		choosePoiBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
		mapView = (MapView) findViewById(R.id.mapView); 
		mapController = mapView.getController();
		mapController.setZoom(14);
		
	}	
	
	// 定位相关
	public static GeoPoint myGeoPoint = null;
	public MyLocationListener myLocationListener = new MyLocationListener(routeActivityHandler);
	private LocationClient locationClient = null;
	private LocationData locationData = null;
	//定位图层
	private MyLocationOverlay myLocationOverlay = null;
	
	private void initLocationService() {
		// 定位初始化
        locationClient = new LocationClient(this);
        locationData = new LocationData();
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(5000);
        locationClient.setLocOption(option);
        locationClient.start();
        //定位图层初始化
  		myLocationOverlay = new MyLocationOverlay(mapView);
  		//设置定位数据
  	    myLocationOverlay.setData(locationData);
  	    myLocationOverlay.setMarker(null);
  	    //添加定位图层
  		mapView.getOverlays().add(myLocationOverlay);
  		myLocationOverlay.enableCompass();
  		//修改定位数据后刷新图层生效
  		mapView.refresh();
  		
	}
	
	private RouteSearchListener routeSearchListener = new RouteSearchListener(drawRouteHandler);
	
	private void searchRoute() {
		// 设置起始与结束点
		MKPlanNode startNode = new MKPlanNode();
        MKPlanNode endNode = new MKPlanNode();
        startNode.pt = myGeoPoint;
        endNode.pt = targetPoi.getPoiGeoPoint();
		// 搜索初始化
        mkSearch = new MKSearch();
        mkSearch.init(mapManager, routeSearchListener);
        mkSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
        mkSearch.drivingSearch(null, startNode, null, endNode);
	}
	
	
//	private void drawTargetPoint() {
//		GeoPoint targetPoint = targetPoi.getPoiGeoPoint();
//		OverlayItem targetOverlayItem = new OverlayItem(targetPoint, null, null);
//		 
//        // 创建图标资源（用于显示在overlayItem所标记的位置） 
//        Drawable marker = this.getResources().getDrawable(R.drawable.pic_target_point_32);  
//        // 为maker定义位置和边界  
//        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
//		// 目的地图层
//		ItemizedOverlay targetOverlay = new ItemizedOverlay<OverlayItem>(marker, mapView);
//		targetOverlay.addItem(targetOverlayItem);
//		mapView.getOverlays().add(targetOverlay);
//		mapView.refresh();
//	}
	
	private void fillInTargetPointInfo() {
		this.poiNameTextView.setText(targetPoi.getPoiName());
		this.poiAddressTextView.setText(targetPoi.getPoiAddress() + "\n" + targetPoi.getPoiTelephoneNo());
	}
	
	private static class RouteActivityHandler extends Handler {
		// 持有弱引用，消除因Handler类设置static关键字而产生的"This Handler class should be static or leaks might occur"警告
    	private WeakReference<RouteActivity> weakReference;
    	private RouteActivity routeActivity;
    	
		private static final int GET_LOCATION = 0;
		
		private RouteActivityHandler(RouteActivity activity) {
    		weakReference = new WeakReference<RouteActivity>(activity);
        }
		
		@Override
		public void handleMessage(Message msg) {
			routeActivity = weakReference.get();
			switch(msg.what) {
			case GET_LOCATION:
				//更新定位数据
				routeActivity.locationData = (LocationData) msg.obj;
				routeActivity.myLocationOverlay.setData(routeActivity.locationData);
		        //更新图层数据执行刷新后生效
				routeActivity.mapView.refresh();
		    	//移动地图到定位点
            	Toast.makeText(routeActivity, "正在定位...", Toast.LENGTH_SHORT).show();
            	myGeoPoint = routeActivity.myLocationListener.getMyGeoPoint();
            	routeActivity.mapController.animateTo(myGeoPoint);
            	routeActivity.locationClient.stop();
            	routeActivity.searchRoute();
	            break;
			}
		}
		
	}
	
	private static class DrawRouteHandler extends Handler {
		// 持有弱引用，消除因Handler类设置static关键字而产生的"This Handler class should be static or leaks might occur"警告
    	private WeakReference<RouteActivity> weakReference;
    	private RouteActivity routeActivity;
    	
		private static final int GET_ROUTE = 1;
		
		private DrawRouteHandler(RouteActivity activity) {
    		weakReference = new WeakReference<RouteActivity>(activity);
        }
		
		@Override
		public void handleMessage(Message msg) {
			routeActivity = weakReference.get();
			switch(msg.what) {
			case GET_ROUTE:
				drawRoute(msg);
				break;
			}
		}
		
		private void drawRoute(Message message) {
			MKDrivingRouteResult routes = (MKDrivingRouteResult) message.obj;
			RouteOverlay routeOverlay = new RouteOverlay(routeActivity, routeActivity.mapView);
			routeOverlay.setData(routes.getPlan(0).getRoute(0));
			routeActivity.mapView.getOverlays().add(routeOverlay);
			routeActivity.mapView.refresh();
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			routeActivity.mapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
		}
		
	}
	
	
	@Override
	protected void onDestroy() {
		mapView.destroy();
		if (locationClient != null)
			locationClient.stop();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		super.onResume();
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mapView.onRestoreInstanceState(savedInstanceState);
    }
	
}
