package com.caibowen.prma.jdbc.mapper;


import com.caibowen.prma.jdbc.stmt.UpdateStatementCreator;

/**
 *
 * @author BowenCai
 *
 * @since 2013-5-7
 */
public interface ObjectMapping<T> {

	
	UpdateStatementCreator insert(T obj);

	UpdateStatementCreator update(T obj);
	
}
