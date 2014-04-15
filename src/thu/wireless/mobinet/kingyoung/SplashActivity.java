package thu.wireless.mobinet.kingyoung;

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

public class SplashActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);
	    // Make sure the splash screen is shown in portrait orientation
	    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	    
		// 创建日志路径
		try {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				String mobilePath = android.os.Environment.getExternalStorageDirectory() + "/MobiNetPlus";
				String pathDate = Config.dirDateFormat.format(new Date(System.currentTimeMillis()));
				File mobileFile = new File(mobilePath);
				mobileFile.mkdirs();
				mobilePath = mobilePath + "/" + pathDate;
				mobileFile = new File(mobilePath);
				mobileFile.mkdirs();
				Config.fosMobile = new FileOutputStream(mobilePath + "/Mobile.txt", true);
				Config.fosSignal = new FileOutputStream(mobilePath + "/Signal.txt", true);
				Config.fosSpeed = new FileOutputStream(mobilePath + "/Speed.txt", true);
				Config.fosCell = new FileOutputStream(mobilePath + "/Cell.txt", true);
				Config.fosUplink = new FileOutputStream(mobilePath + "/Uplink.txt", true);
				Config.fosDownlink = new FileOutputStream(mobilePath + "/Downlink.txt", true);
				Config.fosTCPFlow = new FileOutputStream(mobilePath + "/DownlinkFlow.txt", true);
				Config.fosPing = new FileOutputStream(mobilePath + "/Ping.txt", true);
				Config.fosAddition = new FileOutputStream(mobilePath + "/Addition.txt", true);
				Config.fosDNS = new FileOutputStream(mobilePath + "/DNS.txt", true);
				Config.fosTrace = new FileOutputStream(mobilePath + "/Trace.txt", true);
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
