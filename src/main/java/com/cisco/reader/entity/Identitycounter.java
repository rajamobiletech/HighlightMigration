package com.cisco.reader.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "identitycounters")
public class Identitycounter {
	
	@Id
	String id;
	String model;
	String field;
	int count;
	
	public Identitycounter(String id, String model, String field, int count) {
		super();
		this.id = id;
		this.model = model;
		this.field = field;
		this.count = count;
	}
	public String getId() {
		return id;
	}
	public String getModel() {
		return model;
	}
	public String getField() {
		return field;
	}
	public int getCount() {
		return count;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public void setField(String field) {
		this.field = field;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "Identitycounters [id=" + id + ", model=" + model + ", field=" + field + ", count=" + count + "]";
	}
}
