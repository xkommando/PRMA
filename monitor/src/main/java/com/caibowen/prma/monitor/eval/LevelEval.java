//package com.caibowen.prma.monitor.eval;
//
//import com.caibowen.prma.api.LogLevel;
//import com.caibowen.prma.api.model.EventVO;
//
//import javax.inject.Inject;
//
///**
// * @author BowenCai
// * @since 5-11-2014.
// */
//public class LevelEval implements Evaluator {
//
//    @Inject
//    protected int lowerBound = Integer.MIN_VALUE;
//    @Inject
//    protected int upperBound = Integer.MAX_VALUE;
//
//    @Override
//    public String eval(EventVO vo) {
//        int level = vo.level.levelInt;
//        return lowerBound <= level && level<= upperBound ? ACCEPT : REJECT;
//    }
//
//    public LogLevel getLowerBound() {
//        return LogLevel.from(lowerBound);
//    }
//
//    public void setLowerBound(String name) {
//        this.lowerBound = LogLevel.from(name).levelInt;
//    }
//
//    public LogLevel getUpperBound() {
//        return LogLevel.from(upperBound);
//    }
//
//    public void setUpperBound(String name) {
//        this.upperBound = LogLevel.from(name).levelInt;
//    }
//}
