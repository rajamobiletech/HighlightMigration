package com.cisco.reader.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "Notes")
public class Notes {
	
	@Id
	@Column(name = "id")
	private int id;
	
	@Column(name = "UserId")
	private int UserId;

	@Column(name = "PageId")
	private int PageId;
	
	@Column(name = "comments")
	private String comments;
	
	@Column(name = "indexed")
	private byte indexed;
	
	@Column(name = "deleted")
	private byte deleted;
	
	@Column(name = "createdAt")
	private String createdAt;
	
	@Column(name = "updatedAt")
	private String updatedAt;
	
	@Column(name = "usn")
	private int usn;
	
	@Column(name = "guid")
	private String guid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return UserId;
	}

	public void setUserId(int userId) {
		UserId = userId;
	}

	public int getPageId() {
		return PageId;
	}

	public void setPageId(int pageId) {
		PageId = pageId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public byte getIndexed() {
		return indexed;
	}

	public void setIndexed(byte indexed) {
		this.indexed = indexed;
	}

	public byte getDeleted() {
		return deleted;
	}

	public void setDeleted(byte deleted) {
		this.deleted = deleted;
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

	public int getUsn() {
		return usn;
	}

	public void setUsn(int usn) {
		this.usn = usn;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}
}
