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
	
	// ��λ���
	public static GeoPoint myGeoPoint = null;
	public MyLocationListener myLocationListener = new MyLocationListener(routeActivityHandler);
	private LocationClient locationClient = null;
	private LocationData locationData = null;
	//��λͼ��
	private MyLocationOverlay myLocationOverlay = null;
	
	private void initLocationService() {
		// ��λ��ʼ��
        locationClient = new LocationClient(this);
        locationData = new LocationData();
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//��gps
        option.setCoorType("bd09ll");     //������������
        option.setScanSpan(5000);
        locationClient.setLocOption(option);
        locationClient.start();
        //��λͼ���ʼ��
  		myLocationOverlay = new MyLocationOverlay(mapView);
  		//���ö�λ����
  	    myLocationOverlay.setData(locationData);
  	    myLocationOverlay.setMarker(null);
  	    //��Ӷ�λͼ��
  		mapView.getOverlays().add(myLocationOverlay);
  		myLocationOverlay.enableCompass();
  		//�޸Ķ�λ���ݺ�ˢ��ͼ����Ч
  		mapView.refresh();
  		
	}
	
	private RouteSearchListener routeSearchListener = new RouteSearchListener(drawRouteHandler);
	
	private void searchRoute() {
		// ������ʼ�������
		MKPlanNode startNode = new MKPlanNode();
        MKPlanNode endNode = new MKPlanNode();
        startNode.pt = myGeoPoint;
        endNode.pt = targetPoi.getPoiGeoPoint();
		// ������ʼ��
        mkSearch = new MKSearch();
        mkSearch.init(mapManager, routeSearchListener);
        mkSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
        mkSearch.drivingSearch(null, startNode, null, endNode);
	}
	
	
//	private void drawTargetPoint() {
//		GeoPoint targetPoint = targetPoi.getPoiGeoPoint();
//		OverlayItem targetOverlayItem = new OverlayItem(targetPoint, null, null);
//		 
//        // ����ͼ����Դ��������ʾ��overlayItem����ǵ�λ�ã� 
//        Drawable marker = this.getResources().getDrawable(R.drawable.pic_target_point_32);  
//        // Ϊmaker����λ�úͱ߽�  
//        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
//		// Ŀ�ĵ�ͼ��
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
		// ���������ã�������Handler������static�ؼ��ֶ�������"This Handler class should be static or leaks might occur"����
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
				//���¶�λ����
				routeActivity.locationData = (LocationData) msg.obj;
				routeActivity.myLocationOverlay.setData(routeActivity.locationData);
		        //����ͼ������ִ��ˢ�º���Ч
				routeActivity.mapView.refresh();
		    	//�ƶ���ͼ����λ��
            	Toast.makeText(routeActivity, "���ڶ�λ...", Toast.LENGTH_SHORT).show();
            	myGeoPoint = routeActivity.myLocationListener.getMyGeoPoint();
            	routeActivity.mapController.animateTo(myGeoPoint);
            	routeActivity.locationClient.stop();
            	routeActivity.searchRoute();
	            break;
			}
		}
		
	}
	
	private static class DrawRouteHandler extends Handler {
		// ���������ã�������Handler������static�ؼ��ֶ�������"This Handler class should be static or leaks might occur"����
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
			// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
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
