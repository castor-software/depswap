package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JsonSerializable;

public class RawValueTest extends BaseMapTest
{
    public void testEquality()
    {
        RawValue raw1 = new RawValue("foo");
        RawValue raw1b = new RawValue("foo");
        RawValue raw2 = new RawValue("bar");

//ARGO_PLACEBO
assertTrue(raw1.equals(raw1));
//ARGO_PLACEBO
assertTrue(raw1.equals(raw1b));

//ARGO_PLACEBO
assertFalse(raw1.equals(raw2));
//ARGO_PLACEBO
assertFalse(raw1.equals(null));

//ARGO_PLACEBO
assertFalse(new RawValue((JsonSerializable) null).equals(raw1));

//ARGO_PLACEBO
assertNotNull(raw1.toString());
    }
}
