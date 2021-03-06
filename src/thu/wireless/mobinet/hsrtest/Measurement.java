package thu.wireless.mobinet.hsrtest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class Measurement {
	public static void pingCmdTest(final String target, final int count) {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();

				String res[] = new String[30];
				String cmd = "ping " + target;

				try {
					String date = Config.contentDateFormat.format(new Date(
							System.currentTimeMillis()));
					Config.fosPing.write(date.getBytes());
					Config.fosPing.write(System.getProperty("line.separator")
							.getBytes());

					int i = 0;
					System.out.println("no time 1");
					Process process = Runtime.getRuntime().exec(cmd);
					System.out.println("no time 2");
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(process.getInputStream()));
					System.out.println("no time 3");
					while (i < count + 1
							&& (res[i] = bufferedReader.readLine()) != null) {
						Config.fosPing.write(res[i].getBytes());
						Config.fosPing.write(System.getProperty(
								"line.separator").getBytes());
						i++;
					}
					Config.fosPing.write(System.getProperty("line.separator")
							.getBytes());
					double avg = 0;
					for (i = 0; i < count; i++) {
						if (res[i + 1].contains("time=")) {

						} else {
							Config.pingFlag = 13;
							System.out.println("no time");
							break;
						}
						String[] tmp = res[i + 1].split("time=");
						String[] tmp2 = tmp[1].split(" ");
						avg += Double.parseDouble(tmp2[0]);
					}
					Config.pingInfo = String.valueOf(avg / count);
					Config.pingFlag = 11;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Config.pingFlag = 12;
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void pingCmdProfession(final String target) {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();

				String res;
				String cmd = "ping " + target;

				try {
					String date = Config.contentDateFormat.format(new Date(
							System.currentTimeMillis()));
					Config.fosPing.write(date.getBytes());
					Config.fosPing.write(System.getProperty("line.separator")
							.getBytes());

					Process process = Runtime.getRuntime().exec(cmd);
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(process.getInputStream()));
					while ((res = bufferedReader.readLine()) != null) {
						Config.fosPing.write(res.getBytes());
						Config.fosPing.write(System.getProperty(
								"line.separator").getBytes());
					}
					Config.pingFlag = 11;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Config.pingFlag = 12;
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void pingJavaTest(final String target, final int count) {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				long pingStartTime = 0;
				long pingEndTime = 0;
				ArrayList<Double> rrts = new ArrayList<Double>();

				try {
					int timeOut = 3000;
					long totalPingDelay = 0;
					String output = Config.contentDateFormat.format(new Date(
							System.currentTimeMillis()))
							+ " "
							+ target
							+ " "
							+ count + "\n";
					for (int i = 0; i < count; i++) {
						pingStartTime = System.currentTimeMillis();
						boolean status = InetAddress.getByName(target)
								.isReachable(timeOut);
						pingEndTime = System.currentTimeMillis();
						long rrtVal = pingEndTime - pingStartTime;
						if (status) {
							totalPingDelay += rrtVal;
							rrts.add((double) rrtVal);
							output += i + ": " + rrtVal + "\n";
						}
					}
					double packetLoss = 1 - ((double) rrts.size() / count);
					Config.pingInfo = String.valueOf(totalPingDelay / count);
					Config.fosPing.write(output.getBytes());
					Config.fosPing.write(Config.pingInfo.getBytes());
					Config.fosPing.write(System.getProperty("line.separator")
							.getBytes());
					Config.pingFlag = 11;
				} catch (Exception e) {
					Config.pingFlag = 12;
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void pingURLTest(final String target, final int count) {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				long pingStartTime = 0;
				long pingEndTime = 0;
				ArrayList<Double> rrts = new ArrayList<Double>();
				String output = Config.contentDateFormat.format(new Date(System
						.currentTimeMillis()))
						+ " "
						+ target
						+ " "
						+ count
						+ "\n";
				try {
					long totalPingDelay = 0;
					URL url = new URL("http://" + target);
					int timeOut = 3000;

					for (int i = 0; i < count; i++) {
						pingStartTime = System.currentTimeMillis();
						HttpURLConnection httpClient = (HttpURLConnection) url
								.openConnection();
						httpClient.setRequestProperty("Connection", "close");
						httpClient.setRequestMethod("HEAD");
						httpClient.setReadTimeout(timeOut);
						httpClient.setConnectTimeout(timeOut);
						httpClient.connect();
						pingEndTime = System.currentTimeMillis();
						httpClient.disconnect();
						totalPingDelay += pingEndTime - pingStartTime;
						output += i + ": " + totalPingDelay + "\n";
						rrts.add((double) pingEndTime - pingStartTime);
					}
					double packetLoss = 1 - ((double) rrts.size() / count);
					Config.pingInfo = String.valueOf(totalPingDelay / count);
					Config.fosPing.write(Config.pingInfo.getBytes());
					Config.fosPing.write(System.getProperty("line.separator")
							.getBytes());
					Config.pingFlag = 11;
				} catch (Exception e) {
					Config.pingFlag = 12;
					e.printStackTrace();
				}
			}
		}.start();
	}
}
