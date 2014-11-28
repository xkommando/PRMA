package com.caibowen.prma.core.filter.basic;

import java.util.regex.Pattern;

/**
 * filter based on prefix, suffix or an optional pattern
 *
 *
 * @author BowenCai
 * @since 11-11-2014.
 */
public class MessageFilter extends PartialStrFilter {

    Pattern pattern;

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public void addMatch(String buf) {
        super.addMatch(buf);
    }

    @Override
    protected int doAccept(String e) {
        if (!pattern.matcher(e).matches())
            return -1;
        return super.doAccept(e);
    }


}
