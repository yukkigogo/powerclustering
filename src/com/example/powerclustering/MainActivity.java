package com.example.powerclustering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BufferedHeader;

import com.example.powerclustering.controller.BusesEdgeInitialController;
import com.example.powerclustering.model.Bus;
import com.example.powerclustering.model.Edge;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.R.color;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends  FragmentActivity{
	
	//system variable
	AssetManager assetManager;
	
	// for google map
    GoogleMap mMap;
	boolean satellie = false;
	
	//lists for bus and edge
	static final String BUSES_CSV = "coordinates.csv";
	static final String EDGES_CSV ="edgesList.csv";
	HashMap<String,Bus> buses;
	ArrayList<Edge> edges;
	
	// For Navigation Drawer 
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] clsterTypes;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
      //Initialization for system  
       assetManager = getAssets();		

       //make lists for buses and edges
       BusesEdgeInitialController controller = new BusesEdgeInitialController();
       buses = controller.getBusListFromCSV(BUSES_CSV, assetManager);
	   edges = controller.getEdgesListFromCSV(EDGES_CSV, assetManager, buses);

       
       //set up the map
        setUpMapIfNeeded();
        
        //set up drawer
        setUpDrawer();
    }


    private void setUpDrawer() {
    	
    	
    	
    	//setup up the list
    	clsterTypes = getResources().getStringArray(R.array.drawer_array);
    	mDrawerListView = (ListView) findViewById(R.id.left_drawer);
    	mDrawerListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, clsterTypes));
    	mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
    	
    	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);	
    	mDrawerToggle = new ActionBarDrawerToggle(this, 
    								mDrawerLayout, 
    								R.drawable.ic_drawer, 
    								R.string.drawer_open, 
    								R.string.drawer_close);
    	
    	
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
		  
		  // show whole UK
		  CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(new LatLng(54.41893,-2.618179))
			.zoom((float) 6.2)
			.build();
		  mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

		  plotBusesAndEdgees(buses, edges);
		  
	}

	
	
	

	private void plotBusesAndEdgees(HashMap<String, Bus> b, ArrayList<Edge> e) {
		
		//plot marker
		for(Bus bus : b.values()){
			BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.dot);
			Marker marker = mMap.addMarker(new MarkerOptions().position(bus.getLatLng())
					.title(bus.getName())
					.icon(icon));
		}	
	
		//plot polyline
		for(Edge edge : e){
			PolylineOptions options = new PolylineOptions();
			options.add(edge.getStartBus().getLatLng());
			options.add(edge.getEndBus().getLatLng());
			options.color(Color.CYAN);
			options.geodesic(true);
			options.width(2);
			
			mMap.addPolyline(options);
		}
	
	}


	



			
			
			
			


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    
    	switch (item.getItemId()) {
		case R.id.go_map_view:
			if(!satellie){
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			}else if(satellie){
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}
			satellie = !satellie;
			break;

		default:
			break;
		}
    	return super.onOptionsItemSelected(item);
    }
    
    
    /**
     * helper class for drawer navigater 
     *
     *
     *
     *
     *
     *
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			Log.e("pc", "");
		}
    	
    }
    
}
