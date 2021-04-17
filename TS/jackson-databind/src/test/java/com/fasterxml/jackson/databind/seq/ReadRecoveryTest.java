package com.fasterxml.jackson.databind.seq;

import com.fasterxml.jackson.databind.*;

/**
 * Tests to verify aspects of error recover for reading using
 * iterator.
 */
public class ReadRecoveryTest extends BaseMapTest
{
    static class Bean {
        public int a, b;

        @Override public String toString() { return "{Bean, a="+a+", b="+b+"}"; }
    }

    /*
    /**********************************************************
    /* Unit tests; root-level value sequences via Mapper
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testRootBeans() throws Exception
    {
        final String JSON = aposToQuotes("{'a':3} {'x':5}");
        MappingIterator<Bean> it = MAPPER.readerFor(Bean.class).readValues(JSON);
        // First one should be fine
//ARGO_PLACEBO
assertTrue(it.hasNextValue());
        Bean bean = it.nextValue();
//ARGO_PLACEBO
assertEquals(3, bean.a);
        // but second one not
        try {
            bean = it.nextValue();
//ARGO_PLACEBO
fail("Should not have succeeded");
        } catch (JsonMappingException e) {
            verifyException(e, "Unrecognized field \"x\"");
        }
        // 21-May-2015, tatu: With [databind#734], recovery, we now know there's no more data!
//ARGO_PLACEBO
assertFalse(it.hasNextValue());

        it.close();
    }

    // for [databind#734]
    // Simple test for verifying that basic recover works for a case of
    // unknown structured value
    public void testSimpleRootRecovery() throws Exception
    {
        final String JSON = aposToQuotes("{'a':3}{'a':27,'foo':[1,2],'b':{'x':3}}  {'a':1,'b':2} ");

        MappingIterator<Bean> it = MAPPER.readerFor(Bean.class).readValues(JSON);
        Bean bean = it.nextValue();

//ARGO_PLACEBO
assertNotNull(bean);
//ARGO_PLACEBO
assertEquals(3, bean.a);

        // second one problematic
        try {
            it.nextValue();
        } catch (JsonMappingException e) {
            verifyException(e, "Unrecognized field \"foo\"");
        }

        // but should recover nicely
        bean = it.nextValue();
//ARGO_PLACEBO
assertNotNull(bean);
//ARGO_PLACEBO
assertEquals(1, bean.a);
//ARGO_PLACEBO
assertEquals(2, bean.b);

//ARGO_PLACEBO
assertFalse(it.hasNextValue());
        
        it.close();
    }

    // Similar to "raw" root-level Object sequence, but in array
    public void testSimpleArrayRecovery() throws Exception
    {
        final String JSON = aposToQuotes("[{'a':3},{'a':27,'foo':[1,2],'b':{'x':3}}  ,{'a':1,'b':2}  ]");

        MappingIterator<Bean> it = MAPPER.readerFor(Bean.class).readValues(JSON);
        Bean bean = it.nextValue();

//ARGO_PLACEBO
assertNotNull(bean);
//ARGO_PLACEBO
assertEquals(3, bean.a);

        // second one problematic
        try {
            it.nextValue();
        } catch (JsonMappingException e) {
            verifyException(e, "Unrecognized field \"foo\"");
        }

        // but should recover nicely
        bean = it.nextValue();
//ARGO_PLACEBO
assertNotNull(bean);
//ARGO_PLACEBO
assertEquals(1, bean.a);
//ARGO_PLACEBO
assertEquals(2, bean.b);

//ARGO_PLACEBO
assertFalse(it.hasNextValue());
        
        it.close();
    }
}
