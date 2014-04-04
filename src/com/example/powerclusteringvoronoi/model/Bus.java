package com.example.powerclusteringvoronoi.model;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Bus {
	
	private LatLng latlng;
	private String name;
	private int colour;
	private int cluster_num;
	private CircleOptions circleoption;

	
	public Bus(double lat, double lng, String name) {
		latlng = new LatLng(lat, lng);
		this.name=name;	
		//circleoption = new CircleOptions();
		
	}
	
	public LatLng getLatLng(){return this.latlng;}
	public String getName(){return this.name;}
	
	public void setBusColour(int num){
		this.colour=num;
	}
	
	public void setCircleColour(int colour){
		this.colour=colour;
		//circleoption.center(latlng);
		//circleoption.radius(2000);
		//circleoption.fillColor(colour);
	}
	
	public int getCirleColour(){ return this.colour; }
	//public CircleOptions getCircleOptions(){ return this.circleoption;}
	
	public void setClusterNum(int n){ this.cluster_num=n; }
	public int getClusterNum(){return this.cluster_num;}
	
}
