package thu.wireless.mobinet.hsrtest;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
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
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	//private MyPhoneStateListener myListener;
	private XGtest myXGtest;
	public String MYSERVICE = "thu.wireless.mobinet.hsrtest.XGService"; 
	SensorManager mSensorManager = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 设置屏幕常亮
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Settings.System.putInt(getContentResolver(),
				Settings.System.SCREEN_OFF_TIMEOUT, 10 * 60 * 1000);

//		调用百度统计
//		StatService.setAppChannel(this, "Baidu Market", true);
//		StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1,
//				false);

		// 手机状态相关控件
		Config.start = (Button) findViewById(R.id.button_Start);
		Config.start.setOnClickListener(this);
		
		Config.pause = (Button) findViewById(R.id.button_Pause);
		Config.pause.setEnabled(false);
		Config.pause.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Config.start.setEnabled(true);
				Config.pause.setEnabled(false);
//				Config.ping_switch.setChecked(false);
//				Config.ping_switch.setEnabled(false);
				Config.myTcpTest.on_off = false;
				Config.myTcpTest2.on_off = false;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		Config.TcpDump = new TcpDumpManager("/data/local/tcpdump");
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		//Config.mySpeedTest = new SpeedTest(mSensorManager,mHandler);
		Config.tcpdump_switch = (CheckBox)findViewById(R.id.tcpdump_check);
		Config.tcpdump_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
            	if (arg1){
            		Config.TcpDump.startCapture();
            	}else{
            		Config.TcpDump.stopCapture();
            	}
            }
        });
		
		Config.ping_switch = (CheckBox)findViewById(R.id.ping_check);
		//Config.ping_switch.setEnabled(false);
		Config.ping_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
            	if (arg1){
            		Config.ping.startLog();
            	}else{
            		Config.ping.stopLog();
            	}
            }
        });
		// Config.end = (Button) findViewById(R.id.button_End);
		// Config.end.setOnClickListener(this);
		// Config.end.setEnabled(false);

		Config.serverConentEditText = (EditText) findViewById(R.id.editText_serverIP);
		Config.serverConentEditText.clearFocus();
		// duration
		Config.serverTimeEditText = (EditText) findViewById(R.id.editText_serverTime);
		// size
		Config.bufferSizeEditText = (EditText) findViewById(R.id.editText_buffer);
		Config.portCheckBox = (CheckBox) findViewById(R.id.checkBox_inLab);

		Config.asuTextView = (TextView) findViewById(R.id.signalText);
		Config.signalParameterTextView = (TextView) findViewById(R.id.signalParameterText);
		Config.basestationTextView = (TextView) findViewById(R.id.basestationText);
		Config.directionTextView = (TextView) findViewById(R.id.directionText);
		Config.speedTextView = (TextView) findViewById(R.id.speedText);
		Config.gpsTextView = (TextView) findViewById(R.id.gpsText);
		Config.satelliteTextView = (TextView) findViewById(R.id.satelliteText);
		Config.locationTextView = (TextView) findViewById(R.id.locationText);
		Config.typeTextView = (TextView) findViewById(R.id.typeText);
		Config.reportTextView = (TextView) findViewById(R.id.serverText);
		Config.handoffTextView = (TextView) findViewById(R.id.handoffText);
		Config.netTextView = (TextView) findViewById(R.id.netText);
		Config.pingTextView = (TextView) findViewById(R.id.pingText);
		Config.portTextView = (TextView) findViewById(R.id.tv_port);
		Config.settingTextView = (TextView) findViewById(R.id.tv_minute);

		// 获取手机信息
		try {
			//myListener = new MyPhoneStateListener();
			Config.tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Intent serviceIntent = new Intent(); 
            serviceIntent.setAction(MYSERVICE); 
            startService(serviceIntent); 
			//myXGtest = new XGtest(Config.tel);
			//myXGtest.start();
			//Config.tel.listen(myListener, Config.phoneEvents);
			Config.providerName = "No SIM";
			Config.phoneModel = Build.MODEL;
			Config.osVersion = Build.VERSION.RELEASE;

			String infoString = "PhoneModel=" + Build.MODEL + "\nsdkVersion="
					+ Build.VERSION.SDK_INT + "\nosVersion="
					+ Build.VERSION.RELEASE;
			if (Config.tel.getSimState() == TelephonyManager.SIM_STATE_READY) {
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
			NetworkInfo networkInfo = connect
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			infoString += "\nProviderName=" + Config.providerName;
			infoString += "\nDetailedState=" + networkInfo.getDetailedState();
			infoString += "\nReason=" + networkInfo.getReason();
			infoString += "\nSubtypeName=" + networkInfo.getSubtypeName();
			infoString += "\nExtraInfo=" + networkInfo.getExtraInfo();
			infoString += "\nTypeName=" + networkInfo.getTypeName();
			infoString += "\nIMEI=" + Config.tel.getDeviceId();
			infoString += "\nIMSI=" + Config.tel.getSubscriberId();
			infoString += "\nNetworkOperatorName="
					+ Config.tel.getNetworkOperatorName();
			infoString += "\nSimOperatorName="
					+ Config.tel.getSimOperatorName();
			infoString += "\nSimSerialNumber="
					+ Config.tel.getSimSerialNumber();
			Config.fosMobile.write(infoString.getBytes());
			Config.fosMobile.write(System.getProperty("line.separator")
					.getBytes());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// 端口设置
		Config.setRemoteParameter();
		System.out.println(Config.testServerip);
		// Config.serverConentEditText.setText(Config.testServerip);
		Config.serverTimeEditText.setText(Config.testMeasuretime);
		Config.bufferSizeEditText.setText(String.valueOf(Config.bufferSize));
		// Config.bufferSizeEditText.setEnabled(false);
		Config.portCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						Config.portInLab = true;
						if (isChecked) {
							Config.tcpUploadPort = 2501;
							Config.tcpDownloadPort = 2502;
							Config.udpUploadPort = 2503;
							Config.udpDownloadPort = 2504;
							Config.tcpFlowPort = 2505;

							Config.portTextView.setText("Port:250?");
						} else {
							Config.portCheckBox.setChecked(true);
						}
						Toast.makeText(MainActivity.this,
								isChecked ? "已选中: port固定" : "暂时无法取消",
								Toast.LENGTH_SHORT).show();
					}
				});

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
					// windows: 123.56.64.240
					// ubuntu: 123.56.225.51
					Config.serverConentEditText.setText("139.129.44.108");
					Config.portTextView.setText("Port:"
							+ Config.tcpDownloadPort);
					Config.settingTextView.setText("min  Size:");
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					Config.bufferSizeEditText.setVisibility(View.VISIBLE);
					Config.bufferSizeEditText.setText("1024");
					break;
				case 1:
					Config.serverConentEditText.setText("139.129.44.108");
					Config.portTextView.setText("Port:" + Config.tcpUploadPort);
					Config.settingTextView.setText("min  Size:");
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					Config.bufferSizeEditText.setVisibility(View.VISIBLE);
					Config.bufferSizeEditText.setText("1024");
					break;
				case 2:
					Config.serverConentEditText.setText("139.129.44.108");
					Config.portTextView.setText("Port:"
							+ Config.udpDownloadPort);
					Config.settingTextView.setText("min");
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					Config.bufferSizeEditText.setVisibility(View.INVISIBLE);
					break;
				case 3:
					Config.serverConentEditText.setText("139.129.44.108");
					Config.portTextView.setText("Port:" + Config.udpUploadPort);
					Config.settingTextView.setText("min  Sleep(ms):");
					Toast.makeText(getApplicationContext(),
							"可设置sleep interval控制UDP发送速率\r\n即：每发一个包sleep多久",
							Toast.LENGTH_SHORT).show();
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					Config.bufferSizeEditText.setVisibility(View.VISIBLE);
					Config.bufferSizeEditText.setText("0");
					break;
				case 4:
					Config.serverConentEditText.setText("139.129.44.108");
					Config.portTextView.setText(Config.tcpUploadPort + "&"
							+ Config.tcpDownloadPort);
					Config.settingTextView.setText("min  Size:");
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					Config.bufferSizeEditText.setVisibility(View.VISIBLE);
					Config.bufferSizeEditText.setText("1024");
					break;
				case 5:
					Config.serverConentEditText.setText("139.129.44.108");
					Config.portTextView.setText(Config.tcpDownloadPort + "&"
							+ (Config.tcpDownloadPort + 4));
					Config.settingTextView.setText("min  Size:");
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					Config.bufferSizeEditText.setVisibility(View.VISIBLE);
					Config.bufferSizeEditText.setText("1024");
					break;
				case 6:
					Config.serverConentEditText.setText("139.129.44.108");
					Config.portTextView.setText(Config.tcpUploadPort + "&"
							+ (Config.tcpUploadPort + 4));
					Config.settingTextView.setText("min  Size:");
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					Config.bufferSizeEditText.setVisibility(View.VISIBLE);
					Config.bufferSizeEditText.setText("1024");
					break;
				case 7:
					Config.serverConentEditText.setText("139.129.44.108");
					Config.portTextView.setText(Config.tcpDownloadPort + "&"
							+ Config.udpDownloadPort);
					Config.settingTextView.setText("min  Size:");
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					Config.bufferSizeEditText.setVisibility(View.VISIBLE);
					Config.bufferSizeEditText.setText("1024");
					break;
				case 8:
					Config.serverConentEditText.setText("139.129.44.108");
					Config.portTextView.setText(Config.tcpUploadPort + "&"
							+ Config.udpUploadPort);
					Config.settingTextView.setText("min  Sleep(ms):");
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					Config.bufferSizeEditText.setVisibility(View.VISIBLE);
					Config.bufferSizeEditText.setText("0");
					Toast.makeText(getApplicationContext(),
							"可设置sleep interval控制UDP发送速率\r\n即：每发一个包sleep多久",
							Toast.LENGTH_SHORT).show();
					break;
				case 9:
					Config.serverConentEditText.setText("139.129.44.108");
					Config.portTextView.setText(Config.tcpUploadPort);
					Config.settingTextView.setText("min  Size:");
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(true);
					Config.bufferSizeEditText.setVisibility(View.VISIBLE);
					Config.bufferSizeEditText.setText("1024");
					Toast.makeText(getApplicationContext(),
							"TCP Uplink Flow Test\r\n测试时间完成后,自动开启新连接",
							Toast.LENGTH_SHORT).show();
					break;
				default:
					Config.portTextView.setText("");
					Config.serverConentEditText.setEnabled(true);
					Config.serverTimeEditText.setEnabled(false);
					Config.bufferSizeEditText.setText("-1");
					Config.bufferSizeEditText.setVisibility(View.VISIBLE);
					Config.bufferSizeEditText.setEnabled(false);
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		String serverIPString = Config.serverConentEditText.getText()
				.toString();
		Config.ping = new Pingtest(serverIPString,"/sdcard/ping/",1000);
		Config.ping.start();
		startThread();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(1, 1, 0, "连接测试");
		menu.add(1, 2, 0, "版本介绍");
		menu.add(1, 3, 0, "完全退出");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
			// Intent MyIntent = new Intent(Intent.ACTION_MAIN);
			// MyIntent.addCategory(Intent.CATEGORY_HOME);
			// startActivity(MyIntent);
			if (Config.wifiState.equals("Disconnected")
					&& Config.dataConnectionState.equals("Disconnected")) {
				Config.reportTextView.setText("网络已断开，请检查网络连接");
				Toast.makeText(getApplicationContext(), "网络已断开，请检查网络连接",
						Toast.LENGTH_SHORT).show();
				return false;
			}
			Config.reportTextView.setText("Testing...");
			handler4Ping.post(runnable4Ping);

			new Thread() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					Measurement.pingCmdTest(Config.addressSina, 10);
				}
			}.start();

			break;
		case 2:
			String tmp = "HSRNetTest支持三网的全网制式\r\nCopyright © 2016  恪家饭";
			Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_LONG)
					.show();
			break;
		case 3:
			try {
				Config.fosMobile.close();
				Config.fosUplink.close();
				Config.fosDownlink.close();
				Config.fosPing.close();
				handler4Speed.removeCallbacks(runnable4Speed);
				handler4GPS.removeCallbacks(runnable4GPS);
				handler4Ping.removeCallbacks(runnable4Ping);
				handler4Wifi.removeCallbacks(runnable4Wifi);
				handler4Show.removeCallbacks(runnable4Show);
	            Intent serviceIntent = new Intent(); 
	            serviceIntent.setAction(MYSERVICE); 
	            stopService(serviceIntent); 
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
			//myXGtest.start_listen();
			Log.d("output","destroy");
			//Config.tel.listen(myXGtest.MyListener, Config.phoneEvents);
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
			//myXGtest.start_listen();
			Log.d("output","pause");
			//Config.tel.listen(myXGtest.MyListener, Config.phoneEvents);
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), "Check your SIM card!",
					Toast.LENGTH_LONG).show();
		}
		//StatService.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			//myXGtest.start_listen();
			//Config.tel.listen(myXGtest.MyListener, Config.phoneEvents);
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), "Check your SIM card!",
					Toast.LENGTH_LONG).show();
		}
		//StatService.onResume(this);
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
    	
    	super.onSaveInstanceState(savedInstanceState);
    	      
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle("Exit");
			builder.setMessage("退出MobiNet?");
			builder.setPositiveButton("返回",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							return;
						}
					});
			builder.setNegativeButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							android.os.Process.killProcess(android.os.Process
									.myPid());
						}
					});
			builder.show();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Config.start.setEnabled(false);
		Config.pause.setEnabled(true);
		if (v.equals(Config.start)) {
			Config.testMeasuretime = Config.serverTimeEditText.getText()
					.toString();
			if (Config.wifiState.equals("Disconnected")
					&& Config.dataConnectionState.equals("Disconnected")) {
				Config.reportTextView.setText("网络已断开，请检查网络连接");
				Toast.makeText(getApplicationContext(), "网络已断开，请检查网络连接",
						Toast.LENGTH_SHORT).show();
				return;
			}

			Config.pingFlag = 0;
			String serverIPString = Config.serverConentEditText.getText()
					.toString();
			String measureTimeString = Config.testMeasuretime;
			String measureIntervalString = Config.testInterval;
			Config.bufferSize = Integer.valueOf(Config.bufferSizeEditText
					.getText().toString());
			Config.bufferSizeEditText.setEnabled(false);

			switch (Config.measurementID) {
			case 0:
				Config.reportTextView.setText("TCP downlink testing...");
				Config.myTcpTest = new TCPTest(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosDownlink, 1);
				Config.myTcpTest2 = new TCPTest(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosUplink, 2);
//				Config.ping = new Pingtest(serverIPString,"/sdcard/ping/",1000);
//				Config.ping.start();
//				Config.ping_switch.setEnabled(true);
//				Config.TcpDump.start_capture();
				handler4Show.post(runnable4Show);
				break;
			case 1:
				Config.reportTextView.setText("TCP uplink testing...");
				Config.myTcpTest = new TCPTest(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosUplink, 2);
				handler4Show.post(runnable4Show);
				break;
			case 2:
				Config.reportTextView.setText("UDP downlink testing...");
				Config.start.setEnabled(false);
				Config.myUdpTest = new UDPTest(serverIPString,
						measureTimeString, Config.fosDownlink, 1);
				break;
			case 3:
				Config.reportTextView.setText("UDP uplink testing...");
				Config.start.setEnabled(false);
				Config.myUdpTest = new UDPTest(serverIPString,
						measureTimeString, Config.fosUplink, 2);
				break;
			case 4:
				Config.reportTextView
						.setText("TCP downlink & uplink testing...");
				Config.start.setEnabled(false);
				Config.mySender = new Sender(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosDownlink, Config.fosUplink);
				break;
			case 5:
				Config.reportTextView.setText("TCP double downlink testing...");
				Config.start.setEnabled(false);
				Config.myTcpTest = new TCPTest(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosDownlink, 1);

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Config.myTcpTest2 = new TCPTest(serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosDownlink, 5);
				handler4Show.post(runnable4Show);
				break;
			case 6:
				Config.reportTextView.setText("TCP double uplink testing...");
				Config.start.setEnabled(false);
				Config.myTcpTest = new TCPTest(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosUplink, 2);

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Config.myTcpTest2 = new TCPTest(serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosUplink, 6);
				handler4Show.post(runnable4Show);
				break;
			case 7:
				Config.reportTextView.setText("TCP DL + UDP DL testing...");
				Config.start.setEnabled(false);
				Config.myTcpTest = new TCPTest(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosDownlink, 1);
				Config.myUdpTest = new UDPTest(serverIPString,
						measureTimeString, Config.fosDownlink, 1);
				break;
			case 8:
				Config.reportTextView.setText("TCP UL + UDP UL testing...");
				Config.start.setEnabled(false);
				Config.myTcpTest = new TCPTest(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosUplink, 2);
				Config.myUdpTest = new UDPTest(serverIPString,
						measureTimeString, Config.fosUplink, 2);
				break;
			case 9:
				Config.reportTextView.setText("TCP uplink flow testing...");
				Config.myTcpTest = new TCPTest(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosUplink, 7);
				handler4Show.post(runnable4Show);
				break;
			default:
				Config.reportTextView.setText("Test doesn't support");
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
				Config.netTextView.setText("平均上行:"
						+ Config.myTcpTest2.mAvgUplinkThroughput + " 平均下行:"
						+ Config.myTcpTest.mAvgDownlinkThroughput + " kbps");
				Config.start.setEnabled(true);
			} else if (msg.what == 4) {
				Config.reportTextView.setText(msg.getData().getString("acce"));
				Config.start.setEnabled(true);
			}
		};
	};

	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			// TODO Auto-generated method stub
			/**
			 * 获取信号强度参数
			 * http://www.oschina.net/code/explore/android-4.0.1/telephony
			 * /java/android/telephony/SignalStrength.java 0:
			 * GsmSignalStrength(0-31) GsmBitErrorRate(0-7) 2: CdmaDbm CdmaEcio
			 * EvdoDbm EvdoEcio EvdoSnr(0-8) 7: LteSignalStrength LteRsrp
			 * LteRsrq LteRssnr LteCqi 非4G则全为-1 getGsmLevel getLteLevel
			 * getCdmaLevel getEvdoLevel
			 */
			String allSignal = signalStrength.toString();
			try {
				String[] parts = allSignal.split(" ");
				Config.gsmSignalStrength = parts[1];
				Config.cdmaDbm = parts[3];
				Config.cdmaEcio = parts[4];
				Config.evdoDbm = parts[5];
				Config.evdoEcio = parts[6];
				Config.evdoSnr = parts[7];
				Config.lteSignalStrength = parts[8];
				Config.lteRsrp = parts[9];
				Config.lteRsrq = parts[10];
				Config.lteRssnr = parts[11];
			} catch (Exception e) {
				// TODO: handle exception
			}

			/**
			 * asu与Level关系 Note3: 30、23、19 Other: 11、7、4
			 */
			// int level = SignalUtil.getCurrentLevel(signalStrength.isGsm());

			/**
			 * 记录全部信号信息
			 */
			TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			Config.networkType = telManager.getNetworkType();
			SignalUtil.getCurrentNetworkType(Config.networkType);

			if (signalStrength.isGsm()) {
				allSignal = (Integer.parseInt(Config.gsmSignalStrength) * 2 - 113)
						+ " "
						+ (Integer.parseInt(Config.lteSignalStrength) * 2 - 113)
						+ " "
						+ Config.lteRsrp
						+ " "
						+ Config.lteRsrq
						+ " "
						+ Config.lteRssnr
						+ " "
						+ Config.networkType
						+ " "
						+ Config.servingCid;
			} else {
				allSignal = (Integer.parseInt(Config.gsmSignalStrength) * 2 - 113)
						+ " "
						+ Config.cdmaDbm
						+ " "
						+ Config.cdmaEcio
						+ " "
						+ Config.evdoDbm
						+ " "
						+ Config.evdoEcio
						+ " "
						+ Config.evdoSnr
						+ " "
						+ Config.networkType
						+ " "
						+ Config.servingCid;
			}

			Config.typeTextView.setText(Config.providerName + "-"
					+ Config.networkTypeString + " " + Config.networkType
					+ " (" + Build.MODEL + "-" + Build.VERSION.RELEASE + ")");

			String cellContent = Config.networkType + " " + Config.servingCid;
			if (cellContent.equals(Config.lastCellInfoString)) {

			} else {
				Config.lastCellInfoString = cellContent;
				Config.handoffNumber++;
			}

			String parameter = "";
			switch (Config.networkType) {
			case 4:
			case 5:
			case 6:
			case 7:
			case 12:
				Config.asuShowString = "1x:" + Config.cdmaDbm + " 3G:"
						+ Config.evdoDbm;
				parameter = "Ecio:" + Config.cdmaEcio + "/" + Config.evdoEcio
						+ " SNR:" + Config.evdoSnr;
				break;
			case 13:
				Config.asuShowString = "2G:" + Config.gsmSignalStrength
						+ " 4G:" + Config.lteSignalStrength;
				parameter = "RSRP:" + Config.lteRsrp + " RSRQ:"
						+ Config.lteRsrq + " SNR:" + Config.lteRssnr;
				break;
			default:
				Config.asuShowString = Config.gsmSignalStrength;
				parameter = "NULL";
				break;
			}
			Config.asuTextView.setText(Config.asuShowString);
			Config.signalParameterTextView.setText(parameter);

			super.onSignalStrengthsChanged(signalStrength);
		}

		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {
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
				if (Config.lastConnect) {
					Config.disconnectNumber++;
					Config.lastConnect = false;
				}
				break;
			case TelephonyManager.DATA_CONNECTING:// 1
				// 网络正在连接
				Config.dataConnectionState = "Connecting";
				break;
			case TelephonyManager.DATA_CONNECTED:// 2
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
			String cellContent = Config.networkType + " " + Config.servingCid;
			if (cellContent.equals(Config.lastCellInfoString)) {

			} else {
				Config.lastCellInfoString = cellContent;
				Config.handoffNumber++;
			}

			if (Config.dataConnectionState.equals(Config.lastDataStateString)) {

			} else {
				Config.lastDataStateString = Config.dataConnectionState;
				Config.directionTextView.setText(Config.dataConnectionState
						+ " 方向:" + Config.dataDirection);
			}

			Config.handoffTextView.setText("切换:" + Config.handoffNumber
					+ " 断网:" + Config.disconnectNumber);

			super.onDataConnectionStateChanged(state, networkType);
		}

		@Override
		public void onDataActivity(int direction) {
			// TODO Auto-generated method stub
			switch (direction) {
			case TelephonyManager.DATA_ACTIVITY_NONE:// 0
				// No IP Traffic
				Config.dataDirection = "NONE";
				break;
			case TelephonyManager.DATA_ACTIVITY_IN:// 1
				Config.dataDirection = "In";
				break;
			case TelephonyManager.DATA_ACTIVITY_OUT:// 2
				Config.dataDirection = "Out";
				break;
			case TelephonyManager.DATA_ACTIVITY_INOUT:// 3
				Config.dataDirection = "InOut";
				break;
			case TelephonyManager.DATA_ACTIVITY_DORMANT:// 4
				// Data connection is active, but physical link is down
				Config.dataDirection = "Dormant";
				break;
			default:
				Config.dataDirection = "Unknown";
				break;
			}
			/**
			 * 写入日志
			 */
			if (Config.dataDirection.equals(Config.lastDataDirectionString)) {

			} else {
				Config.lastDataDirectionString = Config.dataDirection;
				Config.directionTextView.setText(Config.dataConnectionState
						+ " 方向:" + Config.dataDirection);
			}

			super.onDataActivity(direction);
		}

		@Override
		public void onCellLocationChanged(CellLocation location) {
			// TODO Auto-generated method stub
			TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			Config.networkType = telManager.getNetworkType();
			SignalUtil.getCurrentNetworkType(Config.networkType);
			try {
				switch (Config.networkType) {
				case 4:
				case 5:
				case 6:
				case 7:
				case 12:
					CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) location;
					Config.servingCid = cdmaCellLocation.getBaseStationId();
					Config.servingLac = cdmaCellLocation.getNetworkId();
					Config.servingPsc = cdmaCellLocation.getSystemId();
					break;
				default:
					GsmCellLocation gsmCellLocation = (GsmCellLocation) location;
					Config.servingCid = gsmCellLocation.getCid() & 0xffff;
					Config.servingLac = gsmCellLocation.getLac();
					Config.servingPsc = gsmCellLocation.getPsc();
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				// when no SIM card
			}

			String cellContent = Config.networkType + " " + Config.servingCid;
			if (cellContent.equals(Config.lastCellInfoString)) {

			} else {
				Config.lastCellInfoString = cellContent;

				Config.handoffNumber++;
				Config.handoffTextView.setText("切换:" + Config.handoffNumber
						+ " 断网:" + Config.disconnectNumber);
			}

			Config.basestationTextView.setText(Config.servingCid + " "
					+ Config.servingLac);

			super.onCellLocationChanged(location);
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
			Config.gpsTextView.setText(Config.gpsFixNumber + "/"
					+ Config.gpsAvailableNumber + " " + Config.gpsStateString);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Config.gpsStateString = "Disabled";
			Config.gpsTextView.setText(Config.gpsFixNumber + "/"
					+ Config.gpsAvailableNumber + " " + Config.gpsStateString);
			Config.prepareGPSFlag = false;
			Config.mobilitySpeed = "Unknown";
		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			/**
			 * 测速
			 */
			Config.loc = Config.locationManager
					.getLastKnownLocation(Config.bestProvider);
			Config.speed = Config.loc.getSpeed();
			Config.latitude = Config.loc.getLatitude();
			Config.longitude = Config.loc.getLongitude();
			Config.accuracy = Config.loc.getAccuracy();
			Config.locationTextView.setText(Config.latitude + ","
					+ Config.longitude);

			/**
			 * 整理日志数据
			 */
			float speed2 = (float) (Config.speed * 3.6);
			Config.mobilitySpeed = String.valueOf(speed2);
			Config.speedTextView.setText(Config.mobilitySpeed + " km/h");
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
				while (it.hasNext()
						&& Config.gpsAvailableNumber <= maxSatellites) {
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
		if (Config.locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| Config.locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Config.criteria = new Criteria();
			Config.criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
			Config.criteria.setAltitudeRequired(true); // 显示海拔
			Config.criteria.setBearingRequired(true); // 显示方向
			Config.criteria.setSpeedRequired(true); // 显示速度
			Config.criteria.setCostAllowed(false); // 不允许有花费
			Config.criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
			Config.bestProvider = Config.locationManager.getBestProvider(
					Config.criteria, true);

			// locationManager用来监听定位信息的改变
			Config.locationManager.requestLocationUpdates(Config.bestProvider,
					100, 5, locationListener);
			Config.locationManager.addGpsStatusListener(statusListener);

			Location gpsLocation = Config.locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (gpsLocation == null) {
				gpsLocation = Config.locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if (gpsLocation != null) {
				Config.latitude = gpsLocation.getLatitude(); // 经度
				Config.longitude = gpsLocation.getLongitude(); // 纬度
				Config.locationTextView.setText(Config.latitude + ","
						+ Config.longitude);
			}
		}
		if (Config.locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Config.prepareGPSFlag = true;
		} else {
			Config.gpsTextView.setText("Disabled");
			if (Config.prepareGPSFlag) {

			} else {
				showGPSDialog(this);
			}
		}
	}

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
			Config.netTextView.setText("上行:"
					+ Config.myTcpTest2.mUplinkThroughput + " 下行:"
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
				Config.pingTextView.setText("Ping:" + Config.pingInfo + " DNS:"
						+ Config.dnsLookupInfo + " HTTP:" + Config.httpInfo);
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
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
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
							+ wifiInfo.getRssi() + " Speed:"
							+ wifiInfo.getLinkSpeed();
				} else {
					Config.wifiState = "Disconnected";
					wifiContent = Config.wifiState;
				}

				if (wifiContent.equals(Config.lastWifiState)) {

				} else {
					// Config.wifiTextView.setText(wifiContent);
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
		builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(intent, 0);
			}
		});
		builder.show();
	}

	private void startThread() {
		try {
			// 启动线程
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
}
