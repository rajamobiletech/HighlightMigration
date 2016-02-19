package com.cisco.reader.controllers;

import java.util.List;

import org.hibernate.Session;

import com.cisco.reader.models.Bookmarks;
import com.cisco.reader.models.Notes;

public class BookmarksController {
	Session _session =null;
	public BookmarksController(Session session) {
		_session = session;
	}
	
	public Bookmarks findBookmarksByQuery (String query){
		StringBuilder qry = new StringBuilder("FROM Bookmarks where " + query);
		List<Bookmarks> bookmark = _session.createQuery(qry.toString()).list();
		if(bookmark.size() >0 ){
				return (Bookmarks) bookmark.get(0);
		} else {
			System.out.println("Bookmark note not found for " + query );
			return null;
		}
		 
	}
}
