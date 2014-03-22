package thu.wireless.mobinet.kingyoung;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.os.Handler;

public class UDPTest {
	private Handler mHandler;
	public String mUplinkThroughput = "0";
	public String mDownlinkThroughput = "0";
	public String mAvgUplinkThroughput = "0";
	public String mAvgDownlinkThroughput = "0";
	
	private static DatagramSocket upSocket;
	private static DatagramSocket downSocket;
	private static String measureIP = "";
	private static String measureTime;
	private static String measureInterval;
	private static int testmode = 0;
	FileOutputStream fosUplink = null;
	FileOutputStream fosDownlink = null;
	
	public UDPTest(Handler _mHandler, String serverIP, String measuretime,
			String interval, FileOutputStream fosDown, FileOutputStream fosUp, int mode) {

		measureIP = serverIP;
		measureTime = measuretime;
		measureInterval = interval;
		fosDownlink = fosDown;
		fosUplink = fosUp;
		testmode = mode;

		this.mHandler = _mHandler;

		(new myThread()).start();
	}
	
	public UDPTest(String serverIP, String measuretime, int mode) {

		measureIP = serverIP;
		measureTime = measuretime;
		measureInterval = Config.testInterval;
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
		try {
			int bufLen = 1 * 1024;// MTU
        	String sendStr = "";
			for (int j = 0; j < bufLen; j++)
				sendStr += '1';
            byte[] sendBuf;
            sendBuf = sendStr.getBytes();          
            
            int port = Config.udpUploadPort;           
	        DatagramSocket client = new DatagramSocket();
	        long start = System.currentTimeMillis();
	        while (true) {
	        	InetAddress addr = InetAddress.getByName(measureIP);//127.0.0.1
	            DatagramPacket sendPacket = new DatagramPacket(sendBuf,sendBuf.length, addr, port);
	            client.send(sendPacket);
	            long now = System.currentTimeMillis() - start;
	            if (now > Long.valueOf(measureTime)*60000) {
	            	System.out.println(now/1000);
					break;
				}
			}
	        client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void server2client() {
		try {
			DatagramSocket client = new DatagramSocket();
			long start = System.currentTimeMillis();
			String sendStr = "Hello! I'm Client";
			byte[] sendBuf;
			sendBuf = sendStr.getBytes();//115.28.12.102
			InetAddress addr = InetAddress.getByName(measureIP);// 127.0.0.1
			int port = Config.udpDownloadPort;
			DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, addr, port);
			client.send(sendPacket);

			while (true) {
				byte[] recvBuf = new byte[1024];
				DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
				client.receive(recvPacket);
				String recvStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
				System.out.println("Got:" + recvStr.length());
				long now = System.currentTimeMillis() - start;
	            if (now > Long.valueOf(measureTime)*60000) {
	            	System.out.println(now/1000);
					break;
				}
			}
			client.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
