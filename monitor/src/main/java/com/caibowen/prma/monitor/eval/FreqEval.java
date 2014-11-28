package com.caibowen.prma.monitor.eval;

import com.caibowen.gplume.annotation.Const;
import com.caibowen.prma.api.model.EventVO;
import com.caibowen.prma.core.FreqCounter;

import javax.annotation.Nonnull;

/**
 *  wrapper
 *
 * @author BowenCai
 * @since 28/11/2014.
 */
public class FreqEval implements Evaluator {

    private Evaluator evaluator;
    private FreqCounter counter;
    private double limit;

    @Override
    public String eval(@Nonnull @Const EventVO vo) {
        String ret = evaluator.eval(vo);
        if (ret != REJECT) {
            counter.count();
            if (counter.freq() > limit)
                return ret;
        }
        return REJECT;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public FreqCounter getCounter() {
        return counter;
    }

    public void setCounter(FreqCounter counter) {
        this.counter = counter;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }
}
