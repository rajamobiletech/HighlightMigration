package com.cisco.reader.repository;

public interface SequenceCounterDao {
	
	public int getNextSequence(String modelName);

}
