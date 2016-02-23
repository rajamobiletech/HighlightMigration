package com.cisco.reader.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cisco.reader.entity.Book;

public interface BookRepository extends CrudRepository<Book, Long>
{
		@Query("{'path' : ?0}")
		public Iterable<Book> searchByPath(String booksPath);
		
		@Query("{'path' : ?0}")
		public Book findOneByPath(String booksPath);

}
