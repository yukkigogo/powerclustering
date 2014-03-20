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
	HashMap<String,Bus> buses;
	ArrayList<Edge> edges;
	ArrayList<String> Ads; // index-Name for data
	ArrayList<String> PFs; // index-Name for data
	ArrayList<Marker> bus_edge_marker_list;
	ArrayList<ArrayList<Marker>> marker_list2;
	
	//Cluster Data 4 versions
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
	private int indx_mrker_list2 = 2;
	
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
  	  	plotBusesAndEdgees(buses, edges);
        
       //read csv and plot 
        readClusterData();
        
        ///// make an array for icons
        Just4Colours just4Colours = new Just4Colours();
        icons = just4Colours.getIcons();
        
        //plot CL_AD_16
        plotCluster(clusterData.get(0));
        
        //set up drawer lines 91 97
        setUpDrawer(39);
         
        
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
				setUpDrawer(39);
				break;

			case R.id.radio_case2:
				indx_mrker_list2=1;
				plotCluster(clusterData.get(1));
				setUpDrawer(34);
				break;

			case R.id.radio_case3:
				indx_mrker_list2=2;
				plotCluster(clusterData.get(2));
				setUpDrawer(37);
				break;

			case R.id.radio_case4:
				indx_mrker_list2=3;
				plotCluster(clusterData.get(3));
				setUpDrawer(33);
				break;
				
			case R.id.radio_case5:
				indx_mrker_list2=4;
				plotCluster(clusterData.get(4));
				setUpDrawer(32);
				break;

			case R.id.radio_case6:
				indx_mrker_list2=5;
				plotCluster(clusterData.get(5));
				setUpDrawer(34);
				break;

			case R.id.radio_case7:
				indx_mrker_list2=6;
				plotCluster(clusterData.get(6));
				setUpDrawer(34);
				break;

			case R.id.radio_case8:
				indx_mrker_list2=7;
				plotCluster(clusterData.get(7));
				setUpDrawer(33);
				break;

			case R.id.radio_case9:
				indx_mrker_list2=8;
				plotCluster(clusterData.get(8));
				setUpDrawer(31);
				break;

			case R.id.radio_case10:
				indx_mrker_list2=9;
				plotCluster(clusterData.get(9));
				setUpDrawer(35);
				break;

			default:
				break;
			}
			
			
		}
	});
	}

	private void plotCluster(HashMap<String, Integer> list) {
		
//delete all marker on the map
//		for(Marker m: marker_list){
//			m.remove();
//		}
//marker_list.clear();
		
//		for(String s: list.keySet()){
//			// pair <cluster_num,colour>
//			Bus b = buses.get(s);			
//			Log.e("pc", "should be cluster num "+list.get(s).first);
//			if(b!=null){
//				b.setCircleColour(list.get(s).second);
//				b.setClusterNum(list.get(s).first);
//			}
//		}
		
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
			
			
//			int px = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
//			Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
//			Canvas canvas = new Canvas(mDotMarkerBitmap);
//			Drawable shape = getResources().getDrawable(R.drawable.marker_deb);
//			shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
//			shape.setColorFilter(new PorterDuffColorFilter
//					(bus.getCirleColour(), Mode.MULTIPLY));
//			shape.draw(canvas);
//			
			 
			//BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.test_icon);

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
			
			
//			CircleOptions option = new CircleOptions();
//			option.center(b.getLatLng())
//			.radius(2000)
//			.strokeWidth(0)
//			.fillColor(b.getCirleColour());
//			mMap.addCircle(option);
		}

		
	}

	private void readClusterData() {
		// make 
		clusterData = 	new ArrayList<HashMap<String,  Integer>>();	
		
		int CLUSTER_NUM=10;
		
		for(int i=0;i<CLUSTER_NUM;i++){			
			HashMap<String, Integer> list = new HashMap<String, Integer>();

			//// 10 cases clusters start /////
			String fileList="";			
			switch (i){
			case 0:
				fileList ="Solution_1_39.csv";
				break;

			case 1:
				fileList ="Solution_2_34.csv";
				break;

			case 2:
				fileList ="Solution_3_37.csv";
				break;

			case 3:
				fileList ="Solution_4_33.csv";
				break;

			case 4:
				fileList ="Solution_5_32.csv";
				break;

			case 5:
				fileList ="Solution_6_34.csv";
				break;

			case 6:
				fileList ="Solution_7_34.csv";
				break;

			case 7:
				fileList ="Solution_8_33.csv";
				break;

			case 8:
				fileList ="Solution_9_31.csv";
				break;

			case 9:
				fileList ="Solution_10_35.csv";
				break;
				
			}					
			
			
			try {			
				BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(fileList)));
				String str;
				while ((str = br.readLine()) !=null) {
					String[] s = str.split(",");
					try {
						list.put(s[0],Integer.parseInt(s[1]));						
					} catch (Exception e) {
						String str2 = e.getMessage();
						Log.v("pc","parse problem"+str2);
					}
					
				}
			}catch (Exception e) {
				String str3 = e.getMessage();
				Log.e("pc", str3);
			}
				
				
			//// 10 case clusters end //////
			
			
/////// 4 types clusters start ////
//			String fileList="";
//			switch (i){
//			case 0:
//				//fileList ="CL_AD_16.csv";
//				break;
//
//			case 1:
//				//fileList ="CL_AD_37.csv";
//				break;
//
//			case 2:
//				//fileList ="CL_PF_11.csv";
//				break;
//
//			case 3:
//				fileList ="CL_PF_20.csv";
//				break;
//			
//			}		
//			
//		try {			
//			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(fileList)));
//			String str;
//			while ((str = br.readLine()) !=null) {
//				String[] s = str.split(",");
//				String name;
//				
//				if(i==2 || i==3) name = PFs.get(Integer.parseInt(s[0]));
//				else name = Ads.get(Integer.parseInt(s[0]));
//				
//				int cluster_num = Integer.parseInt(s[1]);
//				//int col = colours.get(cluster_num);
//				
// 				list.put(name, cluster_num);
//				
//			}
//			
//		}catch (IOException e) {
//			String s = e.getMessage();
//			Log.e("pc", s);
//		}
///////// 4 types clusters end //////

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
	
		//// test for cluster 3		
//		try {
//			BufferedReader br = new BufferedReader(
//					new InputStreamReader(assetManager.open("filename.csv")));
//			String str;
//			PolygonOptions options = new PolygonOptions();
//			
//			while((str=br.readLine())!=null){
//				String[] strs = str.split(",");
//				double lng = Double.parseDouble(strs[0]);
//				double lat = Double.parseDouble(strs[1]);
//				options.add(new LatLng(lng, lat));
//			}
//			options.fillColor(0x44ff0000);
//			options.strokeWidth(0);
//			mMap.addPolygon(options);
//			
//		} catch (Exception e2) {
//			String str = e2.getMessage();
//			Log.e("pc",str);
//		}
		
		
		
		////////
	
	
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
