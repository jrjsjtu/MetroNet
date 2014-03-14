package thu.wireless.mobinet.kingyoung;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;

import android.app.Activity;
import android.app.AlertDialog;
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
 * bug1: HSDPA��cid���� -- & 0xffff 
 * bug2: HSDPA��asu���� -- Ŀǰֻ����S4���������� 
 * bug3: ��������ͳ�ƴ��� -- disconnectNumber 
 * bug4: IN/OUT����Ƶ�� -- Dormant/None 
 * bug5: WiFi״̬����ʱʱ����--handler4Wifi
 * bug6: ���ڻ�վʵʱ����
 * bug7: GPS��ʾ�ٶȻ���
 * add1: ���ٷ���--ʱʱ��ƽ�� 
 * add2: ��Ϣ�ش�������Ϣ 
 * add3: ping
 * add4: ��γ��
 * add5: רҵģʽ
 * note1:��վ�̱߳�onCellLocationChanged������
 * note2:Ϊ����д��־Ƶ��,ֻ�з����л�ʱ�ŻὫ���ڻ�վ��Ϣ��¼
 * note3:Ϊ����д��־Ƶ��,ֻ���ٶȱ仯ʱ�ŻὫ�ٶ���Ϣ��¼
 * note4:invokeDownǰ�� 
 * note5:�޸�Downlink�˳�����--packetTimed <= mEndTimed
 * 2014.2.28
 * note6:ȥ���˰ٶ�ͳ�Ƽ����
 */
public class MainActivity extends Activity implements OnClickListener {

	private MyPhoneStateListener myListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// ������Ļ����
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		appStartDialog(this);

		// ���ðٶ�ͳ��
		StatService.setAppChannel(this, "Baidu Market", true);
		StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1, false);

		// �ֻ�״̬��ؿؼ�
		Config.start = (Button) findViewById(R.id.button_Start);
		Config.start.setOnClickListener(this);

		Config.serverConentEditText = (EditText) findViewById(R.id.editText_serverIP);
		Config.serverConentEditText.clearFocus();
		Config.asuTextView = (TextView) findViewById(R.id.signalText);
		Config.basestationTextView = (TextView) findViewById(R.id.basestationText);
		Config.cellidTextView = (TextView) findViewById(R.id.cellidText);
		Config.directionTextView = (TextView) findViewById(R.id.directionText);
		Config.speedTextView = (TextView) findViewById(R.id.speedText);
		Config.gpsTextView = (TextView) findViewById(R.id.gpsText);
		Config.locationTextView = (TextView) findViewById(R.id.locationText);
		Config.typeTextView = (TextView) findViewById(R.id.typeText);
		Config.reportTextView = (TextView) findViewById(R.id.serverText);
		Config.handoffTextView = (TextView) findViewById(R.id.handoffText);
		Config.wifiTextView = (TextView) findViewById(R.id.wifiText);
		Config.netTextView = (TextView) findViewById(R.id.netText);
		Config.pingTextView = (TextView) findViewById(R.id.pingText);
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
				Config.serverConentEditText.setText(Config.defaultTarget[arg2]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		// ������־Ŀ¼				
		try {
//			File file = getDir("MobiNet", Context.MODE_PRIVATE);
//			mobilePath = file.getAbsolutePath();
//			fosMobile = new FileOutputStream(mobilePath + "/Mobile.txt", true);
//			fosSignal = new FileOutputStream(mobilePath + "/Signal.txt", true);
//			fosSpeed = new FileOutputStream(mobilePath + "/Speed.txt", true);
//			fosCell = new FileOutputStream(mobilePath + "/Cell.txt", true);
//			fosUplink = new FileOutputStream(mobilePath + "/Uplink.txt", true);
//			fosDownlink = new FileOutputStream(mobilePath + "/Downlink.txt", true);
//			fosPing = new FileOutputStream(mobilePath + "/Ping.txt", true);
			
			//MobiNet/XXX/Mobile.txt
			Config.mobilePath = android.os.Environment.getExternalStorageDirectory() + "/MobiNet";
			String pathDate = Config.dirDateFormat.format(new Date(System.currentTimeMillis()));
			File mobileFile = new File(Config.mobilePath);
			mobileFile.mkdirs();
			Config.mobilePath = Config.mobilePath + "/" + pathDate;
			mobileFile = new File(Config.mobilePath);
			mobileFile.mkdirs();
			Config.fosMobile = new FileOutputStream(Config.mobilePath + "/Mobile.txt", true);
			Config.fosSignal = new FileOutputStream(Config.mobilePath + "/Signal.txt", true);
			Config.fosSpeed = new FileOutputStream(Config.mobilePath + "/Speed.txt", true);
			Config.fosCell = new FileOutputStream(Config.mobilePath + "/Cell.txt", true);
			Config.fosUplink = new FileOutputStream(Config.mobilePath + "/Uplink.txt", true);
			Config.fosDownlink = new FileOutputStream(Config.mobilePath + "/Downlink.txt", true);
			Config.fosPing = new FileOutputStream(Config.mobilePath + "/Ping.txt", true);
			Config.fosAddition = new FileOutputStream(Config.mobilePath + "/Addition.txt", true);
			Config.fosDNS = new FileOutputStream(Config.mobilePath + "/DNS.txt", true);
					
			//data/data/xxx/files/Mobile.txt
//			fosMobile = this.openFileOutput("Mobile.txt", Context.MODE_APPEND);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// ��ȡ�ֻ���Ϣ
		try {
			myListener = new MyPhoneStateListener();
			Config.tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			Config.tel.listen(myListener, Config.phoneEvents);
			if(Config.tel.getSimState()!=TelephonyManager.SIM_STATE_READY) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		        builder.setIcon(R.drawable.ic_launcher);
		        builder.setTitle("No SIM Card");
		        builder.setMessage("�����SIM��");
		        builder.setPositiveButton("֪����",  
		                new DialogInterface.OnClickListener() {  
		                    public void onClick(DialogInterface dialog, int whichButton) {  
		                        return;
		                    }  
		                });
			}
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), "Check your SIM card!",
					Toast.LENGTH_LONG).show();
		}

		try {
			String IMSI = Config.tel.getSubscriberId();
			Config.providerName = null;
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002")
					|| IMSI.startsWith("46007")) {
				Config.providerName = "�й��ƶ�";
			} else if (IMSI.startsWith("46001")) {
				Config.providerName = "�й���ͨ";
			} else if (IMSI.startsWith("46003")) {
				Config.providerName = "�й�����";
			} else {
				Config.providerName = "�Ǵ�½�û�";
			}

			ConnectivityManager connect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connect
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			String infoString = "PhoneModel=" + Build.MODEL + "\nsdkVersion="
					+ String.valueOf(Build.VERSION.SDK_INT) + "\nosVersion="
					+ Build.VERSION.RELEASE + "\nProviderName=" + Config.providerName
					+ "\nDetailedState=" + networkInfo.getDetailedState()
					+ "\nReason=" + networkInfo.getReason() + "\nSubtypeName="
					+ networkInfo.getSubtypeName() + "\nExtraInfo="
					+ networkInfo.getExtraInfo() + "\nTypeName="
					+ networkInfo.getTypeName() + "\nIMEI=" + Config.tel.getDeviceId()
					+ "\nIMSI=" + Config.tel.getSubscriberId()
					+ "\nNetworkOperatorName=" + Config.tel.getNetworkOperatorName()
					+ "\nSimOperatorName=" + Config.tel.getSimOperatorName()
					+ "\nSimSerialNumber=" + Config.tel.getSimSerialNumber();
			Config.phoneModel = Build.MODEL;
			Config.osVersion = Build.VERSION.RELEASE;
			Config.fosMobile.write(infoString.getBytes());
			Config.fosMobile.write(System.getProperty("line.separator").getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		initNetwork();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(1, 1, 0, "����������"); // רҵģʽ
		menu.add(1, 2, 0, "Ping_DNS"); // ȥ�����
		menu.add(1, 3, 0, "�汾����");
		menu.add(1, 4, 0, "��ȫ�˳�");
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
				Config.reportTextView.setText("�����ѶϿ���������������");
				Toast.makeText(getApplicationContext(), "�����ѶϿ���������������",
						Toast.LENGTH_SHORT).show();
				return false;
			}
			Config.reportTextView.setText("Testing...");
			
			new Thread(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					String serverIPString = Config.serverConentEditText.getText().toString();					
					String measureTimeString = Config.testMeasuretime;
					String measureIntervalString = Config.testInterval;
					Config.mySender = new Sender(mHandler, serverIPString,
							measureTimeString, measureIntervalString,
							Config.fosDownlink, Config.fosUplink);
					handler4Show.post(runnable4Show);
				}				
			}.start();
			
			break;
		case 2:
//			handler4Ad.removeCallbacks(runnable4Ad);
			
			if (Config.wifiState.equals("Disconnected")
					&& Config.dataConnectionState.equals("Disconnected")) {
				Config.reportTextView.setText("�����ѶϿ���������������");
				Toast.makeText(getApplicationContext(), "�����ѶϿ���������������",
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
					Measurement.pingCmdProfession(Config.addressSina);
				}				
			}.start();
			
			new Thread(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					for (int i = 0; i < 9999999; i++) {
						Measurement.dnsLookupTest(Config.addressSina, 10);
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}				
			}.start();
			
			break;
		case 3:
			String tmp = "MobiNet���������ֻ�������״̬\r\n֧���ƶ���ͨ����������ȫ����ʽ\r\nCopyright  2014  㡼ҷ�";
			Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_LONG)
					.show();
			break;
		case 4:
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
				handler4Ad.removeCallbacks(runnable4Ad);
				handler4GPS.removeCallbacks(runnable4GPS);
				handler4Ping.removeCallbacks(runnable4Ping);
				handler4Wifi.removeCallbacks(runnable4Wifi);
				handler4Show.removeCallbacks(runnable4Show);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ����
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
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Toast.makeText(getApplicationContext(), "�����˳��������˵��е��˳�ѡ��", Toast.LENGTH_SHORT).show();
			return false;
		case KeyEvent.KEYCODE_HOME:
			return false;

		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(Config.start)) {
			if (Config.wifiState.equals("Disconnected")
					&& Config.dataConnectionState.equals("Disconnected")) {
				Config.reportTextView.setText("�����ѶϿ���������������");
				Toast.makeText(getApplicationContext(), "�����ѶϿ���������������",
						Toast.LENGTH_SHORT).show();
				return;
			}

			Config.pingFlag = 0;
			String serverIPString = Config.serverConentEditText.getText().toString();
			
			switch (Config.measurementID) {
			case 0:
				String measureTimeString = Config.testMeasuretime;
				String measureIntervalString = Config.testInterval;
				Config.mySender = new Sender(mHandler, serverIPString,
						measureTimeString, measureIntervalString,
						Config.fosDownlink, Config.fosUplink);
				handler4Show.post(runnable4Show);
				break;
			case 1:
				
				break;
			case 2:
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
			case 3:
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
			case 4:
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
				Config.netTextView.setText("ƽ������:" + Config.mySender.mAvgUplinkThroughput
						+ " ƽ������:" + Config.mySender.mAvgDownlinkThroughput + " kbps");
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
			 * ��ȡ��ǰϵͳʱ��
			 */
			String date = Config.contentDateFormat.format(new Date(System.currentTimeMillis()));

			/**
			 * ��ȡ�ź�ǿ�Ȳ���
			 */
			int asu = signalStrength.getGsmSignalStrength();
			int evdodbm = signalStrength.getEvdoDbm();
			int cdmadbm = signalStrength.getCdmaDbm();

			/**
			 * http://www.oschina.net/code/explore/android-4.0.1/telephony/java/android/telephony/SignalStrength.java
			 * 0: GsmSignalStrength(0-31) GsmBitErrorRate(0-7)
			 * 2: CdmaDbm CdmaEcio EvdoDbm EvdoEcio EvdoSnr(0-8)
			 * 7: LteSignalStrength LteRsrp LteRsrq LteRssnr LteCqi ��4G��ȫΪ-1
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
			 * �����������
			 */
			if (signalStrength.isGsm()) {
				Config.dianxinFlag = 2;
			}
			Config.networkType = telManager.getNetworkType();
			SignalUtil.getCurrentNetworkType(Config.networkType);
			/**
			 * asu��Level��ϵ
			 * Note3: 30��23��19
			 * Other: 11��7��4 
			 */
			int level = SignalUtil.getCurrentLevel(signalStrength.isGsm());
			
			/**
			 * ��¼ȫ���ź���Ϣ
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
			 * ������־����
			 */
			String content = Config.networkTypeString + " ";
			Config.typeTextView.setText(Config.providerName + "-" + Config.networkTypeString);
			
			switch (Config.dianxinFlag) {
			case 1:
			case 3:
				content = content + String.valueOf(cdmadbm) + " "
						+ String.valueOf(evdodbm);
				Config.asuShowString = "1x:" + String.valueOf(cdmadbm) + " 3G:"
						+ String.valueOf(evdodbm);
				break;
			case 4:
				content = content + String.valueOf(asu) + " "
						+ String.valueOf(Config.lteSignalStrength);
				Config.asuShowString = "2G:" + String.valueOf(asu) + " 4G:"
						+ String.valueOf(Config.lteSignalStrength);
				break;
			default:
				content = content + String.valueOf(asu);
				Config.asuShowString = String.valueOf(asu);
				break;
			}
			Config.asuShowString += " Level:" + level;
			Config.asuTextView.setText(Config.asuShowString);

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
				// ����Ͽ�		
				Config.dataConnectionState = "Disconnected";
				if (Config.lastConnect) {
					Config.disconnectNumber++;
					Config.lastConnect = false;
					Config.handoffTextView.setText("�л�:"
							+ String.valueOf(Config.handoffTotal) + " ����:"
							+ String.valueOf(Config.disconnectNumber));
				}
				break;
			case TelephonyManager.DATA_CONNECTING:
				// ������������
				Config.dataConnectionState = "Connecting";
				break;
			case TelephonyManager.DATA_CONNECTED:
				// ����������
				Config.dataConnectionState = "Connected";
				Config.lastConnect = true;
				break;
			default:
				Config.dataConnectionState = "Unknown";
				break;
			}

			/**
			 * д����־
			 */
			if (Config.dataConnectionState.equals("Connected")) {
				
			} else {
				String tmp = Config.dataConnectionState + " " + Config.dataDirection;
				if (tmp.equals(Config.lastConnectString)) {

				} else {
					Config.lastConnectString = tmp;
					Config.directionTextView.setText(Config.dataConnectionState + " ����:" + Config.dataDirection);
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
			 * д����־
			 */
			if (Config.dataDirection.equals("Dormant") || Config.dataDirection.equals("NONE")
					|| Config.lastConnectString == null) {
				String tmp = Config.dataConnectionState + " " + Config.dataDirection;
				if (tmp.equals(Config.lastConnectString)) {

				} else {
					Config.lastConnectString = tmp;
					Config.directionTextView.setText(Config.dataConnectionState + " ����:" + Config.dataDirection);
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
				Config.gpsTextView.setText(Config.gpsStateString + " ������:"
						+ String.valueOf(Config.gpsFixNumber) + "/"
						+ String.valueOf(Config.gpsAvailableNumber));
				break;
			case LocationProvider.OUT_OF_SERVICE:
				Config.gpsStateString = "OutOfService";
				Config.gpsTextView.setText(Config.gpsStateString + " ������:"
						+ String.valueOf(Config.gpsFixNumber) + "/"
						+ String.valueOf(Config.gpsAvailableNumber));
				Config.mobilitySpeed = "Unknown";
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Config.gpsStateString = "Unavailable";
				Config.gpsTextView.setText(Config.gpsStateString + " ������:"
						+ String.valueOf(Config.gpsFixNumber) + "/"
						+ String.valueOf(Config.gpsAvailableNumber));
				Config.mobilitySpeed = "Unknown";
				break;
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			// GPS����ʱ����
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
			 * ����
			 */
			Config.loc = Config.locationManager.getLastKnownLocation(Config.bestProvider);
			Config.speed = Config.loc.getSpeed();
			Config.latitude = Config.loc.getLatitude();
			Config.longitude = Config.loc.getLongitude();
			Config.accuracy = Config.loc.getAccuracy();
			Config.locationTextView.setText(Config.latitude + "," + Config.longitude);

			/**
			 * ������־����
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

	// ��ȡ��ǰ����GPS����
	private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {

		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub
			LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			GpsStatus status = locManager.getGpsStatus(null); // ȡ��ǰ״̬

			switch (event) {
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				// ����״̬�ı�
				int maxSatellites = status.getMaxSatellites(); // ��ȡ���ǿ�����Ĭ�����ֵ
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
				Config.gpsTextView.setText(Config.gpsStateString + " ������:"
						+ String.valueOf(Config.gpsFixNumber) + "/"
						+ String.valueOf(Config.gpsAvailableNumber));
				break;
			case GpsStatus.GPS_EVENT_STARTED:
				Config.gpsStateString = "Start";
				Config.gpsTextView.setText(Config.gpsStateString + " ������:"
						+ String.valueOf(Config.gpsFixNumber) + "/"
						+ String.valueOf(Config.gpsAvailableNumber));
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				Config.gpsStateString = "Stop";
				Config.gpsTextView.setText(Config.gpsStateString + " ������:"
						+ String.valueOf(Config.gpsFixNumber) + "/"
						+ String.valueOf(Config.gpsAvailableNumber));
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Config.gpsStateString = "FirstFix";
				Config.gpsTextView.setText(Config.gpsStateString + " ������:"
						+ String.valueOf(Config.gpsFixNumber) + "/"
						+ String.valueOf(Config.gpsAvailableNumber));
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
			Config.criteria.setAccuracy(Criteria.ACCURACY_FINE); // �߾���
			Config.criteria.setAltitudeRequired(true); // ��ʾ����
			Config.criteria.setBearingRequired(true); // ��ʾ����
			Config.criteria.setSpeedRequired(true); // ��ʾ�ٶ�
			Config.criteria.setCostAllowed(false); // �������л���
			Config.criteria.setPowerRequirement(Criteria.POWER_LOW); // �͹���
			Config.bestProvider = Config.locationManager.getBestProvider(Config.criteria, true);

			// locationManager����������λ��Ϣ�ĸı�
			Config.locationManager.requestLocationUpdates(Config.bestProvider, 100, 5,
					locationListener);
			Config.locationManager.addGpsStatusListener(statusListener);
			
			Location gpsLocation = Config.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(gpsLocation == null){     
				gpsLocation = Config.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(gpsLocation != null){     
            	Config.latitude = gpsLocation.getLatitude(); //����     
            	Config.longitude = gpsLocation.getLongitude(); //γ��  
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
		//��������
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
				Config.typeTextView.setText(Config.providerName + "-" + Config.networkTypeString);
				Config.basestationTextView.setText(String.valueOf(Config.servingCid) 
						+ "/" + String.valueOf(Config.servingLac));
				
				if (cid == Config.lastcellid) {

				} else {
					Config.lastcellid = cid;
					Config.handoffTotal++;
					Config.handoffTextView.setText("�л�:"
							+ String.valueOf(Config.handoffTotal) + " ����:"
							+ String.valueOf(Config.disconnectNumber));
				}

				// show neighboring cell
				if (infos.size() == 0) {
					// NoNeighboringCell
					Config.cellidTextView.setText("No");
				} else {
					NeighboringCellInfo[] info = infos.toArray(new NeighboringCellInfo[0]);
					String nbcString = String.valueOf(info[0].getRssi())
							+ "\t" + String.valueOf(info[0].getCid()) + "/"
							+ String.valueOf(info[0].getLac()) + " \t"
							+ String.valueOf(info[0].getNetworkType());

					for (int i = 1; i < infos.size(); i++) {
						nbcString = nbcString + "\n"
								+ String.valueOf(info[i].getRssi()) + "\t"
								+ String.valueOf(info[i].getCid()) + "/"
								+ String.valueOf(info[i].getLac()) + " \t"
								+ String.valueOf(info[i].getNetworkType());
					}
					Config.cellidTextView.setText(nbcString);
				}
				
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

				// ÿ50msִ��һ��
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
			// ���
//			initBaiduAd();
//			handler4Ad.post(runnable4Ad);
		}
	};

	private Handler handler4Show = new Handler();

	private Runnable runnable4Show = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler4Show.postDelayed(runnable4Show, 1000);
			Config.netTextView.setText("����:" + Config.mySender.mUplinkThroughput + " ����:"
					+ Config.mySender.mDownlinkThroughput + " kbps");
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
						+ " ms  DNS:" + Config.dnsLookupInfo + " ms HTTP:" + Config.httpInfo);
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
		        builder.setMessage("���Ļ����ݲ�֧����ͨ�Բ��ԣ����ע�����汾");
				builder.setPositiveButton("ĬĬ�ȴ�", null);
				builder.show();
			} else if (Config.pingFlag == 21) {
				Config.pingTextView.setText("Ping:" + Config.pingInfo
						+ " ms  DNS:" + Config.dnsLookupInfo + " ms HTTP:" + Config.httpInfo);
				Config.reportTextView.setText("DNS lookup test finished");
				Config.start.setEnabled(true);
				Config.pingFlag = 20;
			} else if (Config.pingFlag == 22) {
				Config.reportTextView.setText("DNS lookup test failed");
				Config.start.setEnabled(true);
				Config.pingFlag = 20;
			} else if (Config.pingFlag == 31) {
				Config.pingTextView.setText("Ping:" + Config.pingInfo
						+ " ms  DNS:" + Config.dnsLookupInfo + " ms HTTP:" + Config.httpInfo);
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
			 * �Ƿ�����Wifi
			 */
			try {
				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				String wifiContent = null;

				if (activeNetInfo != null
						&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					Config.wifiState = "Connected";
					WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					wifiContent = Config.wifiState + " RSSI:"
							+ String.valueOf(wifiInfo.getRssi());
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

	private Handler handler4Ad = new Handler();

	private Runnable runnable4Ad = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub		
			handler4Ad.postDelayed(runnable4Ad, 90000);
	    	//after initBaiduAd
	    	if (interAd.isAdReady()) {
				interAd.showAd(MainActivity.this);
			} else {
				interAd.loadAd();
			}
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
        builder.setMessage("GPS��δ��������������˶��ٶ��빴ѡ");
        builder.setPositiveButton("�ݲ�����",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    	return;
                    }  
                });  
        builder.setNegativeButton("ȥ����",  
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
        builder.setMessage("�������ӻ�δ�������빴ѡ�ƶ�����");
        builder.setPositiveButton("�ݲ�����",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                        return;
                    }  
                });  
        builder.setNegativeButton("ȥ����",  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) { 
            			Intent intent = new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
            			startActivityForResult(intent, 0); 
                    }  
                });  
        builder.show();  
    }

	private InterstitialAd interAd;
	
	private void initBaiduAd() {
		interAd = new InterstitialAd(this);
		interAd.setListener(new InterstitialAdListener() {

			@Override
			public void onAdClick(InterstitialAd arg0) {
//				Log.i("InterstitialAd", "onAdClick");
			}

			@Override
			public void onAdDismissed() {
//				Log.i("InterstitialAd", "onAdDismissed");
				interAd.loadAd();
			}

			@Override
			public void onAdFailed(String arg0) {
//				Log.i("InterstitialAd", "onAdFailed");
			}

			@Override
			public void onAdPresent() {
//				Log.i("InterstitialAd", "onAdPresent");
			}

			@Override
			public void onAdReady() {
//				Log.i("InterstitialAd", "onAdReady");
			}

		});
		interAd.loadAd();
	}
	
	private void appStartDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("���ܽ���");
		String msg = "1.�ź�����:\r\n �ź�ǿ����ֵԽ���ź�Խ��\r\n"
				+ "2.�ֻ�����:\r\n �������ٰ�ť���ɿ�ʼ����\r\n" 
				+ "3.���縲��:\r\n ���ڻ�վԽ�����縲��Խ��\r\n"
				+ "4.WiFi����:\r\n RSSI��ֵԽ������WiFi�ź�Խǿ\r\n"
				+ "5.�˶��ٶ�:\r\n ����������ʱ�ٰ�(�迪��GPS)";
		builder.setMessage(msg);
		builder.setPositiveButton("֪����", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
            	try {
        			// �����߳�
        			handler4Cell.post(runnable4Cell);		
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
            	return;
            }
        });
		builder.show();
	}
}