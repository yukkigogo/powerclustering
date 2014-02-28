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
import com.example.powerclustering.controller.IDAdmittancePowerFlowsController;
import com.example.powerclustering.model.Bus;
import com.example.powerclustering.model.Edge;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.R.color;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
	static final String ADs_CSV ="NodeIDsAd.csv";
	static final String PFs_CSV = "NodeIDsPF.csv";
	HashMap<String,Bus> buses;
	ArrayList<Edge> edges;
	ArrayList<String> Ads;
	ArrayList<String> PFs;
	ArrayList<Marker> marker_list;
	ArrayList<Marker> marker_list2;
	
	//RadioButton 
	RadioGroup radioGroup;
	RadioButton radioButton;
	
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
	   IDAdmittancePowerFlowsController controller2 = new IDAdmittancePowerFlowsController();
	   Ads = controller2.getNameFileToArray(ADs_CSV, assetManager);
	   PFs = controller2.getNameFileToArray(PFs_CSV, assetManager);
       
	   
       //set up the map
        setUpMapIfNeeded();
        
        //set up drawer lines 91 97
       // setUpDrawer();
        
        
        
       //read csv and plot 
        readClusterData();
        plotCluster();
        
        //set radio button
        setRadios();
        
    }

	private void setRadios() {
        radioGroup = (RadioGroup) findViewById(R.id.radio_cluster);
        radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
       radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			radioButton = (RadioButton) findViewById(checkedId);
			switch (checkedId) {
			case R.id.radio_ad_16:
				Toast.makeText(MainActivity.this, "AD 16 ", Toast.LENGTH_SHORT)
				.show();
				break;
			case R.id.radio_ad_37:
				Toast.makeText(MainActivity.this, "AD 37 ", Toast.LENGTH_SHORT)
				.show();
				break;
			default:
				break;
			}
			
			Toast.makeText(MainActivity.this, "show the num! " + checkedId, Toast.LENGTH_SHORT)
			.show();
		}
	});
	}

	private void plotCluster() {
		marker_list2 = new ArrayList<Marker>();
		//delete all marker on the map
//		for(Marker m: marker_list){
//			m.remove();
//		}
		marker_list.clear();
		
		for(Bus bus : buses.values()){			
			int px = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
			Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(mDotMarkerBitmap);
			Drawable shape = getResources().getDrawable(R.drawable.marker_deb);
			shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
			shape.setColorFilter(new PorterDuffColorFilter
					(bus.getCirleColour(), Mode.MULTIPLY));
			shape.draw(canvas);
			
			Marker marker	= mMap.addMarker(new MarkerOptions()
						.position(bus.getLatLng())
						.snippet("Cluster :" +bus.getClusterNum())
						.title(bus.getName())
						.icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap)));
			
				marker_list2.add(marker);
			
			
//			CircleOptions option = new CircleOptions();
//			option.center(b.getLatLng())
//			.radius(2000)
//			.strokeWidth(0)
//			.fillColor(b.getCirleColour());
//			mMap.addCircle(option);
		}

		
	}

	private void readClusterData() {
		// make colour list
		ArrayList<Integer> colours = new ArrayList<Integer>();
		colours.add(0);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open("colour20.csv")));
			String str;
			while ((str = br.readLine()) !=null) {
				String s = "77"+str;
				Log.w("pc", s);
				colours.add(Integer.parseInt(s,16));
			}
			Log.e("pc", "size? "+ colours.size());
		} catch (Exception e) {
			String s = e.getMessage();
			Log.e("pc", "we got a problem "+s );
		}
		
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open("CL_PF_20.csv")));
			String str;
			while ((str = br.readLine()) !=null) {
				String[] s = str.split(",");
				String name = PFs.get(Integer.parseInt(s[0]));
				int cluster_num = Integer.parseInt(s[1]);
				int col = colours.get(cluster_num);
				Bus b = buses.get(name);
				if(b!=null){
					b.setCircleOptions(col);
					b.setClusterNum(cluster_num);
				}
			}
			
		}catch (IOException e) {
				e.printStackTrace();
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		//mDrawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//mDrawerToggle.onConfigurationChanged(newConfig);
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
    								R.string.drawer_close){
      	  
        	@Override
        	public void onDrawerClosed(View drawerView) { }
        	  
        	@Override
        	public void onDrawerOpened(View drawerView) {  }
        	  
          };;
    	
    	mDrawerLayout.setDrawerListener(mDrawerToggle);
    	
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
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
		marker_list = new  ArrayList<Marker>();
		//plot marker
		for(Bus bus : b.values()){
			BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.dot);
			Marker marker = mMap.addMarker(new MarkerOptions().position(bus.getLatLng())
					.title(bus.getName())
					.icon(icon));
			marker_list.add(marker);
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
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			selectItem(position);
		}

		private void selectItem(int position) {
			
			Log.e("pc", "position num : " + position);
			mDrawerLayout.closeDrawer(mDrawerListView);
						
		}
    	
	
    }
    
}
