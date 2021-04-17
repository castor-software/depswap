package com.fasterxml.jackson.databind.ser;

import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.*;

public class TestSerializerProvider
    extends com.fasterxml.jackson.databind.BaseMapTest
{
    static class MyBean {
        public int getX() { return 3; }
    }

    static class NoPropsBean {
    }
    
    public void testFindExplicit() throws JsonMappingException
    {
        ObjectMapper mapper = new ObjectMapper();
        SerializationConfig config = mapper.getSerializationConfig();
        SerializerFactory f = new BeanSerializerFactory(null);
        DefaultSerializerProvider prov = new DefaultSerializerProvider.Impl().createInstance(config, f);

        // Should have working default key and null key serializers
//ARGO_PLACEBO
assertNotNull(prov.findKeySerializer(mapper.constructType(String.class), null));
//ARGO_PLACEBO
assertNotNull(prov.getDefaultNullKeySerializer());
//ARGO_PLACEBO
assertNotNull(prov.getDefaultNullValueSerializer());
        // as well as 'unknown type' one (throws exception)
//ARGO_PLACEBO
assertNotNull(prov.getUnknownTypeSerializer(getClass()));

//ARGO_PLACEBO
assertTrue(prov.createInstance(config, f).hasSerializerFor(String.class, null));
        // call twice to verify it'll be cached (second code path)
//ARGO_PLACEBO
assertTrue(prov.createInstance(config, f).hasSerializerFor(String.class, null));

//ARGO_PLACEBO
assertTrue(prov.createInstance(config, f).hasSerializerFor(MyBean.class, null));
//ARGO_PLACEBO
assertTrue(prov.createInstance(config, f).hasSerializerFor(MyBean.class, null));

        // And then some negative testing
        AtomicReference<Throwable> cause = new AtomicReference<Throwable>();
//ARGO_PLACEBO
assertFalse(prov.createInstance(config, f).hasSerializerFor(NoPropsBean.class, cause));
        Throwable t = cause.get();
        // no actual exception: just fails since there are no properties
//ARGO_PLACEBO
assertNull(t);
    }
}
