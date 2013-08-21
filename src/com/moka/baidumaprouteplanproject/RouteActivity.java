package com.moka.baidumaprouteplanproject;

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
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.moka.baidumaprouteplanproject.application.MainApplication;
import com.moka.baidumaprouteplanproject.entity.Poi;
import com.moka.baidumaprouteplanproject.listener.MyLocationListener;
import com.moka.baidumaprouteplanproject.util.PoiListUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RouteActivity extends Activity {
	
	private List<Poi> pois;
	private int poiIndex;
	private Poi targetPoi;
	private RouteActivityHandler handler = new RouteActivityHandler();
	
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
		drawTargetPoint();
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
		mapView = (MapView) findViewById(R.id.mapView); 
		mapController = mapView.getController();
		mapController.setZoom(15);
		
	}	
	
	// 定位相关
	public static GeoPoint myGeoPoint = null;
	public MyLocationListener myLocationListener = new MyLocationListener(this.handler);
	private LocationClient locationClient = null;
	private LocationData locationData = null;
	//定位图层
	private MyLocationOverlay myLocationOverlay = null;
	//是否首次定位
	private boolean isFirstLoc = true;
	
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
        // 搜索初始化
        mkSearch = new MKSearch();
	}
	
	private void drawTargetPoint() {
		GeoPoint targetPoint = targetPoi.getPoiGeoPoint();
		OverlayItem targetOverlayItem = new OverlayItem(targetPoint, null, null);
		 
        // 创建图标资源（用于显示在overlayItem所标记的位置） 
        Drawable marker = this.getResources().getDrawable(R.drawable.pic_target_point_32);  
        // 为maker定义位置和边界  
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
		// 目的地图层
		ItemizedOverlay targetOverlay = new ItemizedOverlay<OverlayItem>(marker, mapView);
		targetOverlay.addItem(targetOverlayItem);
		mapView.getOverlays().add(targetOverlay);
		mapView.refresh();
	}
	
	private void fillInTargetPointInfo() {
		this.poiNameTextView.setText(targetPoi.getPoiName());
		this.poiAddressTextView.setText(targetPoi.getPoiAddress() + "\n" + targetPoi.getPoiTelephoneNo());
	}
	
	private class RouteActivityHandler extends Handler {
		
		private static final int GET_LOCATION = 0;
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case GET_LOCATION:
				//更新定位数据
				locationData = (LocationData) msg.obj;
		        myLocationOverlay.setData(locationData);
		        //更新图层数据执行刷新后生效
		        mapView.refresh();
		    	//移动地图到定位点
	            if (isFirstLoc) {
	            	Toast.makeText(RouteActivity.this, "正在定位...", Toast.LENGTH_SHORT).show();
	            	myGeoPoint = myLocationListener.getMyGeoPoint();
	                mapController.animateTo(myGeoPoint);
	                locationClient.stop();
	            }
	            //首次定位完成
	            isFirstLoc = false;
	            break;
			}
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
	
}
