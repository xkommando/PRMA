package com.caibowen.prma.base.collect;

import com.google.common.primitives.Ints;

import javax.annotation.Nullable;
import java.util.*;


/**
 * @author BowenCai
 * @since 12-10-2014.
 */
public class C {

    public static <K, V> HashMap<K, V>
    hashMap() {
        return new HashMap<K, V>(4);
    }

    public static <K, V> HashMap<K, V>
    hashMap(int expectedSize) {
        if (expectedSize < 3) {
            expectedSize++;
        }
        if (expectedSize < Ints.MAX_POWER_OF_TWO) {
            expectedSize += expectedSize / 3;
        }
        return new HashMap<K, V>(expectedSize);
    }

    public static <K, V> HashMap<K, V>
    hashMap(Map<? extends K, ? extends V> map) {
        return new HashMap<K, V>(map);
    }

    public static <K, V> HashMap<K, V>
    hashMap(Object[][] d2arr) {
        return new ImmutableArrayMap<K, V>(d2arr).toHashMap();
    }

    public static <K, V> LinkedHashMap<K, V> linkedHashMap() {
        return new LinkedHashMap<K, V>();
    }

    public static <K, V> LinkedHashMap<K, V> linkedHashMap(Map<? extends K, ? extends V> map) {
        return new LinkedHashMap<K, V>(map);
    }


    public static <K extends Comparable, V> TreeMap<K, V>
    treeMap() {
        return new TreeMap<K, V>();
    }

    public static <K, V> TreeMap<K, V>
    treeMap(Map<K, ? extends V> map) {
        if (map instanceof SortedMap)
            return new TreeMap<>((SortedMap)map);
        else
            return new TreeMap<K, V>(map);
    }

    public static <K, V> TreeMap<K, V>
    treeMap(SortedMap<K, ? extends V> map) {
        return new TreeMap<K, V>(map);
    }

    public static <C, K extends C, V> TreeMap<K, V>
    treeMap(@Nullable Comparator<C> comparator) {
        return new TreeMap<K, V>(comparator);
    }

    public static <C, K extends C, V> TreeMap<K, V>
    treeMap(Object[][] d2arr) {
        return new ImmutableArrayMap<K, V>(d2arr).toTreeMap();
    }

    public static <K extends Enum<K>, V> EnumMap<K, V>
    enumMap(Class<K> type) {
        return new EnumMap<K, V>(type);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V>
    enumMap(Map<K, ? extends V> map) {
        return new EnumMap<K, V>(map);
    }

    public static <K, V> IdentityHashMap<K, V> identityHashMap() {
        return new IdentityHashMap<K, V>();
    }


    public static <E> HashSet<E>
    hashSet() {
        return new HashSet<>(4);
    }

    public static <E> HashSet<E>
    hashSet(int cap) {
        if (cap < 3)
            cap++;
        if (cap < Ints.MAX_POWER_OF_TWO)
            cap += cap / 3;

        return new HashSet<E>(cap);
    }

    public static <E> ArrayList<E>
    arrayList() {
        return new ArrayList<>(8);
    }

}
