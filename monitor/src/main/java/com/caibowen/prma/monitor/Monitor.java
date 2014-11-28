package com.caibowen.prma.monitor;

import com.caibowen.gplume.annotation.Const;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.monitor.eval.Evaluator;
import com.caibowen.prma.monitor.notify.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * @author BowenCai
 * @since 28/11/2014.
 */
public class Monitor {

    private static final Logger LOG = LoggerFactory.getLogger(Monitor.class);

    private Executor executor;

    protected Evaluator evaluator;
    protected CopyOnWriteArrayList<Notifier> notifiers;
    protected HashMap<String, Notifier> maps;

    public Monitor(){}

    public Monitor(Evaluator evaluator, List<Notifier> notifiers) {
        this.evaluator = evaluator;
        this.notifiers = new CopyOnWriteArrayList<>(notifiers);
        for (Notifier n : notifiers)
            maps.put(n.getName(), n);
    }

    public void recieve(@Nonnull @Const final EventVO vo) {
        String result = null;
        try {
            result = evaluator.eval(vo);
        } catch (Throwable e) {
            LOG.error("Could not evaluate [" + vo + "] with evaluator [" + evaluator + "]", e);
        }

        if (result == Evaluator.ACCEPT) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    for (Notifier not : notifiers) {
                        try {
                            not.notify(vo);
                        } catch (Throwable e) {
                            LOG.error("Could not notify ["
                                    + vo + "] with notifier ["
                                    + not.getName() + "]", e);
                        }
                    }
                }
            });

        } else if (result != Evaluator.REJECT) {
            final Notifier not = maps.get(result);
            if (not != null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            not.notify(vo);
                        } catch (Throwable e) {
                            LOG.error("Could not notify ["
                                    + vo + "] with notifier ["
                                    + not.getName() + "]", e);
                        }
                    }
                });

            } else LOG.warn("Could not find notifier named [" + result + "] for event[" + vo + "]");
        }
    }


    public Evaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public List<Notifier> getNotifiers() {
        return notifiers;
    }

    public void setNotifiers(List<Notifier> notifiers) {
        this.notifiers = new CopyOnWriteArrayList<>(notifiers);
        for (Notifier n : notifiers)
            maps.put(n.getName(), n);
    }

    public void addNotifiers(Notifier notifier) {
        this.notifiers.add(notifier);
    }
}
