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
 *  a array based set, value is nullable.
 *
 *  Note that this set is a simple wrapper around the array,
 *  there is no guarantee that values in this set is unique.
 *
 * @author bowen.cbw
 */
public class ImmutableArraySet<V> implements Set<V>, Cloneable, Serializable {
	
	private static final long serialVersionUID = 753227636731523250L;
	
	private final Object[] value;

	public ImmutableArraySet(Object[] value) {
		this.value = value;
	}

    public ImmutableArraySet(Set<V> jdkSet) {
        value = new Object[jdkSet.size()];
        int i = 0;
        for (V v : jdkSet) {
            value[i++] = v;
        }
    }

	@Override
	public int size() {
		return value.length;
	}

	@Override
	public boolean isEmpty() {
		return value.length == 0;
	}

	@Override
	public boolean contains(Object o) {
		if (o == null) {
			for (Object object : value) {
				if (object == null)
					return true;
			}
		}else {
			for (Object object : value) {
				if (object != null && o.equals(object))
					return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<V> iterator() {
		return new Iterator<V>() {
            private int idx = 0;
            public boolean hasNext() {return idx < value.length;}
            public V next()          {return (V)value[idx++];}
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
	}

	/**
	 * returns a array which is a copy of this set
     * element within this set will not be copied
	 */
	@Override
	public Object[] toArray() {
		Object[] cp = new Object[value.length];
		System.arraycopy(value, 0, cp, 0, value.length);
		return cp;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < value.length)
			return (T[])toArray();
		System.arraycopy(value, 0, a, 0, value.length);
		if (a.length > value.length)
			a[value.length] = null;
		return a;
	}

	@Override
	public boolean add(V e) {
        throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
        throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
        for (Object e : c)
            if (!contains(e))
                return false;
        return true;
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
        throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
        throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
        int result = 0;
        for (Object element : value)
            result += (element == null ? 0 : element.hashCode());
        return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (! (obj instanceof Set))
			return false;
        Collection c = (Collection) obj;
        if (c.size() != size())
            return false;
        try {
            return containsAll(c);
        } catch (ClassCastException unused)   {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }
	}

	/**
	 * returns a new set which is a copy of this set 
	 */
	public Object clone() {
		try {
			ImmutableArraySet<V> s = (ImmutableArraySet<V>) super.clone();
			return s;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("this exception will never throw");
		}
	}

    public void appendJson(Appendable b) {
        try {
            b.append("{\"" + super.toString() + "\" : [");
            final int _l = value.length - 1;
            int _i = 0;
            for (; _i < _l; ++ _i) {
                final Object v = value[_i];
                b.append('\"');
                if (v != null) b.append(v.toString());
                else b.append("null");
                b.append("\", ");
            }
            if (_l >= 0) {
                final Object v = value[_l];
                b.append('\"');
                if (v != null) b.append(v.toString()).append('\"');
                else b.append("null\"");
            }
            b.append("]}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJson() {
        StringBuilder b = new StringBuilder(256);
        appendJson(b);
        return b.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }

    public void addTo(Set<V> set) {
        for (Object v : value) {
            set.add((V)v);
        }
    }

    public HashSet<V> toHashSet() {
        HashSet s = new HashSet(value.length * 4 / 3);
        addTo(s);
        return s;
    }

    public TreeSet<V> toTreeSet(Comparator<? super V> comp) {
        TreeSet s = new TreeSet(comp);
        addTo(s);
        return s;
    }

}
