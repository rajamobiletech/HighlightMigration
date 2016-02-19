package com.cisco.reader.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Users")
public class Users {

	@Id
	@Column(name = "id")
	private int id;
	
	@Column(name = "uid")
	private String uid;
	
	@Column(name = "createdAt")
	private String createdAt;
	
	@Column(name = "updatedAt")
	private String updatedAt;
	
	@Column(name = "last_synced_usn")
	private int lastSyncedUSN;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public int getLastSyncedUSN() {
		return lastSyncedUSN;
	}

	public void setLastSyncedUSN(int lastSyncedUSN) {
		this.lastSyncedUSN = lastSyncedUSN;
	}
	
}
