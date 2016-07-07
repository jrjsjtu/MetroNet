package thu.wireless.mobinet.hsrtest;

import android.telephony.TelephonyManager;

public class SignalUtil {
	public static void getCurrentNetworkType(int type) {
		switch (type) {
		case TelephonyManager.NETWORK_TYPE_GPRS:// 2.5G
			Config.networkTypeString = "GPRS";
			break;
		case TelephonyManager.NETWORK_TYPE_EDGE:// 2.5G
			Config.networkTypeString = "EDGE";
			break;
		case TelephonyManager.NETWORK_TYPE_UMTS:// 3G
			Config.networkTypeString = "UMTS";
			break;
		case TelephonyManager.NETWORK_TYPE_HSDPA:// 3.5G
			Config.networkTypeString = "HSDPA";
			break;
		case TelephonyManager.NETWORK_TYPE_HSUPA://3.5G
			Config.networkTypeString = "HSUPA";
			break;
		case TelephonyManager.NETWORK_TYPE_HSPA:
			Config.networkTypeString = "HSPA";
			break;
		case TelephonyManager.NETWORK_TYPE_HSPAP:// 3.75G
			Config.networkTypeString = "HSPA+";
			break;
		case TelephonyManager.NETWORK_TYPE_CDMA:// 2G
			Config.networkTypeString = "CDMA";
			break;
		case TelephonyManager.NETWORK_TYPE_1xRTT:// 2.5G
			Config.networkTypeString = "1xRTT";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			Config.networkTypeString = "EVDO0";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:// 3.5G
			Config.networkTypeString = "EVDOA";
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			Config.networkTypeString = "EVDOB";
			break;
		case TelephonyManager.NETWORK_TYPE_LTE:// 4G
			Config.networkTypeString = "LTE";
			break;
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			Config.networkTypeString = "eHRPD";
			break;
		case TelephonyManager.NETWORK_TYPE_IDEN:// 2G
			Config.networkTypeString = "iDen";
			break;
		default:
			Config.networkTypeString = "Other";
			break;
		}
	}

	public static int getCurrentLevel(boolean isGsm) {
		int level;
		if (isGsm) {
			if (Config.lteSignalStrength.equals("-1")
					&& Config.lteRsrp.equals("-1")
					&& Config.lteRsrq.equals("-1")
					&& Config.lteRssnr.equals("-1")) {
				level = getGsmLevel();
			} else {
				level = getLteLevel();
			}
		} else {
			int cdmaLevel = getCdmaLevel();
			int evdoLevel = getEvdoLevel();
			if (evdoLevel == 0) {
				/** We don't know evdo, use cdma */
				level = getCdmaLevel();
			} else if (cdmaLevel == 0) {
				/** We don't know cdma, use evdo */
				level = getEvdoLevel();
			} else {
				/** We know both, use the lowest level */
				level = cdmaLevel < evdoLevel ? cdmaLevel : evdoLevel;
			}
		}
		return level;
	}

	public static int getGsmLevel() {
		int level;
		// ASU ranges from 0 to 31 - TS 27.007 Sec 8.5
		// asu = 0 (-113dB or less) is very weak
		// signal, its better to show 0 bars to the user in such cases.
		// asu = 99 is a special case, where the signal strength is unknown.
		int asu = Integer.parseInt(Config.gsmSignalStrength);
		if (asu <= 2 || asu == 99)
			level = 0;
		else if (asu >= 12)
			level = 4;
		else if (asu >= 8)
			level = 3;
		else if (asu >= 5)
			level = 2;
		else
			level = 1;
		return level;
	}

	public static int getCdmaLevel() {
		final int cdmaDbm = Integer.parseInt(Config.cdmaDbm);
		final int cdmaEcio = Integer.parseInt(Config.cdmaEcio);
		int levelDbm;
		int levelEcio;
		if (cdmaDbm >= -75)
			levelDbm = 4;
		else if (cdmaDbm >= -85)
			levelDbm = 3;
		else if (cdmaDbm >= -95)
			levelDbm = 2;
		else if (cdmaDbm >= -100)
			levelDbm = 1;
		else
			levelDbm = 0;
		// Ec/Io are in dB*10
		if (cdmaEcio >= -90)
			levelEcio = 4;
		else if (cdmaEcio >= -110)
			levelEcio = 3;
		else if (cdmaEcio >= -130)
			levelEcio = 2;
		else if (cdmaEcio >= -150)
			levelEcio = 1;
		else
			levelEcio = 0;
		int level = (levelDbm < levelEcio) ? levelDbm : levelEcio;
		return level;
	}

	public static int getEvdoLevel() {
		int evdoDbm = Integer.parseInt(Config.evdoDbm);
		int evdoSnr = Integer.parseInt(Config.evdoSnr);
		int levelEvdoDbm;
		int levelEvdoSnr;

		if (evdoDbm >= -65)
			levelEvdoDbm = 4;
		else if (evdoDbm >= -75)
			levelEvdoDbm = 3;
		else if (evdoDbm >= -90)
			levelEvdoDbm = 2;
		else if (evdoDbm >= -105)
			levelEvdoDbm = 1;
		else
			levelEvdoDbm = 0;

		if (evdoSnr >= 7)
			levelEvdoSnr = 4;
		else if (evdoSnr >= 5)
			levelEvdoSnr = 3;
		else if (evdoSnr >= 3)
			levelEvdoSnr = 2;
		else if (evdoSnr >= 1)
			levelEvdoSnr = 1;
		else
			levelEvdoSnr = 0;
		int level = (levelEvdoDbm < levelEvdoSnr) ? levelEvdoDbm : levelEvdoSnr;
		return level;
	}

	public static int getLteLevel() {
		int levelLteRsrp = 0;
		int rsrp = Integer.parseInt(Config.lteRsrp);
		if (rsrp == -1)
			levelLteRsrp = 0;
		else if (rsrp >= -85) // Great
			levelLteRsrp = 4;
		else if (rsrp >= -95) // Good
			levelLteRsrp = 3;
		else if (rsrp >= -105) // MODERATE
			levelLteRsrp = 2;
		else if (rsrp >= -115) // Poor
			levelLteRsrp = 1;
		else
			levelLteRsrp = 0;
		return levelLteRsrp;
	}
}
