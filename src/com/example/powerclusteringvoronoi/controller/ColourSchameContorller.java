package com.example.powerclusteringvoronoi.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.res.AssetManager;

public class ColourSchameContorller {

	public ArrayList<int[]> getColurSchameList(String filename, AssetManager assetManager){
		ArrayList<int[]> array  = new ArrayList<int[]>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(assetManager.open(filename)));
			String str;
			while((str = br.readLine()) !=null) {
				String[] s= str.split(",");
				int r = Integer.parseInt(s[0]);
				int g = Integer.parseInt(s[1]);
				int b = Integer.parseInt(s[2]);
				int[] rgb = {r,g,b};
				array.add(rgb);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		
		return array;
	}
	
}
