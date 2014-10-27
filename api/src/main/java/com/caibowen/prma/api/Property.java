package com.caibowen.prma.api;

import java.io.Serializable;

/**
 * @author BowenCai
 * @since 26-10-2014.
 */
public class Property<K, V> implements Serializable {

    private static final long serialVersionUID = 8314098692180596054L;


    public K key;
    public V value;

    public Property() {}

    public Property(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;

        Property property = (Property) o;

        if (!key.equals(property.key)) return false;
        if (!value.equals(property.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Property{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
