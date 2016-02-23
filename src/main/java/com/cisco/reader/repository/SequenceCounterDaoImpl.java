package com.cisco.reader.repository;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.cisco.reader.entity.Identitycounter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.*;

@Repository
public class SequenceCounterDaoImpl implements SequenceCounterDao {
	
	@Autowired
	private MongoOperations mongoOperation;

	public int getNextSequence(String modelName) {
		//get sequence id
		  Query query = new Query(Criteria.where("model").is(modelName));
		  
		  Update update = new Update();
		  update.inc("count", 1);
		  
		  FindAndModifyOptions options = new FindAndModifyOptions();
		  options.returnNew(true);
		  
		  
		  Identitycounter identityCount = mongoOperation.findAndModify(query, update, options, Identitycounter.class);
		  
		  if(identityCount == null) {
			  return 0;
		  }
		  
		return identityCount.getCount();
	}

}
