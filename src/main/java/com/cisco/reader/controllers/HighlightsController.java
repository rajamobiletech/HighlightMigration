package com.cisco.reader.controllers;

import java.util.List;

import org.hibernate.Session;
 
import com.cisco.reader.models.Highlights;

public class HighlightsController {
	Session _session =null;
	public HighlightsController(Session session) {
		_session = session;
	}
	public Highlights findHighlihtByQuery (String query){
		StringBuilder qry = new StringBuilder("FROM Highlights where " + query);
		List<Highlights> highligt = _session.createQuery(qry.toString()).list();
		if(highligt.size() >0 ){
				return (Highlights) highligt.get(0);
		} else {
			System.out.println("Highligt not found for " + query );
			return null;
		}
		 
	}
}
