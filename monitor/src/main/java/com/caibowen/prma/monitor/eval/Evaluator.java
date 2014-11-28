package com.caibowen.prma.monitor.eval;

import com.caibowen.gplume.annotation.Const;
import com.caibowen.prma.api.model.EventVO;

import javax.annotation.Nonnull;

/**
 * @author BowenCai
 * @since 5-11-2014.
 */
public interface Evaluator {

    public static final String ACCEPT = Evaluator.class.getName() + ".ACCEPT";
    public static final String REJECT = Evaluator.class.getName() + ".REJECT";
    /**
     * TODO
     * 1. store cache
     * 2. java code block eval integrate
     * 3. monitor eval chain building
     * 4. notify stmp, http post
     * 5. gplume bean namespace support -> OK
     * 6. context build
     * 7. analyzing API
     *
     *
     * optional :
     * 1. bean assembler <required></required> -> pending
     * 2. bean proxy support -> OK
     * 3. socket logger
     * 4. distributed logging system demo
     */
    String eval(@Nonnull @Const EventVO vo);
}
