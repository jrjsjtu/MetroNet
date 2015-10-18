package thu.wireless.mobinet.hsrtest;

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
	static String testServerip = "202.112.3.78"; // "166.111.68.231";
	static String testMeasuretime = "60";
	static String testInterval = "5";
	static int tcpUploadPort = 5001;
	static int tcpDownloadPort = 5002;
	static int udpUploadPort = 5003;
	static int udpDownloadPort = 5004;
	static int tcpFlowPort = 5005;
	static int bufferSize = -1;

	static FileOutputStream fosMobile = null;
	static FileOutputStream fosUplink = null;
	static FileOutputStream fosDownlink = null;
	static FileOutputStream fosPing = null;

	static Button start;
//	static Button end;
	static TextView directionTextView;
	static TextView asuTextView;
	static TextView signalParameterTextView;
	static TextView speedTextView;
	static TextView basestationTextView;
	static TextView gpsTextView;
	static TextView satelliteTextView;
	static TextView locationTextView;
	static TextView typeTextView;
	static TextView reportTextView;
	static TextView handoffTextView;
	static TextView netTextView;
	static TextView pingTextView;
	static TextView timeTextView;
	static TextView portTextView;
	static EditText serverConentEditText;
	static EditText serverTimeEditText;
	static EditText bufferSizeEditText;	

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
	static TelephonyManager tel;
	static int phoneEvents = PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
			| PhoneStateListener.LISTEN_SERVICE_STATE
			| PhoneStateListener.LISTEN_CELL_LOCATION
			| PhoneStateListener.LISTEN_DATA_ACTIVITY
			| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE;

//	static String laststateString = null;
	static boolean lastConnect = false;
//	static String lastConnectString = null;
	static int disconnectNumber = 0;
	static String cellInfoContent = null;
	static String lastCellInfoString = null;
	static String dataDirection = "Initial";
	static String dataConnectionState = "Initial";
	static String dataContentString = null;

//	static String lastlocationString = null;
//	static String speedcontent = null;
	static int gpsAvailableNumber = -1;
	static int gpsFixNumber = -1;
	static String gpsStateString = "Initial";
	static float speed = -1;
	static double latitude = -1;
	static double longitude = -1;
	static float accuracy = -1;
	static int servingCid = -1;
	static int servingLac = -1;
	static int servingPsc = -1;

	static LocationManager locationManager;
	static Location loc;
	static Criteria criteria;
	static String bestProvider = null;

	static Sender mySender = null;
	static TCPTest myTcpTest = null;
	static TCPTest myTcpTest2 = null;
	static TCPTest myTcpTest3 = null;
	static UDPTest myUdpTest = null;
	static String lastWifiState = null;
	static String lastAddition = null;
	static boolean initialGPSFlag = false;
	static boolean prepareGPSFlag = false;
	static boolean prepare3GFlag = false;
	static SimpleDateFormat dirDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	static SimpleDateFormat contentDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	static String mobilePath = null;
	static int pingFlag = 0;
	
	static String[] measurementNames = { "TCP Downlink Test",
			"TCP Uplink Test", "UDP Downlink Test", "UDP Uplink Test",
			"TCP Double Test" };// 1127

	static int measurementID = 0;
	static String addressSina = "3g.sina.com.cn";
	static String addressBaidu = "m.baidu.com";
	
	// variable for signalStrength
	static String gsmSignalStrength = "99"; // Valid values are (0-31, 99) as defined in TS 27.007 8.5
	static String cdmaDbm = "-1";   // This value is the RSSI value
	static String cdmaEcio = "-1";  // This value is the Ec/Io
	static String evdoDbm = "-1";   // This value is the EVDO RSSI value
	static String evdoEcio = "-1";  // This value is the EVDO Ec/Io
	static String evdoSnr = "-1";   // Valid values are 0-8.  8 is the highest signal to noise ratio
	static String lteSignalStrength = "-1";
	static String lteRsrp = "-1";
	static String lteRsrq = "-1";
	static String lteRssnr = "-1";
	
	// add on 20141128
	static String lastDataStateString = "";
	static String lastDataDirectionString = "";
	static int lastNetworkType = -1;
	static int handoffNumber = -1;
	
	public static void setRemoteParameter() {
		if (phoneModel.equals("SCH-I959")) {
			testServerip = "101.201.141.119";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 1521;
			tcpDownloadPort = 1522;
			udpUploadPort = 1523;
			udpDownloadPort = 1524;
			tcpFlowPort = 1525;
		} else if (phoneModel.equals("HTC 609d")) {
			testServerip = "101.201.141.119";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 1531;
			tcpDownloadPort = 1532;
			udpUploadPort = 1533;
			udpDownloadPort = 1534;
			tcpFlowPort = 1535;
		} else if (phoneModel.equals("GT-I9500")) {
			testServerip = "101.201.141.119"; // 59.66.122.103
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 1541;
			tcpDownloadPort = 1542;
			udpUploadPort = 1543;
			udpDownloadPort = 1544;
			tcpFlowPort = 1545;
		} else if (phoneModel.equals("HTC X920e")) {
			testServerip = "101.201.141.119";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 1551;
			tcpDownloadPort = 1552;
			udpUploadPort = 1553;
			udpDownloadPort = 1554;
			tcpFlowPort = 1555;
		} else if (phoneModel.equals("SM-N9008V")) {
			testServerip = "101.201.141.119";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 1561;
			tcpDownloadPort = 1562;
			udpUploadPort = 1563;
			udpDownloadPort = 1564;
			tcpFlowPort = 1565;
		} else if (phoneModel.equals("M351")) {
			testServerip = "101.201.141.119"; // 0424实验中对应5号手机
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 1571;
			tcpDownloadPort = 1572;
			udpUploadPort = 1573;
			udpDownloadPort = 1574;
			tcpFlowPort = 1575;
		} else if (phoneModel.equals("SM-N9008S")) { // Galaxy Nexus
			testServerip = "101.201.141.119";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 1581;
			tcpDownloadPort = 1582;
			udpUploadPort = 1583;
			udpDownloadPort = 1584;
			tcpFlowPort = 1585;
		} else if (phoneModel.equals("HTC 606w")) { // H60-L02
			testServerip = "101.201.141.119";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 1591;
			tcpDownloadPort = 1592;
			udpUploadPort = 1593;
			udpDownloadPort = 1594;
			tcpFlowPort = 1595;
		} else if (phoneModel.equals("HTC D820u")) { // H60-L02
			testServerip = "101.201.141.119";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 1601;
			tcpDownloadPort = 1602;
			udpUploadPort = 1603;
			udpDownloadPort = 1604;
			tcpFlowPort = 1605;
		} else {
			testServerip = "101.201.141.119";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 2601;
			tcpDownloadPort = 2602;
			udpUploadPort = 2603;
			udpDownloadPort = 2604;
			tcpFlowPort = 2605;
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
