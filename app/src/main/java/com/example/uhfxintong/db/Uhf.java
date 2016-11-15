package com.example.uhfxintong.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Uhf implements Serializable{

	private String uhfId;
	private String uhfName;//设备名称
	private String time;
	private String operator;//操作者
	private String voltGrade;
	private String lineSpace;
	private String factory;
	private String defect = "";//缺陷
	private String notes = "";  //备注
	private String photos = "";  //图片路径，已，分隔
	public String getDefect() {
		return defect;
	}

	public void setDefect(String defect) {
		this.defect = defect;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Uhf() {

	}

	public Uhf(String uhfId, String name,String time,String factory,String volt,
			String line,String operator) {
		this.uhfId=uhfId;
		this.uhfName=name;
		this.time=time;
		this.factory=factory;
		this.voltGrade=volt;
		this.lineSpace=line;
		this.operator=operator;
	}
		
	public String getFactory() {
		return factory;
	}

	public void setFactory(String factory) {
		this.factory = factory;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Uhf(String name) {

	}

	public String getUhfId() {
		return uhfId;
	}

	public void setUhf(String uhfId) {
		this.uhfId = uhfId;
	}

	public String getUhfName() {
		return uhfName;
	}

	public void setUhfName(String uhfName) {
		this.uhfName = uhfName;
	}

	public void setUhfId(String uhfId) {
		this.uhfId = uhfId;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getVoltGrade() {
		return voltGrade;
	}

	public void setVoltGrade(String voltGrade) {
		this.voltGrade = voltGrade;
	}

	public String getLineSpace() {
		return lineSpace;
	}

	public void setLineSpace(String lineSpace) {
		this.lineSpace = lineSpace;
	}
	
	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public boolean equals(Object obj) {   
        if (obj instanceof Uhf) {   
        	Uhf u = (Uhf) obj;   
            return this.uhfId.equals(u.uhfId);
        }   
        return super.equals(obj);  
	}

	@Override
	public String toString() {
		return "Uhf [uhfId=" + uhfId + ", uhfName=" + uhfName + ", time="
				+ time + ", operator=" + operator + ", voltGrade=" + voltGrade
				+ ", lineSpace=" + lineSpace + ", factory=" + factory
				+ ", defect=" + defect + ", notes=" + notes + ", photos="
				+ photos + "]";
	}
	
	
}
