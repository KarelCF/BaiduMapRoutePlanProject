package com.moka.baidumaprouteplanproject.listener;

import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MyLocationListener implements BDLocationListener {
	
	private static final int GET_LOCATION = 0;
	
	private LocationData locationData = new LocationData();
	private GeoPoint myGeoPoint = null;
	private Handler handler = null;
	
	public MyLocationListener() {}
	
	public MyLocationListener(Handler handler) {
		this.handler = handler;
	}
	
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null)
            return ;
        locationData.latitude = location.getLatitude();
        locationData.longitude = location.getLongitude();
        myGeoPoint = new GeoPoint( (int) (locationData.latitude * 1e6), (int) (locationData.longitude * 1e6) );
        //如果不显示定位精度圈，将accuracy赋值为0即可
        locationData.accuracy = location.getRadius();
        locationData.direction = location.getDerect();
        if(handler != null) {
        	Message locationMsg = handler.obtainMessage(GET_LOCATION, locationData); 
        	handler.sendMessage(locationMsg);
        }
	}

	@Override
	public void onReceivePoi(BDLocation poiLocation) {
		if (poiLocation == null)
            return ;
	}
	
	public GeoPoint getMyGeoPoint() {
		return myGeoPoint;
	}
}
