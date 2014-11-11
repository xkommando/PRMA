package com.caibowen.prma.core.filter.basic;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.regex.Pattern;

/**
 * filter based on prefix, suffix or an optional pattern
 *
 *
 * @author BowenCai
 * @since 11-11-2014.
 */
public class MessageFilter extends PartialStrFilter {

    @Inject
    @Nullable
    Pattern pattern;
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }


    @Override
    protected void handle(String buf) {
        super.handle(buf);
    }

    @Override
    protected int doAccept(String e) {
        return super.doAccept(e);
    }


}
