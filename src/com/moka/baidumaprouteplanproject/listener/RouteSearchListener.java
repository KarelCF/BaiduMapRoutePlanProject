package com.moka.baidumaprouteplanproject.listener;

import android.os.Handler;
import android.os.Message;

import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

public class RouteSearchListener extends MKSearchListenerImpl {
	
	private static final int GET_ROUTE = 1;
	private Handler drawRouteHandler;
	
	public RouteSearchListener(Handler drawRouteHandler) {
		this.drawRouteHandler = drawRouteHandler;
	}

	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
		if (result == null)
			return;
		Message routeResultMsg = drawRouteHandler.obtainMessage(GET_ROUTE, result);
		drawRouteHandler.sendMessage(routeResultMsg);
	}

	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {
		if (result == null)
			return;
		Message routeResultMsg = drawRouteHandler.obtainMessage(GET_ROUTE, result);
		drawRouteHandler.sendMessage(routeResultMsg);
	}

	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
		if (result == null)
			return;
		Message routeResultMsg = drawRouteHandler.obtainMessage(GET_ROUTE, result);
		drawRouteHandler.sendMessage(routeResultMsg);
	}
	
	
}
