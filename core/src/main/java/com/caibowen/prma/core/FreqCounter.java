package com.caibowen.prma.core;

import com.caibowen.gplume.common.collection.primitive.Int4CircularList;

import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author BowenCai
 * @since 26/11/2014.
 */
public class FreqCounter {

    public static final long OFFSET = System.currentTimeMillis();
    double period = 1;

    Int4CircularList history = new Int4CircularList(256);

    public void count(int num) {
        int now = (int)(System.currentTimeMillis() - OFFSET);
        while (num-- > 0)
            history.add(now);
        cut(now);
    }
    public void count() {
        int now = (int)(System.currentTimeMillis() - OFFSET);
        history.add(now);
        cut(now);
    }

    public double freqToNow() {
        int now = (int)(System.currentTimeMillis() - OFFSET);
        cut(now);
        double count = history.size();
        count /= period;
        return count;
    }

    public double freq() {
        double count = history.size();
        count /= period;
        return count;
    }

    protected void cut(int now) {
        int limit = now - (int)(1000 * period);
        while (!history.isEmpty() && history.front() < limit)
            history.popFront();
    }

    public void setBufferSize(int size) {
        history.ensureCapacity(size);
    }
    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return "com.caibowen.prma.core.filter.freq.Counter{" +
                "period=" + period +
                ", history=" +  history +
                '}';
    }
}
