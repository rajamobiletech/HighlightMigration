package com.cisco.reader.controllers;

import java.util.List;

import org.hibernate.Session;

import com.cisco.reader.helpers.HibernateReader;
import com.cisco.reader.models.Books;
public class BooksController {
	
	Session _session =null;
	public BooksController(Session session) {
		_session = session;
	}
 
	public Books findBooksByQuery (String query){
		StringBuilder qry = new StringBuilder("FROM Books where " + query);
		List<Books> books = _session.createQuery(qry.toString()).list();
		if(books.size() >0 ){
				return (Books) books.get(0);
		} else {
			System.out.println("Book not found " + query );
			return null;
		}
		 
	}
}
