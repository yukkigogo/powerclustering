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
import android.widget.ImageView;

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
