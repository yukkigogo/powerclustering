package com.example.rubenpowerclustering.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.res.AssetManager;
import android.util.Log;

import com.example.rubenpowerclustering.model.Bus;
import com.example.rubenpowerclustering.model.Edge;
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
	
	
	
	
}
