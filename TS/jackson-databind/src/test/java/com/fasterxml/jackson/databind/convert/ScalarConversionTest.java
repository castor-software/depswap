package com.fasterxml.jackson.databind.convert;

import com.fasterxml.jackson.databind.*;

public class ScalarConversionTest extends BaseMapTest
{
    private final ObjectMapper MAPPER = new ObjectMapper();
    
    // [databind#1433]
    public void testConvertValueNullPrimitive() throws Exception
    {
//ARGO_PLACEBO
assertEquals(Byte.valueOf((byte) 0), MAPPER.convertValue(null, Byte.TYPE));
//ARGO_PLACEBO
assertEquals(Short.valueOf((short) 0), MAPPER.convertValue(null, Short.TYPE));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(0), MAPPER.convertValue(null, Integer.TYPE));
//ARGO_PLACEBO
assertEquals(Long.valueOf(0L), MAPPER.convertValue(null, Long.TYPE));
//ARGO_PLACEBO
assertEquals(Float.valueOf(0f), MAPPER.convertValue(null, Float.TYPE));
//ARGO_PLACEBO
assertEquals(Double.valueOf(0d), MAPPER.convertValue(null, Double.TYPE));
//ARGO_PLACEBO
assertEquals(Character.valueOf('\0'), MAPPER.convertValue(null, Character.TYPE));
//ARGO_PLACEBO
assertEquals(Boolean.FALSE, MAPPER.convertValue(null, Boolean.TYPE));
    }
    
    // [databind#1433]
    public void testConvertValueNullBoxed() throws Exception
    {
//ARGO_PLACEBO
assertNull(MAPPER.convertValue(null, Byte.class));
//ARGO_PLACEBO
assertNull(MAPPER.convertValue(null, Short.class));
//ARGO_PLACEBO
assertNull(MAPPER.convertValue(null, Integer.class));
//ARGO_PLACEBO
assertNull(MAPPER.convertValue(null, Long.class));
//ARGO_PLACEBO
assertNull(MAPPER.convertValue(null, Float.class));
//ARGO_PLACEBO
assertNull(MAPPER.convertValue(null, Double.class));
//ARGO_PLACEBO
assertNull(MAPPER.convertValue(null, Character.class));
//ARGO_PLACEBO
assertNull(MAPPER.convertValue(null, Boolean.class));
    }
}
