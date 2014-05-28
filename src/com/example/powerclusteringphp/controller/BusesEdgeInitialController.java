package com.example.powerclusteringphp.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

import com.example.powerclusteringphp.R;
import com.example.powerclusteringphp.model.Bus;
import com.example.powerclusteringphp.model.Edge;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class BusesEdgeInitialController {

	public HashMap<String, Pair<Bus,Marker>> getBusListFromCSV(String filename, AssetManager assetManager, GoogleMap map) {		
		HashMap<String, Pair<Bus,Marker>> buses = new HashMap<String, Pair<Bus,Marker>>();
		// small icon
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.dot);
		
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
					//buses.put(strs[0],bus);

					Marker marker = map.addMarker(new MarkerOptions().position(bus.getLatLng())
							.title(bus.getName()).icon(icon));
					
					buses.put(strs[0], new Pair<Bus, Marker>(bus, marker));
				
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
	
	
	
	
	
	
		
	public ArrayList<Pair<Edge,Polyline>> getEdgesListFromCSV(String edgesCsv, AssetManager assetManager, HashMap<String, Pair<Bus,Marker>> buses, GoogleMap map) {
		ArrayList<Pair<Edge,Polyline>> edges = new ArrayList<Pair<Edge,Polyline>>();
		
	
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(edgesCsv)));
			String str;
			int count =0;
			while((str = br.readLine()) !=null) {
				Edge edge = null;
				String[] strs = str.split(",");
				Pair<Bus,Marker> start = buses.get(strs[0]);
				Pair<Bus,Marker> end = buses.get(strs[1]);
				if(start!=null && end!=null){
					edge = new Edge(start.first, end.first);

					PolylineOptions options = new PolylineOptions();
					options.add(edge.getStartBus().getLatLng());
					options.add(edge.getEndBus().getLatLng());
					options.color(Color.CYAN);
					options.geodesic(true);
					options.width(2);

					Polyline line = map.addPolyline(options);
					edges.add(new Pair<Edge,Polyline>(edge,line));
					
					/// edges.add(edge);	
				}else if(start==null){
					//Log.e("pc","start : "+strs[0]);
					count++;
				}else if(end==null){
					//Log.e("pc", "end : "+strs[1]);
					count++;
				}
			
			
			}
			
			
			
			Log.e("pc", "number of null : "+count);
		}catch (IOException e) {
			String str = e.getMessage();
			Log.v("pc",str);
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
			} 
				
			
		}catch (IOException e) {
			String string = e.getMessage();
			Log.e("pc", "got problem" + string);
		}	
		return latlon.substring(0, latlon.length()-1);
	}
	
	
	
	
	
}
