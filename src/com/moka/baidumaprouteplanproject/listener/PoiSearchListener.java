package com.moka.baidumaprouteplanproject.listener;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.moka.baidumaprouteplanproject.MainActivity;

public class PoiSearchListener extends MKSearchListenerImpl {
	
	private static final int GET_POI_RESULT = 0;
	
	private List<MKPoiInfo> targetPoints =  new ArrayList<MKPoiInfo>();
	private Handler routeActivityHandler = null;
	private static int count = 0;
	private String[] key;
	private MKSearch mkSearch;
	
	public PoiSearchListener(Handler handler, String[] key,
			MKSearch mkSearch) {
		this.routeActivityHandler = handler;
		this.key = key;  
		this.mkSearch = mkSearch;
	}
	
	@Override
	public void onGetPoiResult(MKPoiResult result, int type, int iError) {
		count ++;
		if (result == null && targetPoints.isEmpty() && count == key.length)
			return;
		if (result == null)
			judgeTheResult();
		if (result != null) {
			targetPoints.addAll(result.getAllPoi()); 
			judgeTheResult();
		} 
	}
	
	private void judgeTheResult() {
		if (count == key.length) {
			Message poiResultMsg = routeActivityHandler.obtainMessage(GET_POI_RESULT, targetPoints);
			routeActivityHandler.sendMessage(poiResultMsg);
			count = 0;
		} else {
			mkSearch.poiSearchNearBy(key[count], MainActivity.myGeoPoint, MainActivity.SEARCH_RADIUS);
		}
	}

}
