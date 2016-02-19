package com.cisco.reader.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.Session;

@Entity
@Table(name = "Pages")
public class Pages {

	
	@Id
	@Column(name = "id")
	private int id;
	
	@Column(name = "pageSrc")
	private String pageSrc;
	
	@Column(name = "pageTitle")
	private String pageTitle;
	
	@Column(name = "BookId")
	private int BookId; 
	
	@Column(name = "pageNumber")
	private int pageNumber;
	
	@Column(name = "createdAt")
	private String createdAt;
	
	public Bookmarks getBookMarks(int userId , Session session) {
		StringBuilder qry = new StringBuilder("FROM Bookmarks where PageId=" +this.getId() + " and UserId=" + userId);
		List<Bookmarks> bookMarks = session.createQuery(qry.toString()).list();
		if(bookMarks.size()>0){
			return bookMarks.get(0);
		}
		return null;
	}

	public List<Highlights> getHighlights(int userId , Session session) {
		StringBuilder qry = new StringBuilder("FROM Highlights where PageId=" +this.getId() + " and UserId=" + userId);
		List<Highlights> highlights = session.createQuery(qry.toString()).list();
		return highlights;
	}

	public Notes getNotes(int userId , Session session) {
		StringBuilder qry = new StringBuilder("FROM Notes where PageId=" +this.getId() + " and UserId=" + userId);
		List<Notes> notes = session.createQuery(qry.toString()).list();
		if(notes.size()>0){
			return notes.get(0);
		}
		return null;
	}

 

	@Column(name = "updatedAt")
	private String updatedAt;
	
	
	  

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPageSrc() {
		return pageSrc;
	}

	public void setPageSrc(String pageSrc) {
		this.pageSrc = pageSrc;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public int getBookId() {
		return BookId;
	}

	public void setBookId(int bookId) {
		BookId = bookId;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
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
	
	
	
}
