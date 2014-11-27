package com.caibowen.prma.monitor.notify;

import com.caibowen.prma.api.model.EventVO;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public interface Notifier {

    void setName(String name);
    String getName();
    boolean notify(EventVO vo);
}
