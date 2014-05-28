package com.example.powerclusteringphp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;











import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class PowerFlowReaderAsyncTaskContorller extends
		AsyncTask<Integer, Integer, HashMap<String, Double> > {

	ProgressDialog dialog;
	Context context;
	String URL = "http://152.78.41.26/powerclustering/androidcsv.php";
	String type="1";
	
	ArrayList<String> PFs10R;

	
	public PowerFlowReaderAsyncTaskContorller(Context con, ArrayList<String>pfs10) {
		this.PFs10R = pfs10;
		this.context=con;
	}
	
	@Override
	protected HashMap<String, Double> doInBackground(Integer... params) {
		HashMap<String, Double> list = new HashMap<String, Double>();
		
	  	HttpClient httpClient = new DefaultHttpClient();
	  	HttpPost httpPost = new HttpPost(URL);
		
		String key = Integer.toString(params[0]);
		
		
		try {
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("type", type));
			pairs.add(new BasicNameValuePair("key", key));
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = httpClient.execute(httpPost);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String str;
			
				
				int count=1;
				while ((str = br.readLine()) !=null) {
					String name;						
					name =PFs10R.get(count);
					
					double val = Double.parseDouble(str);
					//int col = colours.get(cluster_num);
					
	 				list.put(name, val);
	 				count++;
				}
			}catch (IOException e) {
				String s = e.getMessage();
				Log.e("pc", s);
			}
			
		
		
		return list;
	}

	@Override
	protected void onPostExecute(HashMap<String, Double> result) {
		super.onPostExecute(result);
		dialog.dismiss();
	}
	
	@Override
	protected void onPreExecute() {
		 	dialog = new ProgressDialog(context);
		    dialog.setTitle("Please wait");
		    dialog.setMessage("Reading the data...");
		    dialog.show();
	}
	
	
}
