package com.example.powerclusteringvoronoi;

import com.example.powerclusteringvoronoi.model.Cluster;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

public class ClusterDialog extends DialogFragment {

	Cluster cluster;
	
	public ClusterDialog(Cluster cl) {
		this.cluster=cl;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		setRetainInstance(true);		
		View view = getActivity().getLayoutInflater().inflate(R.layout.main_msg_detailfdialog, null);
		AlertDialog.Builder builder = new Builder(getActivity());
		setInterface(view);
		builder.setView(view);
		
		
		Dialog dialog = builder.create();
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
		return dialog;
		
	}
	
	private void setInterface(View view) {
		
		TextView clustertitle = (TextView) view.findViewById(R.id.cluster_num);
		clustertitle.setText("Cluster No : "+ cluster.getClusterNum());
		
		TextView totalnum = (TextView) view.findViewById(R.id.total_num);
		totalnum.setText("Total Number of Clusters : "+ cluster.getTotalNode()+" buses");

		TextView totalpf = (TextView) view.findViewById(R.id.total_pf);
		int val = (int) cluster.getTotalPF();
		
		totalpf.setText("Total Power Flow : "+ val +" MW");

		
		// cancel button
		ImageView closebtn = (ImageView) view.findViewById(R.id.closebtn);
		closebtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClusterDialog.this.getDialog().dismiss();
			}
		});
		
	}
	
	
}
