package com.changhongit.loving.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class TerminalUser {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	@Column(unique = true)
	private String terminalImei;
	
	private String terminalCheckCode;
	
	@Column(columnDefinition = "LONGTEXT")
	private String avatar;
	
	private String gender;
	
	private String address;
	
	private String remarks;
	
	private String telNum;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	
	private String cityCode;
	
	private String realName;
	
	private String nickName;
	
	private String height;
	
	private String weight;
	
	private String bloodType;
	
	private String sbp;
	
	private String dbp;
	
	private String healthStatus;
	
	private String drugAllergy;
	
	@ManyToMany
	private List<MedicalHistory> medicalHistories;
	
	@OneToOne
	private Terminal terminal;
	
	public TerminalUser() {
	}
	
	public TerminalUser(String id, String telNum, String realName,
			Terminal terminal) {
		this.id = id;
		this.telNum = telNum;
		this.realName = realName;
		this.terminal = terminal;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTerminalImei() {
		return terminalImei;
	}
	
	public void setTerminalImei(String terminalImei) {
		this.terminalImei = terminalImei;
	}
	
	public String getTerminalCheckCode() {
		return terminalCheckCode;
	}
	
	public void setTerminalCheckCode(String terminalCheckCode) {
		this.terminalCheckCode = terminalCheckCode;
	}
	
	public String getAvatar() {
		return avatar;
	}
	
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getTelNum() {
		return telNum;
	}
	
	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}
	
	public String getRealName() {
		return realName;
	}
	
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getHeight() {
		return height;
	}
	
	public void setHeight(String height) {
		this.height = height;
	}
	
	public String getWeight() {
		return weight;
	}
	
	public void setWeight(String weight) {
		this.weight = weight;
	}
	
	public String getBloodType() {
		return bloodType;
	}
	
	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}
	
	public String getSbp() {
		return sbp;
	}
	
	public void setSbp(String sbp) {
		this.sbp = sbp;
	}
	
	public String getDbp() {
		return dbp;
	}
	
	public void setDbp(String dbp) {
		this.dbp = dbp;
	}
	
	public String getHealthStatus() {
		return healthStatus;
	}
	
	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}
	
	public String getDrugAllergy() {
		return drugAllergy;
	}
	
	public void setDrugAllergy(String drugAllergy) {
		this.drugAllergy = drugAllergy;
	}
	
	public List<MedicalHistory> getMedicalHistories() {
		return medicalHistories;
	}
	
	public void setMedicalHistories(List<MedicalHistory> medicalHistories) {
		this.medicalHistories = medicalHistories;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}
	
	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}
	
	public Date getBirthday() {
		return birthday;
	}
	
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public String getCityCode() {
		return cityCode;
	}
	
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	
	public void init() {
		this.avatar = "";
		this.gender = "";
		this.address = "";
		this.remarks = "";
		this.telNum = "";
		this.realName = "";
		this.nickName = "";
		this.height = "";
		this.weight = "";
		this.bloodType = "";
		this.sbp = "";
		this.dbp = "";
		this.healthStatus = "";
		this.drugAllergy = "";
		this.cityCode = "";
	}
	
}
