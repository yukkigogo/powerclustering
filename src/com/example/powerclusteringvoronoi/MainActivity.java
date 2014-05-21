package com.example.powerclusteringvoronoi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.http.message.BufferedHeader;
import org.json.JSONException;

import com.example.powerclusteringvoronoi.controller.BusesEdgeInitialController;
import com.example.powerclusteringvoronoi.controller.ClusterDataReaderContorller;
import com.example.powerclusteringvoronoi.controller.ColourSchameContorller;
import com.example.powerclusteringvoronoi.controller.IDAdmittancePowerFlowsController;
import com.example.powerclusteringvoronoi.model.Bus;
import com.example.powerclusteringvoronoi.model.Cluster;
import com.example.powerclusteringvoronoi.model.Edge;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
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
import com.google.common.primitives.Doubles;

import android.R.color;
import android.R.integer;
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
import android.view.Window;
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




public class MainActivity extends  FragmentActivity implements OnMapLongClickListener{
	
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

	static final String Colur_CSV = "colour_deg.csv";	
	
	HashMap<String, Pair<Bus,Marker>> buses;
	ArrayList<Pair<Edge,Polyline>> edges;
	HashMap<Coordinate, String> geo_list;
	
	
	private ClusterDataReaderContorller cdrc;
	
	// coastline for clipper 
	String coast;
	
	
	
	// colour
	ArrayList<int[]> colurs;
	
	ArrayList<String> PFs;
	ArrayList<String> PFs10R;
	//ArrayList<Marker> bus_edge_marker_list;
	//ArrayList<ArrayList<Marker>> marker_list2;
	
	HashMap<Integer, Cluster> cluster_list;
	boolean isFuzzy=false;
	
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

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
      //Initialization for system  
       assetManager = getAssets();		


	   IDAdmittancePowerFlowsController controller2 = new IDAdmittancePowerFlowsController();
	   //Ads = controller2.getNameFileToArray(ADs_CSV, assetManager);
	   PFs = controller2.getNameFileToArray(PFs_CSV, assetManager);
       PFs10R = controller2.getNameFileToArray(PFs_10CSV, assetManager);
	   
       cdrc = new ClusterDataReaderContorller(assetManager, PFs10R, PFs);
       
       //create colour schame
       ColourSchameContorller csc = new ColourSchameContorller();
       colurs = csc.getColurSchameList(Colur_CSV, assetManager);
	
	   
       //set up initial map
        setUpMapIfNeeded();
        
        // initial setting  for buses and edges icons -  read file and plot on the map 
        BusesEdgeInitialController controller = new BusesEdgeInitialController();
        buses = controller.getBusListFromCSV(BUSES_CSV, assetManager, mMap);
        edges = controller.getEdgesListFromCSV(EDGES_CSV, assetManager, buses, mMap);
        	
        // obtain costline latlng string
        coast = controller.getCoastPairs(UKcoastline,assetManager);

        drawCluster(0);
        //drawFuzzyCluster(1);    
        
        //set radio button
        setRadios();
        
        
    }

	private void setAllClusters(boolean b){

		for (int c : cluster_list.keySet()){
	    		mDrawerListView.setItemChecked(c-1, b);
	    		cluster_list.get(c).setVisiblity(b);
	    		for(Polygon p : cluster_list.get(c).getPolygonList()) p.setVisible(b);
	    	}	

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
				drawCluster(0);								
				break;

			case R.id.radio_case2:
				drawCluster(1);
				break;

			case R.id.radio_case3:
				drawCluster(2);
				break;

			case R.id.radio_case4:
				drawCluster(3);
				break;
				
			case R.id.radio_case5:
				drawCluster(4);
				break;

			case R.id.radio_case6:
				drawCluster(5);
				break;

			case R.id.radio_case7:
				drawCluster(6);
				break;

			case R.id.radio_case8:
				drawCluster(7);
				break;

			case R.id.radio_case9:
				drawCluster(8);
				break;

			case R.id.radio_case10:
				drawCluster(9);
				break;

			case R.id.fuzzy1:
				drawFuzzyCluster(1);
				break;
			case R.id.fuzzy2:
				drawFuzzyCluster(2);
				break;
			case R.id.fuzzy3:
				drawFuzzyCluster(3);
				break;
	
				
				
			default:
				break;
			}
			
			
		}
	});
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
    	
    	ArrayList<String> clusterTypes = new ArrayList<String>();
    	
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
    	
    	// delete button
    	if(isFuzzy){
    		b_all.setVisibility(View.GONE);
    		b_clear.setVisibility(View.GONE);
    	}else{
    		b_all.setVisibility(View.VISIBLE);
    		b_clear.setVisibility(View.VISIBLE);
    	}
     
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
		  
		  //setup Longclick
		  mMap.setOnMapLongClickListener(this);
		  
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


    // build voronoi geometories
    private void buildVoronoiRegion(HashMap<String, Pair<Bus,Marker>> bus){
    	
		geo_list = new HashMap<Coordinate, String>();

		for (Pair<Bus,Marker> p : bus.values()) 
			geo_list.put(new Coordinate(
    			p.first.getLatLng().latitude, p.first.getLatLng().longitude), p.first.getName());
    	
    	GeometryFactory fact = new GeometryFactory();
	    VoronoiDiagramBuilder vdb = new VoronoiDiagramBuilder();
	    vdb.setSites(geo_list.keySet()); // create voronoi
	    Geometry voronoi = vdb.getDiagram(fact); // geometory collections
	    
	    for(int i=0;i<voronoi.getNumGeometries();i++){ // loop all geometories
	    	Geometry geom = voronoi.getGeometryN(i); // each geometory
	    	//Log.v("pc", "lat lon da yo " + ((Coordinate) geom.getUserData()).x + "  and " + ((Coordinate) geom.getUserData()).y);
	    	String name = geo_list.get(geom.getUserData());

	    	bus.get(name).first.setVoronoiGeo(geom);
	    }
	    
    }
    
    private void drawCluster(int pf_case){

    	// collect clusters and create cluster objects 	
    	// get data
		HashMap<String, Integer> list = cdrc.readClusterDataBinary(pf_case);	
		HashMap<String,Double> totalpf = cdrc.readTotalPF(pf_case);
		HashMap<String, Pair<Bus,Marker>> good_bus = new HashMap<String, Pair<Bus,Marker>>();
		
		// create new cluster_list or delete clusters in the list
		if(cluster_list==null){
			cluster_list = new HashMap<Integer, Cluster>();
		}else{
	    	// detele previous clusters
	    	for(Cluster c : cluster_list.values()){
	    			for (Polygon p: c.getPolygonList()) p.remove();
	    	}
			cluster_list.clear();
		}
    	isFuzzy=false;
		
		for(Pair<Bus, Marker> p:buses.values()){
			Bus b = p.first;			
			if(list.containsKey(b.getName())){
				int c_num = list.get(b.getName());
				if(!cluster_list.containsKey(c_num)){
					Cluster cluster = new Cluster(c_num);
					cluster.addBus(b);
					cluster.addTotalPF(totalpf.get(b.getName()));
					cluster_list.put(c_num, cluster);
					cluster.addTotalPF(totalpf.get(b.getName()));
					p.second.setSnippet("Cluster Number"+ c_num);
				}else{
					Cluster cluster = cluster_list.get(c_num);
					cluster.addBus(b);
					cluster.addTotalPF(totalpf.get(b.getName()));
					p.second.setSnippet("Cluster Number"+ c_num);
				}				
				good_bus.put(b.getName(), p);
			}else{
				b.setClusterNum(0);
			}
				
		}
		buildVoronoiRegion(good_bus);

		cluster_list = fixClusterOrder(cluster_list);
		
	    // Border 
	    Geometry clip = clipper();
		int i=0;
		for (Cluster cluster : cluster_list.values()){
			
			ArrayList<Geometry> l = new ArrayList<Geometry>();
			for (Bus b : cluster.getBusList()) l.add(b.getVoronoiGeo());
			Geometry mpoly = new CascadedPolygonUnion(l).union();
			
			cluster.setClusterColour(colurs.get(i));
			int col = Color.argb(170, colurs.get(i)[0], colurs.get(i)[1], colurs.get(i)[2]);
			
			cluster.addPolygon(drawpolygon(mpoly, clip, col, cluster));
			
			i++;
		}
		
		setUpDrawer(cluster_list.size());
    }
    
    private void drawFuzzyCluster(int type){
    	
    	// data 
    	ArrayList<HashMap<String, Double>> f_list = cdrc.readClusterDataFuzzy(type);
    
    	if(cluster_list==null){
			cluster_list = new HashMap<Integer, Cluster>();
		}else{
	    	// detele previous clusters
	    	for(Cluster c : cluster_list.values()){
	    			for (Polygon p: c.getPolygonList()) p.remove();
	    	}
			cluster_list.clear();
		}

    	if(isFuzzy)
    		for (Pair<Bus,Marker> p: buses.values()){ 
    			if(p.first.getListClusters() !=null) 
    				p.first.cleanListClusters();
    		}
    	isFuzzy=true;
    	
    	// select matching buses between PFs and coordinate
    	HashMap<String, Pair<Bus,Marker>> good_bus = new HashMap<String, Pair<Bus,Marker>>();
		for(String name: PFs) if(buses.containsKey(name)) good_bus.put(name, buses.get(name));
    	buildVoronoiRegion(good_bus);
    
		
		for(int i=0;i<f_list.size();i++){
			HashMap<String, Double> ls = f_list.get(i);
			
			// median, u
			ArrayList<Double> pripri = new ArrayList<Double>();
			for(double d: ls.values()){
				if(d!=1) pripri.add(d);
			}
			
			
			double l_quartile = 0.25;
			double med = 0.5;
			double u_quartile = 0.25;
			
			if(pripri.size()>0){
				Median median = new Median();			
				
				double[] list = Doubles.toArray(pripri);
				l_quartile = median.evaluate(list , 25.0);
				med = median.evaluate(list , 50.0);
				u_quartile = median.evaluate(list , 75.0);
			}
			
			
			int c_num = i+1;
			Cluster cluster = new Cluster(c_num, true);	
			cluster.setClusterColour(colurs.get(i));
			for(String name : ls.keySet()){
				if(buses.containsKey(name)){
					Bus b = buses.get(name).first;
					
					//cluster.addFuzzyBus(b, getOpacitylevel(ls.get(name)), ls.get(name));
					cluster.addFuzzyBus(b, convertOpacityVal(ls.get(name), l_quartile,
							med, u_quartile), ls.get(name));
					b.setListClusters(new Pair<Integer,Double>(c_num , ls.get(name)));
				}
			}				
			cluster_list.put(c_num, cluster);
		}
		
		// draw cluster 1
		drawFuzzyClusterPolygon(cluster_list.get(1));
		// setup drawer but 
		setUpDrawer(cluster_list.size());
		for(int i=1;i<cluster_list.size();i++) mDrawerListView.setItemChecked(i, false);
    }
   
    private void drawFuzzyClusterPolygon(Cluster cl){
		// boarder
		Geometry clip = clipper();

    	for(Integer o_level : cl.getFuzzyBus().keySet()){	
			//Geometry mpoly = new CascadedPolygonUnion(cl.getFuzzyBus().get(o_level)).union();
    	
			//int col = Color.argb(o_level, cl.getClusterColour()[0], cl.getClusterColour()[1], cl.getClusterColour()[2]);
			//int col = Color.argb(o_level, 141,0,211);
			
			
    		List<Integer> c = getHeatCol(o_level);
			int col = Color.argb(150, c.get(0), c.get(1), c.get(2));
			
			
			//cl.addPolygon(drawpolygon(mpoly, clip, col));
			
			for(Geometry mpoly : cl.getFuzzyBus().get(o_level)){
				cl.addPolygon(drawpolygon(mpoly, clip, col, cl));
			}
			
		}
    }
    
    private List<Integer> getHeatCol(int col){
    	
    	switch (col) {
		case 51:
			return Arrays.asList(170,246,11);
		case 90:
			return Arrays.asList(224,188,0);
		case 128:
			return Arrays.asList(244,140,0);
		case 166:
			return Arrays.asList(253,102,0);
		case 205:
			return Arrays.asList(255,3,0);	
		default:
			return null;
		}
		
    	
    }
    
    
    private HashMap<Integer,Cluster> fixClusterOrder(HashMap<Integer,Cluster> list){
    	ArrayList<Cluster> clist = new ArrayList<Cluster>();
    	for(Cluster c : list.values()){
    		clist.add(c);
    	}
    	Collections.sort(clist);
    	list.clear(); 	
    	for(int i=0;i<clist.size();i++){
    		Cluster c = clist.get(i);
    		int c_num = i+1;
    		c.setClusterNum(c_num);
    		list.put(c_num, c);		
    	}
    	return list;
    }
    
    private int convertOpacityVal(double d, double l, double m, double u){
    	if(d>0 && d<=l) return 51;
    	else if(d>l && d<=m) return 90;
    	else if(d>m && d<=u) return 128;
    	else if(d>u && d<1) return 166;
    	else return 205;
    	
    	
    }
    
    private int getOpacitylevel(Double d){	
    	if(d>0 && d<=0.25) return 51;
    	else if(d>0.25 && d<=0.5) return 90;
    	else if(d>0.5 && d<=0.75) return 128;
    	else if(d>0.75 && d<1) return 166;
    	else return 205;
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
    private ArrayList<Polygon> drawpolygon(Geometry mpoly, Geometry clipper, int col, Cluster cl) {
    	
    	ArrayList<Polygon> arraylist = new ArrayList<Polygon>();	
        	
    	for(int j=0;j<mpoly.getNumGeometries();j++){
	    	Geometry geom = mpoly.getGeometryN(j);
	    	if(!clipper.contains(geom)){
	    		geom = geom.intersection(clipper);
	    		cl.addGeoList(geom);
	    	}else{
	    		cl.addGeoList(geom);
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
			
			if(!isFuzzy){ // normal cluster
				if(mDrawerListView.isItemChecked(position)){
					cluster_list.get(c_num).setVisiblity(true);
					for(Polygon p : cluster_list.get(c_num).getPolygonList()) p.setVisible(true);
					
				}else{
					cluster_list.get(c_num).setVisiblity(false);
					for(Polygon mk: cluster_list.get(c_num).getPolygonList()) mk.setVisible(false);
				}
			}else{  // Fuzzy 
				if(mDrawerListView.isItemChecked(position)){
					Log.v("pc", "show size "+cluster_list.get(c_num).getPolygonList().size());
					if(cluster_list.get(c_num).getPolygonList().size()>0){
						Log.v("pc", " polygon exist" + c_num);
						for(Polygon p : cluster_list.get(c_num).getPolygonList()) p.setVisible(true);
					}else{
						Log.v("pc", " new polygon" + c_num);
						drawFuzzyClusterPolygon(cluster_list.get(c_num));
					}
						
						
				}else{
					Log.v("pc", "show size "+cluster_list.get(c_num).getPolygonList().size());
					cluster_list.get(c_num).setVisiblity(false);
					for(Polygon p : cluster_list.get(c_num).getPolygonList()) p.setVisible(false); 
				}
			}
			
			//mDrawerLayout.closeDrawer(mDrawerLinearLayout);
						
		}
	
    }


	@Override
	public void onMapLongClick(LatLng point) {
		
		// show cluster information 
		if(!isFuzzy){
			for(Cluster cl: cluster_list.values()){
				GeometryFactory factory = new GeometryFactory();
				Point po = factory.createPoint(new Coordinate(point.latitude, point.longitude));
				Log.e("pc", cl.getGeoList().size()+ "cluster size");

				outerloop:
				for(Geometry geo : cl.getGeoList()){
					if(po.intersects(geo)){ 			
						if(cl.getVisiblity()){
							
							
							
//							ClusterDialog clusterDialog = new ClusterDialog(cl);
//							clusterDialog.show(getSupportFragmentManager(), "pc");
							
						}
						Log.v("pc",point.latitude +" and "+ point.longitude + "cluster num" + cl.getClusterNum());
						break outerloop;
					}else{
						Log.v("pc", "without cluster" + cl.getClusterNum());
					}
				}
			}
		
		}
	}
	
}
