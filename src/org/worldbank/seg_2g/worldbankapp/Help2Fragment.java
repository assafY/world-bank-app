package org.worldbank.seg_2g.worldbankapp;



import org.worldbank.seg_2g.worldbankapp.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Help2Fragment extends Fragment {
	
	public Help2Fragment()
	{
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_multi_help2, container, false);
		
		return rootView;
	}
}
