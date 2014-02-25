package com.example.powerclustering.model;

import com.google.android.gms.maps.model.LatLng;

public class Bus {
	
	private LatLng latlng;
	private String name;
	private int cluster;
	
	public Bus(double lat, double lng, String name) {
		latlng = new LatLng(lat, lng);
		this.name=name;	
	}
	
	public LatLng getLatLng(){return this.latlng;}
	public String getName(){return this.name;}
	
	public void setBusCluster(int num){
		this.cluster=num;
	}
	
}
