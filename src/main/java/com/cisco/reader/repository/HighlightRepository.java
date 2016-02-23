package com.cisco.reader.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cisco.reader.entity.Highlight;

public interface HighlightRepository extends CrudRepository<Highlight, Long>{

	@Query("{ page : ?0, user : ?1 }")
	public Iterable<Highlight> searchByPageAndUser(int highlightPage, int highlightUser);
	
	@Query("{ startOffset: ?0, endOffset: ?1, page : ?2, user : ?3 }")
	public Iterable<Highlight> searchByStartOffsetAndEndOffsetAndPageAndUser(int highlightStartOffset, int highlightEndOffset, int highlightPage, int highlightUser);
	
}
