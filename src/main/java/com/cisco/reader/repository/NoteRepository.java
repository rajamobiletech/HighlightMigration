package com.cisco.reader.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cisco.reader.entity.Note;

public interface NoteRepository extends CrudRepository<Note, String>{

	@Query("{'page' : ?0}, {'user' : ?1}")
	public Note searchByPageIdAndUserId(int notePage, int noteUser);
}
