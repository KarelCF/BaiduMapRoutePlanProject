package com.moka.baidumaprouteplanproject.listener;

import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

public class MKSearchListenerImpl implements MKSearchListener {

	@Override
	public void onGetAddrResult(MKAddrInfo result, int iError) {
		
	}

	@Override
	public void onGetBusDetailResult(MKBusLineResult result, int iError) {

	}

	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {

	}

	@Override
	public void onGetPoiDetailSearchResult(int type, int iError) {

	}

	@Override
	public void onGetPoiResult(MKPoiResult result, int type, int iError) {

	}

	@Override
	public void onGetShareUrlResult(MKShareUrlResult result, int type, int iError) {

	}

	@Override
	public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {

	}

	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {

	}

	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {

	}

}
