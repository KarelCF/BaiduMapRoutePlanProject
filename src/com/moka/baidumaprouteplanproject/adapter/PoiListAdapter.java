package com.moka.baidumaprouteplanproject.adapter;

import java.util.List;

import com.moka.baidumaprouteplanproject.R;
import com.moka.baidumaprouteplanproject.entity.Poi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PoiListAdapter extends BaseAdapter {
	
	private Context context;
	private List<Poi> poiList;
	
	public PoiListAdapter(Context context, List<Poi> poiList) {
		super();
		this.context = context;
		this.poiList = poiList;
	}

	@Override
	public int getCount() {
		return poiList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.poi_list_item, null, false);
			holder = new ViewHolder();
			holder.poiNameTextView = (TextView) convertView.findViewById(R.id.poiNameTextView);
			holder.poiDistanceTextView = (TextView) convertView.findViewById(R.id.poiDistanceTextView);
			holder.poiAddressTextView = (TextView) convertView.findViewById(R.id.poiAddressTextView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Poi poi = poiList.get(position);
		holder.poiNameTextView.setText(poi.getPoiName());
		holder.poiAddressTextView.setText(poi.getPoiAddress());
		
		StringBuffer distaceToString = getDistaceToString(poi);
		holder.poiDistanceTextView.setText(distaceToString.toString());
		
		
		return convertView;
	}
	
	private static class ViewHolder {
		TextView poiNameTextView;
		TextView poiDistanceTextView;
		TextView poiAddressTextView;
	}
	
	private StringBuffer getDistaceToString(Poi poi) {
		double distance = poi.getPoiDistance() / 1000;
		StringBuffer distaceToString = new StringBuffer();
		if (distance < 1) {
			distance = distance * 1000;
			distaceToString.append(String.format("%.0f", distance));
			distaceToString.append("m");
		} else {
			distaceToString.append(String.format("%.2f", distance));
			distaceToString.append("km");
		}
		return distaceToString;
	}
	
}
