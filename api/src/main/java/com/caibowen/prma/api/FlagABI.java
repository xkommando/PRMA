package com.caibowen.prma.api;

import com.caibowen.prma.api.model.ExceptionVO;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author BowenCai
 * @since 14-11-2014.
 */
public final class FlagABI {
    private FlagABI(){}

    //    4294967296 >= exp
//    65536   >= markers
    public static final long getFlag(@Nullable Map prop,
                                     @Nullable Set<String> markers,
                                     @Nullable List<ExceptionVO> exceptions) {

        int sz1 = exceptions != null ? exceptions.size() : 0;

        short sz11 = markers != null ? (short)markers.size() : 0;
        short sz12 = prop != null ? (short)prop.size() : 0;

        int sz2 = add(sz11, sz12);

        return add(sz1, sz2);
    }


    public static int exceptionCount(long flag) {
        return part1(flag);
    }

    public static int markerCount(long flag) {
        return part1(part2(flag));
    }

    public static int propertyCount(long flag) {
        return part2(part2(flag));
    }

    public static boolean hasException(long flag) {
        return flag > 4294967296L;
    }

    public static boolean hasMarkers(long flag) {
        return flag > 65536L;
    }

    public static boolean hasProperty(long flag) {
        throw new UnsupportedOperationException();
    }




    private static final int add(short a, short b) {
        return ((int)a << 16) | ((int)b & 0xFFFF);
    }

    private static final long add(int a, int b) {
        return ((long)a << 32) | ((long)b & 0xFFFFFFFFL);
    }

    private static final int part1(long c) {
        return (int)(c >> 32);
    }

    private static final int part2(long c) {
        return (int)c;
    }

    private static final short part1(int c) {
        return (short)(c >> 16);
    }

    private static final short part2(int c) {
        return (short)c;
    }

}
