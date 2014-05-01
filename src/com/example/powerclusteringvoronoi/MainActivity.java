package com.example.powerclusteringvoronoi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.message.BufferedHeader;
import org.json.JSONException;

import com.example.powerclusteringvoronoi.controller.BusesEdgeInitialController;
import com.example.powerclusteringvoronoi.controller.ColourSchameContorller;
import com.example.powerclusteringvoronoi.controller.IDAdmittancePowerFlowsController;
import com.example.powerclusteringvoronoi.model.Bus;
import com.example.powerclusteringvoronoi.model.Edge;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polygon;

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









//JTS imports
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;

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
	static final String UKcoastline = "coast_with_arr_num_trim_add14_orderadd.csv";
	//static final String UKcoastline2 = "coast_with_arr_num.csv";

	static final String Colur_CSV = "colour_deg.csv";	
	
	HashMap<String, Pair<Bus,Marker>> buses;
	ArrayList<Pair<Edge,Polyline>> edges;
	
	// coastline for clipper 
	String coast;
	
	// colour
	ArrayList<int[]> colurs;
	
	///ArrayList<Pair<Integer, Pair<Integer, Pair<Double, Double>>>> coast2;
	
	//ArrayList<String> Ads; // index-Name for data
	//ArrayList<String> PFs; // index-Name for data
	ArrayList<String> PFs10R;
	ArrayList<Marker> bus_edge_marker_list;
	ArrayList<ArrayList<Marker>> marker_list2;
	
	ArrayList<ArrayList<Polygon>> poly_list=null;
	
	
	//
	int indx_mrker_list2;
	int CASE_NUM=10;
	final int MaxClusterType = 39;
	
	
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
	private ArrayList<Integer> num_clusters;

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
      //Initialization for system  
       assetManager = getAssets();		


	   IDAdmittancePowerFlowsController controller2 = new IDAdmittancePowerFlowsController();
	   //Ads = controller2.getNameFileToArray(ADs_CSV, assetManager);
	   //PFs = controller2.getNameFileToArray(PFs_CSV, assetManager);
       PFs10R = controller2.getNameFileToArray(PFs_10CSV, assetManager);
	   
       //create colour schame
       ColourSchameContorller csc = new ColourSchameContorller();
       colurs = csc.getColurSchameList(Colur_CSV, assetManager);
	
	   
       //set up the map
        setUpMapIfNeeded();
        
        // initial setting  for buses and edges icons -  read file and plot on the map 
        BusesEdgeInitialController controller = new BusesEdgeInitialController();
        buses = controller.getBusListFromCSV(BUSES_CSV, assetManager, mMap);
        edges = controller.getEdgesListFromCSV(EDGES_CSV, assetManager, buses, mMap);
        	
        // obtain costline latlng string
        coast = controller.getCoastPairs(UKcoastline,assetManager);

        
  	  	//plotBusesAndEdgees(buses, edges);
        
       //read csv and plot 
        readClusterData();
        
        ///// make an array for icons
        //Just4Colours just4Colours = new Just4Colours();
        //icons = just4Colours.getIcons();
        
        //plot CL_AD_16
        plotCluster(clusterData.get(0));
        
        //set up drawer lines 91 97
        setUpDrawer(num_clusters.get(0));
         
        
        //set radio button
        setRadios();
        
       // Log.e("pc", "polylist "+ poly_list.size());
        
    }

	private void setAllClusters(boolean b){
		for(ArrayList<Polygon> ary : poly_list)
			for(Polygon mk :  ary ) mk.setVisible(false); 
		if(b){ // if true, means all on
			// all listview items are ON and plot all the cluster
			//plotCluster(clusterData.get(indx_mrker_list2));
	    	for (ArrayList<Polygon> ary : poly_list){
	    		for(Polygon p : ary) p.setVisible(true);
	    	}	
			
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
//		Log.e("pc", "size "+coast2.size());
		
	 	//plotBorders(coast2);

		
//		if(marker_list2==null){
//			marker_list2 = new ArrayList<ArrayList<Marker>>();
//			for(int i=0;i<=37;i++) marker_list2.add(new ArrayList<Marker>()); 
//		}else for(ArrayList<Marker> ary : marker_list2)
//						for(Marker mk :  ary ) mk.remove(); 
		
		
		ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
		HashMap<Coordinate, Integer> clusterlist = new HashMap<Coordinate, Integer>();
		
		// 
		for(Pair<Bus,Marker> p : buses.values()){			
			
			if(list.containsKey(p.first.getName())){
				//bus.setCircleColour(list.get(bus.getName()).second);
				p.first.setClusterNum(list.get(p.first.getName()));
				//Log.e("pc", "show the name "+list.get(bus.getName()));
				int cluster_num = p.first.getClusterNum();
				LatLng geo = p.first.getLatLng();
				Coordinate coord = new Coordinate(geo.latitude, geo.longitude);
				clusterlist.put(coord, cluster_num);
				coords.add(coord);
//				}
//				Marker marker = mMap.addMarker(new MarkerOptions()
//							.position(bus.getLatLng())
//							.snippet("Cluster :" +cluster_num)
//							.title(bus.getName())
//							.icon(icons.get(cluster_num)));
//
//				marker_list2.get(cluster_num).add(marker);

			}else{
				//bus.setCircleColour(0x00ffffff);
				p.first.setClusterNum(0);
			}

		}
		drawVoronoi(coords, clusterlist);
		
	}


	private void readClusterData() {
		// make 
		clusterData = 	new ArrayList<HashMap<String,  Integer>>();	
		num_clusters = new ArrayList<Integer>();

		for(int i=0;i<CASE_NUM;i++){			
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
			
			// total number of clusters
			int cluster_num=0;	
		try {			
			BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(fileList)));
			String str;
			while ((str = br.readLine()) !=null) {
				String[] s = str.split(",");
				String name;
				
				
				name =PFs10R.get(Integer.parseInt(s[0]));
				
				cluster_num = Integer.parseInt(s[1]);
				//int col = colours.get(cluster_num);
				
 				list.put(name, cluster_num);
				
			}
			
		}catch (IOException e) {
			String s = e.getMessage();
			Log.e("pc", s);
		}
		
		// add the last number of cluster
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
    	
    	// array for drawer list
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

	
	
	

//	private void plotBusesAndEdgees(HashMap<String, Bus> b, ArrayList<Edge> e) {
//		bus_edge_marker_list = new  ArrayList<Marker>();
//		//plot marker
//		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.dot);
//		for(Bus bus : b.values()){
//			
//			Marker marker = mMap.addMarker(new MarkerOptions().position(bus.getLatLng())
//					.title(bus.getName())
//					.icon(icon));
//			bus_edge_marker_list.add(marker);
//		}	
//	
//		//plot polyline
//		for(Edge edge : e){
//			PolylineOptions options = new PolylineOptions();
//			options.add(edge.getStartBus().getLatLng());
//			options.add(edge.getEndBus().getLatLng());
//			options.color(Color.CYAN);
//			options.geodesic(true);
//			options.width(2);
//			
//			mMap.addPolyline(options);
//		}
//	
//
//	
//	
//	}


	

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
//				for(Bus bus : buses.values()){		
//					if(bus.getClusterNum()==c_num){ 
//						Marker marker = mMap.addMarker(new MarkerOptions()
//						.position(bus.getLatLng())
//						.snippet("Cluster :" +c_num)
//						.title(bus.getName())
//						.icon(icons.get(c_num)));
//					marker_list2.get(c_num).add(marker);
//			}	}
				for(Polygon p : poly_list.get(c_num)) p.setVisible(true);
				
			}else{
				for(Polygon mk: poly_list.get(c_num)) mk.setVisible(false);
			}
			
			//mDrawerLayout.closeDrawer(mDrawerLinearLayout);
						
		}
	
    }

    
    // draw voronoi
    private void drawVoronoi(ArrayList<Coordinate> coords, Map<Coordinate, Integer> clusterlist){
    	
    	if(poly_list==null) poly_list = new ArrayList<ArrayList<Polygon>>();
    	
    	// remove current voronoi
  		for(ArrayList<Polygon> ary : poly_list){  
  			for(Polygon p : ary)
  				p.remove(); 
  		}
    	poly_list.clear();
    	poly_list.add(new ArrayList<Polygon>()); // fake array 
    	
    	//
    	GeometryFactory fact = new GeometryFactory();
	    VoronoiDiagramBuilder vdb = new VoronoiDiagramBuilder();
	    vdb.setSites(coords); // create voronoi
	    Geometry voronoi = vdb.getDiagram(fact); // geometory collections
	 
	    
	    Map<Integer, ArrayList<Geometry>> regions = new HashMap<Integer, ArrayList<Geometry>>();
	   
	    // clustering regions 
	    for(int i=0;i<voronoi.getNumGeometries();i++){ // loop all geometories
	    	Geometry geom = voronoi.getGeometryN(i); // each geometory
	    	int cluster_num = clusterlist.get(geom.getUserData()); // key coordinate val cluster_num
			if(regions.containsKey(cluster_num)){ // num of cluster already made then add
				regions.get(cluster_num).add(geom);
			}else{ // otherwise make new 
				ArrayList<Geometry> cl = new ArrayList<Geometry>();
				cl.add(geom);
				regions.put(cluster_num, cl);
			}	    	
	    }
	    
	    Geometry clip = clipper();

	    for(Integer key : regions.keySet()){
	    	Geometry mpoly = new CascadedPolygonUnion(regions.get(key)).union();
//	        Random rnd = new Random();
//	    	int col = Color.argb(100,rnd.nextInt(256),rnd.nextInt(256),rnd.nextInt(256));

	    	int col = Color.argb(100, colurs.get(key)[0], colurs.get(key)[1], colurs.get(key)[2]);
	    	ArrayList<Polygon> arraylist = drawpolygon(mpoly, clip, col);
	    	poly_list.add(arraylist);
	    }
	    Log.e("pc",regions.keySet().size()+" size of key");
	    Log.e("pc",poly_list.size()+" size of polylist");
	    	    
    }
    
    
    // clipping polygon
    private Geometry clipper(){
        WKTReader wktReader = new WKTReader();
        Geometry clip = null;
        try{
        	// String lines = "58.602671 -8.369429, 58.989711 -2.063277, 52.836028 2.375199, 50.659982 2.309281, 49.418196 -6.655562,53.677257 -5.117477, 58.602671 -8.369429";
        	//clip = wktReader.read("POLYGON((58.602671 -8.369429, 58.989711 -2.063277, " +
       		//	"52.836028 2.375199, 50.659982 2.309281, 49.418196 -6.655562,53.677257 -5.117477, 58.602671 -8.369429))");
        	clip = wktReader.read("POLYGON((" +coast +"))");
        
        }catch  (Exception e){
        	String str = e.getMessage();
        	Log.e("pf","Main activity"+str);
        }
        return(clip);
    }
    // draw multi-polygon component wisely,  return an array of polygons. 
    private ArrayList<Polygon> drawpolygon(Geometry mpoly, Geometry clipper, int col) {
    	
    	ArrayList<Polygon> arraylist = new ArrayList<Polygon>();	
        	
    	for(int j=0;j<mpoly.getNumGeometries();j++){
	    	Geometry geom = mpoly.getGeometryN(j);
	    	if(!clipper.contains(geom)){
	    		geom = geom.intersection(clipper);
	    	}	    	
	    	
	    	// polygon options 
	        PolygonOptions poly = new PolygonOptions();
	        poly.fillColor(col);
	        poly.strokeWidth((float) 0.0);
	        
	    	ArrayList<LatLng> array = poly2latlng(geom);
	    	
	    	// fix geometry info if the starting geolocation and end geolocation are not the same.
	    	if(!array.get(0).equals(array.get(array.size()-1))){   		
	    		Log.e("pc", "Start and end latnon don't match - need to fix it");
	    		fixGeoInfoAndPlot(poly,array,arraylist);

	    	}else{ // if geom is normal
	    		for(LatLng point : array) poly.add(point);

	    		Polygon polygon =  mMap.addPolygon(poly);
	    		arraylist.add(polygon);
	    	}
	    		    	
	    }
  
    	return arraylist;
    }
    
	private void fixGeoInfoAndPlot(PolygonOptions poly, ArrayList<LatLng> geoms, ArrayList<Polygon> ary) {
		//fix and make another array in array list
		// I believe the geometry wasn't separated and contains more than 2 geometries  	
		boolean init = true;
		LatLng top=null;
		for(LatLng geo : geoms){
			if(init){
				top = geo;
				poly.add(geo);
				init=false;
			}else{	
				if(!top.equals(geo))	poly.add(geo);
				else{
					poly.add(geo);
					Polygon polygon =  mMap.addPolygon(poly); // ploting to map 
					ary.add(polygon);
					init = true;
				}
			}
			
		}
		
			Polygon polygon =  mMap.addPolygon(poly);
			ary.add(polygon);
	}

	// convert polygon to latlng
	private ArrayList<LatLng> poly2latlng(Geometry poly){
		ArrayList<LatLng> array = new ArrayList<LatLng>();
		Coordinate[] coords = poly.getCoordinates();
		for(int i=0;i<poly.getNumPoints();i++){
			array.add(new LatLng(coords[i].x,coords[i].y));
		}
		return array;
	}
}
