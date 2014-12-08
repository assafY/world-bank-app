package org.worldbank.seg_2g.worldbankapp;


import org.worldbank.seg_2g.worldbankapp.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;



public class MenuFragment extends Fragment {
	
	//naming two variables
	Fragment frag;
	FragmentTransaction fragTransaction;
	
	public MenuFragment()
	{
		
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_multi,container, false);
		
		frag = new Help1Fragment();
		fragTransaction = getFragmentManager().beginTransaction().add(R.id.container, frag);
		fragTransaction.commit();
		
		Button btnKoala = (Button)view.findViewById(R.id.buttonHelp1);
		Button btnPeguin = (Button)view.findViewById(R.id.buttonHelp2);
		Button btnOther = (Button)view.findViewById(R.id.buttonHelp3);
		
		
		btnKoala.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				frag = new Help1Fragment();
				fragTransaction = getFragmentManager().beginTransaction().replace(R.id.container, frag); // replace will remove the fragement and then rettach it again
				fragTransaction.commit();
				

			}
		});
		
		btnPeguin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				frag = new Help2Fragment();
				fragTransaction = getFragmentManager().beginTransaction().replace(R.id.container, frag); // replace will remove the fragement and then rettach it again
				fragTransaction.commit();

			}
		});

		btnOther.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				frag = new Help3Fragment();
				fragTransaction = getFragmentManager().beginTransaction().replace(R.id.container, frag); // replace will remove the fragement and then rettach it again
				fragTransaction.commit();

			}
		});

		return view;
	}
}
