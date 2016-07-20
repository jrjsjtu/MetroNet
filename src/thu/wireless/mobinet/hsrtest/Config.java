package thu.wireless.mobinet.hsrtest;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class Config {
	static String testServerip = "202.112.3.78"; // "166.111.68.231";
	static String testMeasuretime = "60";
	static String testInterval = "5";
	static int tcpUploadPort = 1501;
	static int tcpDownloadPort = 1502;
	static int udpUploadPort = 1503;
	static int udpDownloadPort = 1504;
	static int tcpFlowPort = 1505;
	static int bufferSize = -1;
	static boolean portInLab = false;

	static FileOutputStream fosMobile = null;
	static FileOutputStream fosUplink = null;
	static FileOutputStream fosDownlink = null;
	static FileOutputStream fosPing = null;
	
	static String fos4Cell = null;
	static String fos4Ping = null;
	static String fos4Tcpdump = null;

	static Button start;
	static Button pause;
	static CheckBox tcpdump_switch;
	static CheckBox ping_switch;
	// static Button end;
	static TextView tcpdump_start;
	static TextView tcpdump_close;
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
	static CheckBox portCheckBox;
	static TextView settingTextView;

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

	static TcpDumpManager TcpDump = null;
	static Pingtest ping = null;
	static SpeedTest mySpeedTest = null;
	static boolean lastConnect = false;
	static int disconnectNumber = 0;
	static String cellInfoContent = null;
	static String lastCellInfoString = null;
	static String dataDirection = "Initial";
	static String dataConnectionState = "Initial";
	static String dataContentString = null;

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
	static XGtest myXGtest = null;
	static String lastWifiState = null;
	static String lastAddition = null;
	static boolean initialGPSFlag = false;
	static boolean prepareGPSFlag = false;
	static boolean prepare3GFlag = false;
	static SimpleDateFormat dirDateFormat = new SimpleDateFormat(
			"yyyyMMdd_HHmmss");
	static SimpleDateFormat contentDateFormat = new SimpleDateFormat(
			"HH:mm:ss.SSS");
	static String mobilePath = null;
	static int pingFlag = 0;

	static String[] measurementNames = { "For Metro Test",
			"TCP Uplink Test", "UDP Downlink Test", "UDP Uplink Test",
			"TCP DL+UL Test", "Double TCP DL", "Double TCP UL",
			"TCP DL+UDP DL", "TCP UL+UDP UL", "TCP UL Flow Test" };

	static int measurementID = 0;
	static String addressSina = "3g.sina.com.cn";
	static String addressBaidu = "m.baidu.com";

	// variable for signalStrength
	static String gsmSignalStrength = "99"; // Valid values are (0-31, 99) as
											// defined in TS 27.007 8.5
	static String cdmaDbm = "-1"; // This value is the RSSI value
	static String cdmaEcio = "-1"; // This value is the Ec/Io
	static String evdoDbm = "-1"; // This value is the EVDO RSSI value
	static String evdoEcio = "-1"; // This value is the EVDO Ec/Io
	static String evdoSnr = "-1"; // Valid values are 0-8. 8 is the highest
									// signal to noise ratio
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
		if (phoneModel.equals("HTC 609d")) {
			testServerip = "202.112.3.82";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 2511;
			tcpDownloadPort = 2512;
			udpUploadPort = 2513;
			udpDownloadPort = 2514;
			tcpFlowPort = 2515;
		} else if (phoneModel.equals("SCH-I959")) {
			testServerip = "202.112.3.82";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 2521;
			tcpDownloadPort = 2522;
			udpUploadPort = 2523;
			udpDownloadPort = 2524;
			tcpFlowPort = 2525;
		} else if (phoneModel.equals("MI 1SC")) {
			testServerip = "202.112.3.74";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 2531;
			tcpDownloadPort = 2532;
			udpUploadPort = 2533;
			udpDownloadPort = 2534;
			tcpFlowPort = 2535;
		} else if (phoneModel.equals("GT-I9500")) {
			testServerip = "202.112.3.82"; // 59.66.122.103
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 2541;
			tcpDownloadPort = 2542;
			udpUploadPort = 2543;
			udpDownloadPort = 2544;
			tcpFlowPort = 2545;
		} else if (phoneModel.equals("H60-L02")) {// HTC X920e
			testServerip = "202.112.3.82";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 2551;
			tcpDownloadPort = 2552;
			udpUploadPort = 2553;
			udpDownloadPort = 2554;
			tcpFlowPort = 2555;
		} else if (phoneModel.equals("SM-N9008V")) {
			testServerip = "202.112.3.78";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 2561;
			tcpDownloadPort = 2562;
			udpUploadPort = 2563;
			udpDownloadPort = 2564;
			tcpFlowPort = 2565;
		} else if (phoneModel.equals("HTC D820u")) {
			testServerip = "202.112.3.74";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 2571;
			tcpDownloadPort = 2572;
			udpUploadPort = 2573;
			udpDownloadPort = 2574;
			tcpFlowPort = 2575;
		} else if (phoneModel.equals("SM-N9008S")) {
			testServerip = "202.112.3.78";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 2581;
			tcpDownloadPort = 2582;
			udpUploadPort = 2583;
			udpDownloadPort = 2584;
			tcpFlowPort = 2585;
		} else if (phoneModel.equals("HTC X920e")) {
			testServerip = "202.112.3.82";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 2591;
			tcpDownloadPort = 2592;
			udpUploadPort = 2593;
			udpDownloadPort = 2594;
			tcpFlowPort = 2595;
		} else {
			testServerip = "139.129.44.108";
			testMeasuretime = "100";
			testInterval = "5";
			tcpUploadPort = 2501;
			tcpDownloadPort = 2502;
			udpUploadPort = 2503;
			udpDownloadPort = 2504;
			tcpFlowPort = 2505;
		}
	}
}
