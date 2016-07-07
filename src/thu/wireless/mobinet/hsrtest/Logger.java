package thu.wireless.mobinet.hsrtest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class Logger extends Thread{
	public String logFileName;
	public int logInterval; // ms
	public boolean stopFlag;
	public String log="";
	public static final String TAG="MyLogger";
	public static final int FLUSH_COUNT = 100;
	public BufferedWriter out;
	public Logger(String logFileName, int interval){
		super();
		this.logFileName = logFileName;
		this.logInterval = interval;
		this.stopFlag =false;
	}
	public void startLog()
	{
		SimpleDateFormat bartDateFormat =  new SimpleDateFormat("yyyy-MM-dd-HH:mm");  
	    long timeInMillis = System.currentTimeMillis();
	    Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		Date date = cal.getTime();
		try {
			out = new BufferedWriter(new FileWriter(this.logFileName+bartDateFormat.format(date)+".txt",true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.stopFlag = true;
	}
	public void stopLog()
	{
		this.stopFlag = false;
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public abstract String getLogValue();
	
	public void run() {
		try {
		    //out = new BufferedWriter(new FileWriter(this.logFileName,true));
		    SimpleDateFormat bartDateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");  
			while(true){
				if (stopFlag){
						log = "";
						long timeInMillis = System.currentTimeMillis();
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(timeInMillis);
						Date date = cal.getTime();
						log += bartDateFormat.format(date)+";"+System.currentTimeMillis()+";"+ this.getLogValue() + "\r\n";
						out.write(log);
						out.flush();
				}
				try {			
					Thread.sleep(this.logInterval);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} catch (IOException e) {//open file failed!
			e.printStackTrace();
		}

	}

}

