package thu.wireless.mobinet.hsrtest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.os.Environment;
import android.os.PowerManager;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class XGtest extends Thread{
    private String filename;
    private TelephonyManager Tel;
    private MyPhoneStateListener MyListener;
    private String lastinfo = "";
    private String lastStrength = "";
    SimpleDateFormat bartDateFormat;  
    FileOutputStream fos;
    SignalStrength last_signalStrength = null;
    
    public XGtest(TelephonyManager para_tel) {
		MyListener = new MyPhoneStateListener();
		Tel = para_tel;
		long timeInMillis = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		Date date = cal.getTime();
		bartDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
		filename = "cell/"+bartDateFormat.format(date)+".txt";
//		try {
//			fos = new FileOutputStream(filename, true);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		bartDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		start_listen();
    }
    
    //由于google 4在锁屏后会导致断网 ，如果用线程进行记录不受断网影响，这一部分仍然在施工中
    public void start_thread_listen(){
    	try{
    		while (true){
    			//SignalStrength my_signal = new SignalStrength();
    		}
    	}catch(Exception e){
    		
    	}
    }
  //由于google 4在锁屏后会导致断网 ，如果用线程进行记录不受断网影响，这一部分仍然在施工中
    public void run(){
    	
    }
    public void start_listen(){
    	Tel.listen(MyListener, Config.phoneEvents);
    }
    private void saveDataToFile(String LocalFileWriteBufferStr, String id) {
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
                out.write(bartDateFormat.format(date)+";"+System.currentTimeMillis()+";"); 
                /* now save the data buffer into the file */
                out.write(id+";");
                out.write(LocalFileWriteBufferStr);
                Log.d("output", LocalFileWriteBufferStr);
                out.close();
            }
        }    
        catch (IOException e) {
        	e.printStackTrace();
        /* don't do anything for the moment */
        }
    }
    
    private class MyPhoneStateListener extends PhoneStateListener {
  	  /* Get the Signal strength from the provider each time there is an update */
        @Override
	     public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        	if (!signalStrength.equals(last_signalStrength)){
            	String outputText; 
    	        outputText = GetBufferString(signalStrength.toString());
    	        saveDataToFile(outputText, "1");
    	        lastinfo = outputText;
    	        last_signalStrength = signalStrength;
        	}
	     }
      
        @Override
		 public void onCellLocationChanged(CellLocation location) {
        	String outputText; 
	    	outputText = GetBufferString(lastStrength);
	        if (!lastinfo.equals(outputText)){
	        	saveDataToFile(outputText, "2");
	         	lastinfo = outputText;
	        }
		}
     
		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {
			String outputText; 
			// TODO Auto-generated method stub
			if (networkType == Config.lastNetworkType) {

			} else {
				Config.lastNetworkType = networkType;
				Config.networkType = networkType;
				SignalUtil.getCurrentNetworkType(networkType);
			}

			switch (state) {
			case TelephonyManager.DATA_DISCONNECTED:// 0
				// 网络断开
				Config.dataConnectionState = "Disconnected";
				outputText = "Disconnected";
				break;
			case TelephonyManager.DATA_CONNECTING:// 1
				// 网络正在连接
				Config.dataConnectionState = "Connecting";
				outputText = "Connecting";
				break;
			case TelephonyManager.DATA_CONNECTED:// 2
				// 网络连接上
				Config.dataConnectionState = "Connected";
				Config.lastConnect = true;
				outputText = "Connected";
				break;
			default:
				Config.dataConnectionState = "Unknown";
				outputText = "Unknown";
				break;
			}
			
			outputText += ";"+ networkType+ "\r\n";

	        if (!lastinfo.equals(outputText)){
		           saveDataToFile(outputText, "3");
		           lastinfo = outputText;
		    }

			super.onDataConnectionStateChanged(state, networkType);
		}
      
      private String GetBufferString(String signalStrength){
    	 lastStrength = signalStrength;
    	 String outputText; 
       	 long NewCellId = 0; 
       	 long NewLacId = 0;
      	 /* a try enclosure is necessary as an exception is thrown inside if the network is currently
      	  * not available.
      	  */
       	 try{
       		outputText = "";
       		//write data
       		//write network type
       		outputText += String.valueOf(Tel.getNetworkType()) + ";";
            
       		//outputText += String.valueOf(signalStrength.getGsmSignalStrength()) +  ";";
           
       		GsmCellLocation myLocation = (GsmCellLocation) Tel.getCellLocation();
                 
       		NewCellId = myLocation.getCid();  
       		outputText += String.valueOf(NewCellId) + ";";
                       
       		NewLacId = myLocation.getLac();
       		outputText += String.valueOf(NewLacId) + ";";
            
			String allSignal = signalStrength.toString();
			try {
				String[] parts = allSignal.split(" ");
				Config.gsmSignalStrength = parts[1];
				outputText += parts[1]+";";
				Config.cdmaDbm = parts[3];
				outputText += parts[3]+";";
				Config.cdmaEcio = parts[4];
				outputText += parts[4]+";";
				Config.evdoDbm = parts[5];
				outputText += parts[5]+";";
				Config.evdoEcio = parts[6];
				outputText += parts[6]+";";
				Config.evdoSnr = parts[7];
				outputText += parts[7]+";";
				Config.lteSignalStrength = parts[8];
				outputText += parts[8]+";";
				Config.lteRsrp = parts[9];
				outputText += parts[9]+";";
				Config.lteRsrq = parts[10];
				outputText += parts[10]+";";
				Config.lteRssnr = parts[11];
				outputText += parts[11]+";";
			} catch (Exception e) {
				// TODO: handle exception
			}
       		
       		 /* Neighbor Cell Stuff */
       		 @SuppressWarnings("deprecation")
			 List<NeighboringCellInfo> nbcell = Tel.getNeighboringCellInfo ();
       		 outputText += String.valueOf(nbcell.size()) + ";";
       		 Iterator<NeighboringCellInfo> it = nbcell.iterator();
       		 while (it.hasNext()) {
       			 outputText += String.valueOf((it.next().getNetworkType())) + ";";
       			 outputText += String.valueOf((it.next().getCid())) + ";";
       		 }
                                     	        
       		 outputText += "\r\n";
       	 }catch(Exception e){
       		 //e.printStackTrace();
       		 outputText = "No network information available..."; 
       	 }
       	 return outputText;
      }
    }
}
