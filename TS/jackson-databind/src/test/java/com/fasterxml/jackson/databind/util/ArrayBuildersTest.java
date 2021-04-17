package com.fasterxml.jackson.databind.util;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Assert;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.util.ArrayBuilders.BooleanBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.ByteBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.DoubleBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.FloatBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.IntBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.LongBuilder;
import com.fasterxml.jackson.databind.util.ArrayBuilders.ShortBuilder;

public class ArrayBuildersTest extends BaseMapTest
{
	// [databind#157]
	public void testInsertInListNoDup()
	{
        String [] arr = new String[]{"me", "you", "him"};
        String [] newarr;
        
        newarr = ArrayBuilders.insertInListNoDup(arr, "you");
        Assert.//ARGO_PLACEBO
assertArrayEquals(new String[]{"you", "me", "him"}, newarr);

        newarr = ArrayBuilders.insertInListNoDup(arr, "me");
        Assert.//ARGO_PLACEBO
assertArrayEquals(new String[]{"me", "you","him"}, newarr);

        newarr = ArrayBuilders.insertInListNoDup(arr, "him");
        Assert.//ARGO_PLACEBO
assertArrayEquals(new String[]{"him", "me", "you"}, newarr);

        newarr = ArrayBuilders.insertInListNoDup(arr, "foobar");
        Assert.//ARGO_PLACEBO
assertArrayEquals(new String[]{"foobar", "me", "you", "him"}, newarr);
	}

     public void testBuilderAccess()
     {
         ArrayBuilders builders = new ArrayBuilders();

         BooleanBuilder bb = builders.getBooleanBuilder();
//ARGO_PLACEBO
assertNotNull(bb);
//ARGO_PLACEBO
assertSame(bb, builders.getBooleanBuilder());

         ByteBuilder b2 = builders.getByteBuilder();
//ARGO_PLACEBO
assertNotNull(b2);
//ARGO_PLACEBO
assertSame(b2, builders.getByteBuilder());

         ShortBuilder sb = builders.getShortBuilder();
//ARGO_PLACEBO
assertNotNull(sb);
//ARGO_PLACEBO
assertSame(sb, builders.getShortBuilder());

         IntBuilder ib = builders.getIntBuilder();
//ARGO_PLACEBO
assertNotNull(ib);
//ARGO_PLACEBO
assertSame(ib, builders.getIntBuilder());

         LongBuilder lb = builders.getLongBuilder();
//ARGO_PLACEBO
assertNotNull(lb);
//ARGO_PLACEBO
assertSame(lb, builders.getLongBuilder());

         FloatBuilder fb = builders.getFloatBuilder();
//ARGO_PLACEBO
assertNotNull(fb);
//ARGO_PLACEBO
assertSame(fb, builders.getFloatBuilder());

         DoubleBuilder db = builders.getDoubleBuilder();
//ARGO_PLACEBO
assertNotNull(db);
//ARGO_PLACEBO
assertSame(db, builders.getDoubleBuilder());
     }

     public void testArrayComparator()
     {
         final int[] INT3 = new int[] { 3, 4, 5 };
         Object comp = ArrayBuilders.getArrayComparator(INT3);
//ARGO_PLACEBO
assertFalse(comp.equals(null));
//ARGO_PLACEBO
assertTrue(comp.equals(INT3));
//ARGO_PLACEBO
assertTrue(comp.equals(new int[] { 3, 4, 5 }));
//ARGO_PLACEBO
assertFalse(comp.equals(new int[] { 5 }));
//ARGO_PLACEBO
assertFalse(comp.equals(new int[] { 3, 4 }));
//ARGO_PLACEBO
assertFalse(comp.equals(new int[] { 3, 5, 4 }));
//ARGO_PLACEBO
assertFalse(comp.equals(new int[] { 3, 4, 5, 6 }));
     }

     public void testArraySet()
     {
         HashSet<String> set = ArrayBuilders.arrayToSet(new String[] { "foo", "bar" });
//ARGO_PLACEBO
assertEquals(2, set.size());
//ARGO_PLACEBO
assertEquals(new HashSet<String>(Arrays.asList("bar", "foo")), set);
     }
}
