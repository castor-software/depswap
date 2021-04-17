package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.testutil.NoCheckSubTypeValidator;

public class NoTypeInfoTest extends BaseMapTest
{
    @JsonTypeInfo(use=JsonTypeInfo.Id.NONE)
    @JsonDeserialize(as=NoType.class)
    static interface NoTypeInterface {
    }
    
    final static class NoType implements NoTypeInterface {
        public int a = 3;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testWithIdNone() throws Exception
    {
        final ObjectMapper mapper = jsonMapperBuilder()
            .activateDefaultTyping(NoCheckSubTypeValidator.instance)
            .build();
        // serialize without type info
        String json = mapper.writeValueAsString(new NoType());
//ARGO_PLACEBO
assertEquals("{\"a\":3}", json);

        // and deserialize successfully
        NoTypeInterface bean = mapper.readValue("{\"a\":6}", NoTypeInterface.class);
//ARGO_PLACEBO
assertNotNull(bean);
        NoType impl = (NoType) bean;
//ARGO_PLACEBO
assertEquals(6, impl.a);
    }
}
