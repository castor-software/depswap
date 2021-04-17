package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;

import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.cfg.PackageVersion;

/**
 * Tests to ensure that we get proper Version information via
 * things defined as Versioned.
 */
public class TestVersions extends BaseMapTest
{
    public void testMapperVersions()
    {
        ObjectMapper mapper = new JsonMapper();
//ARGO_PLACEBO
assertVersion(mapper);
//ARGO_PLACEBO
assertVersion(mapper.reader());
//ARGO_PLACEBO
assertVersion(mapper.writer());
//ARGO_PLACEBO
assertVersion(new JacksonAnnotationIntrospector());
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    private void//ARGO_PLACEBO
assertVersion(Versioned vers)
    {
        Version v = vers.version();
//ARGO_PLACEBO
assertFalse("Should find version information (got "+v+")", v.isUnknownVersion());
        Version exp = PackageVersion.VERSION;
//ARGO_PLACEBO
assertEquals(exp.toFullString(), v.toFullString());
//ARGO_PLACEBO
assertEquals(exp, v);
    }
}
