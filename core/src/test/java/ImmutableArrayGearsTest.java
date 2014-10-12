/*
 * *****************************************************************************
 *  Copyright 2014 Bowen Cai
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * *****************************************************************************
 */

import com.caibowen.prma.base.collect.ImmutableArrayMap;
import com.caibowen.prma.base.collect.ImmutableArraySet;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ImmutableArrayGearsTest {
	
	@Test
	public void testSet() {
		ImmutableArraySet<String> arraySet = new ImmutableArraySet<String>(
				new Object[]{"v1", "v2", "v3", "v4"});
		Set<String> hashSet = new HashSet<String>();
		hashSet.add("v1");
		hashSet.add("v2");
		hashSet.add("v3");
		hashSet.add("v4");
		
		System.out.println("ImmutanbleArraySet.toString()");
		System.out.println(arraySet.toString());
		
		assertEquals(hashSet.size(), arraySet.size());
		
		assertEquals(hashSet.hashCode(), arraySet.hashCode());
		assertTrue(hashSet.equals(arraySet));
		assertTrue(arraySet.equals(hashSet));
		assertTrue(hashSet.containsAll(arraySet));
		assertTrue(arraySet.containsAll(hashSet));
		
		List<Object> hashList = Arrays.asList(hashSet.toArray());
		List<Object> arrayList = Arrays.asList(arraySet.toArray());
		
		assertEquals(hashList.size(), arrayList.size());
		assertTrue(hashList.containsAll(arrayList));
		assertTrue(arrayList.containsAll(hashList));

		assertEquals(hashSet.contains("v1"), arraySet.contains("v1"));
		assertEquals(hashSet.contains("no such key"), arraySet.contains("no such key"));
	}
	
	@Test
	public void testMap() {
		ImmutableArrayMap<String, String> arrayMap
		= new ImmutableArrayMap<String, String>(new Object[][]
			{
			{"k1", "v1"},
			{"k2", "v2"},
			{"k3", "v3"},
			{null, null},
			});
	HashMap<String, String> hashMap = new HashMap<String, String>();
	hashMap.put("k1", "v1");
	hashMap.put("k2", "v2");
	hashMap.put("k3", "v3");
	hashMap.put(null, null);
	
	System.out.println("ImmutableArrayMap.toString()");
	System.out.println(arrayMap.toString());
	
	assertEquals(hashMap.size(), arrayMap.size());
	
	assertEquals(hashMap.hashCode(), arrayMap.hashCode());
	assertTrue(hashMap.equals(arrayMap));
	assertTrue(arrayMap.equals(hashMap));
	
	assertTrue(hashMap.keySet().equals(arrayMap.keySet()));
	assertTrue(arrayMap.keySet().equals(hashMap.keySet()));
	
	assertTrue(hashMap.entrySet().equals(arrayMap.entrySet()));
	assertTrue(arrayMap.entrySet().equals(hashMap.entrySet()));
	
	List<String> hashList = new ArrayList<String>(hashMap.values());
	List<String> arrayList = new ArrayList<String>(arrayMap.values());

	assertEquals(hashList.size(), arrayList.size());
	assertTrue(hashList.containsAll(arrayList));
	assertTrue(arrayList.containsAll(hashList));
	
	
	assertEquals(hashMap.get("k1"), arrayMap.get("k1"));
	assertTrue(arrayMap.get("no such key") == null);
	assertTrue(hashMap.get("no such key") == arrayMap.get("no such key"));

        ImmutableArrayMap m = new ImmutableArrayMap(new Object[][]{{"kkk", "222"}});
        m.appendJson(System.out);
        System.out.println(m.keySet());

        m = new ImmutableArrayMap(new Object[][]{});
        m.appendJson(System.out);
        System.out.println(m.keySet());
        System.out.println(m.keySet());

        System.out.println(tableSizeFor(5));

    }

    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * Returns a power of two size for the given target capacity.
     */
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

}
