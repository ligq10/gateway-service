package com.changhongit.loving.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.msgpack.annotation.Message;

@Entity
@Table(name = "GROUP_TBL")
@Message
public class Group {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	private String name;
	
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "parent", fetch = FetchType.EAGER)
	private List<Group> childrens = new ArrayList<>();
	
	@ManyToOne()
	@JoinColumn(name = "parent")
	private Group parent;
	
	@OneToOne(cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, optional = true)
	@JoinColumn(name = "sosid")
	private SOSSetting sosSetting;
	
	public SOSSetting getSosSetting() {
		return sosSetting;
	}
	
	public void setSosSetting(SOSSetting sosSetting) {
		this.sosSetting = sosSetting;
	}
	
	public Group() {
	}
	
	public Group(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Group> getChildrens() {
		return childrens;
	}
	
	public void setChildrens(List<Group> childrens) {
		this.childrens = childrens;
	}
	
	public Group getParent() {
		return parent;
	}
	
	public void setParent(Group parent) {
		this.parent = parent;
	}
	
}
