package com.example.powerclusteringvoronoi.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.vividsolutions.jts.geom.Geometry;

public class Cluster implements Comparable{

	ArrayList<Bus> c_buses;
	//ArrayList<Pair<Bus,Integer>> fuzzy_bueses;
	ArrayList<Polygon> polygon_list; // add all cluters includes different opcities 
	HashMap<Integer, ArrayList<Geometry>> fuzzy_list;	
	ArrayList<Geometry> geo_list;
	int cluster_num;
	
	boolean fuzzy=false;
	int[] colour;
	
	double total_pf=0.0;
	LatLng southest;
	//LatLng westest;
	
	double median;
	double u_quartile;
	double l_quartile;
	ArrayList<Double> values;
	
	boolean cluster_visible=true;
	
	
	
	public Cluster(int c_num) {
		this.cluster_num=c_num;
		c_buses = new ArrayList<Bus>();
		polygon_list = new ArrayList<Polygon>();
		geo_list = new ArrayList<Geometry>();
	}

	public Cluster(int c_num, boolean f){
		this.fuzzy = f;
		//fuzzy_bueses = new ArrayList<Pair<Bus,Integer>>();
		fuzzy_list = new HashMap<Integer, ArrayList<Geometry>>();
		polygon_list = new ArrayList<Polygon>();
		geo_list = new ArrayList<Geometry>();
	}
	
	
	public boolean getVisiblity(){
		return this.cluster_visible;
	}
	public void setVisiblity(boolean b){
		this.cluster_visible=b;
	}
	
	
	public void setClusterColour(int[] col){
		this.colour=col;
	}
	public int[] getClusterColour(){
		return this.colour;
	}
	
	public void setClusterNum(int i){
		this.cluster_num=i;
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

	public void addFuzzyBus(Bus b, int opacity, double val){
		if(!fuzzy_list.containsKey(opacity)) fuzzy_list.put(opacity, new ArrayList<Geometry>());
		fuzzy_list.get(opacity).add(b.getVoronoiGeo());
	
	}

	public void addGeoList(Geometry geo){
		geo_list.add(geo);
	}
	public ArrayList<Geometry> getGeoList(){
		return this.geo_list;
	}

 
	
	
	public HashMap<Integer,ArrayList<Geometry>> getFuzzyBus(){
		return this.fuzzy_list;
	}
	
	
	
	public void addPolygon(ArrayList<Polygon> poly){
		for(Polygon p: poly)  polygon_list.add(p);

	}
	public ArrayList<Polygon> getPolygonList(){
		return this.polygon_list;
	}
	
	public int getTotalNode(){
		return c_buses.size();
	}

	public void addTotalPF(double d){
		this.total_pf=this.total_pf+d;
	}
	public double getTotalPF(){
		return this.total_pf;
	}
	

	public LatLng getSouthest(){
		return this.southest;
	}
	private void compareSouth(LatLng l){
		if(l.latitude<southest.latitude)
			southest=l;
	}

	@Override
	public int compareTo(Object another) {
		if(this.southest.latitude > ((Cluster) another).southest.latitude) return 1;
		else if (this.southest.latitude < ((Cluster) another).southest.latitude ) return -1;
		else return 0;
	
	}
	
	
	
	
}
