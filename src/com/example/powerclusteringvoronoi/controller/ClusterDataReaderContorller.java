package com.example.powerclusteringvoronoi.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.res.AssetManager;
import android.util.Log;
import android.util.Pair;

public class ClusterDataReaderContorller {

	AssetManager assetManager;
	ArrayList<String> PFs10R;
	ArrayList<String> PFs;
	
	public ClusterDataReaderContorller(AssetManager as, ArrayList<String>pfs10, ArrayList<String> pf) {
		this.assetManager = as;
		this.PFs10R = pfs10;
		this.PFs = pf;
	}
	
	public HashMap<String,Integer> readClusterDataBinary(int i){
		
		HashMap<String, Integer> list = new HashMap<String, Integer>();

		
		String fileList="";
		switch (i){
		case 0:
			fileList ="pf_10_01.csv";  // pf
			break;

		case 1:
			fileList ="pf_10_02.csv";
			break;

		case 2:
			fileList ="pf_10_03.csv";
			break;

		case 3:
			fileList ="pf_10_04.csv";
			break;

		case 4:
			fileList ="pf_10_05.csv";
			break;

		case 5:
			fileList ="pf_10_06.csv";
			break;

		case 6:
			fileList ="pf_10_07.csv";
			break;

		case 7:
			fileList ="pf_10_08.csv";
			break;

		case 8:
			fileList ="pf_10_09.csv";
			break;

		case 9:
			fileList ="pf_10_10.csv";
			break;
		
		}		

		try {			
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(fileList)));
			String str;
			while ((str = br.readLine()) !=null) {
				String[] s = str.split(",");
				String name;				
				
				name =PFs10R.get(Integer.parseInt(s[0]));
				
				int cluster_num = Integer.parseInt(s[1]);
				//int col = colours.get(cluster_num);
				
 				list.put(name, cluster_num);
				
			}
		}catch (IOException e) {
			String s = e.getMessage();
			Log.e("pc", s);
		}
		
		return list;
	}
	
	// readClsuterDataFuzzy 
	public ArrayList<HashMap<String,Double>> readClusterDataFuzzy(int i){
		
		ArrayList<HashMap<String, Double>> list_fuzzy = 
				new ArrayList<HashMap<String, Double>>();
		
		String fileList="";
		switch (i){
		case 1:
			fileList ="fuzzy1.csv";  // pf
			break;
		case 2:
			fileList ="fuzzy2.csv";  // pf
			break;
		case 3:
			fileList ="fuzzy3.csv";  // pf
			break;
		}
		
		try {			
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(fileList)));
			String str;
			
			int c_num=1;
			while ((str = br.readLine()) !=null) {
				String[] s = str.split(",");
				HashMap<String, Double> l = new HashMap<String, Double>();
				for(int j=0; j<s.length ; j++){
					String name = PFs.get(j+1);
					double opacity = Double.parseDouble(s[j]);
					
					if(opacity>0)
					 l.put(name, opacity);
					
				}
				list_fuzzy.add(l);
 				c_num++;
			}
			
		}catch (IOException e) {
			String s = e.getMessage();
			Log.e("pc", s);
		}
		
		return list_fuzzy;
	}
	
}
