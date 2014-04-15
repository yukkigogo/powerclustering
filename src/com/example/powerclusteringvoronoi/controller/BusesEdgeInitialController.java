package com.example.powerclusteringvoronoi.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.res.AssetManager;
import android.util.Log;
import android.util.Pair;

import com.example.powerclusteringvoronoi.model.Bus;
import com.example.powerclusteringvoronoi.model.Edge;
import com.google.android.gms.maps.model.LatLng;

public class BusesEdgeInitialController {

	public HashMap<String, Bus> getBusListFromCSV(String filename, AssetManager assetManager) {
		HashMap<String, Bus> buses = new HashMap<String, Bus>();
		
		//missing Name
		File file =  new File("missing");
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(filename)));
			String str;
			while((str = br.readLine()) !=null) {
				
				String[] strs = str.split(",");
				LatLng position=null;
				try { // parse latitude and longitude string to double
					double lat = Double.parseDouble(strs[2]);
					double lon = Double.parseDouble(strs[1]);
					Bus bus = new Bus(lat, lon, strs[0]);
					buses.put(strs[0],bus);
				} catch (Exception e) {
					Log.e("pc" , " lat lng problem");
				}

				}	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return buses;
	}		
		
		
	public ArrayList<Edge> getEdgesListFromCSV(String edgesCsv, AssetManager assetManager, HashMap<String,Bus> buses) {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		
	
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(edgesCsv)));
			String str;
			int count =0;
			while((str = br.readLine()) !=null) {
				String[] strs = str.split(",");
				Bus start = buses.get(strs[0]);
				Bus end = buses.get(strs[1]);
				if(start!=null && end!=null)
					edges.add(new Edge(start, end));	
				else if(start==null){
					//Log.e("pc","start : "+strs[0]);
					count++;
				}else if(end==null){
					//Log.e("pc", "end : "+strs[1]);
					count++;
				}
			}
			Log.e("pc", "number of null : "+count);
		}catch (IOException e) {
				e.printStackTrace();
			}
		
		return edges;
	}


	public String getCoastPairs(String filename, AssetManager assetManager){
		String latlon="";
		
		try {
			BufferedReader br = new BufferedReader(new 
					InputStreamReader(assetManager.open(filename)));
			String str;
			int count=0;
			while((str = br.readLine()) !=null){
				latlon = latlon+str+",";
//				if(count==0){
//				latlon = latlon+str+",";
//				count++;
//				}else if(count==10){
//					count=0;
//				}else{	
//					count++;
//				}
			} 
				
			
		}catch (IOException e) {
			String string = e.getMessage();
			Log.e("pc", "got problem" + string);
		}	
		return latlon.substring(0, latlon.length()-1);
	}
	
	public ArrayList<Pair<Integer, Pair<Integer, Pair<Double, Double>>>> getCoastPairs2(
			String ukcoast, AssetManager assetManager) {
		
		ArrayList<Pair<Integer, Pair<Integer, Pair<Double, Double>>>> ary = 
				new ArrayList<Pair<Integer,Pair<Integer, Pair<Double,Double>>>>();
		
		try {
			BufferedReader br = new BufferedReader(new 
					InputStreamReader(assetManager.open(ukcoast)));
			String str;
			
			//int count =0;
			while((str = br.readLine()) !=null) {
//				if(str.startsWith(">")){
//					count++;
//				}else{
					String[] latlon = str.split(" ");
					int line_num = Integer.parseInt(latlon[0]);
					int cat_num = Integer.parseInt(latlon[1]);
					double lat = Double.parseDouble(latlon[2]);
					double lng = Double.parseDouble(latlon[3]);
					
					Pair ll = new Pair<Double, Double>(lat, lng);
					Pair c = new Pair<Integer, Pair>(cat_num, ll);
					Pair d = new Pair<Integer, Pair>(line_num, c);
					
					ary.add(d);
//				}
				
			}
			
			
			
		}catch (IOException e) {
			e.printStackTrace();
		}	
		
		return ary;
	}
	
	
	
	
}
