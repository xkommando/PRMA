package com.caibowen.prma.store.dao.impl.cao;

import com.caibowen.prma.store.dao.ExceptionDAO;
import com.caibowen.prma.store.dao.impl.ExceptionDAOImpl;

import javax.inject.Inject;

/**
 * @author BowenCai
 * @since 10-11-2014.
 */
public class ExceptionCAO extends ExceptionDAOImpl implements ExceptionDAO {

    @Inject ExceptionDAO db;


}
