package com.fasterxml.jackson.databind.cfg;

import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.*;

public class SerConfigTest extends BaseMapTest
{
    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testSerConfig() throws Exception
    {
        SerializationConfig config = MAPPER.getSerializationConfig();
//ARGO_PLACEBO
assertTrue(config.hasSerializationFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS.getMask()));
//ARGO_PLACEBO
assertFalse(config.hasSerializationFeatures(SerializationFeature.CLOSE_CLOSEABLE.getMask()));
//ARGO_PLACEBO
assertEquals(JsonInclude.Value.empty(), config.getDefaultPropertyInclusion());
//ARGO_PLACEBO
assertEquals(JsonInclude.Value.empty(), config.getDefaultPropertyInclusion(String.class));
//ARGO_PLACEBO
assertFalse(config.useRootWrapping());

        // if no changes then same config object
//ARGO_PLACEBO
assertSame(config, config.without());
//ARGO_PLACEBO
assertSame(config, config.with());
//ARGO_PLACEBO
assertSame(config, config.with(MAPPER.getSubtypeResolver()));

        // and then change
        SerializationConfig newConfig = config.with(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
//ARGO_PLACEBO
assertNotSame(config, newConfig);
        config = newConfig;
//ARGO_PLACEBO
assertSame(config, config.with(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
//ARGO_PLACEBO
assertNotSame(config, config.with(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, false));

//ARGO_PLACEBO
assertNotSame(config, config.with(SerializationFeature.INDENT_OUTPUT,
                SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS));
        
//ARGO_PLACEBO
assertSame(config, config.withRootName((PropertyName) null)); // defaults to 'none'

        newConfig = config.withRootName(PropertyName.construct("foobar"));
//ARGO_PLACEBO
assertNotSame(config, newConfig);
//ARGO_PLACEBO
assertTrue(newConfig.useRootWrapping());

//ARGO_PLACEBO
assertSame(config, config.with(config.getAttributes()));
//ARGO_PLACEBO
assertNotSame(config, config.with(new ContextAttributes.Impl(Collections.singletonMap("a", "b"))));

//ARGO_PLACEBO
assertNotNull(config.introspectDirectClassAnnotations(getClass()));
    }

    public void testGeneratorFeatures() throws Exception
    {
        SerializationConfig config = MAPPER.getSerializationConfig();
//ARGO_PLACEBO
assertNotSame(config, config.with(JsonWriteFeature.ESCAPE_NON_ASCII));
        SerializationConfig newConfig = config.withFeatures(JsonGenerator.Feature.IGNORE_UNKNOWN);
//ARGO_PLACEBO
assertNotSame(config, newConfig);

//ARGO_PLACEBO
assertNotSame(config, config.without(JsonWriteFeature.ESCAPE_NON_ASCII));
//ARGO_PLACEBO
assertNotSame(config, config.withoutFeatures(JsonGenerator.Feature.IGNORE_UNKNOWN));
    }

    public void testFormatFeatures() throws Exception
    {
        SerializationConfig config = MAPPER.getSerializationConfig();
//ARGO_PLACEBO
assertNotSame(config, config.with(BogusFormatFeature.FF_DISABLED_BY_DEFAULT));
//ARGO_PLACEBO
assertNotSame(config, config.withFeatures(BogusFormatFeature.FF_DISABLED_BY_DEFAULT,
                BogusFormatFeature.FF_ENABLED_BY_DEFAULT));
//ARGO_PLACEBO
assertNotSame(config, config.without(BogusFormatFeature.FF_ENABLED_BY_DEFAULT));
//ARGO_PLACEBO
assertNotSame(config, config.withoutFeatures(BogusFormatFeature.FF_DISABLED_BY_DEFAULT,
                BogusFormatFeature.FF_ENABLED_BY_DEFAULT));
    }
}
