/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson;

import com.google.gson.common.MoreAsserts;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Unit test for the {@link JsonPrimitive} class.
 *
 * @author Joel Leitch
 */
public class JsonPrimitiveTest extends TestCase {

  @SuppressWarnings("unused")
  public void testNulls() {
    try {
      new JsonPrimitive((Boolean) null);
      //ARGO_PLACEBO
      fail();
    } catch (NullPointerException ignored) {
    }
    try {
      new JsonPrimitive((Number) null);
      //ARGO_PLACEBO
      fail();
    } catch (NullPointerException ignored) {
    }
    try {
      new JsonPrimitive((String) null);
      //ARGO_PLACEBO
      fail();
    } catch (NullPointerException ignored) {
    }
    try {
      new JsonPrimitive((Character) null);
      //ARGO_PLACEBO
      fail();
    } catch (NullPointerException ignored) {
    }
  }

  public void testBoolean() throws Exception {
    JsonPrimitive json = new JsonPrimitive(Boolean.TRUE);

    //ARGO_PLACEBO
    assertTrue(json.isBoolean());
    //ARGO_PLACEBO
    assertTrue(json.getAsBoolean());

    // Extra support for booleans
    json = new JsonPrimitive(1);
    //ARGO_PLACEBO
    assertFalse(json.getAsBoolean());

    json = new JsonPrimitive("1");
    //ARGO_PLACEBO
    assertFalse(json.getAsBoolean());

    json = new JsonPrimitive("true");
    //ARGO_PLACEBO
    assertTrue(json.getAsBoolean());

    json = new JsonPrimitive("TrUe");
    //ARGO_PLACEBO
    assertTrue(json.getAsBoolean());

    json = new JsonPrimitive("1.3");
    //ARGO_PLACEBO
    assertFalse(json.getAsBoolean());
  }

  public void testParsingStringAsBoolean() throws Exception {
    JsonPrimitive json = new JsonPrimitive("true");

    //ARGO_PLACEBO
    assertFalse(json.isBoolean());
    //ARGO_PLACEBO
    assertTrue(json.getAsBoolean());
  }

  public void testParsingStringAsNumber() throws Exception {
    JsonPrimitive json = new JsonPrimitive("1");

    //ARGO_PLACEBO
    assertFalse(json.isNumber());
    //ARGO_PLACEBO
    assertEquals(1D, json.getAsDouble(), 0.00001);
    //ARGO_PLACEBO
    assertEquals(1F, json.getAsFloat(), 0.00001);
    //ARGO_PLACEBO
    assertEquals(1, json.getAsInt());
    //ARGO_PLACEBO
    assertEquals(1L, json.getAsLong());
    //ARGO_PLACEBO
    assertEquals((short) 1, json.getAsShort());
    //ARGO_PLACEBO
    assertEquals((byte) 1, json.getAsByte());
    //ARGO_PLACEBO
    assertEquals(new BigInteger("1"), json.getAsBigInteger());
    //ARGO_PLACEBO
    assertEquals(new BigDecimal("1"), json.getAsBigDecimal());
  }

  public void testStringsAndChar() throws Exception {
    JsonPrimitive json = new JsonPrimitive("abc");
    //ARGO_PLACEBO
    assertTrue(json.isString());
    //ARGO_PLACEBO
    assertEquals('a', json.getAsCharacter());
    //ARGO_PLACEBO
    assertEquals("abc", json.getAsString());

    json = new JsonPrimitive('z');
    //ARGO_PLACEBO
    assertTrue(json.isString());
    //ARGO_PLACEBO
    assertEquals('z', json.getAsCharacter());
    //ARGO_PLACEBO
    assertEquals("z", json.getAsString());
  }

  public void testExponential() throws Exception {
    JsonPrimitive json = new JsonPrimitive("1E+7");

    //ARGO_PLACEBO
    assertEquals(new BigDecimal("1E+7"), json.getAsBigDecimal());
    //ARGO_PLACEBO
    assertEquals(1E+7, json.getAsDouble(), 0.00001);
    //ARGO_PLACEBO
    assertEquals(1E+7, json.getAsDouble(), 0.00001);

    try {
      json.getAsInt();
      //ARGO_PLACEBO
      fail("Integers can not handle exponents like this.");
    } catch (NumberFormatException expected) { }
  }

  public void testByteEqualsShort() {
    JsonPrimitive p1 = new JsonPrimitive(Byte.valueOf((byte)10));
    JsonPrimitive p2 = new JsonPrimitive(Short.valueOf((short)10));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testByteEqualsInteger() {
    JsonPrimitive p1 = new JsonPrimitive(Byte.valueOf((byte)10));
    JsonPrimitive p2 = new JsonPrimitive(Integer.valueOf(10));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testByteEqualsLong() {
    JsonPrimitive p1 = new JsonPrimitive(Byte.valueOf((byte)10));
    JsonPrimitive p2 = new JsonPrimitive(Long.valueOf(10L));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testByteEqualsBigInteger() {
    JsonPrimitive p1 = new JsonPrimitive(Byte.valueOf((byte)10));
    JsonPrimitive p2 = new JsonPrimitive(new BigInteger("10"));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testShortEqualsInteger() {
    JsonPrimitive p1 = new JsonPrimitive(Short.valueOf((short)10));
    JsonPrimitive p2 = new JsonPrimitive(Integer.valueOf(10));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testShortEqualsLong() {
    JsonPrimitive p1 = new JsonPrimitive(Short.valueOf((short)10));
    JsonPrimitive p2 = new JsonPrimitive(Long.valueOf(10));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testShortEqualsBigInteger() {
    JsonPrimitive p1 = new JsonPrimitive(Short.valueOf((short)10));
    JsonPrimitive p2 = new JsonPrimitive(new BigInteger("10"));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testIntegerEqualsLong() {
    JsonPrimitive p1 = new JsonPrimitive(Integer.valueOf(10));
    JsonPrimitive p2 = new JsonPrimitive(Long.valueOf(10L));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testIntegerEqualsBigInteger() {
    JsonPrimitive p1 = new JsonPrimitive(Integer.valueOf(10));
    JsonPrimitive p2 = new JsonPrimitive(new BigInteger("10"));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testLongEqualsBigInteger() {
    JsonPrimitive p1 = new JsonPrimitive(Long.valueOf(10L));
    JsonPrimitive p2 = new JsonPrimitive(new BigInteger("10"));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testFloatEqualsDouble() {
    JsonPrimitive p1 = new JsonPrimitive(Float.valueOf(10.25F));
    JsonPrimitive p2 = new JsonPrimitive(Double.valueOf(10.25D));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testFloatEqualsBigDecimal() {
    JsonPrimitive p1 = new JsonPrimitive(Float.valueOf(10.25F));
    JsonPrimitive p2 = new JsonPrimitive(new BigDecimal("10.25"));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testDoubleEqualsBigDecimal() {
    JsonPrimitive p1 = new JsonPrimitive(Double.valueOf(10.25D));
    JsonPrimitive p2 = new JsonPrimitive(new BigDecimal("10.25"));
    //ARGO_PLACEBO
    assertEquals(p1, p2);
    //ARGO_PLACEBO
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testValidJsonOnToString() throws Exception {
    JsonPrimitive json = new JsonPrimitive("Some\nEscaped\nValue");
    //ARGO_PLACEBO
    assertEquals("\"Some\\nEscaped\\nValue\"", json.toString());

    json = new JsonPrimitive(new BigDecimal("1.333"));
    //ARGO_PLACEBO
    assertEquals("1.333", json.toString());
  }

  public void testEquals() {
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive("A"), new JsonPrimitive("A"));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(true), new JsonPrimitive(true));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(5L), new JsonPrimitive(5L));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive('a'), new JsonPrimitive('a'));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Float.NaN), new JsonPrimitive(Float.NaN));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Float.NEGATIVE_INFINITY),
        new JsonPrimitive(Float.NEGATIVE_INFINITY));
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Float.POSITIVE_INFINITY),
        new JsonPrimitive(Float.POSITIVE_INFINITY));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Double.NaN), new JsonPrimitive(Double.NaN));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Double.NEGATIVE_INFINITY),
        new JsonPrimitive(Double.NEGATIVE_INFINITY));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Double.POSITIVE_INFINITY),
        new JsonPrimitive(Double.POSITIVE_INFINITY));
    //ARGO_PLACEBO
    assertFalse(new JsonPrimitive("a").equals(new JsonPrimitive("b")));
    //ARGO_PLACEBO
    assertFalse(new JsonPrimitive(true).equals(new JsonPrimitive(false)));
    //ARGO_PLACEBO
    assertFalse(new JsonPrimitive(0).equals(new JsonPrimitive(1)));
  }

  public void testEqualsAcrossTypes() {
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive("a"), new JsonPrimitive('a'));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(new BigInteger("0")), new JsonPrimitive(0));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(0), new JsonPrimitive(0L));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(new BigInteger("0")), new JsonPrimitive(0));
    //ARGO_PLACEBO
    MoreAsserts.assertEqualsAndHashCode(new JsonPrimitive(Float.NaN), new JsonPrimitive(Double.NaN));
  }

  public void testEqualsIntegerAndBigInteger() {
    JsonPrimitive a = new JsonPrimitive(5L);
    JsonPrimitive b = new JsonPrimitive(new BigInteger("18446744073709551621")); // 2^64 + 5
    // Ideally, the following assertion should have failed but the price is too much to pay 
    // assertFalse(a + " equals " + b, a.equals(b));
    //ARGO_PLACEBO
    assertTrue(a + " equals " + b, a.equals(b));
  }

  public void testEqualsDoesNotEquateStringAndNonStringTypes() {
    //ARGO_PLACEBO
    assertFalse(new JsonPrimitive("true").equals(new JsonPrimitive(true)));
    //ARGO_PLACEBO
    assertFalse(new JsonPrimitive("0").equals(new JsonPrimitive(0)));
    //ARGO_PLACEBO
    assertFalse(new JsonPrimitive("NaN").equals(new JsonPrimitive(Float.NaN)));
  }

  public void testDeepCopy() {
    JsonPrimitive a = new JsonPrimitive("a");
    //ARGO_PLACEBO
    assertSame(a, a.deepCopy()); // Primitives are immutable!
  }
}
