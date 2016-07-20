package thu.wireless.mobinet.hsrtest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class XGService extends Service {
	private static final String TAG = "ServiceDemo";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override  
    public void onCreate() {  
        Log.v(TAG, "ServiceDemo onCreate");  
        super.onCreate();  
    }  
      
    @Override  
    public void onStart(Intent intent, int startId) {  
        Log.v(TAG, "ServiceDemo onStart");
        Config.myXGtest = new XGtest(Config.tel);
        super.onStart(intent, startId);  
    }  
      
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        Log.v(TAG, "ServiceDemo onStartCommand");  
        return super.onStartCommand(intent, flags, startId);  
    }  
}
