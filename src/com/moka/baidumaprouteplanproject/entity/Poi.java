package com.moka.baidumaprouteplanproject.entity;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class Poi {
	
	private String poiName;
	private float poiDistance;
	private String poiAddress;
	private String poiTelephoneNo;
	private GeoPoint poiGeoPoint;
	
	public String getPoiName() {
		return poiName;
	}
	
	public void setPoiName(String poiName) {
		this.poiName = poiName;
	}
	
	public float getPoiDistance() {
		return poiDistance;
	}
	
	public void setPoiDistance(float poiDistance) {
		this.poiDistance = poiDistance;
	}
	
	public String getPoiAddress() {
		return poiAddress;
	}
	
	public void setPoiAddress(String poiAddress) {
		this.poiAddress = poiAddress;
	}
	
	public String getPoiTelephoneNo() {
		return poiTelephoneNo;
	}
	
	public void setPoiTelephoneNo(String telephoneNo) {
		this.poiTelephoneNo = telephoneNo;
	}
	
	public GeoPoint getPoiGeoPoint() {
		return poiGeoPoint;
	}

	public void setPoiGeoPoint(GeoPoint poiGeoPoint) {
		this.poiGeoPoint = poiGeoPoint;
	}
	
}
