package com.example.powerclustering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.message.BufferedHeader;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends  FragmentActivity{
	
    GoogleMap mMap;
	
    ArrayList<Marker> DisplayBranchPie = new ArrayList<Marker>();
    ArrayList<Marker> DisplayBus = new ArrayList<Marker>();
    ArrayList<Marker> DisplayBusField = new ArrayList<Marker>();
    ArrayList<Marker> DisplayCircuitBreaker = new ArrayList<Marker>();
    ArrayList<Marker> DisplayGen = new ArrayList<Marker>();
    ArrayList<Marker> DisplayGenField = new ArrayList<Marker>();
    ArrayList<Marker> DisplayLoad = new ArrayList<Marker>();
    ArrayList<Marker> DisplayLoadField= new ArrayList<Marker>() ;
    ArrayList<Marker> DisplayTransformer= new ArrayList<Marker>();
    ArrayList<Marker> DisplayTransmissionLine= new ArrayList<Marker>();
    ArrayList<Marker> Line= new ArrayList<Marker>();
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setUpMapIfNeeded();
        	
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.  
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
        	mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        	// Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }else{
        	setUpMap();
        }		
	}


	private void setUpMap() {
		  UiSettings settings = mMap.getUiSettings();
		  settings.setCompassEnabled(true);
		  settings.setZoomControlsEnabled(true);    	   	
		  
		  setDataDisplayBus();
	
	}

	
	
	

	private void setDataDisplayBus() {
		AssetManager assetManager = getAssets();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open("coordinates.csv")));
			String str;
			while((str = br.readLine()) !=null) {
				Log.v("pc", str);
				String[] strs = str.split(",");
				LatLng position=null;
				try { // parse latitude and longitude string to double
					double lat = Double.parseDouble(strs[2]);
					double lon = Double.parseDouble(strs[1]);
					position = new LatLng(lat, lon);					
				} catch (Exception e) {
					Log.e("pc" , " lat lng problem");
				}
				BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.dot);
				Marker marker = mMap.addMarker(new MarkerOptions().position(position)
						.title(strs[0])
						.icon(icon));
				
			}
			
			
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}


	private void setData() {
		
		// read data from local 
		AssetManager assetManager = getAssets();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open("node2.csv")));
			String str;
			while((str = br.readLine()) !=null) {
				String[] strs = str.split(" ");
				LatLng position=null;
				try { // parse latitude and longitude string to double
					double lat = Double.parseDouble(strs[2]);
					double lon = Double.parseDouble(strs[1]);
					position = new LatLng(lat, lon);					
				} catch (Exception e) {
					Log.e("problem with lat log" , "pc");
				}
				
				if(strs[0].equals("DisplayBranchPie")){
					Marker marker = mMap.addMarker(new MarkerOptions().position(position).title("DisplayBranchPie"));
					DisplayBranchPie.add(marker);
				}else if(strs[0].equals("DisplayBus")){
					Marker marker = mMap.addMarker(new MarkerOptions()
					.position(position)
					.title("DisplayBus")
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
					DisplayBus.add(marker);
				}else if(strs[0].equals("DisplayBusField")){
					Marker marker = mMap.addMarker(new MarkerOptions()
					.title("DisplayBusField")
					.position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
					DisplayBusField.add(marker);
				}else if(strs[0].equals("DisplayCircuitBreaker")){
					Marker marker = mMap.addMarker(new MarkerOptions()
					.title("DisplayCircuitBreaker")
					.position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
					DisplayCircuitBreaker.add(marker);
				}else if(strs[0].equals("DisplayGen")){
					Marker marker = mMap.addMarker(new MarkerOptions()
					.title("DisplayGen")
					.position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
					DisplayGen.add(marker);
				}else if(strs[0].equals("DisplayGenField")){
					Marker marker = mMap.addMarker(new MarkerOptions()
					.title("DisplayGenField")
					.position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
					DisplayGenField.add(marker);
				}else if(strs[0].equals("DisplayLoad")){
					Marker marker = mMap.addMarker(new MarkerOptions()
					.title("DisplayLoad")
					.position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
					DisplayLoad.add(marker);
				}else if(strs[0].equals("DisplayLoadField")){
					Marker marker = mMap.addMarker(new MarkerOptions()
					.title("DisplayLoadField")
					.position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
					DisplayLoadField.add(marker);
				}else if(strs[0].equals("DisplayTransformer")){
					Marker marker = mMap.addMarker(new MarkerOptions()
					.title("DisplayTransformer")
					.position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
					DisplayTransformer.add(marker);
				}else if(strs[0].equals("DisplayTransmissionLine")){
					Marker marker = mMap.addMarker(new MarkerOptions()
					.title("DisplayTransmissionLine")
					.position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
					DisplayTransmissionLine.add(marker);
				}else if(strs[0].equals("Line")){
					Marker marker = mMap.addMarker(new MarkerOptions()
					.title("Line")
					.position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
					Line.add(marker);
				}
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	
		
		
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    
    
}
