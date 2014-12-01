//package com.caibowen.prma.monitor.eval;
//
//import com.caibowen.prma.api.model.EventVO;
//import com.caibowen.prma.core.filter.basic.StrFilter;
//
//import javax.inject.Inject;
//
///**
// * @author BowenCai
// * @since 5-11-2014.
// */
//public class MessageEval implements Evaluator {
//
//
//    @Inject
//    StrFilter filter;
//
//    @Override
//    public String eval(EventVO vo) {
//        return filter.accept(vo.message) == 1 ? ACCEPT : REJECT;
//    }
//
//    public StrFilter getFilter() {
//        return filter;
//    }
//
//    public void setFilter(StrFilter filter) {
//        this.filter = filter;
//    }
//}
