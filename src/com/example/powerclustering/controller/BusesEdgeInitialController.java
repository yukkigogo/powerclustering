package com.example.powerclustering.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.res.AssetManager;
import android.util.Log;

import com.example.powerclustering.model.Bus;
import com.example.powerclustering.model.Edge;
import com.google.android.gms.maps.model.LatLng;

public class BusesEdgeInitialController {

	public HashMap<String, Bus> getBusListFromCSV(String filename, AssetManager assetManager) {
		HashMap<String, Bus> buses = new HashMap<String, Bus>();
		
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
			while((str = br.readLine()) !=null) {
				String[] strs = str.split(",");
				/// maybe there are not matching???
				Bus start = buses.get(strs[0]);
				Bus end = buses.get(strs[1]);
				edges.add(new Edge(start, end));	
				Log.e("pc", start.getName() +" and "+ end.getName());
			}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return edges;
	}
	
	
	
	
}
