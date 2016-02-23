package com.cisco.reader.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="Books")
public class Book {
	
	@Id
	private int id;
	@Field("path")
	private String path;
	@Field("title")
	private String title;
	@Field("createdAt")
	private String createdAt;
	@Field("updatedAt")
	private String updatedAt;
	@Field("zipMD5Hash")
	private String zipMD5Hash;
	@Field("key")
	private String key;
	
	@DBRef(db="Pages")
	List<Page> pages = new ArrayList<Page>();
	
	
	@PersistenceConstructor
	public Book(int id, String path, String title, String createdAt, String updatedAt, String zipMD5Hash, String key) {
		super();
		this.id = id;
		this.path = path;
		this.title = title;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.zipMD5Hash = zipMD5Hash;
		this.key = key;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	 
	public String getZipMD5Hash() {
		return zipMD5Hash;
	}
	public void setZipMD5Hash(String zipMD5Hash) {
		this.zipMD5Hash = zipMD5Hash;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	@Override
	public String toString() {
		return "Books [id=" + id + ", path=" + path + ", title=" + title + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + ", zipMD5Hash=" + zipMD5Hash + ", key=" + key + ", pages=" + pages + "]";
	}
	
 
}
