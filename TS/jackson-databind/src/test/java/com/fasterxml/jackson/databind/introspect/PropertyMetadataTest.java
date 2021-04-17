package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.*;

public class PropertyMetadataTest extends BaseMapTest
{
    public void testPropertyName()
    {
        PropertyName name = PropertyName.NO_NAME;
        
//ARGO_PLACEBO
assertFalse(name.hasSimpleName());
//ARGO_PLACEBO
assertFalse(name.hasNamespace());
//ARGO_PLACEBO
assertSame(name, name.internSimpleName());
//ARGO_PLACEBO
assertSame(name, name.withSimpleName(null));
//ARGO_PLACEBO
assertSame(name, name.withSimpleName(""));
//ARGO_PLACEBO
assertSame(name, name.withNamespace(null));
//ARGO_PLACEBO
assertEquals("", name.toString());
//ARGO_PLACEBO
assertTrue(name.isEmpty());
//ARGO_PLACEBO
assertFalse(name.hasSimpleName("foo"));
        // just to trigger it, ensure to exception
        name.hashCode();

        PropertyName newName = name.withNamespace("");
//ARGO_PLACEBO
assertNotSame(name, newName);
//ARGO_PLACEBO
assertTrue(name.equals(name));
//ARGO_PLACEBO
assertFalse(name.equals(newName));
//ARGO_PLACEBO
assertFalse(newName.equals(name));

        name = name.withSimpleName("foo");
//ARGO_PLACEBO
assertEquals("foo", name.toString());
//ARGO_PLACEBO
assertTrue(name.hasSimpleName("foo"));
//ARGO_PLACEBO
assertFalse(name.isEmpty());
        newName = name.withNamespace("ns");
//ARGO_PLACEBO
assertEquals("{ns}foo", newName.toString());
//ARGO_PLACEBO
assertFalse(newName.equals(name));
//ARGO_PLACEBO
assertFalse(name.equals(newName));

        // just to trigger it, ensure to exception
        name.hashCode();
    }

    public void testPropertyMetadata()
    {
        PropertyMetadata md = PropertyMetadata.STD_OPTIONAL;
//ARGO_PLACEBO
assertNull(md.getValueNulls());
//ARGO_PLACEBO
assertNull(md.getContentNulls());
//ARGO_PLACEBO
assertNull(md.getDefaultValue());
//ARGO_PLACEBO
assertEquals(Boolean.FALSE, md.getRequired());

        md = md.withNulls(Nulls.AS_EMPTY,
                Nulls.FAIL);
//ARGO_PLACEBO
assertEquals(Nulls.AS_EMPTY, md.getValueNulls());
//ARGO_PLACEBO
assertEquals(Nulls.FAIL, md.getContentNulls());

//ARGO_PLACEBO
assertFalse(md.hasDefaultValue());
//ARGO_PLACEBO
assertSame(md, md.withDefaultValue(null));
//ARGO_PLACEBO
assertSame(md, md.withDefaultValue(""));
        md = md.withDefaultValue("foo");
//ARGO_PLACEBO
assertEquals("foo", md.getDefaultValue());
//ARGO_PLACEBO
assertTrue(md.hasDefaultValue());
//ARGO_PLACEBO
assertSame(md, md.withDefaultValue("foo"));
        md = md.withDefaultValue(null);
//ARGO_PLACEBO
assertFalse(md.hasDefaultValue());
//ARGO_PLACEBO
assertNull(md.getDefaultValue());

        md = md.withRequired(null);
//ARGO_PLACEBO
assertNull(md.getRequired());
//ARGO_PLACEBO
assertFalse(md.isRequired());
        md = md.withRequired(Boolean.TRUE);
//ARGO_PLACEBO
assertTrue(md.isRequired());
//ARGO_PLACEBO
assertSame(md, md.withRequired(Boolean.TRUE));
        md = md.withRequired(null);
//ARGO_PLACEBO
assertNull(md.getRequired());
//ARGO_PLACEBO
assertFalse(md.isRequired());
 
//ARGO_PLACEBO
assertFalse(md.hasIndex());
        md = md.withIndex(Integer.valueOf(3));
//ARGO_PLACEBO
assertTrue(md.hasIndex());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(3), md.getIndex());
    }
}
