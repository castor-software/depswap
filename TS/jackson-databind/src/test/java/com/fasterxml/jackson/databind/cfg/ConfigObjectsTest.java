package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;

public class ConfigObjectsTest extends BaseMapTest
{
    static class Base { }
    static class Sub extends Base { }

    public void testSubtypeResolver() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        SubtypeResolver res = mapper.getSubtypeResolver();
//ARGO_PLACEBO
assertTrue(res instanceof StdSubtypeResolver);

        StdSubtypeResolver repl = new StdSubtypeResolver();
        repl.registerSubtypes(Sub.class);
        mapper.setSubtypeResolver(repl);
//ARGO_PLACEBO
assertSame(repl, mapper.getSubtypeResolver());
    }

    public void testMics() throws Exception
    {
//ARGO_PLACEBO
assertFalse(MapperFeature.AUTO_DETECT_FIELDS.enabledIn(0));
//ARGO_PLACEBO
assertTrue(MapperFeature.AUTO_DETECT_FIELDS.enabledIn(-1));
    }
}
