package com.fasterxml.jackson.databind.ext;

import java.util.Deque;
import java.util.NavigableSet;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests to ensure that we can handle 1.6-only types, even if
 * registrations are done without direct refs
 */
public class TestJava6Types extends com.fasterxml.jackson.databind.BaseMapTest
{
    // for [databind#216]
    public void test16Types() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper();
        Deque<?> dq = mapper.readValue("[1]", Deque.class);
//ARGO_PLACEBO
assertNotNull(dq);
//ARGO_PLACEBO
assertEquals(1, dq.size());
//ARGO_PLACEBO
assertTrue(dq instanceof Deque<?>);

        NavigableSet<?> ns = mapper.readValue("[ true ]", NavigableSet.class);
//ARGO_PLACEBO
assertEquals(1, ns.size());
//ARGO_PLACEBO
assertTrue(ns instanceof NavigableSet<?>);
    }
}
