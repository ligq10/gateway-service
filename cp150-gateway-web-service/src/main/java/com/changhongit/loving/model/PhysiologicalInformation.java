package com.changhongit.loving.model;

import java.io.Serializable;

public class PhysiologicalInformation implements Serializable {

	private int gender;
	private int age;
	private int height;
	private int weight;
	private int mode;
	private int stepDistance;

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getStepDistance() {
		return stepDistance;
	}

	public void setStepDistance(int stepDistance) {
		this.stepDistance = stepDistance;
	}

}
