package org.worldbank.seg_2g.worldbankapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;



	public class Splash extends Activity {
		
		RotateAnimation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		

	
	    @Override
	    public void onCreate(Bundle savedInstanceState) { 
	        super.onCreate(savedInstanceState);
	        
	        
	        anim.setRepeatCount(Animation.INFINITE);
    		anim.setDuration(3000);
    		anim.setInterpolator(new LinearInterpolator());
    		
	        setContentView(R.layout.activity_splash);
	        
	        Thread thread = new Thread()
	        {
	        	public void run()
	        	{
	        		final ImageView splashAnim =(ImageView)findViewById(R.id.imageView1);
	        		splashAnim.startAnimation(anim);
	        		
	        		try{
	        			//sleep will indicate the time displaying the splash
	        			
	        			sleep(3500);
	        			//the intent will start the Main Activity when the slash is finished.
	        			Intent intent = new Intent(getApplicationContext(),MainActivity.class);
	        			startActivity(intent);
	        		}
	        		
	        		catch(InterruptedException e)
	        		{
	        			e.printStackTrace();
	        			
	        		}
	        	}
	        };
	        thread.start();
	        

	     
	  
	    }
	}

