package com.example.powerclusteringvoronoi.model;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

public class Cluster {

	ArrayList<String> names;
	ArrayList<Polygon> polygon_list;
	int cluster_num;
	
	int opacity;
	int colour;
	
	double total_pf;
	LatLng southest;
	LatLng westest;
	
	
	
	public Cluster(int c_num, int o, int col) {
		this.cluster_num=c_num;
		this.opacity=o;
		this.colour=col;
		names = new ArrayList<String>();
		polygon_list = new ArrayList<Polygon>();
	}

	public int getColours(){
		return this.colour;
	}
	
	public int getClusterNum(){
		return this.cluster_num;
	}
	
	public int getOpacity(){
		return this.opacity;
	}
	
	public void addName(String busname){
		names.add(busname);
	}
	public ArrayList<String> getNameList(){
		return this.names;
	}
	
	public void addPolygon(Polygon poly){
		polygon_list.add(poly);
	}
	public ArrayList<Polygon> getPolygonList(){
		return this.polygon_list;
	}
	
	public int getTotalNode(){
		return names.size();
	}

	public void setTotalPF(double d){
		this.total_pf=d;
	}
	public double getTotalPF(){
		return this.total_pf;
	}
	
	public void setSouthest(LatLng s){
		this.southest=s;
	}
	public LatLng getSouthest(){
		return this.southest;
	}
	
	public void setWestest(LatLng w){
		this.westest=w;
	}
	public LatLng getWestest(){
		return this.westest;
	}
	
}
