package com.cisco.reader.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cisco.reader.entity.Book;
import com.cisco.reader.entity.Page;


public interface PageRepository extends CrudRepository<Page, String> {

	@Query("{'book' : ?0}")
	public Iterable<Page> searchByBookId(int bookId);
	
}
