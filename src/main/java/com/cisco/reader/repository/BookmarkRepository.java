package com.cisco.reader.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cisco.reader.entity.Bookmark;
import com.cisco.reader.entity.Note;

public interface BookmarkRepository extends CrudRepository<Bookmark, String>{
	
	@Query("{'page' : ?0}, {'user' : ?1}")
	public Bookmark searchByPageIdAndUserId(int pageId, int userId);
}
