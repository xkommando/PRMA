package com.caibowen.prma.store.rdb.dao.impl.cao;

import com.caibowen.prma.core.StrLoader;
import com.caibowen.prma.store.rdb.dao.ExceptionDAO;
import com.caibowen.prma.store.rdb.dao.impl.ExceptionDAOImpl;

import javax.inject.Inject;

/**
 * @author BowenCai
 * @since 10-11-2014.
 */
public class ExceptionCAO extends ExceptionDAOImpl implements ExceptionDAO {

    @Inject ExceptionDAO db;

    public ExceptionCAO(StrLoader loader) {
        super(loader);
    }
}
