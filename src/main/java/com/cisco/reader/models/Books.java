package com.cisco.reader.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;



@Entity
@Table(name = "Books")
public class Books {
	
	private int id;
	private String path;
	private String title;
	private String createdAt;
	private String updatedAt;
	private String zipMD5Hash;
	private String key;
	private List<Pages> pages;
	
	
	@OneToMany
	@JoinColumn(name="BookId")
	public List<Pages> getPages() {
		return pages;
	}
	public void setPages(List<Pages> pages) {
		this.pages = pages;
	}
	
	
	@Id
	@Column(name = "id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	@Column(name = "path")
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
 
	@Column(name = "title")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
 
	@Column(name = "createdAt")
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	 
	@Column(name = "updatedAt")
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	 
	@Column(name = "zipMD5Hash")
	public String getZipMD5Hash() {
		return zipMD5Hash;
	}
	public void setZipMD5Hash(String zipMD5Hash) {
		this.zipMD5Hash = zipMD5Hash;
	}
	
	 
	@Column(name = "`key`")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
 
}
