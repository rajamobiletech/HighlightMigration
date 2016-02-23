package com.cisco.reader.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cisco.reader.entity.User;

public interface UserRepository extends CrudRepository<User, Long>
{
		@Query("{'uid' : ?0}")
		public User findOneByUid(String usersUid);

}
