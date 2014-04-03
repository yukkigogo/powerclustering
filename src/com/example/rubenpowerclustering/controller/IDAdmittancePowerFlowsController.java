package com.example.rubenpowerclustering.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.res.AssetManager;
import android.util.Log;

public class IDAdmittancePowerFlowsController {

	public ArrayList<String> getNameFileToArray(String filename, AssetManager assetManager){
		ArrayList<String> list = new ArrayList<String>();
		list.add("");

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(filename)));
			String str;
			while ((str = br.readLine()) !=null) {
				//Log.v("pc", "show:"+str+":end");
				list.add(str);
			}
			
		}catch (IOException e) {
				e.printStackTrace();
		}
	 
	
		
		return list;
	}
	
	
}
