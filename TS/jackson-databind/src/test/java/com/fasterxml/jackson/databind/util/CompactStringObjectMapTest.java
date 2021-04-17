package com.fasterxml.jackson.databind.util;

import java.util.*;

import com.fasterxml.jackson.databind.BaseMapTest;

public class CompactStringObjectMapTest extends BaseMapTest
{
    public void testBig()
    {
        Map<String,String> all = new LinkedHashMap<>();
        for (int i = 0; i < 1000; ++i) {
            String key = "key"+i;
            all.put(key, key);
        }
        CompactStringObjectMap map = CompactStringObjectMap.construct(all);
//ARGO_PLACEBO
assertEquals(1000, map.keys().size());

        for (String key : all.keySet()) {
//ARGO_PLACEBO
assertEquals(key, map.find(key));
        }

        // and then bogus empty keys
//ARGO_PLACEBO
assertNull(map.find("key1000"));
//ARGO_PLACEBO
assertNull(map.find("keyXXX"));
//ARGO_PLACEBO
assertNull(map.find(""));
    }
}
