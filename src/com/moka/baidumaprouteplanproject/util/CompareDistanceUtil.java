package com.moka.baidumaprouteplanproject.util;

import java.util.Comparator;

import com.moka.baidumaprouteplanproject.entity.Poi;

public class CompareDistanceUtil  implements Comparator {
	
	@Override
	public int compare(Object arg0, Object arg1) {
		Poi poiA = (Poi)arg0;
		Poi poiB = (Poi)arg1;

	    //首先比较距离，如果距离相同，比较名字
		int distanceA = (int) poiA.getPoiDistance();
		int distanceB = (int) poiB.getPoiDistance();
	    int flag = distanceA - distanceB;
	    if (flag == 0) {
	    	return poiA.getPoiAddress().compareTo(poiB.getPoiAddress());
	    } else {
	    	return flag;
	    }  
    }
	
}
