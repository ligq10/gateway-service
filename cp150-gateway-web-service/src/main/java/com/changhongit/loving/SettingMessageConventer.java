package com.changhongit.loving;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.changhongit.loving.message.AddVoiceReminder;
import com.changhongit.loving.message.AddWhiteList;
import com.changhongit.loving.message.DeleteVoiceReminder;
import com.changhongit.loving.message.DeleteWhiteList;
import com.changhongit.loving.message.DialingCall;
import com.changhongit.loving.message.RealTimeBroadcast;
import com.changhongit.loving.message.SendMultimediaMessage;
import com.changhongit.loving.message.SendShortMessage;
import com.changhongit.loving.message.SettingAnswer;
import com.changhongit.loving.message.SettingArea;
import com.changhongit.loving.message.SettingAutoAnswer;
import com.changhongit.loving.message.SettingContactList;
import com.changhongit.loving.message.SettingGpsPower;
import com.changhongit.loving.message.SettingHeartbeatInterval;
import com.changhongit.loving.message.SettingIdelWarning;
import com.changhongit.loving.message.SettingInitialization;
import com.changhongit.loving.message.SettingPassword;
import com.changhongit.loving.message.SettingPedometerInterval;
import com.changhongit.loving.message.SettingPhysiologicalInformation;
import com.changhongit.loving.message.SettingProtectedCircle;
import com.changhongit.loving.message.SettingServerConf;
import com.changhongit.loving.message.SettingSingleContact;
import com.changhongit.loving.message.SettingSos;
import com.changhongit.loving.message.SettingVoiceReminder;
import com.changhongit.loving.message.SettingWhiteList;
import com.changhongit.loving.message.UpdateVoiceReminder;
import com.changhongit.loving.model.Cp150CPcommand;
import com.changhongit.loving.model.PhysiologicalInformation;
import com.changhongit.loving.model.ProtectedCircle;
import com.changhongit.loving.model.ReminderMode;
import com.changhongit.loving.model.SOSConf;
import com.changhongit.loving.model.SettingContact;
import com.changhongit.loving.model.VoiceReminder;
import com.changhongit.loving.model.VoiceReminderV2;

@Component
public class SettingMessageConventer {
	
	public String getSettingMessage(SettingWhiteList msg) {
		String cmdArgus = "0,2,";
		for (SettingContact settingContact : msg.getMessage()) {
			cmdArgus = cmdArgus
					+ String.format("%s,%s,%s;", settingContact.getPosition(),
							settingContact.getTelNum(),
							settingContact.getName());
		}
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(DeleteWhiteList msg) {
		String cmdArgus = "0,2,";
		cmdArgus = cmdArgus
				+ String.format("%s,%s,%s;", msg.getMessage(), "", "");
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(AddWhiteList msg) {
		String cmdArgus = "0,2,";
		cmdArgus = cmdArgus
				+ String.format("%s,%s,%s;", msg.getMessage().getPosition(),
						msg.getMessage().getTelNum(), msg.getMessage()
								.getName());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(DialingCall msg) {
		String cmdArgus = String.format("%s,%s,%s", "1", msg.getMessage()
				.getTelNum(), msg.getMessage().getType());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0053", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(RealTimeBroadcast msg) {
		String cmdArgus = String.format("%s,%s", "0", msg.getMessage()
				.getParam());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0057", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SendMultimediaMessage msg) {
		String cmdArgus = String.format("%s,%s,%s", "1", msg.getMessage()
				.getTelNum(), msg.getMessage().getContent());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0059", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SendShortMessage msg) {
		String cmdArgus = String.format("%s,%s,%s", "1", msg.getMessage()
				.getTelNum(), msg.getMessage().getContent());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0052", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingAnswer msg) {
		String cmdArgus = String.format("%s,%s", "0,5", msg.getMessage()
				.getParam());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingArea msg) {
		String cmdArgus = String.format("%s,%s", "0,11", msg.getMessage()
				.getParam());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingAutoAnswer msg) {
		String cmdArgus = String.format("%s,%s", "0,7", msg.getMessage()
				.getParam());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingContactList msg) {
		String cmdArgus = "0,1,";
		for (SettingContact settingContact : msg.getMessage()) {
			cmdArgus = cmdArgus
					+ String.format("%s,%s,%s;", settingContact.getPosition(),
							settingContact.getTelNum(),
							settingContact.getName());
		}
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingSingleContact msg) {
		String cmdArgus = "0,1,";
		SettingContact settingContact = msg.getMessage();
		cmdArgus = cmdArgus
				+ String.format("%s,%s,%s;", settingContact.getPosition(),
						settingContact.getTelNum(), settingContact.getName());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingGpsPower msg) {
		String cmdArgus = String.format("%s,%s", "0,13", msg.getMessage()
				.getParam());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingHeartbeatInterval msg) {
		String cmdArgus = String.format("%s,%s", "0,9", msg.getMessage()
				.getParam());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingInitialization msg) {
		String cmdArgus = String.format("%s,%s", "0,3", msg.getMessage()
				.getParam());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingPassword msg) {
		String cmdArgus = String.format("%s,%s", "0,12", msg.getMessage()
				.getParam());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingPedometerInterval msg) {
		String cmdArgus = String.format("%s,%s", "0,8", msg.getMessage()
				.getParam());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingIdelWarning msg) {
		String cmdArgus = String.format("%s,%s", "0,14", msg.getMessage()
				.getParam());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingPhysiologicalInformation msg) {
		PhysiologicalInformation message = msg.getMessage();
		String cmdArgus = String.format("%s,%s,%s,%s,%s,%s,%s", "0",
				message.getGender(), message.getAge(), message.getHeight(),
				message.getWeight(), message.getMode(),
				message.getStepDistance());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0060", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingProtectedCircle msg) {
		ProtectedCircle message = msg.getMessage();
		String cmdArgus = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", "0,4",
				message.getType(), message.getCentreLongt(),
				message.getCentreLat(), message.getRadius(),
				message.getEastLongt(), message.getWestLongt(),
				message.getSouthLat(), message.getWestLongt(),
				message.getContactFlag());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingServerConf msg) {
		String cmdArgus = String.format("%s,%s", "0,6", msg.getMessage()
				.getParam());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingSos msg) {
		SOSConf message = msg.getMessage();
		String cmdArgus = String.format(
				"%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", "0,10",
				message.getSendShortMessage(), message.getSentToC1(),
				message.getSentToC2(), message.getSentToC3(),
				message.getSentToC4(), message.getCallC5Tell(),
				message.getCall(), message.getCallC1(), message.getCallC2(),
				message.getCallC3(), message.getCallC4(),
				message.getCallC5Tell(), message.getSmPrefix());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0051", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(SettingVoiceReminder msg) {
		VoiceReminder message = msg.getMessage();
		String cmdArgus = String.format("%s,%s,%s,%s,%s,%s", "0",
				formatDateString(message.getReminderTime()), message.getMode(),
				message.getAction(), message.getIndex(), message.getContent());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				msg.getImei(), "0x0054", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	private String formatDateString(String date) {
		if (date.matches("(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]")) {
			date = date.substring(2);
			date = date.replace("-", "");
			date = date.replace(":", "");
			date = date.replace(" ", "");
		} else {
			DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
			date = dateFormat.format(new Date()) + date + "00";
			date = date.replace(":", "");
		}
		return date;
	}
	
	public String getSettingMessage(DeleteVoiceReminder deleteVoiceReminder) {
		String cmdArgus = String.format("%s,%s,%s,%s,%s,%s", "0", "", "", 2,
				deleteVoiceReminder.getMessage(), "");
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				deleteVoiceReminder.getImei(), "0x0054", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(AddVoiceReminder voiceReminder) {
		VoiceReminderV2 message = voiceReminder.getMessage();
		String cmdArgus = String.format("%s,%s,%s,%s,%s,%s", "0",
				formatDateString(message.getReminderTime()),
				tansformMode(message.getMode(), message.getRepeatMode()), "0",
				message.getIndex(), message.getContent());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				voiceReminder.getImei(), "0x0054", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	public String getSettingMessage(UpdateVoiceReminder voiceReminder) {
		VoiceReminderV2 message = voiceReminder.getMessage();
		String cmdArgus = String.format("%s,%s,%s,%s,%s,%s", "0",
				formatDateString(message.getReminderTime()),
				tansformMode(message.getMode(), message.getRepeatMode()), "1",
				message.getIndex(), message.getContent());
		Cp150CPcommand cp150CPcommand = new Cp150CPcommand(new Date(),
				voiceReminder.getImei(), "0x0054", cmdArgus);
		return cp150CPcommand.formatToString();
	}
	
	private String tansformMode(ReminderMode mode, List<Boolean> repeatMode) {
		if (mode.equals(ReminderMode.OneTime)) {
			return "0000";
		} else if (mode.equals(ReminderMode.EveryDay)) {
			return "1000";
		} else if (mode.equals(ReminderMode.Repeat)) {
			int modeValue = 0;
			for (int i = 0; i < repeatMode.size(); i++) {
				if (repeatMode.get(i)) {
					modeValue += Math.pow(2, i);
				}
			}
			if (modeValue < 100) {
				return ("20" + modeValue);
			} else {
				return ("2" + modeValue);
			}
		}
		return "0000";
	}
}
