package com.caibowen.prma.common;

/**
 * @author BowenCai
 * @since 22-10-2014.
 */
public class Hashing {


    public static long intArray2long(int...arr) {
        long ret = 0;
        for (int i : arr) {
            ret = hash_128_to_64(ret, twang_mix64((long)i));
        }
        return ret;
    }

    public static long hash128To64(long upper, long lower) {
        // Murmur-inspired hashing.
        long kMul = 0x9ddfea08eb382d69L;
        long a = (lower ^ upper) * kMul;
        a ^= (a >> 47);
        long b = (upper ^ a) * kMul;
        b ^= (b >> 47);
        b *= kMul;
        return b;
    }

    public static long twang_mix64(long key) {
        key = (~key) + (key << 21);  // key *= (1 << 21) - 1; key -= 1;
        key = key ^ (key >> 24);
        key = key + (key << 3) + (key << 8);  // key *= 1 + (1 << 3) + (1 << 8)
        key = key ^ (key >> 14);
        key = key + (key << 2) + (key << 4);  // key *= 1 + (1 << 2) + (1 << 4)
        key = key ^ (key >> 28);
        key = key + (key << 31);  // key *= 1 + (1 << 31)
        return key;
    }
}
