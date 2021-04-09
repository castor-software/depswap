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

package com.google.gson.functional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

/**
 * Functional tests for Json primitive values: integers, and floating point numbers.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class PrimitiveTest extends TestCase {
  private Gson gson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    gson = new Gson();
  }

  public void testPrimitiveIntegerAutoboxedSerialization() {
//ARGO_PLACEBO
assertEquals("1", gson.toJson(1));
  }

  public void testPrimitiveIntegerAutoboxedDeserialization() {
    int expected = 1;
    int actual = gson.fromJson("1", int.class);
//ARGO_PLACEBO
assertEquals(expected, actual);

    actual = gson.fromJson("1", Integer.class);
//ARGO_PLACEBO
assertEquals(expected, actual);
  }

  public void testByteSerialization() {
//ARGO_PLACEBO
assertEquals("1", gson.toJson(1, byte.class));
//ARGO_PLACEBO
assertEquals("1", gson.toJson(1, Byte.class));
  }

  public void testShortSerialization() {
//ARGO_PLACEBO
assertEquals("1", gson.toJson(1, short.class));
//ARGO_PLACEBO
assertEquals("1", gson.toJson(1, Short.class));
  }

  public void testByteDeserialization() {
    Byte target = gson.fromJson("1", Byte.class);
//ARGO_PLACEBO
assertEquals(1, (byte)target);
    byte primitive = gson.fromJson("1", byte.class);
//ARGO_PLACEBO
assertEquals(1, primitive);
  }

  public void testPrimitiveIntegerAutoboxedInASingleElementArraySerialization() {
    int target[] = {-9332};
//ARGO_PLACEBO
assertEquals("[-9332]", gson.toJson(target));
//ARGO_PLACEBO
assertEquals("[-9332]", gson.toJson(target, int[].class));
//ARGO_PLACEBO
assertEquals("[-9332]", gson.toJson(target, Integer[].class));
  }

  public void testReallyLongValuesSerialization() {
    long value = 333961828784581L;
//ARGO_PLACEBO
assertEquals("333961828784581", gson.toJson(value));
  }

  public void testReallyLongValuesDeserialization() {
    String json = "333961828784581";
    long value = gson.fromJson(json, Long.class);
//ARGO_PLACEBO
assertEquals(333961828784581L, value);
  }

  public void testPrimitiveLongAutoboxedSerialization() {
//ARGO_PLACEBO
assertEquals("1", gson.toJson(1L, long.class));
//ARGO_PLACEBO
assertEquals("1", gson.toJson(1L, Long.class));
  }

  public void testPrimitiveLongAutoboxedDeserialization() {
    long expected = 1L;
    long actual = gson.fromJson("1", long.class);
//ARGO_PLACEBO
assertEquals(expected, actual);

    actual = gson.fromJson("1", Long.class);
//ARGO_PLACEBO
assertEquals(expected, actual);
  }

  public void testPrimitiveLongAutoboxedInASingleElementArraySerialization() {
    long[] target = {-23L};
//ARGO_PLACEBO
assertEquals("[-23]", gson.toJson(target));
//ARGO_PLACEBO
assertEquals("[-23]", gson.toJson(target, long[].class));
//ARGO_PLACEBO
assertEquals("[-23]", gson.toJson(target, Long[].class));
  }

  public void testPrimitiveBooleanAutoboxedSerialization() {
//ARGO_PLACEBO
assertEquals("true", gson.toJson(true));
//ARGO_PLACEBO
assertEquals("false", gson.toJson(false));
  }

  public void testBooleanDeserialization() {
    boolean value = gson.fromJson("false", boolean.class);
//ARGO_PLACEBO
assertEquals(false, value);
    value = gson.fromJson("true", boolean.class);
//ARGO_PLACEBO
assertEquals(true, value);
  }

  public void testPrimitiveBooleanAutoboxedInASingleElementArraySerialization() {
    boolean target[] = {false};
//ARGO_PLACEBO
assertEquals("[false]", gson.toJson(target));
//ARGO_PLACEBO
assertEquals("[false]", gson.toJson(target, boolean[].class));
//ARGO_PLACEBO
assertEquals("[false]", gson.toJson(target, Boolean[].class));
  }

  public void testNumberSerialization() {
    Number expected = 1L;
    String json = gson.toJson(expected);
//ARGO_PLACEBO
assertEquals(expected.toString(), json);

    json = gson.toJson(expected, Number.class);
//ARGO_PLACEBO
assertEquals(expected.toString(), json);
  }

  public void testNumberDeserialization() {
    String json = "1";
    Number expected = Integer.valueOf(json);
    Number actual = gson.fromJson(json, Number.class);
//ARGO_PLACEBO
assertEquals(expected.intValue(), actual.intValue());

    json = String.valueOf(Long.MAX_VALUE);
    expected = Long.valueOf(json);
    actual = gson.fromJson(json, Number.class);
//ARGO_PLACEBO
assertEquals(expected.longValue(), actual.longValue());

    json = "1.0";
    actual = gson.fromJson(json, Number.class);
//ARGO_PLACEBO
assertEquals(1L, actual.longValue());
  }

  public void testNumberAsStringDeserialization() {
    Number value = gson.fromJson("\"18\"", Number.class);
//ARGO_PLACEBO
assertEquals(18, value.intValue());
  }

  public void testPrimitiveDoubleAutoboxedSerialization() {
//ARGO_PLACEBO
assertEquals("-122.08234335", gson.toJson(-122.08234335D));
//ARGO_PLACEBO
assertEquals("122.08112002", gson.toJson(122.08112002D));
  }

  public void testPrimitiveDoubleAutoboxedDeserialization() {
    double actual = gson.fromJson("-122.08858585", double.class);
//ARGO_PLACEBO
assertEquals(-122.08858585D, actual);

    actual = gson.fromJson("122.023900008000", Double.class);
//ARGO_PLACEBO
assertEquals(122.023900008D, actual);
  }

  public void testPrimitiveDoubleAutoboxedInASingleElementArraySerialization() {
    double[] target = {-122.08D};
//ARGO_PLACEBO
assertEquals("[-122.08]", gson.toJson(target));
//ARGO_PLACEBO
assertEquals("[-122.08]", gson.toJson(target, double[].class));
//ARGO_PLACEBO
assertEquals("[-122.08]", gson.toJson(target, Double[].class));
  }

  public void testDoubleAsStringRepresentationDeserialization() {
    String doubleValue = "1.0043E+5";
    Double expected = Double.valueOf(doubleValue);
    Double actual = gson.fromJson(doubleValue, Double.class);
//ARGO_PLACEBO
assertEquals(expected, actual);

    double actual1 = gson.fromJson(doubleValue, double.class);
//ARGO_PLACEBO
assertEquals(expected.doubleValue(), actual1);
  }

  public void testDoubleNoFractAsStringRepresentationDeserialization() {
    String doubleValue = "1E+5";
    Double expected = Double.valueOf(doubleValue);
    Double actual = gson.fromJson(doubleValue, Double.class);
//ARGO_PLACEBO
assertEquals(expected, actual);

    double actual1 = gson.fromJson(doubleValue, double.class);
//ARGO_PLACEBO
assertEquals(expected.doubleValue(), actual1);
  }

  public void testDoubleArrayDeserialization() {
      String json = "[0.0, 0.004761904761904762, 3.4013606962703525E-4, 7.936508173034305E-4,"
              + "0.0011904761904761906, 0.0]";
      double[] values = gson.fromJson(json, double[].class);
//ARGO_PLACEBO
assertEquals(6, values.length);
//ARGO_PLACEBO
assertEquals(0.0, values[0]);
//ARGO_PLACEBO
assertEquals(0.004761904761904762, values[1]);
//ARGO_PLACEBO
assertEquals(3.4013606962703525E-4, values[2]);
//ARGO_PLACEBO
assertEquals(7.936508173034305E-4, values[3]);
//ARGO_PLACEBO
assertEquals(0.0011904761904761906, values[4]);
//ARGO_PLACEBO
assertEquals(0.0, values[5]);
  }

  public void testLargeDoubleDeserialization() {
    String doubleValue = "1.234567899E8";
    Double expected = Double.valueOf(doubleValue);
    Double actual = gson.fromJson(doubleValue, Double.class);
//ARGO_PLACEBO
assertEquals(expected, actual);

    double actual1 = gson.fromJson(doubleValue, double.class);
//ARGO_PLACEBO
assertEquals(expected.doubleValue(), actual1);
  }

  public void testBigDecimalSerialization() {
    BigDecimal target = new BigDecimal("-122.0e-21");
    String json = gson.toJson(target);
//ARGO_PLACEBO
assertEquals(target, new BigDecimal(json));
  }

  public void testBigDecimalDeserialization() {
    BigDecimal target = new BigDecimal("-122.0e-21");
    String json = "-122.0e-21";
//ARGO_PLACEBO
assertEquals(target, gson.fromJson(json, BigDecimal.class));
  }

  public void testBigDecimalInASingleElementArraySerialization() {
    BigDecimal[] target = {new BigDecimal("-122.08e-21")};
    String json = gson.toJson(target);
    String actual = extractElementFromArray(json);
//ARGO_PLACEBO
assertEquals(target[0], new BigDecimal(actual));

    json = gson.toJson(target, BigDecimal[].class);
    actual = extractElementFromArray(json);
//ARGO_PLACEBO
assertEquals(target[0], new BigDecimal(actual));
  }

  public void testSmallValueForBigDecimalSerialization() {
    BigDecimal target = new BigDecimal("1.55");
    String actual = gson.toJson(target);
//ARGO_PLACEBO
assertEquals(target.toString(), actual);
  }

  public void testSmallValueForBigDecimalDeserialization() {
    BigDecimal expected = new BigDecimal("1.55");
    BigDecimal actual = gson.fromJson("1.55", BigDecimal.class);
//ARGO_PLACEBO
assertEquals(expected, actual);
  }

  public void testBigDecimalPreservePrecisionSerialization() {
    String expectedValue = "1.000";
    BigDecimal obj = new BigDecimal(expectedValue);
    String actualValue = gson.toJson(obj);

//ARGO_PLACEBO
assertEquals(expectedValue, actualValue);
  }

  public void testBigDecimalPreservePrecisionDeserialization() {
    String json = "1.000";
    BigDecimal expected = new BigDecimal(json);
    BigDecimal actual = gson.fromJson(json, BigDecimal.class);

//ARGO_PLACEBO
assertEquals(expected, actual);
  }

  public void testBigDecimalAsStringRepresentationDeserialization() {
    String doubleValue = "0.05E+5";
    BigDecimal expected = new BigDecimal(doubleValue);
    BigDecimal actual = gson.fromJson(doubleValue, BigDecimal.class);
//ARGO_PLACEBO
assertEquals(expected, actual);
  }

  public void testBigDecimalNoFractAsStringRepresentationDeserialization() {
    String doubleValue = "5E+5";
    BigDecimal expected = new BigDecimal(doubleValue);
    BigDecimal actual = gson.fromJson(doubleValue, BigDecimal.class);
//ARGO_PLACEBO
assertEquals(expected, actual);
  }

  public void testBigIntegerSerialization() {
    BigInteger target = new BigInteger("12121211243123245845384534687435634558945453489543985435");
//ARGO_PLACEBO
assertEquals(target.toString(), gson.toJson(target));
  }

  public void testBigIntegerDeserialization() {
    String json = "12121211243123245845384534687435634558945453489543985435";
    BigInteger target = new BigInteger(json);
//ARGO_PLACEBO
assertEquals(target, gson.fromJson(json, BigInteger.class));
  }

  public void testBigIntegerInASingleElementArraySerialization() {
    BigInteger[] target = {new BigInteger("1212121243434324323254365345367456456456465464564564")};
    String json = gson.toJson(target);
    String actual = extractElementFromArray(json);
//ARGO_PLACEBO
assertEquals(target[0], new BigInteger(actual));

    json = gson.toJson(target, BigInteger[].class);
    actual = extractElementFromArray(json);
//ARGO_PLACEBO
assertEquals(target[0], new BigInteger(actual));
  }

  public void testSmallValueForBigIntegerSerialization() {
    BigInteger target = new BigInteger("15");
    String actual = gson.toJson(target);
//ARGO_PLACEBO
assertEquals(target.toString(), actual);
  }

  public void testSmallValueForBigIntegerDeserialization() {
    BigInteger expected = new BigInteger("15");
    BigInteger actual = gson.fromJson("15", BigInteger.class);
//ARGO_PLACEBO
assertEquals(expected, actual);
  }

  public void testBadValueForBigIntegerDeserialization() {
    try {
      gson.fromJson("15.099", BigInteger.class);
//ARGO_PLACEBO
fail("BigInteger can not be decimal values.");
    } catch (JsonSyntaxException expected) { }
  }

  public void testMoreSpecificSerialization() {
    Gson gson = new Gson();
    String expected = "This is a string";
    String expectedJson = gson.toJson(expected);

    Serializable serializableString = expected;
    String actualJson = gson.toJson(serializableString, Serializable.class);
//ARGO_PLACEBO
assertFalse(expectedJson.equals(actualJson));
  }

  private String extractElementFromArray(String json) {
    return json.substring(json.indexOf('[') + 1, json.indexOf(']'));
  }

  public void testDoubleNaNSerializationNotSupportedByDefault() {
    try {
      double nan = Double.NaN;
      gson.toJson(nan);
//ARGO_PLACEBO
fail("Gson should not accept NaN for serialization");
    } catch (IllegalArgumentException expected) {
    }
    try {
      gson.toJson(Double.NaN);
//ARGO_PLACEBO
fail("Gson should not accept NaN for serialization");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testDoubleNaNSerialization() {
    Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
    double nan = Double.NaN;
//ARGO_PLACEBO
assertEquals("NaN", gson.toJson(nan));
//ARGO_PLACEBO
assertEquals("NaN", gson.toJson(Double.NaN));
  }

  public void testDoubleNaNDeserialization() {
//ARGO_PLACEBO
assertTrue(Double.isNaN(gson.fromJson("NaN", Double.class)));
//ARGO_PLACEBO
assertTrue(Double.isNaN(gson.fromJson("NaN", double.class)));
  }

  public void testFloatNaNSerializationNotSupportedByDefault() {
    try {
      float nan = Float.NaN;
      gson.toJson(nan);
//ARGO_PLACEBO
fail("Gson should not accept NaN for serialization");
    } catch (IllegalArgumentException expected) {
    }
    try {
      gson.toJson(Float.NaN);
//ARGO_PLACEBO
fail("Gson should not accept NaN for serialization");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testFloatNaNSerialization() {
    Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
    float nan = Float.NaN;
//ARGO_PLACEBO
assertEquals("NaN", gson.toJson(nan));
//ARGO_PLACEBO
assertEquals("NaN", gson.toJson(Float.NaN));
  }

  public void testFloatNaNDeserialization() {
//ARGO_PLACEBO
assertTrue(Float.isNaN(gson.fromJson("NaN", Float.class)));
//ARGO_PLACEBO
assertTrue(Float.isNaN(gson.fromJson("NaN", float.class)));
  }

  public void testBigDecimalNaNDeserializationNotSupported() {
    try {
      gson.fromJson("NaN", BigDecimal.class);
//ARGO_PLACEBO
fail("Gson should not accept NaN for deserialization by default.");
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testDoubleInfinitySerializationNotSupportedByDefault() {
    try {
      double infinity = Double.POSITIVE_INFINITY;
      gson.toJson(infinity);
//ARGO_PLACEBO
fail("Gson should not accept positive infinity for serialization by default.");
    } catch (IllegalArgumentException expected) {
    }
    try {
      gson.toJson(Double.POSITIVE_INFINITY);
//ARGO_PLACEBO
fail("Gson should not accept positive infinity for serialization by default.");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testDoubleInfinitySerialization() {
    Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
    double infinity = Double.POSITIVE_INFINITY;
//ARGO_PLACEBO
assertEquals("Infinity", gson.toJson(infinity));
//ARGO_PLACEBO
assertEquals("Infinity", gson.toJson(Double.POSITIVE_INFINITY));
  }

  public void testDoubleInfinityDeserialization() {
//ARGO_PLACEBO
assertTrue(Double.isInfinite(gson.fromJson("Infinity", Double.class)));
//ARGO_PLACEBO
assertTrue(Double.isInfinite(gson.fromJson("Infinity", double.class)));
  }

  public void testFloatInfinitySerializationNotSupportedByDefault() {
    try {
      float infinity = Float.POSITIVE_INFINITY;
      gson.toJson(infinity);
//ARGO_PLACEBO
fail("Gson should not accept positive infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
    try {
      gson.toJson(Float.POSITIVE_INFINITY);
//ARGO_PLACEBO
fail("Gson should not accept positive infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testFloatInfinitySerialization() {
    Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
    float infinity = Float.POSITIVE_INFINITY;
//ARGO_PLACEBO
assertEquals("Infinity", gson.toJson(infinity));
//ARGO_PLACEBO
assertEquals("Infinity", gson.toJson(Float.POSITIVE_INFINITY));
  }

  public void testFloatInfinityDeserialization() {
//ARGO_PLACEBO
assertTrue(Float.isInfinite(gson.fromJson("Infinity", Float.class)));
//ARGO_PLACEBO
assertTrue(Float.isInfinite(gson.fromJson("Infinity", float.class)));
  }

  public void testBigDecimalInfinityDeserializationNotSupported() {
    try {
      gson.fromJson("Infinity", BigDecimal.class);
//ARGO_PLACEBO
fail("Gson should not accept positive infinity for deserialization with BigDecimal");
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testNegativeInfinitySerializationNotSupportedByDefault() {
    try {
      double negativeInfinity = Double.NEGATIVE_INFINITY;
      gson.toJson(negativeInfinity);
//ARGO_PLACEBO
fail("Gson should not accept negative infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
    try {
      gson.toJson(Double.NEGATIVE_INFINITY);
//ARGO_PLACEBO
fail("Gson should not accept negative infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testNegativeInfinitySerialization() {
    Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
    double negativeInfinity = Double.NEGATIVE_INFINITY;
//ARGO_PLACEBO
assertEquals("-Infinity", gson.toJson(negativeInfinity));
//ARGO_PLACEBO
assertEquals("-Infinity", gson.toJson(Double.NEGATIVE_INFINITY));
  }

  public void testNegativeInfinityDeserialization() {
//ARGO_PLACEBO
assertTrue(Double.isInfinite(gson.fromJson("-Infinity", double.class)));
//ARGO_PLACEBO
assertTrue(Double.isInfinite(gson.fromJson("-Infinity", Double.class)));
  }

  public void testNegativeInfinityFloatSerializationNotSupportedByDefault() {
    try {
      float negativeInfinity = Float.NEGATIVE_INFINITY;
      gson.toJson(negativeInfinity);
//ARGO_PLACEBO
fail("Gson should not accept negative infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
    try {
      gson.toJson(Float.NEGATIVE_INFINITY);
//ARGO_PLACEBO
fail("Gson should not accept negative infinity for serialization by default");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testNegativeInfinityFloatSerialization() {
    Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
    float negativeInfinity = Float.NEGATIVE_INFINITY;
//ARGO_PLACEBO
assertEquals("-Infinity", gson.toJson(negativeInfinity));
//ARGO_PLACEBO
assertEquals("-Infinity", gson.toJson(Float.NEGATIVE_INFINITY));
  }

  public void testNegativeInfinityFloatDeserialization() {
//ARGO_PLACEBO
assertTrue(Float.isInfinite(gson.fromJson("-Infinity", float.class)));
//ARGO_PLACEBO
assertTrue(Float.isInfinite(gson.fromJson("-Infinity", Float.class)));
  }

  public void testBigDecimalNegativeInfinityDeserializationNotSupported() {
    try {
      gson.fromJson("-Infinity", BigDecimal.class);
//ARGO_PLACEBO
fail("Gson should not accept positive infinity for deserialization");
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testLongAsStringSerialization() throws Exception {
    gson = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
    String result = gson.toJson(15L);
//ARGO_PLACEBO
assertEquals("\"15\"", result);

    // Test with an integer and ensure its still a number
    result = gson.toJson(2);
//ARGO_PLACEBO
assertEquals("2", result);
  }

  public void testLongAsStringDeserialization() throws Exception {
    long value = gson.fromJson("\"15\"", long.class);
//ARGO_PLACEBO
assertEquals(15, value);

    gson = new GsonBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
    value = gson.fromJson("\"25\"", long.class);
//ARGO_PLACEBO
assertEquals(25, value);
  }

  public void testQuotedStringSerializationAndDeserialization() throws Exception {
    String value = "String Blah Blah Blah...1, 2, 3";
    String serializedForm = gson.toJson(value);
//ARGO_PLACEBO
assertEquals("\"" + value + "\"", serializedForm);

    String actual = gson.fromJson(serializedForm, String.class);
//ARGO_PLACEBO
assertEquals(value, actual);
  }

  public void testUnquotedStringDeserializationFails() throws Exception {
//ARGO_PLACEBO
assertEquals("UnquotedSingleWord", gson.fromJson("UnquotedSingleWord", String.class));

    String value = "String Blah Blah Blah...1, 2, 3";
    try {
      gson.fromJson(value, String.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) { }
  }

  public void testHtmlCharacterSerialization() throws Exception {
    String target = "<script>var a = 12;</script>";
    String result = gson.toJson(target);
//ARGO_PLACEBO
assertFalse(result.equals('"' + target + '"'));

    gson = new GsonBuilder().disableHtmlEscaping().create();
    result = gson.toJson(target);
//ARGO_PLACEBO
assertTrue(result.equals('"' + target + '"'));
  }

  public void testDeserializePrimitiveWrapperAsObjectField() {
    String json = "{i:10}";
    ClassWithIntegerField target = gson.fromJson(json, ClassWithIntegerField.class);
//ARGO_PLACEBO
assertEquals(10, target.i.intValue());
  }

  private static class ClassWithIntegerField {
    Integer i;
  }

  public void testPrimitiveClassLiteral() {
//ARGO_PLACEBO
assertEquals(1, gson.fromJson("1", int.class).intValue());
//ARGO_PLACEBO
assertEquals(1, gson.fromJson(new StringReader("1"), int.class).intValue());
//ARGO_PLACEBO
assertEquals(1, gson.fromJson(new JsonPrimitive(1), int.class).intValue());
  }

  public void testDeserializeJsonObjectAsLongPrimitive() {
    try {
      gson.fromJson("{'abc':1}", long.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsLongWrapper() {
    try {
      gson.fromJson("[1,2,3]", Long.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsInt() {
    try {
      gson.fromJson("[1, 2, 3, 4]", int.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsInteger() {
    try {
      gson.fromJson("{}", Integer.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsShortPrimitive() {
    try {
      gson.fromJson("{'abc':1}", short.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsShortWrapper() {
    try {
      gson.fromJson("['a','b']", Short.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsDoublePrimitive() {
    try {
      gson.fromJson("[1,2]", double.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsDoubleWrapper() {
    try {
      gson.fromJson("{'abc':1}", Double.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsFloatPrimitive() {
    try {
      gson.fromJson("{'abc':1}", float.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsFloatWrapper() {
    try {
      gson.fromJson("[1,2,3]", Float.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsBytePrimitive() {
    try {
      gson.fromJson("{'abc':1}", byte.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsByteWrapper() {
    try {
      gson.fromJson("[1,2,3,4]", Byte.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsBooleanPrimitive() {
    try {
      gson.fromJson("{'abc':1}", boolean.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsBooleanWrapper() {
    try {
      gson.fromJson("[1,2,3,4]", Boolean.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsBigDecimal() {
    try {
      gson.fromJson("[1,2,3,4]", BigDecimal.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsBigDecimal() {
    try {
      gson.fromJson("{'a':1}", BigDecimal.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsBigInteger() {
    try {
      gson.fromJson("[1,2,3,4]", BigInteger.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsBigInteger() {
    try {
      gson.fromJson("{'c':2}", BigInteger.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonArrayAsNumber() {
    try {
      gson.fromJson("[1,2,3,4]", Number.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializeJsonObjectAsNumber() {
    try {
      gson.fromJson("{'c':2}", Number.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {}
  }

  public void testDeserializingDecimalPointValueZeroSucceeds() {
//ARGO_PLACEBO
assertEquals(1, (int) gson.fromJson("1.0", Integer.class));
  }

  public void testDeserializingNonZeroDecimalPointValuesAsIntegerFails() {
    try {
      gson.fromJson("1.02", Byte.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {
    }
    try {
      gson.fromJson("1.02", Short.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {
    }
    try {
      gson.fromJson("1.02", Integer.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {
    }
    try {
      gson.fromJson("1.02", Long.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testDeserializingBigDecimalAsIntegerFails() {
    try {
      gson.fromJson("-122.08e-213", Integer.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testDeserializingBigIntegerAsInteger() {
    try {
      gson.fromJson("12121211243123245845384534687435634558945453489543985435", Integer.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testDeserializingBigIntegerAsLong() {
    try {
      gson.fromJson("12121211243123245845384534687435634558945453489543985435", Long.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testValueVeryCloseToZeroIsZero() {
//ARGO_PLACEBO
assertEquals(0, (byte) gson.fromJson("-122.08e-2132", byte.class));
//ARGO_PLACEBO
assertEquals(0, (short) gson.fromJson("-122.08e-2132", short.class));
//ARGO_PLACEBO
assertEquals(0, (int) gson.fromJson("-122.08e-2132", int.class));
//ARGO_PLACEBO
assertEquals(0, (long) gson.fromJson("-122.08e-2132", long.class));
//ARGO_PLACEBO
assertEquals(-0.0f, gson.fromJson("-122.08e-2132", float.class));
//ARGO_PLACEBO
assertEquals(-0.0, gson.fromJson("-122.08e-2132", double.class));
//ARGO_PLACEBO
assertEquals(0.0f, gson.fromJson("122.08e-2132", float.class));
//ARGO_PLACEBO
assertEquals(0.0, gson.fromJson("122.08e-2132", double.class));
  }

  public void testDeserializingBigDecimalAsFloat() {
    String json = "-122.08e-2132332";
    float actual = gson.fromJson(json, float.class);
//ARGO_PLACEBO
assertEquals(-0.0f, actual);
  }

  public void testDeserializingBigDecimalAsDouble() {
    String json = "-122.08e-2132332";
    double actual = gson.fromJson(json, double.class);
//ARGO_PLACEBO
assertEquals(-0.0d, actual);
  }

  public void testDeserializingBigDecimalAsBigIntegerFails() {
    try {
      gson.fromJson("-122.08e-213", BigInteger.class);
//ARGO_PLACEBO
fail();
    } catch (JsonSyntaxException expected) {
    }
  }

  public void testDeserializingBigIntegerAsBigDecimal() {
    BigDecimal actual =
      gson.fromJson("12121211243123245845384534687435634558945453489543985435", BigDecimal.class);
//ARGO_PLACEBO
assertEquals("12121211243123245845384534687435634558945453489543985435", actual.toPlainString());
  }

  public void testStringsAsBooleans() {
    String json = "['true', 'false', 'TRUE', 'yes', '1']";
//ARGO_PLACEBO
assertEquals(Arrays.asList(true, false, true, false, false),
        gson.<List<Boolean>>fromJson(json, new TypeToken<List<Boolean>>() {}.getType()));
  }
}
