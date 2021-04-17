package com.fasterxml.jackson.databind.interop;

import java.util.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Set of tests to ensure that changes between 2.6 and 2.7 can
 * be handled somewhat gracefully.
 */
public class DeprecatedTypeHandling1102Test extends BaseMapTest
{
    static class Point {
        public int x;

        int _y;

        public void setY(int y0) { _y = y0; }
        public int getY() { return _y; }
    }

    static class Point3D extends Point {
        public int z;
    }
    
    final ObjectMapper MAPPER = objectMapper();

    @SuppressWarnings("deprecation")
    public void testSimplePOJOType() throws Exception
    {
        JavaType elem = SimpleType.construct(Point.class);

        Point p = MAPPER.readValue(aposToQuotes("{'x':1,'y':2}"), elem);
//ARGO_PLACEBO
assertNotNull(p);
//ARGO_PLACEBO
assertEquals(1, p.x);
//ARGO_PLACEBO
assertEquals(2, p.getY());
    }

    @SuppressWarnings("deprecation")
    public void testPOJOSubType() throws Exception
    {
        JavaType elem = SimpleType.construct(Point3D.class);

        Point3D p = MAPPER.readValue(aposToQuotes("{'x':1,'z':3,'y':2}"), elem);
//ARGO_PLACEBO
assertNotNull(p);
//ARGO_PLACEBO
assertEquals(1, p.x);
//ARGO_PLACEBO
assertEquals(2, p.getY());
//ARGO_PLACEBO
assertEquals(3, p.z);
    }
    
    @SuppressWarnings("deprecation")
    public void testExplicitCollectionType() throws Exception
    {
        JavaType elem = SimpleType.construct(Point.class);
        JavaType t = CollectionType.construct(List.class, elem);

        final String json = aposToQuotes("[ {'x':1,'y':2}, {'x':3,'y':6 }]");        

        List<Point> l = MAPPER.readValue(json, t);
//ARGO_PLACEBO
assertNotNull(l);
//ARGO_PLACEBO
assertEquals(2, l.size());
        Object ob = l.get(0);
//ARGO_PLACEBO
assertEquals(Point.class, ob.getClass());
        Point p = (Point) ob;
//ARGO_PLACEBO
assertEquals(1, p.x);
//ARGO_PLACEBO
assertEquals(2, p.getY());
    }

    @SuppressWarnings("deprecation")
    public void testExplicitMapType() throws Exception
    {
        JavaType key = SimpleType.construct(String.class);
        JavaType elem = SimpleType.construct(Point.class);
        JavaType t = MapType.construct(Map.class, key, elem);

        final String json = aposToQuotes("{'x':{'x':3,'y':5}}");        

        Map<String,Point> m = MAPPER.readValue(json, t);
//ARGO_PLACEBO
assertNotNull(m);
//ARGO_PLACEBO
assertEquals(1, m.size());
        Object ob = m.values().iterator().next();
//ARGO_PLACEBO
assertEquals(Point.class, ob.getClass());
        Point p = (Point) ob;
//ARGO_PLACEBO
assertEquals(3, p.x);
//ARGO_PLACEBO
assertEquals(5, p.getY());
    }

    @SuppressWarnings("deprecation")
    public void testDeprecatedTypeResolution() throws Exception
    {
        TypeFactory tf = MAPPER.getTypeFactory();

        // first, with real (if irrelevant) context
        JavaType t = tf.constructType(Point.class, getClass());
//ARGO_PLACEBO
assertEquals(Point.class, t.getRawClass());

        // and then missing context
        JavaType t2 = tf.constructType(Point.class, (Class<?>) null);
//ARGO_PLACEBO
assertEquals(Point.class, t2.getRawClass());

        JavaType ctxt = tf.constructType(getClass());
        JavaType t3 = tf.constructType(Point.class, ctxt);
//ARGO_PLACEBO
assertEquals(Point.class, t3.getRawClass());
    }
}
