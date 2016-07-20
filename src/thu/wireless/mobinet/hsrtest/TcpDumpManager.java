package thu.wireless.mobinet.hsrtest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;
import android.util.Log;

public class TcpDumpManager {
	String TcpDumpPath;
	final String[] commands;
	public TcpDumpManager(String MyPath){
		TcpDumpPath = MyPath;
        Log.d("TcpDumpManager", "start_measure");
        commands = new String[4];
        commands[0] = "adb shell";  
        commands[1] = "su";  
        commands[2] = "chmod 777 /data/local/tcpdump";  
	}
	
	   public void startCapture(){
		  SimpleDateFormat bartDateFormat =  new SimpleDateFormat("yyyy-MM-dd-HH:mm");  
		  long timeInMillis = System.currentTimeMillis();
	      Calendar cal = Calendar.getInstance();
		  cal.setTimeInMillis(timeInMillis);
		  Date date = cal.getTime();
		  commands[3] = TcpDumpPath+" -p -vv -s 0 -w "+Config.fos4Tcpdump+"/"+bartDateFormat.format(date)+".pcap";
		  Log.d("for log", commands[3]);
	      new Thread(new Runnable(){
			 @Override
			 public void run() {
			 	// TODO Auto-generated method stub
			 	execCmd(commands);
			 }	
	        }).start();
	   }
		
	   public void stopCapture() {  
	        // 找出所有的带有tcpdump的进程  
	        String[] commands = new String[2];  
	        commands[0] = "adb shell";  
	        commands[1] = "ps|grep tcpdump|grep root";  
	        Process process = execCmd(commands);  
	        String result = parseInputStream(process.getInputStream());  
	        if (!TextUtils.isEmpty(result)) {  
	            String[] pids = result.split("\n");  
	            if (null != pids) {  
	                String[] killCmds = new String[pids.length];  
	                for (int i = 0; i < pids.length; ++i) {  
	                    killCmds[i] = "kill -9 " + pids[i];  
	                    Log.d("TcpDumpManager", "try to kill: "+pids[i]);
	                }  
	                execCmd(killCmds);  
	            }  
	        }  
	    } 
	    
	    private static String parseInputStream(InputStream is) {  
	        InputStreamReader isr = new InputStreamReader(is);  
	        BufferedReader br = new BufferedReader(isr);  
	        String line = null;  
	        StringBuilder sb = new StringBuilder();  
	        try {  
	            while ( (line = br.readLine()) != null) {
	            	String[] tmp = line.split("\\s{1,}");
	                sb.append(tmp[1]).append("\n");  
	            }  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	          
	        return sb.toString();  
	    }  
	    
	    public static Process execCmd(String command) {  
	        return execCmd(new String[] { command }, true);  
	    }
	    
	    public static Process execCmd(String[] commands) {  
	        return execCmd(commands, true);  
	    }  
	    
	    public static Process execCmd(String[] commands, boolean waitFor) {  
	        Process suProcess = null;  
	        try {  
	            suProcess = Runtime.getRuntime().exec("su");  
	            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());  
	            for (String cmd : commands) {  
	                if (!TextUtils.isEmpty(cmd)) {  
	                    os.writeBytes(cmd + "\n");  
	                }  
	            }  
	            os.flush();  
	            os.writeBytes("exit\n");  
	            os.flush();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	  
	        if (waitFor) {  
	            boolean retval = false;  
	            try {  
	                int suProcessRetval = suProcess.waitFor();  
	                if (255 != suProcessRetval) {  
	                    retval = true;  
	                } else {  
	                    retval = false;  
	                }  
	            } catch (Exception ex) {  
	                Log.w("Error ejecutando el comando Root", ex);  
	            }  
	        }  
	          
	        return suProcess;  
	    }  

}
