package thu.wireless.mobinet.kingyoung;

import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Handler;

public class UDPTest {
	private Handler mHandler;
	public String mUplinkThroughput = "0";
	public String mDownlinkThroughput = "0";
	public String mAvgUplinkThroughput = "0";
	public String mAvgDownlinkThroughput = "0";

	private static String measureIP = "";
	private static String measureTime;
	private static int testmode = 0;
	FileOutputStream fosUplink = null;
	FileOutputStream fosDownlink = null;
	String sendStr2 = "";
	byte[] sendBuf2;
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
	
	public UDPTest(Handler _mHandler, String serverIP, String measuretime,
			String interval, FileOutputStream fosDown, FileOutputStream fosUp, int mode) {

		measureIP = serverIP;
		measureTime = measuretime;
		fosDownlink = fosDown;
		fosUplink = fosUp;
		testmode = mode;

		this.mHandler = _mHandler;

		(new myThread()).start();
	}
	
	public UDPTest(String serverIP, String measuretime, FileOutputStream fos, int mode) {

		measureIP = serverIP;
		measureTime = measuretime;
		if (mode == 2) {
			fosUplink = fos;
		} else if (mode == 1) {
			fosDownlink = fos;
		}
		testmode = mode;

		(new myThread()).start();
	}
	
	class myThread extends Thread {

		@Override
		public void run() {
			if (testmode == 2) {
				connect2server();
			} else if (testmode == 1) {
				server2client();
			}			
		}
	}
	
	private void connect2server() {
		int port = Config.udpUploadPort;
		int sleepInterval = Config.bufferSize;
		int bufLen = 1 * (1024-32);// MTU 64
    	sendStr2 = "";
		for (int j = 0; j < bufLen; j++)
			sendStr2 += '0';
		String tmp = String.format("%032d", 0);
		tmp = tmp + sendStr2;
		sendBuf2 = tmp.getBytes();
		while (true) {
			try {
				InetAddress addr = InetAddress.getByName(measureIP);//127.0.0.1
		        DatagramSocket client = new DatagramSocket();
		        long start = System.currentTimeMillis();

	        	int i = 1;
				while (true) {								
					String t = String.format("%032d", i);					
					t = t + sendStr2;
					sendBuf2 = t.getBytes();		        	
		            DatagramPacket sendPacket = new DatagramPacket(sendBuf2, sendBuf2.length, addr, port);
		            client.send(sendPacket);
		            // new add for sleep
					if (sleepInterval > 0) {
						Thread.sleep(sleepInterval);
					}
		            if (i%5 == 0) {
		            	fosUplink.write((df.format(new Date()) + " Send: " + i + "\n").getBytes());
					}
		            i++;
		            long now = System.currentTimeMillis() - start;
		            if (now > Long.valueOf(measureTime)*60000) {
		            	System.out.println(now/1000);
						break;
					}
				}
		        client.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				try {
					DatagramSocket client = new DatagramSocket();
					InetAddress addr = InetAddress.getByName(measureIP);//127.0.0.1
					byte[] buffer = "I'm Client".getBytes();
		            DatagramPacket sendPacket = new DatagramPacket(buffer,buffer.length, addr, port);
		            client.send(sendPacket);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
	
	private void server2client() {
		int port = Config.udpDownloadPort;
		while (true) {
			try {
				DatagramSocket client = new DatagramSocket();
				long start = System.currentTimeMillis();
				String sendStr = "Hello! I'm Client";
				byte[] sendBuf;
				sendBuf = sendStr.getBytes();//115.28.12.102
				InetAddress addr = InetAddress.getByName(measureIP);// 127.0.0.1		
				DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
				client.send(sendPacket);
				
				long i = 1;
				while (true) {
					byte[] recvBuf = new byte[1024];
					DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
					client.receive(recvPacket);
					String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
					System.out.println("Got:" + recvStr.length());
					if (i%5 == 0) {
		            	fosDownlink.write((df.format(new Date()) + " Receive: " + i + "\n").getBytes());
					}
					long now = System.currentTimeMillis() - start;
		            if (now > Long.valueOf(measureTime)*60000) {
		            	System.out.println(now/1000);
						break;
					}
		            i++;
				}
				client.close();
			} catch (Exception e) {
				// TODO: handle exception
				try {
					DatagramSocket client = new DatagramSocket();
					InetAddress addr = InetAddress.getByName(measureIP);//127.0.0.1
					byte[] buffer = "I'm Client".getBytes();
		            DatagramPacket sendPacket = new DatagramPacket(buffer,buffer.length, addr, port);
		            client.send(sendPacket);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}	
	}
}
