package com.example.powerclusteringvoronoi.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.res.AssetManager;
import android.util.Log;

public class ClusterDataReaderContorller {

	AssetManager assetManager;
	ArrayList<String> PFs10R;
	
	public ClusterDataReaderContorller(AssetManager as, ArrayList<String> l) {
		this.assetManager = as;
		this.PFs10R = l;
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
	
	
}
