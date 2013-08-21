package com.moka.baidumaprouteplanproject.util;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.search.MKPoiInfo;
import com.moka.baidumaprouteplanproject.entity.Poi;

public class PoiListUtil {
	
	private static List<Poi> SortedPois = new ArrayList<Poi>();
	private List<Float> distances = new ArrayList<Float>();
	private List<MKPoiInfo> targetPoints = new ArrayList<MKPoiInfo>();
	private List<Poi> pois = new ArrayList<Poi>();
	
	
	public PoiListUtil(List<Float> distances, List<MKPoiInfo> targetPoints) {
		this.distances = distances;
		this.targetPoints = targetPoints;
	}
	
	public List<Poi> createPoiList() {
		for (int i = 0; i < targetPoints.size(); i++) {
			Poi poi = new Poi();
			poi.setPoiAddress(targetPoints.get(i).address);
			poi.setPoiName(targetPoints.get(i).name);
			poi.setPoiTelephoneNo(targetPoints.get(i).phoneNum);
			poi.setPoiDistance(distances.get(i));
			poi.setPoiGeoPoint(targetPoints.get(i).pt);
			pois.add(poi);
		}
		return pois;
	}

	public static List<Poi> getSortedPois() {
		return SortedPois;
	}

	public static void setSortedPois(List<Poi> sortedPois) {
		SortedPois = sortedPois;
	}
	
}
