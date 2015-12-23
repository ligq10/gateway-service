package com.changhongit.loving.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.changhongit.loving.utils.MessageUtils;

public class Cp150CPcommand {
	
	private static String HEADER = "C P";
	
	private String seq;
	
	private String commanddr;
	
	private String command;
	
	private String cmdargus;
	
	private static String END = "\r\n";
	
	public Cp150CPcommand(Date date, String commanddr, String command,
			String cmdargus) {
		super();
		this.seq = getSeq(date);
		this.commanddr = commanddr;
		this.command = command;
		this.cmdargus = cmdargus;
	}
	
	private String getLength() {
		return MessageUtils.getLengthAscii(String.format("%s %s %s |%s|%s|%s ",
				HEADER, seq, commanddr, "0x0000", command, cmdargus));
	}
	
	private String getVerifycode() {
		return MessageUtils.getVerifycodeAscii(String.format(
				"%s %s %s |%s|%s|%s ", HEADER, seq, commanddr, getLength(),
				command, cmdargus));
	}
	
	public String formatToString() {
		return String.format("%s %s %s |%s|%s|%s %s%s", HEADER, seq, commanddr,
				getLength(), command, cmdargus, getVerifycode(), END);
	}
	
	private String getSeq(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSS");
		return dateFormat.format(date);
	}
	
}
