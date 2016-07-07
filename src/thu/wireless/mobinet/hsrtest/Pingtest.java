package thu.wireless.mobinet.hsrtest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;
import android.util.Log;

public class Pingtest extends Logger{
	private String ip_address;
	private boolean is_recording;
	private Runtime r;
	private String[] pingCommand = new String[3];
	private Process p;
	public Pingtest(String ip,String logFileName, int interval){
		super(logFileName, interval);
		ip_address = "139.129.44.108";
		is_recording = false;
		r = Runtime.getRuntime();
		pingCommand[0] = "adb shell";
		pingCommand[1] = "su";
		pingCommand[2] = "ping " + ip_address;  
	}
	
	@Override
	public String getLogValue() {
		String line = "";
		String cmd = "ping -c 1 " + "139.129.44.108";
		Process process;
		try {
			process = Runtime.getRuntime().exec(cmd);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			line = bufferedReader.readLine();
			Log.d("for log", line);
			line = bufferedReader.readLine();
			Log.d("for log", line);
	        //Pattern pattern = Pattern.compile("(.*time=.*)",    Pattern.CASE_INSENSITIVE);  
	        //Matcher matcher = pattern.matcher(line);  
			//Log.d("for log", matcher.group(1));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return line;
	}
}
