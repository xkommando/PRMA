package com.caibowen.prma.store;

import com.caibowen.gplume.annotation.Const;
import com.caibowen.prma.api.model.EventVO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author BowenCai
 * @since 27-10-2014.
 */
public interface EventPersist {

    long  persist(@Nonnull @Const EventVO event);

    void batchPersist(@Nonnull @Const List<EventVO> ls);

//    /**
//     *
//     * just for test
//     */
//    @Nullable
//    EventVO get(long id);
//
//    @Nonnull
//    List<EventVO> getWithException(long minTime, int limit);
}
