package thu.wireless.mobinet.hsrtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);
		// Make sure the splash screen is shown in portrait orientation
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		// Create Log Directory
		try {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				String mobilePath = android.os.Environment
						.getExternalStorageDirectory()+"";
				String pathDate = Config.dirDateFormat.format(new Date(System
						.currentTimeMillis()));

				Config.fos4Cell = mobilePath+"/cell";
				File mobileFile = new File(Config.fos4Cell);
				if (!mobileFile.exists()){
					mobileFile.mkdirs();
				}
				
				Config.fos4Ping = mobilePath+"/ping";
				mobileFile = new File(Config.fos4Ping);
				if (!mobileFile.exists()){
					mobileFile.mkdirs();
				}
				
				Config.fos4Tcpdump = mobilePath+"/tcpdump";
				mobileFile = new File(Config.fos4Tcpdump);
				if (!mobileFile.exists()){
					mobileFile.mkdirs();
				}
				
				mobilePath = mobilePath + "/HSR";
				
				mobileFile = new File(mobilePath);
				mobileFile.mkdirs();
				
				mobilePath = mobilePath + "/" + pathDate;
				mobileFile = new File(mobilePath);
				mobileFile.mkdirs();
				Config.fosGps = new FileOutputStream(mobilePath+"/Gps.txt",true);
				Config.fosMobile = new FileOutputStream(mobilePath
						+ "/Mobile.txt", true);
				Config.fosUplink = new FileOutputStream(mobilePath
						+ "/Uplink.txt", true);
				Config.fosDownlink = new FileOutputStream(mobilePath
						+ "/Downlink.txt", true);
				Config.fosPing = new FileOutputStream(mobilePath + "/Ping.txt",
						true);
			} else {
				return;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent = new Intent(SplashActivity.this,
						MainActivity.class);
				SplashActivity.this.startActivity(mainIntent);
				SplashActivity.this.finish();
			}
		}, 2000);
	}
}
