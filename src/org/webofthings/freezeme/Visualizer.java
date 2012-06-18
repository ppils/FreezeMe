package org.webofthings.freezeme;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.webofthings.freezeme.dao.AbstractFoodDAO;
import org.webofthings.freezeme.dao.FakeFoodDAO;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Visualizer extends Activity {
	String urlToAccessMoreInfo = "http://";
	final ExecutorService ex = Executors.newSingleThreadExecutor();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.visualizer);
		
		//final Bundle receiveBundle = this.getIntent().getExtras();
		final String uri = this.getIntent().getData().toString();
		
		Log.d(AppData.log, uri);
		
		this.ex.submit(new DataLoader(new FakeFoodDAO())); // Ignore the Future<>
    }
    
    public void goToURL(View v) {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.urlToAccessMoreInfo));
    	startActivity(browserIntent);
    }
    
	@Override
	public void onResume() {
		super.onResume();
	}
	
	class DataLoader implements Runnable {
		AbstractFoodDAO dao;
		
		public DataLoader(AbstractFoodDAO dao) {
			this.dao = dao;
		}

		@Override
		public void run() {
			this.dao.load();
			
	        //final Bundle receiveBundle = this.getIntent().getExtras();
			Visualizer.this.urlToAccessMoreInfo = this.dao.getInfoUrl();
	        
	        final TextView lblName = (TextView) findViewById(R.id.textView1);
	        lblName.setText(this.dao.getName());
	        
	        final TextView lblDaysLeft = (TextView) findViewById(R.id.textView2);
	        lblDaysLeft.setText(this.dao.getExpiration());
	        
	        try {
	    	  ImageView i = (ImageView)findViewById(R.id.imageView1);
	    	  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(this.dao.getImageUrl()).getContent());
	    	  i.setImageBitmap(bitmap); 
	    	} catch (MalformedURLException e) {
	    		Log.d(AppData.log, e.getMessage());
	    	} catch (IOException e) {
	    		Log.d(AppData.log, "Something went wrong loading the image");
	    		Log.d(AppData.log, e.getMessage());
	    	}
		}
	}
}