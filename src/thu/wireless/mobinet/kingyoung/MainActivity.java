package thu.wireless.mobinet.kingyoung;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author XQY
 * @version 1.2.22 
 * bug1: HSDPA下cid错误 -- & 0xffff 
 * bug2: HSDPA下asu错误 -- 目前只发现S4该这种问题 
 * bug3: 断网次数统计错误 -- disconnectNumber 
 * bug4: IN/OUT过于频繁 -- Dormant/None 
 * bug5: WiFi状态不是时时更新--handler4Wifi
 * bug6: 相邻基站实时更新
 * bug7: GPS显示速度缓慢
 * add1: 网速反馈--时时及平均 
 * add2: 短息回传测试信息 
 * add3: ping
 * add4: 经纬度
 * add5: 专业模式
 * note1:基站线程比onCellLocationChanged更灵敏
 * note2:为避免写日志频繁,只有发生切换时才会将相邻基站信息记录
 * note3:为避免写日志频繁,只有速度变化时才会将速度信息记录
 * note4:invokeDown前移 
 * note5:修改Downlink退出条件--packetTimed <= mEndTimed
 */
public class MainActivity extends Activity implements OnClickListener {

	private MyPhoneStateListener myListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// 设置屏幕常亮
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
//		appStartDialog(this);

		// 调用百度统计
		StatService.setAppChannel(this, "Baidu Market", true);
		StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1, false);

		// 手机状态相关控件
		Config.start = (Button) findViewById(R.id.button_Start);
		Config.start.setOnClickListener(this);

		Config.serverConentEditText = (EditText) findViewById(R.id.editText_serverIP);
		Config.serverConentEditText.clearFocus();
		Config.serverTimeEditText = (EditText) findViewById(R.id.editText_serverTime);
		Config.asuTextView = (TextView) findViewById(R.id.signalText);
		Config.signalParameterTextView = (TextView) findViewById(R.id.signalParameterText);
		Config.basestationTextView = (TextView) findViewById(R.id.basestationText);
		Config.cellidTextView = (TextView) findViewById(R.id.cellidText);
		Config.directionTextView = (TextView) findViewById(R.id.directionText);
		Config.speedTextView = (TextView) findViewById(R.id.speedText);
		Config.gpsTextView = (TextView) findViewById(R.id.gpsText);
		Config.satelliteTextView = (TextView) findViewById(R.id.satelliteText);
		Config.locationTextView = (TextView) findViewById(R.id.locationText);
		Config.typeTextView = (TextView) findViewById(R.id.typeText);
		Config.reportTextView = (TextView) findViewById(R.id.serverText);
		Config.handoffTextView = (TextView) findViewById(R.id.handoffText);
		Config.wifiTextView = (TextView) findViewById(R.id.wifiText);
		Config.netTextView = (TextView) findViewById(R.id.netText);
		Config.pingTextView = (TextView) findViewById(R.id.pingText);
		Config.timeTextView = (TextView) findViewById(R.id.timeText);
		Config.portTextView = (TextView) findViewById(R.id.tv_port);

		// 获取手机信息
		try {
			myListener = new MyPhoneStateListener();
			Config.tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			Config.tel.listen(myListener, Config.phoneEvents);
			Config.providerName = "No SIM";
			Config.phoneModel = Build.MODEL;
			Config.osVersion = Build.VERSION.RELEASE;
		
			String infoString = "PhoneModel=" + Build.MODEL 
					+ "\nsdkVersion=" + Build.VERSION.SDK_INT 
					+ "\nosVersion=" + Build.VERSION.RELEASE;
			if(Config.tel.getSimState() == TelephonyManager.SIM_STATE_READY) {
				String IMSI = Config.tel.getSubscriberId();			
				if (IMSI.startsWith("46000") || IMSI.startsWith("46002")
						|| IMSI.startsWith("46007")) {
					Config.providerName = "中国移动";
				} else if (IMSI.startsWith("46001")) {
					Config.providerName = "中国联通";
				} else if (IMSI.startsWith("46003")) {
					Config.providerName = "中国电信";
				} else {
					Config.providerName = "非大陆用户";
				}
			} else {
				Config.reportTextView.setText("No SIM Card");
			}
			
			ConnectivityManager connect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			infoString += "\nProviderName=" + Config.providerName;
			infoString += "\nDetailedState=" + networkInfo.getDetailedState();
			infoString += "\nReason=" + networkInfo.getReason();
			infoString += "\nSubtypeName=" + networkInfo.getSubtypeName();
			infoString += "\nExtraInfo=" + networkInfo.getExtraInfo();
			infoString += "\nTypeName=" + networkInfo.getTypeName();
			infoString += "\nIMEI=" + Config.tel.getDeviceId();
			infoString += "\nIMSI=" + Config.tel.getSubscriberId();
			infoString += "\nNetworkOperatorName=" + Config.tel.getNetworkOperatorName();
			infoString += "\nSimOperatorName=" + Config.tel.getSimOperatorName();
			infoString += "\nSimSerialNumber=" + Config.tel.getSimSerialNumber();
			Config.fosMobile.write(infoString.getBytes());
			Config.fosMobile.write(System.getProperty("line.separator").getBytes());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// 端口设置
		Config.setRemoteParameter();
		System.out.println(Config.testServerip);
//		Config.serverConentEditText.setText(Config.testServerip);
		Config.serverTimeEditText.setText(Config.testMeasuretime);	

		Spinner spinner = (Spinner) findViewById(R.id.measurementTypeSpinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Config.measurementNames);
		// R.layout.spinner_dropdown
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Config.measurementID = arg2;
				
				Config.serverConentEditText.setText(Config.testServerip);
				switch (arg2) {
				case 0:
					Config.serverConentEditText.setText("Synchronize time");
					Config.serverConentEditText.setEnabled(false);
					Config.serverTimeEditText.setEnabled(false);
					break;
				case 1:
					Config.serverConentEditText.setEnabled(false);
					Config.serverTimeEditText.setEnabled(false);
					break;
				case 2:
					Config.serverConentEditText.setText("Capture packet");
					Config.serverConentEditText.setEnabled(false);
					Config.serverTimeEditText.setEnabled(false);
					break;
				case 3:
					Config.portTextView.setText("min Port:" + Config.tcpDownloadPort);
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					break;
				case 4:
					Config.portTextView.setText("min Port:" + Config.tcpUploadPort);
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					break;
				case 5:
					Config.portTextView.setText("min Port:" + Config.udpDownloadPort);
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					break;
				case 6:
					Config.portTextView.setText("min Port:" + Config.udpUploadPort);
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					break;
				case 7:
					Config.portTextView.setText("min Port:" + Config.tcpFlowPort);
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					break;
				default:
					Config.portTextView.setText("min");
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(false);
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		startThread();
		
//		initNetwork();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(1, 1, 0, "连通性测试"); // 专业模式
		menu.add(1, 2, 0, "版本介绍");
		menu.add(1, 3, 0, "完全退出");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
//			Intent MyIntent = new Intent(Intent.ACTION_MAIN);
//			MyIntent.addCategory(Intent.CATEGORY_HOME);
//			startActivity(MyIntent);
			if (Config.wifiState.equals("Disconnected")
					&& Config.dataConnectionState.equals("Disconnected")) {
				Config.reportTextView.setText("网络已断开，请检查网络连接");
				Toast.makeText(getApplicationContext(), "网络已断开，请检查网络连接",
						Toast.LENGTH_SHORT).show();
				return false;
			}
			Config.reportTextView.setText("Testing...");
			handler4Ping.post(runnable4Ping);
			
			new Thread(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					Measurement.pingCmdTest(Config.addressSina, 10);
				}				
			}.start();
			
			new Thread(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					Measurement.dnsLookupTest(Config.addressSina, 10);
				}				
			}.start();
			
			break;
		case 2:
			String tmp = "MobiNet帮您分析手机的网络状态\r\n支持移动联通电信三网的全网制式\r\nCopyright  2014  恪家饭";
			Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_LONG)
					.show();
			break;
		case 3:
			try {
				Config.fosMobile.close();
				Config.fosSignal.close();
				Config.fosSpeed.close();
				Config.fosCell.close();
				Config.fosUplink.close();
				Config.fosDownlink.close();
				Config.fosPing.close();
				handler4Cell.removeCallbacks(runnable4Cell);
				handler4Speed.removeCallbacks(runnable4Speed);
				handler4GPS.removeCallbacks(runnable4GPS);
				handler4Ping.removeCallbacks(runnable4Ping);
				handler4Wifi.removeCallbacks(runnable4Wifi);
				handler4Show.removeCallbacks(runnable4Show);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 销毁
			android.os.Process.killProcess(android.os.Process.myPid());
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			Config.tel.listen(myListener, Config.phoneEvents);
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), "Check your SIM card!",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			Config.tel.listen(myListener, Config.phoneEvents);
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), "Check your SIM card!",
					Toast.LENGTH_LONG).show();
		}
		StatService.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			Config.tel.listen(myListener, Config.phoneEvents);
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), "Check your SIM card!",
					Toast.LENGTH_LONG).show();
		}
		StatService.onResume(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle("Exit");
			builder.setMessage("退出MobiNet?");
			builder.setPositiveButton("返回",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							return;
						}
					});
			builder.setNegativeButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							android.os.Process.killProcess(android.os.Process.myPid());
						}
					});
			builder.show();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(Config.start)) {
			Config.testMeasuretime = Config.serverTimeEditText.getText().toString();
			if (Config.wifiState.equals("Disconnected")
					&& Config.dataConnectionState.equals("Disconnected")) {
				Config.reportTextView.setText("网络已断开，请检查网络连接");
				Toast.makeText(getApplicationContext(), "网络已断开，请检查网络连接",
						Toast.LENGTH_SHORT).show();
				return;
			}

			Config.pingFlag = 0;
			String serverIPString = Config.serverConentEditText.getText().toString();
			String measureTimeString = Config.testMeasuretime;
			String measureIntervalString = Config.testInterval;
			
			switch (Config.measurementID) {
			case 0:
				ComponentName componetName = new ComponentName(
						"ru.org.amip.ClockSync",
						"ru.org.amip.ClockSync.view.Main");
				Intent intent = new Intent();
				intent.setComponent(componetName);
				startActivity(intent);
				break;
			case 1:
				ComponentName componetName1 = new ComponentName(
						"com.scan.traceroute",
						"com.scan.traceroute.TraceActivity");
				Intent intent1 = new Intent();
				intent1.setComponent(componetName1);
				startActivity(intent1);
				break;
			case 2:
				ComponentName componetName2 = new ComponentName("lv.n3o.shark",
						"lv.n3o.shark.SharkMain");
				Intent intent2 = new Intent();
				intent2.setComponent(componetName2);
				startActivity(intent2);
				break;
			case 3:
				Config.reportTextView.setText("TCP downlink testing...");
				Config.myTcpTest = new TCPTest(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosDownlink, 1);
				handler4Show.post(runnable4Show);
				break;
			case 4:
				Config.reportTextView.setText("TCP uplink testing...");
				Config.myTcpTest = new TCPTest(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosUplink, 2);
				handler4Show.post(runnable4Show);
				break;
			case 5:
				Config.reportTextView.setText("UDP downlink testing...");
				Config.start.setEnabled(false);
				Config.myUdpTest = new UDPTest(serverIPString,
						measureTimeString, Config.fosDownlink, 1);
				break;
			case 6:
				Config.reportTextView.setText("UDP uplink testing...");
				Config.start.setEnabled(false);
				Config.myUdpTest = new UDPTest(serverIPString,
						measureTimeString, Config.fosUplink, 2);
				break;
			case 7:
				Config.reportTextView.setText("TCP flow testing...");
				Config.myTcpTest = new TCPTest(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosTCPFlow, 3);
				break;
			case 8:
				Config.reportTextView.setText("DNS lookup testing...");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler4Ping.removeCallbacks(runnable4Ping);
				Config.start.setEnabled(false);
				handler4Ping.post(runnable4Ping);
				Measurement.dnsLookupTest(serverIPString, 10);
				break;
			case 9:
				Config.reportTextView.setText("Ping testing...");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler4Ping.removeCallbacks(runnable4Ping);
				Config.start.setEnabled(false);
				handler4Ping.post(runnable4Ping);
				Measurement.pingCmdTest(serverIPString, 10);
				break;			
			case 10:
				Config.reportTextView.setText("Http test testing...");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler4Ping.removeCallbacks(runnable4Ping);
				Config.start.setEnabled(false);
				handler4Ping.post(runnable4Ping);				
				Measurement.httpTest(serverIPString);
				break;
			
			default:
				Config.reportTextView.setText("Test doesn't support");
//				Measurement.tracert("202.112.3.82");
				break;
			}
		}
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				Config.start.setEnabled(false);
				Config.reportTextView.setText("Connecting...");
			} else if (msg.what == 1) {
				Config.reportTextView.setText("Client has connected to server");
			} else if (msg.what == 2) {
				Config.reportTextView.setText("Reconnecting...");
			} else if (msg.what == 3) {
				Config.reportTextView.setText("Client has closed connection");
				handler4Show.removeCallbacks(runnable4Show);
				Config.netTextView.setText("平均上行:" + Config.myTcpTest.mAvgUplinkThroughput
						+ " 平均下行:" + Config.myTcpTest.mAvgDownlinkThroughput + " kbps");
				Config.start.setEnabled(true);
			} else if (msg.what == 4) {
				Config.reportTextView.setText("Server maybe have some error");
				Config.start.setEnabled(true);
			}
		};
	};

	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			// TODO Auto-generated method stub
			TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			/**
			 * 获取当前系统时间
			 */
			String date = Config.contentDateFormat.format(new Date(System.currentTimeMillis()));

			/**
			 * 获取信号强度参数
			 */
			int asu = signalStrength.getGsmSignalStrength();
			int evdodbm = signalStrength.getEvdoDbm();
			int cdmadbm = signalStrength.getCdmaDbm();

			/**
			 * http://www.oschina.net/code/explore/android-4.0.1/telephony/java/android/telephony/SignalStrength.java
			 * 0: GsmSignalStrength(0-31) GsmBitErrorRate(0-7)
			 * 2: CdmaDbm CdmaEcio EvdoDbm EvdoEcio EvdoSnr(0-8)
			 * 7: LteSignalStrength LteRsrp LteRsrq LteRssnr LteCqi 非4G则全为-1
			 * getGsmLevel getLteLevel getCdmaLevel getEvdoLevel
			 */
			String allSignal = signalStrength.toString();
			try {
				String[] parts = allSignal.split(" ");
				Config.gsmSignalStrength = Integer.parseInt(parts[1]);
				Config.gsmBitErrorRate = Integer.parseInt(parts[2]);
				Config.cdmaDbm = Integer.parseInt(parts[3]);
				Config.cdmaEcio = Integer.parseInt(parts[4]);
				Config.evdoDbm = Integer.parseInt(parts[5]);
				Config.evdoEcio = Integer.parseInt(parts[6]);
				Config.evdoSnr = Integer.parseInt(parts[7]);
				Config.lteSignalStrength = Integer.parseInt(parts[8]); //asuLTE
				Config.lteRsrp = Integer.parseInt(parts[9]);
				Config.lteRsrq = Integer.parseInt(parts[10]);
				Config.lteRssnr = Integer.parseInt(parts[11]);
				Config.lteCqi = Integer.parseInt(parts[12]);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			/**
			 * 检测网络类型
			 */
			if (signalStrength.isGsm()) {
				Config.dianxinFlag = 2;
			}
			Config.networkType = telManager.getNetworkType();
			SignalUtil.getCurrentNetworkType(Config.networkType);
			/**
			 * asu与Level关系
			 * Note3: 30、23、19
			 * Other: 11、7、4 
			 */
			int level = SignalUtil.getCurrentLevel(signalStrength.isGsm());
			
			/**
			 * 记录全部信号信息
			 */
			if (allSignal.equals(Config.lastAddition)) {
				
			} else {
				Config.lastAddition = allSignal;
				try {
					Config.fosAddition.write((date + " " + allSignal + " " + level).getBytes());
					Config.fosAddition.write(System.getProperty("line.separator").getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			/**
			 * 整理日志数据
			 */
			String content = Config.networkTypeString + " ";
			Config.typeTextView.setText(Config.providerName + "-"
					+ Config.networkTypeString + " (" + Build.MODEL + "-"
					+ Build.VERSION.RELEASE + ")");
			
			String parameter = "";
			switch (Config.dianxinFlag) {
			case 1:
			case 3:
				content = content + String.valueOf(cdmadbm) + " "
						+ String.valueOf(evdodbm);
				Config.asuShowString = "1x:" + String.valueOf(cdmadbm) + " 3G:"
						+ String.valueOf(evdodbm);
				parameter = "Ecio:" + Config.cdmaEcio + "/"
						+ Config.evdoEcio + " SNR:" + Config.evdoSnr;
				break;
			case 4:
				content = content + String.valueOf(asu) + " "
						+ String.valueOf(Config.lteSignalStrength);
				Config.asuShowString = "2G:" + String.valueOf(asu) + " 4G:"
						+ String.valueOf(Config.lteSignalStrength);
				parameter = "RSRP:" + Config.lteRsrp + " RSRQ:"
						+ Config.lteRsrq + " SNR:" + Config.lteRssnr;
				break;
			default:
				content = content + String.valueOf(asu);
				Config.asuShowString = String.valueOf(asu);
				parameter = "BER:" + Config.gsmBitErrorRate;
				break;
			}
			Config.asuShowString += " Level:" + level;
			Config.asuTextView.setText(Config.asuShowString);
			Config.signalParameterTextView.setText(parameter);

			if (content.equals(Config.laststateString)) {

			} else {
				Config.laststateString = content;
				content = date + " " + content;
				try {
					Config.fosSignal.write(content.getBytes());
					Config.fosSignal.write(System.getProperty("line.separator").getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			super.onSignalStrengthsChanged(signalStrength);
		}

		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {
			// TODO Auto-generated method stub
			switch (state) {
			case TelephonyManager.DATA_DISCONNECTED:
				// 网络断开		
				Config.dataConnectionState = "Disconnected";
				if (Config.lastConnect) {
					Config.disconnectNumber++;
					Config.lastConnect = false;
					Config.handoffTextView.setText("切换:"
							+ String.valueOf(Config.handoffTotal) + " 断网:"
							+ String.valueOf(Config.disconnectNumber));
				}
				break;
			case TelephonyManager.DATA_CONNECTING:
				// 网络正在连接
				Config.dataConnectionState = "Connecting";
				break;
			case TelephonyManager.DATA_CONNECTED:
				// 网络连接上
				Config.dataConnectionState = "Connected";
				Config.lastConnect = true;
				break;
			default:
				Config.dataConnectionState = "Unknown";
				break;
			}

			/**
			 * 写入日志
			 */
			if (Config.dataConnectionState.equals("Connected")) {
				
			} else {
				String tmp = Config.dataConnectionState + " " + Config.dataDirection;
				if (tmp.equals(Config.lastConnectString)) {

				} else {
					Config.lastConnectString = tmp;
					Config.directionTextView.setText(Config.dataConnectionState + " 方向:" + Config.dataDirection);
					String date = Config.contentDateFormat.format(new Date(System.currentTimeMillis()));
					Config.dataContentString = date + " " + Config.dataConnectionState + " " + Config.dataDirection;
					try {
						Config.fosMobile.write(Config.dataContentString.getBytes());
						Config.fosMobile.write(System.getProperty("line.separator").getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			super.onDataConnectionStateChanged(state, networkType);
		}

		@Override
		public void onDataActivity(int direction) {
			// TODO Auto-generated method stub
			switch (direction) {
			case TelephonyManager.DATA_ACTIVITY_IN:
				Config.dataDirection = "IN";
				break;
			case TelephonyManager.DATA_ACTIVITY_OUT:
				Config.dataDirection = "OUT";
				break;
			case TelephonyManager.DATA_ACTIVITY_INOUT:
				Config.dataDirection = "IN-OUT";
				break;
			case TelephonyManager.DATA_ACTIVITY_DORMANT:
				// Data connection is active, but physical link is down
				Config.dataDirection = "Dormant";
				break;
			case TelephonyManager.DATA_ACTIVITY_NONE:
				// No IP Traffic
				Config.dataDirection = "NONE";
				break;
			default:
				Config.dataDirection = "Unknown";
				break;
			}
			/**
			 * 写入日志
			 */
			if (Config.dataDirection.equals("Dormant") || Config.dataDirection.equals("NONE")
					|| Config.lastConnectString == null) {
				String tmp = Config.dataConnectionState + " " + Config.dataDirection;
				if (tmp.equals(Config.lastConnectString)) {

				} else {
					Config.lastConnectString = tmp;
					Config.directionTextView.setText(Config.dataConnectionState + " 方向:" + Config.dataDirection);
					String date = Config.contentDateFormat.format(new Date(System.currentTimeMillis()));
					Config.dataContentString = date + " " + Config.dataConnectionState + " " + Config.dataDirection;
					try {
						Config.fosMobile.write(Config.dataContentString.getBytes());
						Config.fosMobile.write(System.getProperty("line.separator").getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			super.onDataActivity(direction);
		}
	}
	
	private final LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			// When GPS state change, can capture immediately
			switch (status) {
			case LocationProvider.AVAILABLE:
				Config.gpsStateString = "Available";
				Config.gpsTextView.setText(Config.gpsStateString);
				break;
			case LocationProvider.OUT_OF_SERVICE:
				Config.gpsStateString = "OutOfService";
				Config.gpsTextView.setText(Config.gpsStateString);
				Config.mobilitySpeed = "Unknown";
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Config.gpsStateString = "Unavailable";
				Config.gpsTextView.setText(Config.gpsStateString);
				Config.mobilitySpeed = "Unknown";
				break;
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			// GPS开启时触发
			Config.loc = Config.locationManager.getLastKnownLocation(provider);
			Config.gpsStateString = "Enabled";
			Config.gpsTextView.setText(String.valueOf(Config.gpsFixNumber) + "/"
							+ String.valueOf(Config.gpsAvailableNumber) + " "
							+ Config.gpsStateString);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Config.gpsStateString = "Disabled";
			Config.gpsTextView.setText(String.valueOf(Config.gpsFixNumber) + "/"
							+ String.valueOf(Config.gpsAvailableNumber) + " "
							+ Config.gpsStateString);
			Config.prepareGPSFlag = false;
			Config.mobilitySpeed = "Unknown";
		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			String date = Config.contentDateFormat.format(new Date(System.currentTimeMillis()));

			/**
			 * 测速
			 */
			Config.loc = Config.locationManager.getLastKnownLocation(Config.bestProvider);
			Config.speed = Config.loc.getSpeed();
			Config.latitude = Config.loc.getLatitude();
			Config.longitude = Config.loc.getLongitude();
			Config.accuracy = Config.loc.getAccuracy();
			Config.locationTextView.setText(Config.latitude + "," + Config.longitude);

			/**
			 * 整理日志数据
			 */
			float speed2 = (float) (Config.speed * 3.6);
			Config.mobilitySpeed = String.valueOf(speed2);
			Config.speedTextView.setText(Config.mobilitySpeed + " km/h");
			Config.speedcontent = Config.gpsStateString + " " + String.valueOf(speed2);

			if (Config.speedcontent.equals(Config.lastlocationString)) {

			} else {
				Config.lastlocationString = Config.speedcontent;
				Config.speedcontent = date + " " + Config.speedcontent + " "
						+ String.valueOf(Config.latitude) + " "
						+ String.valueOf(Config.longitude) + " "
						+ String.valueOf(Config.accuracy);
				try {
					Config.fosSpeed.write(Config.speedcontent.getBytes());
					Config.fosSpeed.write(System.getProperty("line.separator")
							.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	// 获取当前所连GPS数量
	private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {

		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub
			LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			GpsStatus status = locManager.getGpsStatus(null); // 取当前状态

			switch (event) {
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				// 卫星状态改变
				int maxSatellites = status.getMaxSatellites(); // 获取卫星颗数的默认最大值
				Iterator<GpsSatellite> it = status.getSatellites().iterator();
				Config.gpsAvailableNumber = 0;
				Config.gpsFixNumber = 0;
				while (it.hasNext() && Config.gpsAvailableNumber <= maxSatellites) {
					GpsSatellite s = it.next();
					Config.gpsAvailableNumber++;
					if (s.usedInFix()) {
						Config.gpsFixNumber++;
					}
				}
				Config.satelliteTextView.setText(Config.gpsFixNumber + "/"
						+ Config.gpsAvailableNumber);
				break;
			case GpsStatus.GPS_EVENT_STARTED:
				Config.gpsStateString = "Start";
				Config.gpsTextView.setText(Config.gpsStateString);
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				Config.gpsStateString = "Stop";
				Config.gpsTextView.setText(Config.gpsStateString);
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Config.gpsStateString = "FirstFix";
				Config.gpsTextView.setText(Config.gpsStateString);
			default:
				break;
			}
		}
	};

	private void initLocation() {
		Config.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (Config.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| Config.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Config.criteria = new Criteria();
			Config.criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
			Config.criteria.setAltitudeRequired(true); // 显示海拔
			Config.criteria.setBearingRequired(true); // 显示方向
			Config.criteria.setSpeedRequired(true); // 显示速度
			Config.criteria.setCostAllowed(false); // 不允许有花费
			Config.criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
			Config.bestProvider = Config.locationManager.getBestProvider(Config.criteria, true);

			// locationManager用来监听定位信息的改变
			Config.locationManager.requestLocationUpdates(Config.bestProvider, 100, 5,
					locationListener);
			Config.locationManager.addGpsStatusListener(statusListener);
			
			Location gpsLocation = Config.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(gpsLocation == null){     
				gpsLocation = Config.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(gpsLocation != null){     
            	Config.latitude = gpsLocation.getLatitude(); //经度     
            	Config.longitude = gpsLocation.getLongitude(); //纬度  
            	Config.locationTextView.setText(Config.latitude + "," + Config.longitude);
            }
		} 
		if (Config.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Config.prepareGPSFlag = true;
		} else {
			Config.gpsTextView.setText("Disabled");
			if (Config.prepareGPSFlag) {

			} else {
				showGPSDialog(this);
			}
		}
	}
	
	private void initNetwork() {
		//网络设置
		ConnectivityManager connect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connect.getActiveNetworkInfo();
		// || networkInfo.isAvailable()
		if (networkInfo != null) {
			Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
		} else {
			show3GDialog(this);
		}
	}

	private Handler handler4Cell = new Handler();

	private Runnable runnable4Cell = new Runnable() {

		@Override
		public void run() {
			int cid = -1;
			int lac = -1;
			int psc = -1;
			double cellLatitude = -1;
			double cellLongitude = -1;
			Config.tel.listen(myListener, Config.phoneEvents);

			try {
				Config.networkType = Config.tel.getNetworkType();
				SignalUtil.getCurrentNetworkType(Config.networkType);				

				switch (Config.dianxinFlag) {
				case 1:
				case 3:
					CdmaCellLocation locationCDMA = (CdmaCellLocation) Config.tel.getCellLocation();
					cid = locationCDMA.getBaseStationId();
					lac = locationCDMA.getNetworkId();
					psc = locationCDMA.getSystemId();
					cellLatitude = locationCDMA.getBaseStationLatitude();
					cellLongitude = locationCDMA.getBaseStationLongitude();
					Config.cellInfoContent = Config.networkTypeString + " "
							+ String.valueOf(cid) + " " + String.valueOf(lac)
							+ " " + String.valueOf(psc) + " "
							+ String.valueOf(cellLatitude) + " "
							+ String.valueOf(cellLongitude);
					break;

				default:
					GsmCellLocation locationGSM = (GsmCellLocation) Config.tel
							.getCellLocation();
					cid = locationGSM.getCid() & 0xffff;
					lac = locationGSM.getLac();
					psc = locationGSM.getPsc();
					Config.cellInfoContent = Config.networkTypeString + " "
							+ String.valueOf(cid) + " " + String.valueOf(lac)
							+ " " + String.valueOf(psc);
					break;
				}

				Config.servingCid = cid;
				Config.servingLac = lac;
				List<NeighboringCellInfo> infos = Config.tel.getNeighboringCellInfo();
				Config.typeTextView.setText(Config.providerName + "-"
						+ Config.networkTypeString + " (" + Build.MODEL + "-"
						+ Build.VERSION.RELEASE + ")");
				Config.basestationTextView.setText(String.valueOf(Config.servingCid) 
						+ "/" + String.valueOf(Config.servingLac));
				
				if (cid == Config.lastcellid) {

				} else {
					Config.lastcellid = cid;
					Config.handoffTotal++;
					Config.handoffTextView.setText("切换:"
							+ String.valueOf(Config.handoffTotal) + " 断网:"
							+ String.valueOf(Config.disconnectNumber));
				}

				// show neighboring cell
				Config.cellidTextView.setText(String.valueOf(infos.size()));
//				if (infos.size() == 0) {
//					// NoNeighboringCell
//					Config.cellidTextView.setText("No");
//				} else {
//					NeighboringCellInfo[] info = infos.toArray(new NeighboringCellInfo[0]);
//					String nbcString = String.valueOf(info[0].getRssi())
//							+ "\t" + String.valueOf(info[0].getCid()) + "/"
//							+ String.valueOf(info[0].getLac()) + " \t"
//							+ String.valueOf(info[0].getNetworkType());
//
//					for (int i = 1; i < infos.size(); i++) {
//						nbcString = nbcString + "\n"
//								+ String.valueOf(info[i].getRssi()) + "\t"
//								+ String.valueOf(info[i].getCid()) + "/"
//								+ String.valueOf(info[i].getLac()) + " \t"
//								+ String.valueOf(info[i].getNetworkType());
//					}
//					Config.cellidTextView.setText(nbcString);
//				}
				
				// write log
				if (Config.cellInfoContent.equals(Config.lastCellInfoString)) {

				} else {
					Config.lastCellInfoString = Config.cellInfoContent;
					Config.cellInfoContent = Config.cellInfoContent + " " + String.valueOf(infos.size());
					if (infos.size() == 0) {
						// NoNeighboringCell
						Config.cellInfoContent = Config.cellInfoContent + " No";
					} else {
						for (NeighboringCellInfo info : infos) {
							Config.cellInfoContent = Config.cellInfoContent + " "
									+ String.valueOf(info.getCid()) + "/"
									+ String.valueOf(info.getLac()) + "/"
									+ String.valueOf(info.getRssi()) + "/"
									+ String.valueOf(info.getPsc()) + "/"
									+ String.valueOf(info.getNetworkType());
						}
					}

					String date = Config.contentDateFormat.format(new Date(System.currentTimeMillis()));
					Config.cellInfoContent = date + " " + Config.cellInfoContent;
					try {
						Config.fosCell.write(Config.cellInfoContent.getBytes());
						Config.fosCell.write(System.getProperty("line.separator").getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// 每50ms执行一次
				handler4Cell.postDelayed(runnable4Cell, 50);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private Handler handler4Speed = new Handler();

	private Runnable runnable4Speed = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			initLocation();
		}
	};

	private Handler handler4Show = new Handler();

	private Runnable runnable4Show = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler4Show.postDelayed(runnable4Show, 1000);
			Config.netTextView.setText("上行:" + Config.myTcpTest.mUplinkThroughput + " 下行:"
					+ Config.myTcpTest.mDownlinkThroughput + " kbps");
		}
	};
	
	private Handler handler4Ping = new Handler();

	private Runnable runnable4Ping = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler4Ping.postDelayed(runnable4Ping, 1000);
			if (Config.pingFlag == 11) {
				Config.pingTextView.setText("Ping:" + Config.pingInfo
						+ " DNS:" + Config.dnsLookupInfo + " HTTP:" + Config.httpInfo);
				Config.start.setEnabled(true);
				Config.reportTextView.setText("Ping test finished");
				Config.pingFlag = 10;
			} else if (Config.pingFlag == 12) {
				Config.reportTextView.setText("Ping test failed");
				Config.pingFlag = 10;
				Config.start.setEnabled(true);
			} else if (Config.pingFlag == 13) {
				Config.reportTextView.setText("Ping test failed");
				Config.pingFlag = 10;
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setIcon(R.drawable.ic_launcher);
				builder.setTitle("Test Failure");
		        builder.setMessage("您的机型暂不支持连通性测试，请关注后续版本");
				builder.setPositiveButton("默默等待", null);
				builder.show();
			} else if (Config.pingFlag == 21) {
				Config.pingTextView.setText("Ping:" + Config.pingInfo + " DNS:"
						+ Config.dnsLookupInfo + " HTTP:" + Config.httpInfo);
				Config.reportTextView.setText("DNS lookup test finished");
				Config.start.setEnabled(true);
				Config.pingFlag = 20;
			} else if (Config.pingFlag == 22) {
				Config.reportTextView.setText("DNS lookup test failed");
				Config.start.setEnabled(true);
				Config.pingFlag = 20;
			} else if (Config.pingFlag == 31) {
				Config.pingTextView.setText("Ping:" + Config.pingInfo + " DNS:"
						+ Config.dnsLookupInfo + " HTTP:" + Config.httpInfo);
				Config.reportTextView.setText("HTTP test finished");
				Config.start.setEnabled(true);
				Config.pingFlag = 30;
			} else if (Config.pingFlag == 32) {
				Config.reportTextView.setText("HTTP test failed");
				Config.start.setEnabled(true);
				Config.pingFlag = 30;
			}
		}
	};

	private Handler handler4Wifi = new Handler();

	private Runnable runnable4Wifi = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			/**
			 * 是否连接Wifi
			 */
			try {
				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				String wifiContent = null;

				if (activeNetInfo != null
						&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {					
					WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					Config.wifiState = "Connected:" + wifiInfo.getSSID();
					wifiContent = Config.wifiState + " RSSI:"
							+ wifiInfo.getRssi() + " Speed:" + wifiInfo.getLinkSpeed();
				} else {
					Config.wifiState = "Disconnected";
					wifiContent = Config.wifiState;
				}

				if (wifiContent.equals(Config.lastWifiState)) {

				} else {
					Config.wifiTextView.setText(wifiContent);
					Config.lastWifiState = wifiContent;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			handler4Wifi.postDelayed(runnable4Wifi, 1000);
		}
	};
	
	private Handler handler4GPS = new Handler();

	private Runnable runnable4GPS = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				initLocation();
			} else {
				handler4GPS.postDelayed(runnable4GPS, 1000);
			}
		}
	};
		
	private void showGPSDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);  
        builder.setIcon(R.drawable.ic_launcher);  
        builder.setTitle("GPS Failure");
        builder.setMessage("GPS还未开启，如需测试运动速度请勾选");
        builder.setPositiveButton("暂不设置",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    	return;
                    }  
                });  
        builder.setNegativeButton("去设置",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {
            			Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            			startActivityForResult(intent, 0); 
                    }  
                });  
        builder.show();  
    }
	
	private void show3GDialog(Context context) {  
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("Internet Failure");
        builder.setMessage("数据连接还未开启，请勾选移动网络");
        builder.setPositiveButton("暂不设置",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        return;
                    }  
                });  
        builder.setNegativeButton("去设置",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) { 
            			Intent intent = new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
            			startActivityForResult(intent, 0); 
                    }  
                });  
        builder.show();  
    }
	
	private void startThread() {
		Config.startTime = System.currentTimeMillis();
		try {
			// 启动线程
			handler4Cell.post(runnable4Cell);		
			Thread.sleep(500);
			handler4Time.post(runnable4Time);
			Thread.sleep(500);
			handler4Wifi.post(runnable4Wifi);
			Thread.sleep(500);
			handler4GPS.post(runnable4GPS);
			Thread.sleep(500);
			handler4Speed.post(runnable4Speed);
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), "Some mistake occured!",
					Toast.LENGTH_LONG).show();
		}
	}
	
	private void appStartDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("功能介绍");
		String msg = "1.信号质量:\r\n 信号强度数值越大信号越好\r\n"
				+ "2.手机网速:\r\n 请点击测速按钮即可开始测速\r\n" 
				+ "3.网络覆盖:\r\n 相邻基站越多网络覆盖越好\r\n"
				+ "4.WiFi质量:\r\n RSSI数值越大所连WiFi信号越强\r\n"
				+ "5.运动速度:\r\n 不妨测测高铁时速吧(需开启GPS)";
		builder.setMessage(msg);
		Config.startTime = System.currentTimeMillis();
		builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
            	startThread();
            	return;
            }
        });
		builder.show();
	}
	
	private Handler handler4Time = new Handler();

	private Runnable runnable4Time = new Runnable() {

		@Override
		public void run() {			
			// TODO Auto-generated method stub
			handler4Time.postDelayed(runnable4Time, 1000);
			long time = System.currentTimeMillis();
			long show = (time - Config.startTime) / 1000;
			Config.totalTime = show / 60 + "'" + show % 60;
			Config.timeTextView.setText(Config.sysDateFormat.format(
					new Date(time)) + " 总时长:" + Config.totalTime);		
		}
	};
}
