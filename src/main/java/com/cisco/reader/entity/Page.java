package com.cisco.reader.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="Pages")
public class Page {
	
	@Id
	private int id;
	@Field("pageSrc")
	private String pageSrc;
	@Field("pageTitle")
	private String pageTitle;
	@Field("book")
	private int book; 
	@Field("pageNumber")
	private int pageNumber;
	@Field("createdAt")
	private String createdAt;
	@Field("updatedAt")
	private String updatedAt;

	@DBRef(db="Bookmarks")
	List<Bookmark> bookmarks = new ArrayList<Bookmark>();
	
	@DBRef(db="Highlights")
	List<Highlight> highlights = new ArrayList<Highlight>();
	
	@DBRef(db="Notes")
	List<Note> notes = new ArrayList<Note>();

	@PersistenceConstructor
	public Page(int id, String pageSrc, String pageTitle, int book, int pageNumber, String createdAt,
			String updatedAt) {
		super();
		this.id = id;
		this.pageSrc = pageSrc;
		this.pageTitle = pageTitle;
		this.book = book;
		this.pageNumber = pageNumber;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public int getId() {
		return id;
	}

	public String getPageSrc() {
		return pageSrc;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public int getBook() {
		return book;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}

	public List<Highlight> getHighlights() {
		return highlights;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPageSrc(String pageSrc) {
		this.pageSrc = pageSrc;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public void setBook(int book) {
		this.book = book;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setBookmarks(List<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}

	public void setHighlights(List<Highlight> highlights) {
		this.highlights = highlights;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}
	
}
