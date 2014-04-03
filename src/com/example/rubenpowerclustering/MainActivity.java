package com.example.rubenpowerclustering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BufferedHeader;

import com.example.rubenpowerclustering.controller.BusesEdgeInitialController;
import com.example.rubenpowerclustering.controller.IDAdmittancePowerFlowsController;
import com.example.rubenpowerclustering.model.Bus;
import com.example.rubenpowerclustering.model.Edge;
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
import com.google.android.gms.maps.model.PolygonOptions;
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
import android.text.Layout;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
	static final String PFs_10CSV = "case10lebelR.csv";
	HashMap<String,Bus> buses;
	ArrayList<Edge> edges;
	//ArrayList<String> Ads; // index-Name for data
	//ArrayList<String> PFs; // index-Name for data
	ArrayList<String> PFs10R;
	ArrayList<Marker> bus_edge_marker_list;
	ArrayList<ArrayList<Marker>> marker_list2;
	
	
	//Cluster Data versions
	ArrayList<HashMap<String, Integer>> clusterData;
	ArrayList<BitmapDescriptor> icons;

	
	
	//RadioButton 
	RadioGroup radioGroup;
	RadioButton radioButton;
	
	// For Navigation Drawer 
	private CharSequence mTitle;
	 private CharSequence mDrawerTitle;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private LinearLayout mDrawerLinearLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ArrayList<String> clusterTypes;
	private ArrayList<Integer> num_clusters;
	
	// numbers 
	int indx_mrker_list2;
	int CASE_NUM=10;
	final int MaxClusterType = 39;

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
//	   Ads = controller2.getNameFileToArray(ADs_CSV, assetManager);
//	   PFs = controller2.getNameFileToArray(PFs_CSV, assetManager);
       PFs10R = controller2.getNameFileToArray(PFs_10CSV, assetManager);
	   
       //set up the map
        setUpMapIfNeeded();
  	  	plotBusesAndEdgees(buses, edges);
        
       //read csv and plot 
        readClusterData();
        
        ///// make an array for icons
        Just4Colours just4Colours = new Just4Colours();
        icons = just4Colours.getIcons();
        
        //plot initial cluster
        plotCluster(clusterData.get(0));
        indx_mrker_list2=0;
        setUpDrawer(num_clusters.get(0));
         
        
        //set radio button
        setRadios();
        
    }

	private void setAllClusters(boolean b){
		for(ArrayList<Marker> ary : marker_list2)
			for(Marker mk :  ary ) mk.remove(); 
		if(b){ // if true, means all on
			// all listview items are ON and plot all the cluster
			plotCluster(clusterData.get(indx_mrker_list2));
	    	for(int i=0;i<clusterData.get(indx_mrker_list2).size(); i++) 
	    		mDrawerListView.setItemChecked(i, true);

		}else{
			// all list view items are off and clear from map 			
	    	for(int i=0;i<clusterData.get(indx_mrker_list2).size(); i++) 
	    		mDrawerListView.setItemChecked(i, false);
		}
		//mDrawerLayout.closeDrawer(mDrawerLinearLayout);

	}
	
	private void setRadios() {
        radioGroup = (RadioGroup) findViewById(R.id.radio_cluster);
        radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
       radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			radioButton = (RadioButton) findViewById(checkedId);
			switch (checkedId) {
			case R.id.radio_case1:
				indx_mrker_list2=0;
				plotCluster(clusterData.get(0));								
				setUpDrawer(num_clusters.get(0));
				break;

			case R.id.radio_case2:
				indx_mrker_list2=1;
				plotCluster(clusterData.get(1));
				setUpDrawer(num_clusters.get(1));
				break;

			case R.id.radio_case3:
				indx_mrker_list2=2;
				plotCluster(clusterData.get(2));
				setUpDrawer(num_clusters.get(2));
				break;

			case R.id.radio_case4:
				indx_mrker_list2=3;
				plotCluster(clusterData.get(3));
				setUpDrawer(num_clusters.get(3));
				break;
				
			case R.id.radio_case5:
				indx_mrker_list2=4;
				plotCluster(clusterData.get(4));
				setUpDrawer(num_clusters.get(4));
				break;

			case R.id.radio_case6:
				indx_mrker_list2=5;
				plotCluster(clusterData.get(5));
				setUpDrawer(num_clusters.get(5));
				break;

			case R.id.radio_case7:
				indx_mrker_list2=6;
				plotCluster(clusterData.get(6));
				setUpDrawer(num_clusters.get(6));
				break;

			case R.id.radio_case8:
				indx_mrker_list2=7;
				plotCluster(clusterData.get(7));
				setUpDrawer(num_clusters.get(8));
				break;

			case R.id.radio_case9:
				indx_mrker_list2=8;
				plotCluster(clusterData.get(8));
				setUpDrawer(num_clusters.get(9));
				break;

			case R.id.radio_case10:
				indx_mrker_list2=9;
				plotCluster(clusterData.get(9));
				setUpDrawer(num_clusters.get(9));
				break;

			default:
				break;
			}
			
			
		}
	});
	}

	private void plotCluster(HashMap<String, Integer> list) {
		
		
		if(marker_list2==null){
			marker_list2 = new ArrayList<ArrayList<Marker>>();
			for(int i=0;i<=39;i++) marker_list2.add(new ArrayList<Marker>()); 
		}else for(ArrayList<Marker> ary : marker_list2)
						for(Marker mk :  ary ) mk.remove(); 
		
		

		for(Bus bus : buses.values()){			
			
			if(list.containsKey(bus.getName())){
				//bus.setCircleColour(list.get(bus.getName()).second);
				bus.setClusterNum(list.get(bus.getName()));
				//Log.e("pc", "show the name "+list.get(bus.getName()));
				
			}else{
				//bus.setCircleColour(0x00ffffff);
				bus.setClusterNum(0);
			}
			
			 

			if(bus.getClusterNum()==0){
				
			}else{
			int cluster_num = bus.getClusterNum();
			Marker marker = mMap.addMarker(new MarkerOptions()
						.position(bus.getLatLng())
						.snippet("Cluster :" +cluster_num)
						.title(bus.getName())
						.icon(icons.get(cluster_num)));

			marker_list2.get(cluster_num).add(marker);

			
			}
			

		}

		
	}

	private void readClusterData() {
		// make 
		clusterData = 	new ArrayList<HashMap<String,  Integer>>();	
		num_clusters = new ArrayList<Integer>();
		
		
		
		for(int i=0;i<CASE_NUM;i++){			
			HashMap<String, Integer> list = new HashMap<String, Integer>();
			
			
			////  cases clusters start /////
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
			
		int cluster_num=0;	
		try {			
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(fileList)));
			String str;
			
			while ((str = br.readLine()) !=null) {
				String[] s = str.split(",");
				String name;
		
				name = PFs10R.get(Integer.parseInt(s[0]));				
				cluster_num = Integer.parseInt(s[1]);
				//int col = colours.get(cluster_num);
				
 				list.put(name, cluster_num);
				
			}
			
		}catch (IOException e) {
			String s = e.getMessage();
			Log.e("pc", s);
		}
			num_clusters.add(cluster_num);
			clusterData.add(list);
		}
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	
	

    private void setUpDrawer(int cluster_num) {
    	
    	
    	if(clusterTypes!=null) clusterTypes.clear();
    	else clusterTypes = new ArrayList<String>();
    	
    	for(int i=1;i<=cluster_num;i++) clusterTypes.add("Cluster "+i);
    	
    	//setup up the list    	
    	 mTitle = mDrawerTitle = getTitle();
    	mDrawerListView = (ListView) findViewById(R.id.left_drawer);
    	mDrawerLinearLayout = (LinearLayout) findViewById(R.id.drawer_view);
    
    	mDrawerListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, clusterTypes){
    		@Override
    		public View getView(int position, View convertView, ViewGroup parent) {
    			View view = super.getView(position, convertView, parent);
    			TextView textView = (TextView) view.findViewById(android.R.id.text1);
    			textView.setTextColor(Color.WHITE);
    			return view;
    		}
    	});
    	mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
    	for(int i=0;i<cluster_num; i++) mDrawerListView.setItemChecked(i, true);
    	
    	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);	
    	mDrawerToggle = new ActionBarDrawerToggle(this, 
    								mDrawerLayout, 
    								R.drawable.ic_drawer, 
    								R.string.drawer_open, 
    								R.string.drawer_close){
      	  
        	@Override
        	public void onDrawerClosed(View drawerView) {
        		 super.onDrawerOpened(drawerView);
        		 getActionBar().setTitle(mDrawerTitle);
        	}
        	  
        	@Override
        	public void onDrawerOpened(View drawerView) {
        		 super.onDrawerClosed(drawerView);
        		 getActionBar().setTitle(mDrawerTitle);
        	}
        	  
          };
          
    
    	
    	mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true); 
    	
    	Button b_all = (Button) findViewById(R.id.drawer_all);
    	b_all.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				setAllClusters(true);
				
			}
		});
    	
    	Button b_clear = (Button) findViewById(R.id.drawer_clear);
    	b_clear.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				setAllClusters(false);				
			}
		});
    	
    	
     
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

	
		  
	}

	
	
	

	private void plotBusesAndEdgees(HashMap<String, Bus> b, ArrayList<Edge> e) {
		bus_edge_marker_list = new  ArrayList<Marker>();
		//plot marker
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.dot);
		for(Bus bus : b.values()){
			
			Marker marker = mMap.addMarker(new MarkerOptions().position(bus.getLatLng())
					.title(bus.getName())
					.icon(icon));
			bus_edge_marker_list.add(marker);
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
			int c_num = position +1;
			
			if(mDrawerListView.isItemChecked(position)){
				for(Bus bus : buses.values()){		
					if(bus.getClusterNum()==c_num){ 
						Marker marker = mMap.addMarker(new MarkerOptions()
						.position(bus.getLatLng())
						.snippet("Cluster :" +c_num)
						.title(bus.getName())
						.icon(icons.get(c_num)));

					marker_list2.get(c_num).add(marker);
					}
				}
				
				
			}else{
				for(Marker mk: marker_list2.get(c_num)) mk.remove();
			}
			
			//mDrawerLayout.closeDrawer(mDrawerLinearLayout);
						
		}
	
    }
    
}
