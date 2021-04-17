package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.BaseMapTest;

public class NameTransformerTest extends BaseMapTest
{
    public void testSimpleTransformer() throws Exception
    {
        NameTransformer xfer;

        xfer = NameTransformer.simpleTransformer("a", null);
//ARGO_PLACEBO
assertEquals("aFoo", xfer.transform("Foo"));
//ARGO_PLACEBO
assertEquals("Foo", xfer.reverse("aFoo"));

        xfer = NameTransformer.simpleTransformer(null, "++");
//ARGO_PLACEBO
assertEquals("foo++", xfer.transform("foo"));
//ARGO_PLACEBO
assertEquals("foo", xfer.reverse("foo++"));

        xfer = NameTransformer.simpleTransformer("(", ")");
//ARGO_PLACEBO
assertEquals("(foo)", xfer.transform("foo"));
//ARGO_PLACEBO
assertEquals("foo", xfer.reverse("(foo)"));
    }
}
