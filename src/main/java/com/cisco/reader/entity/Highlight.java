package com.cisco.reader.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="Highlights")
public class Highlight {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	@Field("startOffset")
	private int startOffset;
	@Field("endOffset")
	private int endOffset;
	@Field("selectedText")
	private String selectedText;
	@Field("highlightComment")
	private String highlightComment;
	@Field("colorOverride")
	private String colorOverride;
	@Field("user")
	private int user;
	@Field("page")
	private int page;
	@Field("indexed")
	private Boolean indexed;
	@Field("deleted")
	private Boolean deleted;
	@Field("createdAt")
	private String createdAt;
	@Field("updatedAt")
	private String updatedAt;
	@Field("usn")
	private int usn;
	@Field("guid")
	private String guid;
	
	
	@PersistenceConstructor
	public Highlight(int id, int startOffset, int endOffset, String selectedText, String highlightComment,
			String colorOverride, int user, int page, Boolean indexed, Boolean deleted, String createdAt,
			String updatedAt, int usn, String guid) {
		super();
		this.id = id;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.selectedText = selectedText;
		this.highlightComment = highlightComment;
		this.colorOverride = colorOverride;
		this.user = user;
		this.page = page;
		this.indexed = indexed;
		this.deleted = deleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.usn = usn;
		this.guid = guid;
	}
	
	public Highlight() {
		super();
	}

	public int getId() {
		return id;
	}
	public int getStartOffset() {
		return startOffset;
	}
	public int getEndOffset() {
		return endOffset;
	}
	public String getSelectedText() {
		return selectedText;
	}
	public String getHighlightComment() {
		return highlightComment;
	}
	public String getColorOverride() {
		return colorOverride;
	}
	public int getUserId() {
		return user;
	}
	public int getPageId() {
		return page;
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
	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}
	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}
	public void setSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}
	public void setHighlightComment(String highlightComment) {
		this.highlightComment = highlightComment;
	}
	public void setColorOverride(String colorOverride) {
		this.colorOverride = colorOverride;
	}
	public void setUserId(int user) {
		this.user = user;
	}
	public void setPageId(int page) {
		this.page = page;
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
		return "Highlights [id=" + id + ", startOffset=" + startOffset + ", endOffset=" + endOffset + ", selectedText="
				+ selectedText + ", highlightComment=" + highlightComment + ", colorOverride=" + colorOverride
				+ ", UserId=" + user + ", PageId=" + page + ", indexed=" + indexed + ", deleted=" + deleted
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", usn=" + usn + ", guid=" + guid + "]";
	}
	
}
