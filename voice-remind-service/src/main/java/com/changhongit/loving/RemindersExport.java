package com.changhongit.loving;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.changhongit.loving.entity.Reminder;
import com.changhongit.loving.entity.ReminderExportFile;
import com.changhongit.loving.entity.ReminderTerminal;
import com.changhongit.loving.entity.Terminal;
import com.changhongit.loving.repository.ReminderExportFileRepository;
import com.changhongit.loving.repository.ReminderRepository;
import com.changhongit.loving.repository.ReminderTerminalRepository;
import com.changhongit.loving.repository.TerminalRepository;

@Component
public class RemindersExport implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private Environment env;
	
	@Autowired
	private TerminalRepository terminalRepository;
	
	@Autowired
	private ReminderExportFileRepository reminderExportFileRepository;
	
	@Autowired
	private ReminderRepository reminderRepository;
	
	@Autowired
	private ReminderTerminalRepository reminderTerminalRepository;
	
	@Override
	public void run() {
		List<Reminder> reminders = reminderRepository.findByNeedExport(true);
		for (Reminder reminder : reminders) {
			reminder.setNeedExport(false);
			reminderRepository.save(reminder);
			
			List<ReminderTerminal> reminderTerminals = reminderTerminalRepository
					.findByReminderId(reminder.getId());
			List<String> terminalIds = new ArrayList<>();
			for (ReminderTerminal reminderTerminal : reminderTerminals) {
				terminalIds.add(reminderTerminal.getTerminalId());
			}
			
			List<Terminal> terminals = (List<Terminal>) terminalRepository
					.findAll(terminalIds);
			int baseNumber = 5000;
			int size = terminals.size();
			int fileNumber = (int) Math.ceil((double) size / baseNumber);
			for (int i = 0; i < fileNumber; i++) {
				int toIndex = ((i + 1) * baseNumber) > size ? size
						: ((i + 1) * baseNumber);
				Iterator<Terminal> oneFile = terminals.subList(i * baseNumber,
						toIndex).iterator();
				HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
				HSSFSheet sheet = hssfWorkbook.createSheet();
				HSSFRow title = sheet.createRow(sheet.getLastRowNum());
				HSSFCell imei = title.createCell(0);
				imei.setCellValue("imei");
				while (oneFile.hasNext()) {
					HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
					HSSFCell cell0 = row.createCell(0);
					cell0.setCellValue(oneFile.next().getImei());
				}
				try {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					hssfWorkbook.write(outputStream);
					ReminderExportFile reminderExportFile = new ReminderExportFile();
					reminderExportFile.setReminderId(reminder.getId());
					reminderExportFile
							.setExportFile(outputStream.toByteArray());
					reminderExportFileRepository.save(reminderExportFile);
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}
}
