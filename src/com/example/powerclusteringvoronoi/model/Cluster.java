package com.example.powerclusteringvoronoi.model;

import java.util.ArrayList;

import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

public class Cluster {

	ArrayList<Bus> c_buses;
	ArrayList<Pair<Bus,Integer>> fuzzy_bueses;
	ArrayList<Polygon> polygon_list; // add all cluters includes different opcities 
	int cluster_num;
	
	boolean fuzzy=false;
	int[] colour;
	
	double total_pf;
	LatLng southest;
	LatLng westest;
	
	
	
	public Cluster(int c_num) {
		this.cluster_num=c_num;
		c_buses = new ArrayList<Bus>();
		polygon_list = new ArrayList<Polygon>();
	}

	public Cluster(int c_num, boolean f){
		this.fuzzy = f;
		fuzzy_bueses = new ArrayList<Pair<Bus,Integer>>();
	}
	
	

	
	public void setClusterColour(int[] col){
		this.colour=col;
	}
	public int[] getClusterColour(){
		return this.colour;
	}
	
	
	public int getClusterNum(){
		return this.cluster_num;
	}
	
	
	public void addBus(Bus busname){
		c_buses.add(busname);
		if(southest!=null) compareSouth(busname.getLatLng());
		else southest = busname.getLatLng();
	}
	public ArrayList<Bus> getBusList(){
		return this.c_buses;
	}

	public void addFuzzyBus(Bus b, int opacity){
		fuzzy_bueses.add(new Pair<Bus,Integer>(b,opacity));
	}
	public ArrayList<Pair<Bus,Integer>> getFuzzyBus(){
		return this.fuzzy_bueses;
	}
	
	
	
	public void addPolygon(ArrayList<Polygon> poly){
		polygon_list = poly;
	}
	public ArrayList<Polygon> getPolygonList(){
		return this.polygon_list;
	}
	
	public int getTotalNode(){
		return c_buses.size();
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
	
	private void compareSouth(LatLng l){
		if(l.latitude<southest.latitude)
			southest=l;
	}
	
	
	
}
