package com.cisco.reader.controllers;

import java.util.List;

import org.hibernate.Session;

import com.cisco.reader.models.Books;
import com.cisco.reader.models.Notes;

public class NotesController {
	
	Session _session =null;
	public NotesController(Session session) {
		_session = session;
	}
	
	public Notes findNotesByQuery (String query){
		StringBuilder qry = new StringBuilder("FROM Notes where " + query);
		List<Notes> notes = _session.createQuery(qry.toString()).list();
		if(notes.size() >0 ){
				return (Notes) notes.get(0);
		} else {
			System.out.println("Page note not found " + query );
			return null;
		}
		 
	}
}
