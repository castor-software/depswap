/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.stream;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import junit.framework.TestCase;

import static com.google.gson.stream.JsonToken.BEGIN_ARRAY;
import static com.google.gson.stream.JsonToken.BEGIN_OBJECT;
import static com.google.gson.stream.JsonToken.BOOLEAN;
import static com.google.gson.stream.JsonToken.END_ARRAY;
import static com.google.gson.stream.JsonToken.END_OBJECT;
import static com.google.gson.stream.JsonToken.NAME;
import static com.google.gson.stream.JsonToken.NULL;
import static com.google.gson.stream.JsonToken.NUMBER;
import static com.google.gson.stream.JsonToken.STRING;

@SuppressWarnings("resource")
public final class JsonReaderTest extends TestCase {
  public void testReadArray() throws IOException {
    JsonReader reader = new JsonReader(reader("[true, true]"));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    reader.endArray();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testReadEmptyArray() throws IOException {
    JsonReader reader = new JsonReader(reader("[]"));
    reader.beginArray();
//ARGO_PLACEBO
assertFalse(reader.hasNext());
    reader.endArray();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testReadObject() throws IOException {
    JsonReader reader = new JsonReader(reader(
        "{\"a\": \"android\", \"b\": \"banana\"}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
//ARGO_PLACEBO
assertEquals("android", reader.nextString());
//ARGO_PLACEBO
assertEquals("b", reader.nextName());
//ARGO_PLACEBO
assertEquals("banana", reader.nextString());
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testReadEmptyObject() throws IOException {
    JsonReader reader = new JsonReader(reader("{}"));
    reader.beginObject();
//ARGO_PLACEBO
assertFalse(reader.hasNext());
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipArray() throws IOException {
    JsonReader reader = new JsonReader(reader(
        "{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    reader.skipValue();
//ARGO_PLACEBO
assertEquals("b", reader.nextName());
//ARGO_PLACEBO
assertEquals(123, reader.nextInt());
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipArrayAfterPeek() throws Exception {
    JsonReader reader = new JsonReader(reader(
        "{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
//ARGO_PLACEBO
assertEquals(BEGIN_ARRAY, reader.peek());
    reader.skipValue();
//ARGO_PLACEBO
assertEquals("b", reader.nextName());
//ARGO_PLACEBO
assertEquals(123, reader.nextInt());
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipTopLevelObject() throws Exception {
    JsonReader reader = new JsonReader(reader(
        "{\"a\": [\"one\", \"two\", \"three\"], \"b\": 123}"));
    reader.skipValue();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipObject() throws IOException {
    JsonReader reader = new JsonReader(reader(
        "{\"a\": { \"c\": [], \"d\": [true, true, {}] }, \"b\": \"banana\"}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    reader.skipValue();
//ARGO_PLACEBO
assertEquals("b", reader.nextName());
    reader.skipValue();
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipObjectAfterPeek() throws Exception {
    String json = "{" + "  \"one\": { \"num\": 1 }"
        + ", \"two\": { \"num\": 2 }" + ", \"three\": { \"num\": 3 }" + "}";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("one", reader.nextName());
//ARGO_PLACEBO
assertEquals(BEGIN_OBJECT, reader.peek());
    reader.skipValue();
//ARGO_PLACEBO
assertEquals("two", reader.nextName());
//ARGO_PLACEBO
assertEquals(BEGIN_OBJECT, reader.peek());
    reader.skipValue();
//ARGO_PLACEBO
assertEquals("three", reader.nextName());
    reader.skipValue();
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipInteger() throws IOException {
    JsonReader reader = new JsonReader(reader(
        "{\"a\":123456789,\"b\":-123456789}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    reader.skipValue();
//ARGO_PLACEBO
assertEquals("b", reader.nextName());
    reader.skipValue();
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipDouble() throws IOException {
    JsonReader reader = new JsonReader(reader(
        "{\"a\":-123.456e-789,\"b\":123456789.0}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    reader.skipValue();
//ARGO_PLACEBO
assertEquals("b", reader.nextName());
    reader.skipValue();
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testHelloWorld() throws IOException {
    String json = "{\n" +
        "   \"hello\": true,\n" +
        "   \"foo\": [\"world\"]\n" +
        "}";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("hello", reader.nextName());
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
//ARGO_PLACEBO
assertEquals("foo", reader.nextName());
    reader.beginArray();
//ARGO_PLACEBO
assertEquals("world", reader.nextString());
    reader.endArray();
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testInvalidJsonInput() throws IOException {
    String json = "{\n"
        + "   \"h\\ello\": true,\n"
        + "   \"foo\": [\"world\"]\n"
        + "}";

    JsonReader reader = new JsonReader(reader(json));
    reader.beginObject();
    try {
      reader.nextName();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }
  
  @SuppressWarnings("unused")
  public void testNulls() {
    try {
      new JsonReader(null);
//ARGO_PLACEBO
fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testEmptyString() {
    try {
      new JsonReader(reader("")).beginArray();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
    try {
      new JsonReader(reader("")).beginObject();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testCharacterUnescaping() throws IOException {
    String json = "[\"a\","
        + "\"a\\\"\","
        + "\"\\\"\","
        + "\":\","
        + "\",\","
        + "\"\\b\","
        + "\"\\f\","
        + "\"\\n\","
        + "\"\\r\","
        + "\"\\t\","
        + "\" \","
        + "\"\\\\\","
        + "\"{\","
        + "\"}\","
        + "\"[\","
        + "\"]\","
        + "\"\\u0000\","
        + "\"\\u0019\","
        + "\"\\u20AC\""
        + "]";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals("a", reader.nextString());
//ARGO_PLACEBO
assertEquals("a\"", reader.nextString());
//ARGO_PLACEBO
assertEquals("\"", reader.nextString());
//ARGO_PLACEBO
assertEquals(":", reader.nextString());
//ARGO_PLACEBO
assertEquals(",", reader.nextString());
//ARGO_PLACEBO
assertEquals("\b", reader.nextString());
//ARGO_PLACEBO
assertEquals("\f", reader.nextString());
//ARGO_PLACEBO
assertEquals("\n", reader.nextString());
//ARGO_PLACEBO
assertEquals("\r", reader.nextString());
//ARGO_PLACEBO
assertEquals("\t", reader.nextString());
//ARGO_PLACEBO
assertEquals(" ", reader.nextString());
//ARGO_PLACEBO
assertEquals("\\", reader.nextString());
//ARGO_PLACEBO
assertEquals("{", reader.nextString());
//ARGO_PLACEBO
assertEquals("}", reader.nextString());
//ARGO_PLACEBO
assertEquals("[", reader.nextString());
//ARGO_PLACEBO
assertEquals("]", reader.nextString());
//ARGO_PLACEBO
assertEquals("\0", reader.nextString());
//ARGO_PLACEBO
assertEquals("\u0019", reader.nextString());
//ARGO_PLACEBO
assertEquals("\u20AC", reader.nextString());
    reader.endArray();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testUnescapingInvalidCharacters() throws IOException {
    String json = "[\"\\u000g\"]";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
    try {
      reader.nextString();
//ARGO_PLACEBO
fail();
    } catch (NumberFormatException expected) {
    }
  }

  public void testUnescapingTruncatedCharacters() throws IOException {
    String json = "[\"\\u000";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
    try {
      reader.nextString();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testUnescapingTruncatedSequence() throws IOException {
    String json = "[\"\\";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
    try {
      reader.nextString();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testIntegersWithFractionalPartSpecified() throws IOException {
    JsonReader reader = new JsonReader(reader("[1.0,1.0,1.0]"));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(1.0, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(1, reader.nextInt());
//ARGO_PLACEBO
assertEquals(1L, reader.nextLong());
  }

  public void testDoubles() throws IOException {
    String json = "[-0.0,"
        + "1.0,"
        + "1.7976931348623157E308,"
        + "4.9E-324,"
        + "0.0,"
        + "-0.5,"
        + "2.2250738585072014E-308,"
        + "3.141592653589793,"
        + "2.718281828459045]";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(-0.0, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(1.0, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(1.7976931348623157E308, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(4.9E-324, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(0.0, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(-0.5, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(2.2250738585072014E-308, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(3.141592653589793, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(2.718281828459045, reader.nextDouble());
    reader.endArray();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testStrictNonFiniteDoubles() throws IOException {
    String json = "[NaN]";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
    try {
      reader.nextDouble();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictQuotedNonFiniteDoubles() throws IOException {
    String json = "[\"NaN\"]";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
    try {
      reader.nextDouble();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientNonFiniteDoubles() throws IOException {
    String json = "[NaN, -Infinity, Infinity]";
    JsonReader reader = new JsonReader(reader(json));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertTrue(Double.isNaN(reader.nextDouble()));
//ARGO_PLACEBO
assertEquals(Double.NEGATIVE_INFINITY, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(Double.POSITIVE_INFINITY, reader.nextDouble());
    reader.endArray();
  }

  public void testLenientQuotedNonFiniteDoubles() throws IOException {
    String json = "[\"NaN\", \"-Infinity\", \"Infinity\"]";
    JsonReader reader = new JsonReader(reader(json));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertTrue(Double.isNaN(reader.nextDouble()));
//ARGO_PLACEBO
assertEquals(Double.NEGATIVE_INFINITY, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(Double.POSITIVE_INFINITY, reader.nextDouble());
    reader.endArray();
  }

  public void testStrictNonFiniteDoublesWithSkipValue() throws IOException {
    String json = "[NaN]";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLongs() throws IOException {
    String json = "[0,0,0,"
        + "1,1,1,"
        + "-1,-1,-1,"
        + "-9223372036854775808,"
        + "9223372036854775807]";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(0L, reader.nextLong());
//ARGO_PLACEBO
assertEquals(0, reader.nextInt());
//ARGO_PLACEBO
assertEquals(0.0, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(1L, reader.nextLong());
//ARGO_PLACEBO
assertEquals(1, reader.nextInt());
//ARGO_PLACEBO
assertEquals(1.0, reader.nextDouble());
//ARGO_PLACEBO
assertEquals(-1L, reader.nextLong());
//ARGO_PLACEBO
assertEquals(-1, reader.nextInt());
//ARGO_PLACEBO
assertEquals(-1.0, reader.nextDouble());
    try {
      reader.nextInt();
//ARGO_PLACEBO
fail();
    } catch (NumberFormatException expected) {
    }
//ARGO_PLACEBO
assertEquals(Long.MIN_VALUE, reader.nextLong());
    try {
      reader.nextInt();
//ARGO_PLACEBO
fail();
    } catch (NumberFormatException expected) {
    }
//ARGO_PLACEBO
assertEquals(Long.MAX_VALUE, reader.nextLong());
    reader.endArray();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void disabled_testNumberWithOctalPrefix() throws IOException {
    String json = "[01]";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
    try {
      reader.peek();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
    try {
      reader.nextInt();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
    try {
      reader.nextLong();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
    try {
      reader.nextDouble();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
//ARGO_PLACEBO
assertEquals("01", reader.nextString());
    reader.endArray();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testBooleans() throws IOException {
    JsonReader reader = new JsonReader(reader("[true,false]"));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
//ARGO_PLACEBO
assertEquals(false, reader.nextBoolean());
    reader.endArray();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testPeekingUnquotedStringsPrefixedWithBooleans() throws IOException {
    JsonReader reader = new JsonReader(reader("[truey]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(STRING, reader.peek());
    try {
      reader.nextBoolean();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
//ARGO_PLACEBO
assertEquals("truey", reader.nextString());
    reader.endArray();
  }

  public void testMalformedNumbers() throws IOException {
//ARGO_PLACEBO
assertNotANumber("-");
//ARGO_PLACEBO
assertNotANumber(".");

    // exponent lacks digit
//ARGO_PLACEBO
assertNotANumber("e");
//ARGO_PLACEBO
assertNotANumber("0e");
//ARGO_PLACEBO
assertNotANumber(".e");
//ARGO_PLACEBO
assertNotANumber("0.e");
//ARGO_PLACEBO
assertNotANumber("-.0e");

    // no integer
//ARGO_PLACEBO
assertNotANumber("e1");
//ARGO_PLACEBO
assertNotANumber(".e1");
//ARGO_PLACEBO
assertNotANumber("-e1");

    // trailing characters
//ARGO_PLACEBO
assertNotANumber("1x");
//ARGO_PLACEBO
assertNotANumber("1.1x");
//ARGO_PLACEBO
assertNotANumber("1e1x");
//ARGO_PLACEBO
assertNotANumber("1ex");
//ARGO_PLACEBO
assertNotANumber("1.1ex");
//ARGO_PLACEBO
assertNotANumber("1.1e1x");

    // fraction has no digit
//ARGO_PLACEBO
assertNotANumber("0.");
//ARGO_PLACEBO
assertNotANumber("-0.");
//ARGO_PLACEBO
assertNotANumber("0.e1");
//ARGO_PLACEBO
assertNotANumber("-0.e1");

    // no leading digit
//ARGO_PLACEBO
assertNotANumber(".0");
//ARGO_PLACEBO
assertNotANumber("-.0");
//ARGO_PLACEBO
assertNotANumber(".0e1");
//ARGO_PLACEBO
assertNotANumber("-.0e1");
  }

  private void//ARGO_PLACEBO
assertNotANumber(String s) throws IOException {
    JsonReader reader = new JsonReader(reader("[" + s + "]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(JsonToken.STRING, reader.peek());
//ARGO_PLACEBO
assertEquals(s, reader.nextString());
    reader.endArray();
  }

  public void testPeekingUnquotedStringsPrefixedWithIntegers() throws IOException {
    JsonReader reader = new JsonReader(reader("[12.34e5x]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(STRING, reader.peek());
    try {
      reader.nextInt();
//ARGO_PLACEBO
fail();
    } catch (NumberFormatException expected) {
    }
//ARGO_PLACEBO
assertEquals("12.34e5x", reader.nextString());
  }

  public void testPeekLongMinValue() throws IOException {
    JsonReader reader = new JsonReader(reader("[-9223372036854775808]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(NUMBER, reader.peek());
//ARGO_PLACEBO
assertEquals(-9223372036854775808L, reader.nextLong());
  }

  public void testPeekLongMaxValue() throws IOException {
    JsonReader reader = new JsonReader(reader("[9223372036854775807]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(NUMBER, reader.peek());
//ARGO_PLACEBO
assertEquals(9223372036854775807L, reader.nextLong());
  }

  public void testLongLargerThanMaxLongThatWrapsAround() throws IOException {
    JsonReader reader = new JsonReader(reader("[22233720368547758070]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(NUMBER, reader.peek());
    try {
      reader.nextLong();
//ARGO_PLACEBO
fail();
    } catch (NumberFormatException expected) {
    }
  }

  public void testLongLargerThanMinLongThatWrapsAround() throws IOException {
    JsonReader reader = new JsonReader(reader("[-22233720368547758070]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(NUMBER, reader.peek());
    try {
      reader.nextLong();
//ARGO_PLACEBO
fail();
    } catch (NumberFormatException expected) {
    }
  }
  
  /**
   * Issue 1053, negative zero.
   * @throws Exception
   */
  public void testNegativeZero() throws Exception {
	  	JsonReader reader = new JsonReader(reader("[-0]"));
	    reader.setLenient(false);
	    reader.beginArray();
//ARGO_PLACEBO
assertEquals(NUMBER, reader.peek());
//ARGO_PLACEBO
assertEquals("-0", reader.nextString());
  }

  /**
   * This test fails because there's no double for 9223372036854775808, and our
   * long parsing uses Double.parseDouble() for fractional values.
   */
  public void disabled_testPeekLargerThanLongMaxValue() throws IOException {
    JsonReader reader = new JsonReader(reader("[9223372036854775808]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(NUMBER, reader.peek());
    try {
      reader.nextLong();
//ARGO_PLACEBO
fail();
    } catch (NumberFormatException e) {
    }
  }

  /**
   * This test fails because there's no double for -9223372036854775809, and our
   * long parsing uses Double.parseDouble() for fractional values.
   */
  public void disabled_testPeekLargerThanLongMinValue() throws IOException {
    JsonReader reader = new JsonReader(reader("[-9223372036854775809]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(NUMBER, reader.peek());
    try {
      reader.nextLong();
//ARGO_PLACEBO
fail();
    } catch (NumberFormatException expected) {
    }
//ARGO_PLACEBO
assertEquals(-9223372036854775809d, reader.nextDouble());
  }

  /**
   * This test fails because there's no double for 9223372036854775806, and
   * our long parsing uses Double.parseDouble() for fractional values.
   */
  public void disabled_testHighPrecisionLong() throws IOException {
    String json = "[9223372036854775806.000]";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(9223372036854775806L, reader.nextLong());
    reader.endArray();
  }

  public void testPeekMuchLargerThanLongMinValue() throws IOException {
    JsonReader reader = new JsonReader(reader("[-92233720368547758080]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(NUMBER, reader.peek());
    try {
      reader.nextLong();
//ARGO_PLACEBO
fail();
    } catch (NumberFormatException expected) {
    }
//ARGO_PLACEBO
assertEquals(-92233720368547758080d, reader.nextDouble());
  }

  public void testQuotedNumberWithEscape() throws IOException {
    JsonReader reader = new JsonReader(reader("[\"12\u00334\"]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(STRING, reader.peek());
//ARGO_PLACEBO
assertEquals(1234, reader.nextInt());
  }

  public void testMixedCaseLiterals() throws IOException {
    JsonReader reader = new JsonReader(reader("[True,TruE,False,FALSE,NULL,nulL]"));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
//ARGO_PLACEBO
assertEquals(false, reader.nextBoolean());
//ARGO_PLACEBO
assertEquals(false, reader.nextBoolean());
    reader.nextNull();
    reader.nextNull();
    reader.endArray();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testMissingValue() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\":}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    try {
      reader.nextString();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testPrematureEndOfInput() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\":true,"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    try {
      reader.nextName();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testPrematurelyClosed() throws IOException {
    try {
      JsonReader reader = new JsonReader(reader("{\"a\":[]}"));
      reader.beginObject();
      reader.close();
      reader.nextName();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }

    try {
      JsonReader reader = new JsonReader(reader("{\"a\":[]}"));
      reader.close();
      reader.beginObject();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }

    try {
      JsonReader reader = new JsonReader(reader("{\"a\":true}"));
      reader.beginObject();
      reader.nextName();
      reader.peek();
      reader.close();
      reader.nextBoolean();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
  }

  public void testNextFailuresDoNotAdvance() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\":true}"));
    reader.beginObject();
    try {
      reader.nextString();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    try {
      reader.nextName();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.beginArray();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.endArray();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.beginObject();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.endObject();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    try {
      reader.nextString();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextName();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.beginArray();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.endArray();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
    reader.close();
  }

  public void testIntegerMismatchFailuresDoNotAdvance() throws IOException {
    JsonReader reader = new JsonReader(reader("[1.5]"));
    reader.beginArray();
    try {
      reader.nextInt();
//ARGO_PLACEBO
fail();
    } catch (NumberFormatException expected) {
    }
//ARGO_PLACEBO
assertEquals(1.5d, reader.nextDouble());
    reader.endArray();
  }

  public void testStringNullIsNotNull() throws IOException {
    JsonReader reader = new JsonReader(reader("[\"null\"]"));
    reader.beginArray();
    try {
      reader.nextNull();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
  }

  public void testNullLiteralIsNotAString() throws IOException {
    JsonReader reader = new JsonReader(reader("[null]"));
    reader.beginArray();
    try {
      reader.nextString();
//ARGO_PLACEBO
fail();
    } catch (IllegalStateException expected) {
    }
  }

  public void testStrictNameValueSeparator() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\"=true}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    try {
      reader.nextBoolean();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("{\"a\"=>true}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    try {
      reader.nextBoolean();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testLenientNameValueSeparator() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\"=true}"));
    reader.setLenient(true);
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());

    reader = new JsonReader(reader("{\"a\"=>true}"));
    reader.setLenient(true);
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
  }

  public void testStrictNameValueSeparatorWithSkipValue() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\"=true}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("{\"a\"=>true}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testCommentsInStringValue() throws Exception {
    JsonReader reader = new JsonReader(reader("[\"// comment\"]"));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals("// comment", reader.nextString());
    reader.endArray();

    reader = new JsonReader(reader("{\"a\":\"#someComment\"}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
//ARGO_PLACEBO
assertEquals("#someComment", reader.nextString());
    reader.endObject();

    reader = new JsonReader(reader("{\"#//a\":\"#some //Comment\"}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("#//a", reader.nextName());
//ARGO_PLACEBO
assertEquals("#some //Comment", reader.nextString());
    reader.endObject();
  }

  public void testStrictComments() throws IOException {
    JsonReader reader = new JsonReader(reader("[// comment \n true]"));
    reader.beginArray();
    try {
      reader.nextBoolean();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("[# comment \n true]"));
    reader.beginArray();
    try {
      reader.nextBoolean();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("[/* comment */ true]"));
    reader.beginArray();
    try {
      reader.nextBoolean();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testLenientComments() throws IOException {
    JsonReader reader = new JsonReader(reader("[// comment \n true]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());

    reader = new JsonReader(reader("[# comment \n true]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());

    reader = new JsonReader(reader("[/* comment */ true]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
  }

  public void testStrictCommentsWithSkipValue() throws IOException {
    JsonReader reader = new JsonReader(reader("[// comment \n true]"));
    reader.beginArray();
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("[# comment \n true]"));
    reader.beginArray();
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("[/* comment */ true]"));
    reader.beginArray();
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testStrictUnquotedNames() throws IOException {
    JsonReader reader = new JsonReader(reader("{a:true}"));
    reader.beginObject();
    try {
      reader.nextName();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testLenientUnquotedNames() throws IOException {
    JsonReader reader = new JsonReader(reader("{a:true}"));
    reader.setLenient(true);
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
  }

  public void testStrictUnquotedNamesWithSkipValue() throws IOException {
    JsonReader reader = new JsonReader(reader("{a:true}"));
    reader.beginObject();
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testStrictSingleQuotedNames() throws IOException {
    JsonReader reader = new JsonReader(reader("{'a':true}"));
    reader.beginObject();
    try {
      reader.nextName();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testLenientSingleQuotedNames() throws IOException {
    JsonReader reader = new JsonReader(reader("{'a':true}"));
    reader.setLenient(true);
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
  }

  public void testStrictSingleQuotedNamesWithSkipValue() throws IOException {
    JsonReader reader = new JsonReader(reader("{'a':true}"));
    reader.beginObject();
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testStrictUnquotedStrings() throws IOException {
    JsonReader reader = new JsonReader(reader("[a]"));
    reader.beginArray();
    try {
      reader.nextString();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStrictUnquotedStringsWithSkipValue() throws IOException {
    JsonReader reader = new JsonReader(reader("[a]"));
    reader.beginArray();
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientUnquotedStrings() throws IOException {
    JsonReader reader = new JsonReader(reader("[a]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals("a", reader.nextString());
  }

  public void testStrictSingleQuotedStrings() throws IOException {
    JsonReader reader = new JsonReader(reader("['a']"));
    reader.beginArray();
    try {
      reader.nextString();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testLenientSingleQuotedStrings() throws IOException {
    JsonReader reader = new JsonReader(reader("['a']"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals("a", reader.nextString());
  }

  public void testStrictSingleQuotedStringsWithSkipValue() throws IOException {
    JsonReader reader = new JsonReader(reader("['a']"));
    reader.beginArray();
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testStrictSemicolonDelimitedArray() throws IOException {
    JsonReader reader = new JsonReader(reader("[true;true]"));
    reader.beginArray();
    try {
      reader.nextBoolean();
      reader.nextBoolean();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testLenientSemicolonDelimitedArray() throws IOException {
    JsonReader reader = new JsonReader(reader("[true;true]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
  }

  public void testStrictSemicolonDelimitedArrayWithSkipValue() throws IOException {
    JsonReader reader = new JsonReader(reader("[true;true]"));
    reader.beginArray();
    try {
      reader.skipValue();
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testStrictSemicolonDelimitedNameValuePair() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\":true;\"b\":true}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    try {
      reader.nextBoolean();
      reader.nextName();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testLenientSemicolonDelimitedNameValuePair() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\":true;\"b\":true}"));
    reader.setLenient(true);
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
//ARGO_PLACEBO
assertEquals("b", reader.nextName());
  }

  public void testStrictSemicolonDelimitedNameValuePairWithSkipValue() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\":true;\"b\":true}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    try {
      reader.skipValue();
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testStrictUnnecessaryArraySeparators() throws IOException {
    JsonReader reader = new JsonReader(reader("[true,,true]"));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    try {
      reader.nextNull();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("[,true]"));
    reader.beginArray();
    try {
      reader.nextNull();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("[true,]"));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    try {
      reader.nextNull();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("[,]"));
    reader.beginArray();
    try {
      reader.nextNull();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testLenientUnnecessaryArraySeparators() throws IOException {
    JsonReader reader = new JsonReader(reader("[true,,true]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    reader.nextNull();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    reader.endArray();

    reader = new JsonReader(reader("[,true]"));
    reader.setLenient(true);
    reader.beginArray();
    reader.nextNull();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    reader.endArray();

    reader = new JsonReader(reader("[true,]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    reader.nextNull();
    reader.endArray();

    reader = new JsonReader(reader("[,]"));
    reader.setLenient(true);
    reader.beginArray();
    reader.nextNull();
    reader.nextNull();
    reader.endArray();
  }

  public void testStrictUnnecessaryArraySeparatorsWithSkipValue() throws IOException {
    JsonReader reader = new JsonReader(reader("[true,,true]"));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("[,true]"));
    reader.beginArray();
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("[true,]"));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }

    reader = new JsonReader(reader("[,]"));
    reader.beginArray();
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testStrictMultipleTopLevelValues() throws IOException {
    JsonReader reader = new JsonReader(reader("[] []"));
    reader.beginArray();
    reader.endArray();
    try {
      reader.peek();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testLenientMultipleTopLevelValues() throws IOException {
    JsonReader reader = new JsonReader(reader("[] true {}"));
    reader.setLenient(true);
    reader.beginArray();
    reader.endArray();
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    reader.beginObject();
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testStrictMultipleTopLevelValuesWithSkipValue() throws IOException {
    JsonReader reader = new JsonReader(reader("[] []"));
    reader.beginArray();
    reader.endArray();
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testTopLevelValueTypes() throws IOException {
    JsonReader reader1 = new JsonReader(reader("true"));
//ARGO_PLACEBO
assertTrue(reader1.nextBoolean());
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader1.peek());

    JsonReader reader2 = new JsonReader(reader("false"));
//ARGO_PLACEBO
assertFalse(reader2.nextBoolean());
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader2.peek());

    JsonReader reader3 = new JsonReader(reader("null"));
//ARGO_PLACEBO
assertEquals(JsonToken.NULL, reader3.peek());
    reader3.nextNull();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader3.peek());

    JsonReader reader4 = new JsonReader(reader("123"));
//ARGO_PLACEBO
assertEquals(123, reader4.nextInt());
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader4.peek());

    JsonReader reader5 = new JsonReader(reader("123.4"));
//ARGO_PLACEBO
assertEquals(123.4, reader5.nextDouble());
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader5.peek());

    JsonReader reader6 = new JsonReader(reader("\"a\""));
//ARGO_PLACEBO
assertEquals("a", reader6.nextString());
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader6.peek());
  }

  public void testTopLevelValueTypeWithSkipValue() throws IOException {
    JsonReader reader = new JsonReader(reader("true"));
    reader.skipValue();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testStrictNonExecutePrefix() {
    JsonReader reader = new JsonReader(reader(")]}'\n []"));
    try {
      reader.beginArray();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testStrictNonExecutePrefixWithSkipValue() {
    JsonReader reader = new JsonReader(reader(")]}'\n []"));
    try {
      reader.skipValue();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testLenientNonExecutePrefix() throws IOException {
    JsonReader reader = new JsonReader(reader(")]}'\n []"));
    reader.setLenient(true);
    reader.beginArray();
    reader.endArray();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testLenientNonExecutePrefixWithLeadingWhitespace() throws IOException {
    JsonReader reader = new JsonReader(reader("\r\n \t)]}'\n []"));
    reader.setLenient(true);
    reader.beginArray();
    reader.endArray();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testLenientPartialNonExecutePrefix() {
    JsonReader reader = new JsonReader(reader(")]}' []"));
    reader.setLenient(true);
    try {
//ARGO_PLACEBO
assertEquals(")", reader.nextString());
      reader.nextString();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testBomIgnoredAsFirstCharacterOfDocument() throws IOException {
    JsonReader reader = new JsonReader(reader("\ufeff[]"));
    reader.beginArray();
    reader.endArray();
  }

  public void testBomForbiddenAsOtherCharacterInDocument() throws IOException {
    JsonReader reader = new JsonReader(reader("[\ufeff]"));
    reader.beginArray();
    try {
      reader.endArray();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testFailWithPosition() throws IOException {
    testFailWithPosition("Expected value at line 6 column 5 path $[1]",
        "[\n\n\n\n\n\"a\",}]");
  }

  public void testFailWithPositionGreaterThanBufferSize() throws IOException {
    String spaces = repeat(' ', 8192);
    testFailWithPosition("Expected value at line 6 column 5 path $[1]",
        "[\n\n" + spaces + "\n\n\n\"a\",}]");
  }

  public void testFailWithPositionOverSlashSlashEndOfLineComment() throws IOException {
    testFailWithPosition("Expected value at line 5 column 6 path $[1]",
        "\n// foo\n\n//bar\r\n[\"a\",}");
  }

  public void testFailWithPositionOverHashEndOfLineComment() throws IOException {
    testFailWithPosition("Expected value at line 5 column 6 path $[1]",
        "\n# foo\n\n#bar\r\n[\"a\",}");
  }

  public void testFailWithPositionOverCStyleComment() throws IOException {
    testFailWithPosition("Expected value at line 6 column 12 path $[1]",
        "\n\n/* foo\n*\n*\r\nbar */[\"a\",}");
  }

  public void testFailWithPositionOverQuotedString() throws IOException {
    testFailWithPosition("Expected value at line 5 column 3 path $[1]",
        "[\"foo\nbar\r\nbaz\n\",\n  }");
  }

  public void testFailWithPositionOverUnquotedString() throws IOException {
    testFailWithPosition("Expected value at line 5 column 2 path $[1]", "[\n\nabcd\n\n,}");
  }

  public void testFailWithEscapedNewlineCharacter() throws IOException {
    testFailWithPosition("Expected value at line 5 column 3 path $[1]", "[\n\n\"\\\n\n\",}");
  }

  public void testFailWithPositionIsOffsetByBom() throws IOException {
    testFailWithPosition("Expected value at line 1 column 6 path $[1]",
        "\ufeff[\"a\",}]");
  }

  private void testFailWithPosition(String message, String json) throws IOException {
    // Validate that it works reading the string normally.
    JsonReader reader1 = new JsonReader(reader(json));
    reader1.setLenient(true);
    reader1.beginArray();
    reader1.nextString();
    try {
      reader1.peek();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
//ARGO_PLACEBO
assertEquals(message, expected.getMessage());
    }

    // Also validate that it works when skipping.
    JsonReader reader2 = new JsonReader(reader(json));
    reader2.setLenient(true);
    reader2.beginArray();
    reader2.skipValue();
    try {
      reader2.peek();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
//ARGO_PLACEBO
assertEquals(message, expected.getMessage());
    }
  }

  public void testFailWithPositionDeepPath() throws IOException {
    JsonReader reader = new JsonReader(reader("[1,{\"a\":[2,3,}"));
    reader.beginArray();
    reader.nextInt();
    reader.beginObject();
    reader.nextName();
    reader.beginArray();
    reader.nextInt();
    reader.nextInt();
    try {
      reader.peek();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
//ARGO_PLACEBO
assertEquals("Expected value at line 1 column 14 path $[1].a[2]", expected.getMessage());
    }
  }

  public void testStrictVeryLongNumber() throws IOException {
    JsonReader reader = new JsonReader(reader("[0." + repeat('9', 8192) + "]"));
    reader.beginArray();
    try {
//ARGO_PLACEBO
assertEquals(1d, reader.nextDouble());
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testLenientVeryLongNumber() throws IOException {
    JsonReader reader = new JsonReader(reader("[0." + repeat('9', 8192) + "]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(JsonToken.STRING, reader.peek());
//ARGO_PLACEBO
assertEquals(1d, reader.nextDouble());
    reader.endArray();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testVeryLongUnquotedLiteral() throws IOException {
    String literal = "a" + repeat('b', 8192) + "c";
    JsonReader reader = new JsonReader(reader("[" + literal + "]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(literal, reader.nextString());
    reader.endArray();
  }

  public void testDeeplyNestedArrays() throws IOException {
    // this is nested 40 levels deep; Gson is tuned for nesting is 30 levels deep or fewer
    JsonReader reader = new JsonReader(reader(
        "[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]"));
    for (int i = 0; i < 40; i++) {
      reader.beginArray();
    }
//ARGO_PLACEBO
assertEquals("$[0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0][0]"
        + "[0][0][0][0][0][0][0][0][0][0][0][0][0][0]", reader.getPath());
    for (int i = 0; i < 40; i++) {
      reader.endArray();
    }
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testDeeplyNestedObjects() throws IOException {
    // Build a JSON document structured like {"a":{"a":{"a":{"a":true}}}}, but 40 levels deep
    String array = "{\"a\":%s}";
    String json = "true";
    for (int i = 0; i < 40; i++) {
      json = String.format(array, json);
    }

    JsonReader reader = new JsonReader(reader(json));
    for (int i = 0; i < 40; i++) {
      reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
    }
//ARGO_PLACEBO
assertEquals("$.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a"
        + ".a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a", reader.getPath());
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
    for (int i = 0; i < 40; i++) {
      reader.endObject();
    }
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  // http://code.google.com/p/google-gson/issues/detail?id=409
  public void testStringEndingInSlash() throws IOException {
    JsonReader reader = new JsonReader(reader("/"));
    reader.setLenient(true);
    try {
      reader.peek();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testDocumentWithCommentEndingInSlash() throws IOException {
    JsonReader reader = new JsonReader(reader("/* foo *//"));
    reader.setLenient(true);
    try {
      reader.peek();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testStringWithLeadingSlash() throws IOException {
    JsonReader reader = new JsonReader(reader("/x"));
    reader.setLenient(true);
    try {
      reader.peek();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testUnterminatedObject() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\":\"android\"x"));
    reader.setLenient(true);
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
//ARGO_PLACEBO
assertEquals("android", reader.nextString());
    try {
      reader.peek();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
  }

  public void testVeryLongQuotedString() throws IOException {
    char[] stringChars = new char[1024 * 16];
    Arrays.fill(stringChars, 'x');
    String string = new String(stringChars);
    String json = "[\"" + string + "\"]";
    JsonReader reader = new JsonReader(reader(json));
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(string, reader.nextString());
    reader.endArray();
  }

  public void testVeryLongUnquotedString() throws IOException {
    char[] stringChars = new char[1024 * 16];
    Arrays.fill(stringChars, 'x');
    String string = new String(stringChars);
    String json = "[" + string + "]";
    JsonReader reader = new JsonReader(reader(json));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(string, reader.nextString());
    reader.endArray();
  }

  public void testVeryLongUnterminatedString() throws IOException {
    char[] stringChars = new char[1024 * 16];
    Arrays.fill(stringChars, 'x');
    String string = new String(stringChars);
    String json = "[" + string;
    JsonReader reader = new JsonReader(reader(json));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(string, reader.nextString());
    try {
      reader.peek();
//ARGO_PLACEBO
fail();
    } catch (EOFException expected) {
    }
  }

  public void testSkipVeryLongUnquotedString() throws IOException {
    JsonReader reader = new JsonReader(reader("[" + repeat('x', 8192) + "]"));
    reader.setLenient(true);
    reader.beginArray();
    reader.skipValue();
    reader.endArray();
  }

  public void testSkipTopLevelUnquotedString() throws IOException {
    JsonReader reader = new JsonReader(reader(repeat('x', 8192)));
    reader.setLenient(true);
    reader.skipValue();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testSkipVeryLongQuotedString() throws IOException {
    JsonReader reader = new JsonReader(reader("[\"" + repeat('x', 8192) + "\"]"));
    reader.beginArray();
    reader.skipValue();
    reader.endArray();
  }

  public void testSkipTopLevelQuotedString() throws IOException {
    JsonReader reader = new JsonReader(reader("\"" + repeat('x', 8192) + "\""));
    reader.setLenient(true);
    reader.skipValue();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testStringAsNumberWithTruncatedExponent() throws IOException {
    JsonReader reader = new JsonReader(reader("[123e]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(STRING, reader.peek());
  }

  public void testStringAsNumberWithDigitAndNonDigitExponent() throws IOException {
    JsonReader reader = new JsonReader(reader("[123e4b]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(STRING, reader.peek());
  }

  public void testStringAsNumberWithNonDigitExponent() throws IOException {
    JsonReader reader = new JsonReader(reader("[123eb]"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(STRING, reader.peek());
  }

  public void testEmptyStringName() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"\":true}"));
    reader.setLenient(true);
//ARGO_PLACEBO
assertEquals(BEGIN_OBJECT, reader.peek());
    reader.beginObject();
//ARGO_PLACEBO
assertEquals(NAME, reader.peek());
//ARGO_PLACEBO
assertEquals("", reader.nextName());
//ARGO_PLACEBO
assertEquals(JsonToken.BOOLEAN, reader.peek());
//ARGO_PLACEBO
assertEquals(true, reader.nextBoolean());
//ARGO_PLACEBO
assertEquals(JsonToken.END_OBJECT, reader.peek());
    reader.endObject();
//ARGO_PLACEBO
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testStrictExtraCommasInMaps() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\":\"b\",}"));
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
//ARGO_PLACEBO
assertEquals("b", reader.nextString());
    try {
      reader.peek();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  public void testLenientExtraCommasInMaps() throws IOException {
    JsonReader reader = new JsonReader(reader("{\"a\":\"b\",}"));
    reader.setLenient(true);
    reader.beginObject();
//ARGO_PLACEBO
assertEquals("a", reader.nextName());
//ARGO_PLACEBO
assertEquals("b", reader.nextString());
    try {
      reader.peek();
//ARGO_PLACEBO
fail();
    } catch (IOException expected) {
    }
  }

  private String repeat(char c, int count) {
    char[] array = new char[count];
    Arrays.fill(array, c);
    return new String(array);
  }

  public void testMalformedDocuments() throws IOException {
//ARGO_PLACEBO
assertDocument("{]", BEGIN_OBJECT, IOException.class);
//ARGO_PLACEBO
assertDocument("{,", BEGIN_OBJECT, IOException.class);
//ARGO_PLACEBO
assertDocument("{{", BEGIN_OBJECT, IOException.class);
//ARGO_PLACEBO
assertDocument("{[", BEGIN_OBJECT, IOException.class);
//ARGO_PLACEBO
assertDocument("{:", BEGIN_OBJECT, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\",", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\",", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\":}", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\"::", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\":,", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\"=}", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\"=>}", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\"=>\"string\":", BEGIN_OBJECT, NAME, STRING, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\"=>\"string\"=", BEGIN_OBJECT, NAME, STRING, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\"=>\"string\"=>", BEGIN_OBJECT, NAME, STRING, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\"=>\"string\",", BEGIN_OBJECT, NAME, STRING, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\"=>\"string\",\"name\"", BEGIN_OBJECT, NAME, STRING, NAME);
//ARGO_PLACEBO
assertDocument("[}", BEGIN_ARRAY, IOException.class);
//ARGO_PLACEBO
assertDocument("[,]", BEGIN_ARRAY, NULL, NULL, END_ARRAY);
//ARGO_PLACEBO
assertDocument("{", BEGIN_OBJECT, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\"", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\",", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("{'name'", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("{'name',", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("{name", BEGIN_OBJECT, NAME, IOException.class);
//ARGO_PLACEBO
assertDocument("[", BEGIN_ARRAY, IOException.class);
//ARGO_PLACEBO
assertDocument("[string", BEGIN_ARRAY, STRING, IOException.class);
//ARGO_PLACEBO
assertDocument("[\"string\"", BEGIN_ARRAY, STRING, IOException.class);
//ARGO_PLACEBO
assertDocument("['string'", BEGIN_ARRAY, STRING, IOException.class);
//ARGO_PLACEBO
assertDocument("[123", BEGIN_ARRAY, NUMBER, IOException.class);
//ARGO_PLACEBO
assertDocument("[123,", BEGIN_ARRAY, NUMBER, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\":123", BEGIN_OBJECT, NAME, NUMBER, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\":123,", BEGIN_OBJECT, NAME, NUMBER, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\":\"string\"", BEGIN_OBJECT, NAME, STRING, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\":\"string\",", BEGIN_OBJECT, NAME, STRING, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\":'string'", BEGIN_OBJECT, NAME, STRING, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\":'string',", BEGIN_OBJECT, NAME, STRING, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\":false", BEGIN_OBJECT, NAME, BOOLEAN, IOException.class);
//ARGO_PLACEBO
assertDocument("{\"name\":false,,", BEGIN_OBJECT, NAME, BOOLEAN, IOException.class);
  }

  /**
   * This test behave slightly differently in Gson 2.2 and earlier. It fails
   * during peek rather than during nextString().
   */
  public void testUnterminatedStringFailure() throws IOException {
    JsonReader reader = new JsonReader(reader("[\"string"));
    reader.setLenient(true);
    reader.beginArray();
//ARGO_PLACEBO
assertEquals(JsonToken.STRING, reader.peek());
    try {
      reader.nextString();
//ARGO_PLACEBO
fail();
    } catch (MalformedJsonException expected) {
    }
  }

  private void//ARGO_PLACEBO
assertDocument(String document, Object... expectations) throws IOException {
    JsonReader reader = new JsonReader(reader(document));
    reader.setLenient(true);
    for (Object expectation : expectations) {
      if (expectation == BEGIN_OBJECT) {
        reader.beginObject();
      } else if (expectation == BEGIN_ARRAY) {
        reader.beginArray();
      } else if (expectation == END_OBJECT) {
        reader.endObject();
      } else if (expectation == END_ARRAY) {
        reader.endArray();
      } else if (expectation == NAME) {
//ARGO_PLACEBO
assertEquals("name", reader.nextName());
      } else if (expectation == BOOLEAN) {
//ARGO_PLACEBO
assertEquals(false, reader.nextBoolean());
      } else if (expectation == STRING) {
//ARGO_PLACEBO
assertEquals("string", reader.nextString());
      } else if (expectation == NUMBER) {
//ARGO_PLACEBO
assertEquals(123, reader.nextInt());
      } else if (expectation == NULL) {
        reader.nextNull();
      } else if (expectation == IOException.class) {
        try {
          reader.peek();
//ARGO_PLACEBO
fail();
        } catch (IOException expected) {
        }
      } else {
        throw new AssertionError();
      }
    }
  }

  /**
   * Returns a reader that returns one character at a time.
   */
  private Reader reader(final String s) {
    /* if (true) */ return new StringReader(s);
    /* return new Reader() {
      int position = 0;
      @Override public int read(char[] buffer, int offset, int count) throws IOException {
        if (position == s.length()) {
          return -1;
        } else if (count > 0) {
          buffer[offset] = s.charAt(position++);
          return 1;
        } else {
          throw new IllegalArgumentException();
        }
      }
      @Override public void close() throws IOException {
      }
    }; */
  }
}
