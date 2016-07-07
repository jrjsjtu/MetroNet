package thu.wireless.mobinet.hsrtest;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SpeedTest {
 	SensorManager mSensorManager = null;
 	Sensor aSensor = null;
 	Sensor mSensor = null;
 	Sensor oSensor = null;
 	
 	Handler handler;
    float[] accelerometerValues={0,0,0};  
    float[] magneticFieldValues={0,0,0};  
    float[] orientationvalues=new float[3];  
    float[] rotate=new float[9]; 
    float[] trueacceleration = {0,0,0};
    float[] linear_acce = {0,0,0};
    
    long last_time;

    double speedx=0,speedy=0;
    private SimpleDateFormat bartDateFormat;  
    private String filename;
 	
 	public SpeedTest(SensorManager the_sensor,Handler the_handler){
 		
		long timeInMillis = System.currentTimeMillis();
		last_time = timeInMillis;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		Date date = cal.getTime();
		bartDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
		filename = "speed/"+bartDateFormat.format(date)+".txt";
		bartDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
 		mSensorManager = the_sensor;
 		handler = the_handler;
      	aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      	mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
      	oSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
      	mSensorManager.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_FASTEST);  
        mSensorManager.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST); 
        mSensorManager.registerListener(myListener, oSensor, SensorManager.SENSOR_DELAY_FASTEST);  
        
//        mSensorManager.registerListener(myListener, aSensor, 300000);  
//        mSensorManager.registerListener(myListener, mSensor, 300000); 
//        mSensorManager.registerListener(myListener, oSensor, 300000);  
        
        last_time = System.currentTimeMillis();
 	}
 	
 	final SensorEventListener myListener=new SensorEventListener(){  
  	  
        public void onAccuracyChanged(Sensor sensor, int accuracy) {  
            // TODO Auto-generated method stub  
        }  
  
        public void onSensorChanged(SensorEvent event) {  
            // TODO Auto-generated method stub  
            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){  
                accelerometerValues=event.values;  
            } else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){  
                magneticFieldValues=event.values;  
            } else if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
            	linear_acce=event.values;  
            	
                orientationvalues[2] *= -1;
                trueacceleration[0] =(float) (linear_acce[0]*(Math.cos(orientationvalues[2])*Math.cos(orientationvalues[0])+Math.sin(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.sin(orientationvalues[0])) + linear_acce[1]*(Math.cos(orientationvalues[1])*Math.sin(orientationvalues[0])) + linear_acce[2]*(-Math.sin(orientationvalues[2])*Math.cos(orientationvalues[0])+Math.cos(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.sin(orientationvalues[0])));
                trueacceleration[1] = (float) (linear_acce[0]*(-Math.cos(orientationvalues[2])*Math.sin(orientationvalues[0])+Math.sin(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.cos(orientationvalues[0])) + linear_acce[1]*(Math.cos(orientationvalues[1])*Math.cos(orientationvalues[0])) + linear_acce[2]*(Math.sin(orientationvalues[2])*Math.sin(orientationvalues[0])+ Math.cos(orientationvalues[2])*Math.sin(orientationvalues[1])*Math.cos(orientationvalues[0])));
                trueacceleration[2] = (float) (linear_acce[0]*(Math.sin(orientationvalues[2])*Math.cos(orientationvalues[1])) + linear_acce[1]*(-Math.sin(orientationvalues[1])) + linear_acce[2]*(Math.cos(orientationvalues[2])*Math.cos(orientationvalues[1])));
                
                
                long cur_time = System.currentTimeMillis();
                long minus = (cur_time-last_time);
                last_time = cur_time;
                speedx += trueacceleration[0]*minus/1000;
                speedy += trueacceleration[1]*minus/1000;
                Message msg = new Message();
                Bundle data = new Bundle();
                double acce = Math.sqrt(speedx*speedx+speedy*speedy);
                String result = trueacceleration[0]+";"+trueacceleration[1]+";"+trueacceleration[2]+";"+ "\r\n";
                saveDataToFile(result);
//                data.putString("acce", acce+"");
//                msg.setData(data);
//                msg.what = 4;
//                handler.sendMessage(msg);
            }
            SensorManager.getRotationMatrix(rotate, null, accelerometerValues, magneticFieldValues);  
            SensorManager.getOrientation(rotate, orientationvalues);  
            //经过SensorManager.getOrientation(rotate, values);得到的values值为弧度  
            //转换为角度  
                       
        }
    };  

    
    private void saveDataToFile(String LocalFileWriteBufferStr) {
        /* write measurement data to the output file */
    	if (LocalFileWriteBufferStr.equals("No network information available...")){
    		return;
    	}
		long timeInMillis = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		Date date = cal.getTime();
   	    try {
//   	    	fos.write(LocalFileWriteBufferStr.getBytes());
//   	    	Log.d("output", LocalFileWriteBufferStr);
		    File root = Environment.getExternalStorageDirectory();
            if (root.canWrite()){
                File logfile = new File(root, filename);
                FileWriter logwriter = new FileWriter(logfile, true); /* true = append */
                BufferedWriter out = new BufferedWriter(logwriter);
                //out.write(bartDateFormat.format(date)+";"+System.currentTimeMillis()+";"); 
                /* now save the data buffer into the file */
                out.write(LocalFileWriteBufferStr);
//                Log.d("output", LocalFileWriteBufferStr);
                out.close();
            }
        }    
        catch (IOException e) {
        	e.printStackTrace();
        }
    }
}