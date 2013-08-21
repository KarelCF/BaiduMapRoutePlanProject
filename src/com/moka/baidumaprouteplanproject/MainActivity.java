package com.moka.baidumaprouteplanproject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.moka.baidumaprouteplanproject.adapter.PoiListAdapter;
import com.moka.baidumaprouteplanproject.application.MainApplication;
import com.moka.baidumaprouteplanproject.constant.Constant;
import com.moka.baidumaprouteplanproject.entity.Poi;
import com.moka.baidumaprouteplanproject.listener.MyLocationListener;
import com.moka.baidumaprouteplanproject.listener.PoiSearchListener;
import com.moka.baidumaprouteplanproject.util.CompareDistanceUtil;
import com.moka.baidumaprouteplanproject.util.PoiListUtil;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

public class MainActivity extends Activity {
	
	private static final int GET_POI_RESULT = 0;
	private static final int START_SEARCH = 1;
	
	private PoiListAdapter poiListAdapter = null;
	private ListView poiListView = null;
	private Button getPoiBtn = null;
	private MainApplication mapApplication = null;
	private MainHandler handler = new MainHandler(this);
	private ProgressDialog progressDialog;
	
	// ��ͼ���
	private BMapManager mapManager = null;
	private MKSearch mkSearch = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkAndInitMapManager();
		super.setContentView(R.layout.layout_mainactivity);
		initMainActivityView();
		initLocationService();
	}
	
	private void checkAndInitMapManager() {
		mapApplication = (MainApplication) this.getApplication();
		mapManager = MainApplication.getBMapManager();
		if (mapManager == null) {
			mapManager = new BMapManager(this);
			mapApplication.initBMapManager(this);
		}
	}
	
	private void initMainActivityView() {
		getPoiBtn = (Button) findViewById(R.id.getPoiBtn); 
		poiListView = (ListView) findViewById(R.id.poiListView); 
		getPoiBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Message startSearchMsg = MainActivity.this.handler.obtainMessage(START_SEARCH, 0);
				MainActivity.this.handler.sendMessage(startSearchMsg);
				setPoiSearchCondition();
			}
		});
		
		poiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(MainActivity.this, RouteActivity.class);
				intent.putExtra("PoiIndex", position);
				startActivity(intent);
			}
		});
		
	}
	
	// ��λ���
	public static GeoPoint myGeoPoint = null;
	public MyLocationListener myLocationListener = new MyLocationListener();
	private PoiSearchListener poiSearchListener;
	private LocationClient locationClient = null;
	
	// ���������趨
	public static final int SEARCH_RADIUS = 6000; 
	private String[] searchKeywords = Constant.SEARCH_KEYWORDS;
	
	private void initLocationService() {
		// ��λ��ʼ��
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(5000);
        locationClient.setLocOption(option);
        locationClient.start();
        // ������ʼ��
        mkSearch = new MKSearch();
        poiSearchListener = new PoiSearchListener(handler, searchKeywords, mkSearch);
		mkSearch.init(mapManager, poiSearchListener);
	}
	
	private void setPoiSearchCondition() {
		myGeoPoint = myLocationListener.getMyGeoPoint();
		if (myGeoPoint == null) {
			Toast.makeText(this, "δ�ܳɹ���λ", Toast.LENGTH_SHORT).show();
			return;
		}
		mkSearch.poiSearchNearBy(searchKeywords[0], myGeoPoint, SEARCH_RADIUS);
	}
	
    private static class MainHandler extends Handler {
    	// ���������ã�������Handler������static�ؼ��ֶ�������"This Handler class should be static or leaks might occur"����
    	private WeakReference<MainActivity> weakReference;
    	private MainActivity mainActivity;
    	private List<MKPoiInfo> targetPoints;
    	private List<Float> distances = new ArrayList<Float>();
    	private List<Poi> pois;
    	
    	private MainHandler(MainActivity activity) {
    		weakReference = new WeakReference<MainActivity>(activity);
        }
    	
		@Override
		public void handleMessage(Message msg) {
			mainActivity = weakReference.get();
			switch (msg.what) {
			case START_SEARCH:
				mainActivity.progressDialog = ProgressDialog.show(mainActivity, "���Ե�...", "����������...", true);
				break;
			case GET_POI_RESULT:
				mainActivity.progressDialog.dismiss();
				Toast.makeText(mainActivity, "�����ɹ�", Toast.LENGTH_SHORT).show();
				targetPoints = (List<MKPoiInfo>) msg.obj;
				countDistanceAndCreatePoiList();
				mainActivity.poiListAdapter = new PoiListAdapter(mainActivity, pois);
				mainActivity.poiListView.setAdapter(mainActivity.poiListAdapter);
				mainActivity.locationClient.stop();
				break;
			}
		
		}
		
		// ��������λ����ÿ����֮�����
		private Location start = new Location("start");  
		private Location end = new Location("end");
		private GeoPoint startPoint;
		private GeoPoint endPoint;
		
	    private void countDistanceAndCreatePoiList() {
	    	startPoint = myGeoPoint;
	    	for (int i = 0; i < targetPoints.size(); i++) {
	    		endPoint = targetPoints.get(i).pt;
	    		getStartAndEndLocation();
		    	float distance = start.distanceTo(end);
		    	distances.add(distance);
			}
	    	PoiListUtil poiListUtil = new PoiListUtil(distances, targetPoints);
	    	pois = poiListUtil.createPoiList();
	    	clearRepeatPois();
	    	// �ȽϾ��벢���մ�С���еĿ��ٷ���
	    	CompareDistanceUtil compareDistanceUtil = new CompareDistanceUtil();
	    	Collections.sort(pois, compareDistanceUtil);
	    	PoiListUtil.setSortedPois(pois);
	    }
	    
	    private Map<String, Poi> poiMap = new HashMap<String, Poi>();
	    
	    // ��HashSet��ʽ����ɾ���ظ�Ԫ������ΪPoi��ÿ��ʵ������ַ��ͬ,����HashSet��Ϊ�����ǲ�ͬ��
	    // ����취:�Ե���Ϊ��������Ϊֵ��ӽ�HashMap�У�����ȡ����ֵװ��List  
	    private void clearRepeatPois() {
			Iterator<Poi> iterator = pois.iterator();
	    	Poi poiNext;
	    	while(iterator.hasNext()) {
    			poiNext = iterator.next();
    			poiMap.put(poiNext.getPoiName(), poiNext);
    		}
	    	pois.clear();
	    	for (Poi poi : poiMap.values()) {
	    		pois.add(poi);
	    	}
	    }
	    
	    private void getStartAndEndLocation() {
	    	start.setLatitude(startPoint.getLatitudeE6() / 1E6);  
    		start.setLongitude(startPoint.getLongitudeE6() / 1E6);  
    		end.setLatitude(endPoint.getLatitudeE6() / 1E6);  
    		end.setLongitude(endPoint.getLongitudeE6() / 1E6);  
	    }

    }
    
}
