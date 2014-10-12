/*******************************************************************************
 * Copyright 2014 Bowen Cai
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.caibowen.prma.base.collect;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 *  a two dimension array based map, key and value is nullable.
 *
 *  Note that this map is a simple wrapper around the 2-column array,
 *  there is no guarantee that keys in this map is unique.
 *
 *  Given two equal keys, the reading method, i.e,  get() and contains()
 *  will check for the first key incurred in the array only.
 *  
 * @author bowen.cbw
 *
 */
public class ImmutableArrayMap<K, V> implements Map<K, V>, Cloneable, Serializable {
	
	private static final long serialVersionUID = -3828245015256271179L;

	private final Object[][] table;


	// views on keys and values. lazy init
	transient private Set<Entry<K, V>> entrySet;
	transient private Object[] keys;
	transient private Object[] vals;

    public ImmutableArrayMap(Object[][] values) {
        this(values, true);
    }

	public ImmutableArrayMap(Object[][] values, boolean doCheck) {

        if (doCheck) { // check if it is a n*2 array
            for (int i = 0; i != values.length; ++i) {
                Object[] pair = values[i];
                if (pair == null || pair.length != 2) {
                    throw new IllegalArgumentException(
                            pair == null ? "enpty entry at index [" + i + "]" :
                                    pair.length > 0 ?
                                            "multi-value for key[" + pair[0] + "] at index [" + i + "]"
                                            : "empty entry at index " + i);
                }
            }
        }
		table = values;
		// init when needed
		keys = null;
		vals = null;
		entrySet = null;
	}

	public ImmutableArrayMap(Map<K, V> jdkMap) {
        table = new Object[jdkMap.size()][2];
        int i = 0;
        for (Entry e : jdkMap.entrySet()) {
            table[i][0] = e.getKey();
            table[i][1] = e.getValue();
            i++;
        }
        // init when needed
        keys = null;
        vals = null;
        entrySet = null;
    }

	public ImmutableArrayMap(Object[] keys, Object[] vals) {
		if (keys.length != vals.length)
			throw new IllegalArgumentException("key array length does not match value array length");
		table = new Object[vals.length][2];
		for (int i = 0; i < keys.length; i++) {
			table[i][0] = keys[i];
			table[i][1] = vals[i];
		}
        this.keys = keys;
        this.vals = vals;
	}

	@Override
	public int size() {
		return table.length;
	}

	@Override
	public boolean isEmpty() {
		return table.length == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		if (key == null) {
			for (Object[] entry : table)
				if (entry[1] == null)
					return true;
		} else {
			for (Object[] entry : table)
				if (key.equals(entry[0]))
					return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null) {
			for (Object[] entry : table)
				if (entry[1] == null)
					return true;
		} else {
			for (Object[] entry : table)
				if (value.equals(entry[1]))
					return true;
		}
		return false;
	}

	@Override
	public V get(Object key) {
		if (key == null) {
			for (Object[] entry : table)
				if (entry[0] == null)
					return (V)entry[1];
		} else {
			for (Object[] entry : table)
				if (key.equals(entry[0]))
					return (V)entry[1];
		}
		return null;
	}

	@Override
	public V put(K key, V value) {
        throw new UnsupportedOperationException();
	}

	@Override
	public V remove(Object key) {
        throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
        throw new UnsupportedOperationException();
	}

	@Override
	public Set<K> keySet() {
		if (keys == null) {
			keys = new Object[table.length];
			for (int i = 0; i < table.length; i++) {
				keys[i] = table[i][0];
			}
		}
		return new ImmutableArraySet<K>(keys);
	}

	@Override
	public Collection<V> values() {
		if (vals == null) {
			vals = new Object[table.length];
			for (int i = 0; i < table.length; i++) {
				vals[i] = table[i][1];
			}
		}
		return new ImmutableArraySet<V>(vals);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {

		Set<Entry<K, V>> es = entrySet;
		return es != null ? es
				: (entrySet = new AbstractSet<Entry<K,V>>() {
			
					@Override
					public Iterator<Entry<K, V>> iterator() {
						return new Iterator<Entry<K,V>>() {
							Enumerator enumerator = new Enumerator();
							int index = 0;
							@Override
							public boolean hasNext() {
								return index < table.length;
							}

							@Override
							public Entry<K, V> next() {
								enumerator.index = index;
								index++;
								return enumerator;
							}

							@Override
							public void remove() {
						        throw new UnsupportedOperationException();
							}
						};
					}

					@Override
					public int size() {
						return table.length;
					}
				});
	}
	
	@Override
	public int hashCode() {
        int h = 0;
        for (Object[] entry : table) {
        	Object k = entry[0];
        	Object v = entry[1];
        	int eh = (k == null ? 0 : k.hashCode()) 
        			 ^ (v == null ? 0 : v.hashCode());
        	
			h += eh;
		}
        return h;
	}


	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof Map))
			return false;
		Map<K, V> m = (Map<K, V>) o;
		if (m.size() != size())
			return false;
        try {
        	for (Object[] entry : table) {
				if (entry[1] == null) {
					if (!(m.containsKey(entry[0]) && m.get(entry[0]) == null))
						return false;
				} else {
					if (!entry[1].equals(m.get(entry[0])))
						return false;
				}
			}
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }
        return true;
	}
	

	private class Enumerator implements Entry<K, V> {
		int index;
		@Override
		public K getKey() {
			return (K)table[index][0];
		}

		@Override
		public V getValue() {
			return (V)table[index][1];
		}

		@Override
		public V setValue(V value) {
	        throw new UnsupportedOperationException();
		}
	}
	
	public Object clone() {
		try {
			ImmutableArrayMap<K, V> m = (ImmutableArrayMap<K, V>) super.clone();
			return m;
		} catch (Exception e) {
			throw new RuntimeException("this exception will never throw");
		}
	}


    public void appendJson(Appendable b) {
        try {
            b.append("{\r\n");
            final int _len = table.length - 1;// -1 0
            int _i = 0;
            for (; _i < _len; ++_i) {
                final Object k = table[_i][0];
                final Object v = table[_i][1];
                b.append("\t\"");
                if (k == null) b.append("null");
                else b.append(k.toString());
                b.append("\" : \"");

                if (v == null) b.append("null");
                else b.append(v.toString());

                b.append("\", \r\n");
            }
            if (_len >= 0) {
                final Object k = table[_len][0];
                final Object v = table[_len][1];
                b.append("\t\"");
                if (k == null) b.append("null");
                else b.append(k.toString());
                b.append("\" : \"");

                if (v == null) b.append("null");
                else b.append(v.toString());

                b.append("\"\r\n");
            }
            b.append('}');
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }

    public String toJson() {
        StringBuilder b = new StringBuilder(512);
        appendJson(b);
        return b.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }

    public void addTo(Map<K, V> h) {
        for (Object[] _p : table) {
            h.put((K)_p[0], (V)_p[1]);
        }
    }

    public HashMap<K, V> toHashMap() {
        final int _z = table.length * 4 / 3;
        HashMap h = new HashMap(_z);
        addTo(h);
        return h;
    }

    public TreeMap<K, V> toTreeMap() {
        final int _z = table.length * 4 / 3;
        TreeMap h = new TreeMap();
        addTo(h);
        return h;
    }

    public TreeMap<K, V> toTreeMap(Comparator<? super K> comp) {
        TreeMap h = new TreeMap(comp);
        addTo(h);
        return h;
    }


}


