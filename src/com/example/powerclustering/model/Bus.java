package com.example.powerclustering.model;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class Bus {
	
	private LatLng latlng;
	private String name;
	private int colour;
	private CircleOptions circleoption;

	
	public Bus(double lat, double lng, String name) {
		latlng = new LatLng(lat, lng);
		this.name=name;	
		circleoption = new CircleOptions();
	}
	
	public LatLng getLatLng(){return this.latlng;}
	public String getName(){return this.name;}
	
	public void setBusColour(int num){
		this.colour=num;
	}
	
	public void setCircleOptions(int colour){
		this.colour=colour;
		//circleoption.center(latlng);
		//circleoption.radius(2000);
		//circleoption.fillColor(colour);
	}
	
	public int getCirleColour(){ return this.colour; }
	public CircleOptions getCircleOptions(){ return this.circleoption;}
	
	
}
