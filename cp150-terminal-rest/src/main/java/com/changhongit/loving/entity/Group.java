package com.changhongit.loving.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "GROUP_TBL")
public class Group {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	
	private String name;
	
	private int level;
	
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "parent")
	private List<Group> childrens = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name = "parent")
	private Group parent;
	
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
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
}
