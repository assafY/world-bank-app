package org.worldbank.seg_2g.worldbankapp;



import java.io.InputStream;

import org.worldbank.seg_2g.worldbankapp.R;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Help1Fragment extends Fragment {
	
	InputStream assetManager;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_multi_help1, null);
		
		TextView txtFileName=(TextView)rootView.findViewById(R.id.txtDisplay);

	try {
		
		assetManager= getActivity().getAssets().open("help.txt");
		
		int size = assetManager.available();
		byte[] buffer = new byte[size];
		assetManager.read(buffer);
		assetManager.close();
		
		String Plaintext = new String(buffer);
		System.out.println(Plaintext);
		txtFileName.setText(Plaintext);
		
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
		
		return rootView;//have to return this other wise nothing will appear there
	}
}
