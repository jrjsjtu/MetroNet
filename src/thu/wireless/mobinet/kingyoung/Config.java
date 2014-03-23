package thu.wireless.mobinet.kingyoung;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Config {
	/**
	 * 2月28日
	 * Lili：59.66.224.230 电信3G
	 * mine：59.66.122.85 移动4G
	 * tanweiB：59.66.122.78 移动3G
	 * tanweiT：59.66.122.143 联通3G
	 * 
	 * 3月2日
	 * Lili: 59.66.224.230
	 * Mine: 59.66.122.85
	 * TanweiB: 59.66.123.33
	 * TanweiT: 59.66.123.40
	 */

	/**
	 * 202.112.3.74 移动 3G:15001 4G:16001
	 * 202.112.3.78 联通
	 * 202.112.3.82 电信
	 * 115.28.12.102 云
	 */
	static String testServerip = "202.112.3.74"; // "166.111.68.231";
	static String testMeasuretime = "60"; //1
	static String testInterval = "5";
	static String appVersion = "1.3.3.p";
	static int tcpUploadPort = 15001;
	static int tcpDownloadPort = 15002;
	static int udpUploadPort = 15003;
	static int udpDownloadPort = 15004;
	static int tcpFlowPort = 15005;

	static FileOutputStream fosMobile = null;
	static FileOutputStream fosSignal = null;
	static FileOutputStream fosSpeed = null;
	static FileOutputStream fosCell = null;
	static FileOutputStream fosUplink = null;
	static FileOutputStream fosDownlink = null;
	static FileOutputStream fosTCPFlow = null;
	static FileOutputStream fosPing = null;
	static FileOutputStream fosAddition = null;
	static FileOutputStream fosDNS = null;
	static FileOutputStream fosTrace = null;

	static Button start;
	static TextView directionTextView;
	static TextView asuTextView;
	static TextView signalParameterTextView;
	static TextView speedTextView;
	static TextView basestationTextView;
	static TextView cellidTextView;
	static TextView gpsTextView;
	static TextView satelliteTextView;
	static TextView locationTextView;
	static TextView typeTextView;
	static TextView reportTextView;
	static TextView handoffTextView;
	static TextView wifiTextView;
	static TextView netTextView;
	static TextView pingTextView;
	static TextView timeTextView;
	static EditText serverConentEditText;
	static EditText serverTimeEditText;

	static String providerName = null;
	static String phoneModel = null;
	static String osVersion = null;
	static String networkTypeString = null;
	static String asuShowString = null;
	static String wifiState = null;
	static String mobilitySpeed = "0";
	static String pingInfo = "";
	static String dnsLookupInfo = "";
	static String httpInfo = "";

	static int networkType = -1;
	static int dianxinFlag = -1;
	static TelephonyManager tel;
	static int phoneEvents = PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
			| PhoneStateListener.LISTEN_SERVICE_STATE
			| PhoneStateListener.LISTEN_CELL_LOCATION
			| PhoneStateListener.LISTEN_DATA_ACTIVITY
			| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE;

	static String laststateString = null;
	static int lastcellid = -1;
	static int handoffTotal = -1;
	static boolean lastConnect = false;
	static String lastConnectString = null;
	static int disconnectNumber = 0;
	static String cellInfoContent = null;
	static String lastCellInfoString = null;
	static String dataDirection = "Initial";
	static String dataConnectionState = "Initial";
	static String dataContentString = null;

	static String lastlocationString = null;
	static String speedcontent = null;
	static int gpsAvailableNumber = -1;
	static int gpsFixNumber = -1;
	static String gpsStateString = "Initial";
	static float speed = -1;
	static double latitude = -1;
	static double longitude = -1;
	static float accuracy = -1;
	static int servingCid = -1;
	static int servingLac = -1;

	static LocationManager locationManager;
	static Location loc;
	static Criteria criteria;
	static String bestProvider = null;

	static Sender mySender = null;
	static TCPTest myTcpTest = null;
	static UDPTest myUdpTest = null;
	static String lastWifiState = null;
	static String lastAddition = null;
	static boolean initialGPSFlag = false;
	static boolean prepareGPSFlag = false;
	static boolean prepare3GFlag = false;
	static SimpleDateFormat dirDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	static SimpleDateFormat contentDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
	static SimpleDateFormat sysDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	static String mobilePath = null;
	static int pingFlag = 0;
	
	static String[] measurementNames = { "TCP Downlink Test",
			"TCP Uplink Test", "UDP Downlink Test", "UDP Uplink Test",
			"TCP Flow Test", "DNS Lookup Test", "Ping Test", "HTTP Test" };
	static String[] defaultTarget = { testServerip, testServerip, testServerip,
			testServerip, testServerip, testServerip, testServerip,
			"3g.sina.com.cn" };
	static int measurementID = 0;
	static String addressSina = "3g.sina.com.cn";
	static String addressBaidu = "m.baidu.com";
	static long startTime = 0;
	static String totalTime = "";
	
	// variable for signalStrength
	static int gsmSignalStrength = 99; // Valid values are (0-31, 99) as defined in TS 27.007 8.5
	static int gsmBitErrorRate = -1;   // bit error rate (0-7, 99) as defined in TS 27.007 8.5
	static int cdmaDbm = -1;   // This value is the RSSI value
	static int cdmaEcio = -1;  // This value is the Ec/Io
	static int evdoDbm = -1;   // This value is the EVDO RSSI value
	static int evdoEcio = -1;  // This value is the EVDO Ec/Io
	static int evdoSnr = -1;   // Valid values are 0-8.  8 is the highest signal to noise ratio
	static int lteSignalStrength = -1;
	static int lteRsrp = -1;
	static int lteRsrq = -1;
	static int lteRssnr = -1;
	static int lteCqi = -1;
	static int currentLevel = -1;
	
	public static void setRemoteParameter() {
		if (phoneModel.equals("GT-I9508")) {
			testServerip = "202.112.3.74";
			testMeasuretime = "120"; //1
			testInterval = "5";
			tcpUploadPort = 15001;
			tcpDownloadPort = 15002;
			udpUploadPort = 15003;
			udpDownloadPort = 15004;
			tcpFlowPort = 15005;
		} else if (phoneModel.equals("HTC 608t")) {
			testServerip = "202.112.3.74";
			testMeasuretime = "120"; //1
			testInterval = "5";
			tcpUploadPort = 15101;
			tcpDownloadPort = 15102;
			udpUploadPort = 15103;
			udpDownloadPort = 15104;
			tcpFlowPort = 15105;
		} else if (phoneModel.equals("SCH-I959")) {
			testServerip = "202.112.3.82";
			testMeasuretime = "120"; //1
			testInterval = "5";
			tcpUploadPort = 15201;
			tcpDownloadPort = 15202;
			udpUploadPort = 15203;
			udpDownloadPort = 15204;
			tcpFlowPort = 15205;
		} else if (phoneModel.equals("HTC 609d")) {
			testServerip = "202.112.3.82";
			testMeasuretime = "120"; //1
			testInterval = "5";
			tcpUploadPort = 15301;
			tcpDownloadPort = 15302;
			udpUploadPort = 15303;
			udpDownloadPort = 15304;
			tcpFlowPort = 15305;
		} else if (phoneModel.equals("GT-I9500")) {
			testServerip = "202.112.3.78";
			testMeasuretime = "120"; //1
			testInterval = "5";
			tcpUploadPort = 15401;
			tcpDownloadPort = 15402;
			udpUploadPort = 15403;
			udpDownloadPort = 15404;
			tcpFlowPort = 15405;
		} else if (phoneModel.equals("HTC 606w")) {
			testServerip = "202.112.3.78";
			testMeasuretime = "120"; //1
			testInterval = "5";
			tcpUploadPort = 15501;
			tcpDownloadPort = 15502;
			udpUploadPort = 15503;
			udpDownloadPort = 15504;
			tcpFlowPort = 15505;
		} else if (phoneModel.equals("SM-N9008V")) {
			testServerip = "202.112.3.74";
			testMeasuretime = "120"; //1
			testInterval = "5";
			tcpUploadPort = 15601;
			tcpDownloadPort = 15602;
			udpUploadPort = 15603;
			udpDownloadPort = 15604;
			tcpFlowPort = 15605;
		} else if (phoneModel.equals("MI 1SC")) {
			testServerip = "202.112.3.82"; // 115.28.12.102
			testMeasuretime = "120"; //1
			testInterval = "5";
			tcpUploadPort = 15701;
			tcpDownloadPort = 15702;
			udpUploadPort = 15703;
			udpDownloadPort = 15704;
			tcpFlowPort = 15705;
		} else if (phoneModel.equals("Galaxy Nexus")) {
			testServerip = "202.112.3.74"; // 202.112.3.74
			testMeasuretime = "120"; //1
			testInterval = "5";
			tcpUploadPort = 15801;
			tcpDownloadPort = 15802;
			udpUploadPort = 15803;
			udpDownloadPort = 15804;
			tcpFlowPort = 15805;
		}  else if (phoneModel.equals("MI 2")) {
			testServerip = "202.112.3.74";
			testMeasuretime = "120"; //1
			testInterval = "5";
			tcpUploadPort = 15901;
			tcpDownloadPort = 15902;
			udpUploadPort = 15903;
			udpDownloadPort = 15904;
			tcpFlowPort = 15905;
		} else {
			testServerip = "115.28.12.102";
			testMeasuretime = "120"; //1
			testInterval = "5";
			tcpUploadPort = 16001;
			tcpDownloadPort = 16002;
			udpUploadPort = 16003;
			udpDownloadPort = 16004;
			tcpFlowPort = 16005;
		}
	}
	
	public void getAllInfo() {
		String phone = "+8615210516820";
		try {
			String context = phoneModel + " " + osVersion + " " + providerName
					+ " " + networkTypeString + " " + asuShowString + " "
					+ mobilitySpeed + " " + wifiState + " ";
			if (mySender == null) {

			} else {
				context += mySender.mAvgUplinkThroughput + " " + mySender.mAvgDownlinkThroughput;
			}
			SmsManager manager = SmsManager.getDefault();
			ArrayList<String> list = manager.divideMessage(context);
			// 因为一条短信有字数限制，因此要将长短信拆分
			for (String text : list) {
				manager.sendTextMessage(phone, null, text, null, null);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
