package com.cisco.reader.controllers;

import java.util.List;

import org.hibernate.Session;

import com.cisco.reader.helpers.HibernateReader;
import com.cisco.reader.models.Books;
import com.cisco.reader.models.Users;

public class UsersController {

	Session _session =null;
	public UsersController(Session session) {
		_session = session;
	}
	public Users findUsersByQuery (String query){
		StringBuilder qry = new StringBuilder("FROM Users where " + query);
		List<Users> users = _session.createQuery(qry.toString()).list();
		if(users.size() >0 ){
				return (Users) users.get(0);
		} else {
			System.out.println("User not found " + query );
			return null;
		}
		 
	}
}
