package com.cisco.reader.entity;

import javax.persistence.Id;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection="Notes")
public class Note {
	
	@Id
	private int id;
	private int user;
	private int page;
	private String comments;
	private Boolean indexed;
	private Boolean deleted;
	private String createdAt;
	private String updatedAt;
	private int usn;
	private String guid;
	
	public Note() {
		super();
	}
	
	@PersistenceConstructor
	public Note(int id, int user, int page, String comments, Boolean indexed, Boolean deleted, String createdAt,
			String updatedAt, int usn, String guid) {
		super();
		this.id = id;
		this.user = user;
		this.page = page;
		this.comments = comments;
		this.indexed = indexed;
		this.deleted = deleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.usn = usn;
		this.guid = guid;
	}
	public int getId() {
		return id;
	}
	public int getUserId() {
		return user;
	}
	public int getPageId() {
		return page;
	}
	public String getComments() {
		return comments;
	}
	public Boolean getIndexed() {
		return indexed;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public int getUsn() {
		return usn;
	}
	public String getGuid() {
		return guid;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setUserId(int user) {
		this.user = user;
	}
	public void setPageId(int page) {
		this.page = page;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public void setUsn(int usn) {
		this.usn = usn;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	@Override
	public String toString() {
		return "Notes [id=" + id + ", UserId=" + user + ", PageId=" + page + ", comments=" + comments + ", indexed="
				+ indexed + ", deleted=" + deleted + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", usn="
				+ usn + ", guid=" + guid + "]";
	}

	
}
