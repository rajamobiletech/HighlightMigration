package com.cisco.reader.entity;

import javax.persistence.Id;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="Users")
public class User {

	@Id
	private int id;
	private String uid;
	private String createdAt;
	private String updatedAt;
	private int last_synced_usn;

	@PersistenceConstructor
	public User(int id, String uid, String createdAt, String updatedAt, int last_synced_usn) {
		super();
		this.id = id;
		this.uid = uid;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.last_synced_usn = last_synced_usn;
	}

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
		return last_synced_usn;
	}

	public void setLastSyncedUSN(int last_synced_usn) {
		this.last_synced_usn = last_synced_usn;
	}

	@Override
	public String toString() {
		return "Users [id=" + id + ", uid=" + uid + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
				+ ", lastSyncedUSN=" + last_synced_usn + "]";
	}
	
}
