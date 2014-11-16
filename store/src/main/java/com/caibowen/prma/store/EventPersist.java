package com.caibowen.prma.store;

import com.caibowen.prma.api.model.EventVO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author BowenCai
 * @since 27-10-2014.
 */
public interface EventPersist {

    long  persist(@Nonnull EventVO event);

    void batchPersist(@Nonnull List<EventVO> ls);

    @Nullable
    EventVO get(long id);

//    List<EventVO> searchByTime();
}
