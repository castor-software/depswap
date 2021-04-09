/*
 * Copyright (C) 2011 Google Inc.
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

package com.google.gson.internal.bind;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import junit.framework.TestCase;

@SuppressWarnings("resource")
public final class JsonElementReaderTest extends TestCase {

  public void testNumbers() throws IOException {
    JsonElement element = JsonParser.parseString("[1, 2, 3]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals(1, reader.nextInt());
//ARGO_ORIGINAL
assertEquals(2L, reader.nextLong());
//ARGO_ORIGINAL
assertEquals(3.0, reader.nextDouble());
    reader.endArray();
  }

  public void testLenientNansAndInfinities() throws IOException {
    JsonElement element = JsonParser.parseString("[NaN, -Infinity, Infinity]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.setLenient(true);
    reader.beginArray();
//ARGO_ORIGINAL
assertTrue(Double.isNaN(reader.nextDouble()));
//ARGO_ORIGINAL
assertEquals(Double.NEGATIVE_INFINITY, reader.nextDouble());
//ARGO_ORIGINAL
assertEquals(Double.POSITIVE_INFINITY, reader.nextDouble());
    reader.endArray();
  }

  public void testStrictNansAndInfinities() throws IOException {
    JsonElement element = JsonParser.parseString("[NaN, -Infinity, Infinity]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.setLenient(false);
    reader.beginArray();
    try {
      reader.nextDouble();
//ARGO_ORIGINAL
fail();
    } catch (NumberFormatException e) {
    }
//ARGO_ORIGINAL
assertEquals("NaN", reader.nextString());
    try {
      reader.nextDouble();
//ARGO_ORIGINAL
fail();
    } catch (NumberFormatException e) {
    }
//ARGO_ORIGINAL
assertEquals("-Infinity", reader.nextString());
    try {
      reader.nextDouble();
//ARGO_ORIGINAL
fail();
    } catch (NumberFormatException e) {
    }
//ARGO_ORIGINAL
assertEquals("Infinity", reader.nextString());
    reader.endArray();
  }

  public void testNumbersFromStrings() throws IOException {
    JsonElement element = JsonParser.parseString("[\"1\", \"2\", \"3\"]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals(1, reader.nextInt());
//ARGO_ORIGINAL
assertEquals(2L, reader.nextLong());
//ARGO_ORIGINAL
assertEquals(3.0, reader.nextDouble());
    reader.endArray();
  }

  public void testStringsFromNumbers() throws IOException {
    JsonElement element = JsonParser.parseString("[1]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals("1", reader.nextString());
    reader.endArray();
  }

  public void testBooleans() throws IOException {
    JsonElement element = JsonParser.parseString("[true, false]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals(true, reader.nextBoolean());
//ARGO_ORIGINAL
assertEquals(false, reader.nextBoolean());
    reader.endArray();
  }

  public void testNulls() throws IOException {
    JsonElement element = JsonParser.parseString("[null,null]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    reader.nextNull();
    reader.nextNull();
    reader.endArray();
  }

  public void testStrings() throws IOException {
    JsonElement element = JsonParser.parseString("[\"A\",\"B\"]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals("A", reader.nextString());
//ARGO_ORIGINAL
assertEquals("B", reader.nextString());
    reader.endArray();
  }

  public void testArray() throws IOException {
    JsonElement element = JsonParser.parseString("[1, 2, 3]");
    JsonTreeReader reader = new JsonTreeReader(element);
//ARGO_ORIGINAL
assertEquals(JsonToken.BEGIN_ARRAY, reader.peek());
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals(JsonToken.NUMBER, reader.peek());
//ARGO_ORIGINAL
assertEquals(1, reader.nextInt());
//ARGO_ORIGINAL
assertEquals(JsonToken.NUMBER, reader.peek());
//ARGO_ORIGINAL
assertEquals(2, reader.nextInt());
//ARGO_ORIGINAL
assertEquals(JsonToken.NUMBER, reader.peek());
//ARGO_ORIGINAL
assertEquals(3, reader.nextInt());
//ARGO_ORIGINAL
assertEquals(JsonToken.END_ARRAY, reader.peek());
    reader.endArray();
//ARGO_ORIGINAL
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testObject() throws IOException {
    JsonElement element = JsonParser.parseString("{\"A\": 1, \"B\": 2}");
    JsonTreeReader reader = new JsonTreeReader(element);
//ARGO_ORIGINAL
assertEquals(JsonToken.BEGIN_OBJECT, reader.peek());
    reader.beginObject();
//ARGO_ORIGINAL
assertEquals(JsonToken.NAME, reader.peek());
//ARGO_ORIGINAL
assertEquals("A", reader.nextName());
//ARGO_ORIGINAL
assertEquals(JsonToken.NUMBER, reader.peek());
//ARGO_ORIGINAL
assertEquals(1, reader.nextInt());
//ARGO_ORIGINAL
assertEquals(JsonToken.NAME, reader.peek());
//ARGO_ORIGINAL
assertEquals("B", reader.nextName());
//ARGO_ORIGINAL
assertEquals(JsonToken.NUMBER, reader.peek());
//ARGO_ORIGINAL
assertEquals(2, reader.nextInt());
//ARGO_ORIGINAL
assertEquals(JsonToken.END_OBJECT, reader.peek());
    reader.endObject();
//ARGO_ORIGINAL
assertEquals(JsonToken.END_DOCUMENT, reader.peek());
  }

  public void testEmptyArray() throws IOException {
    JsonElement element = JsonParser.parseString("[]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    reader.endArray();
  }

  public void testNestedArrays() throws IOException {
    JsonElement element = JsonParser.parseString("[[],[[]]]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    reader.beginArray();
    reader.endArray();
    reader.beginArray();
    reader.beginArray();
    reader.endArray();
    reader.endArray();
    reader.endArray();
  }

  public void testNestedObjects() throws IOException {
    JsonElement element = JsonParser.parseString("{\"A\":{},\"B\":{\"C\":{}}}");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginObject();
//ARGO_ORIGINAL
assertEquals("A", reader.nextName());
    reader.beginObject();
    reader.endObject();
//ARGO_ORIGINAL
assertEquals("B", reader.nextName());
    reader.beginObject();
//ARGO_ORIGINAL
assertEquals("C", reader.nextName());
    reader.beginObject();
    reader.endObject();
    reader.endObject();
    reader.endObject();
  }

  public void testEmptyObject() throws IOException {
    JsonElement element = JsonParser.parseString("{}");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginObject();
    reader.endObject();
  }

  public void testSkipValue() throws IOException {
    JsonElement element = JsonParser.parseString("[\"A\",{\"B\":[[]]},\"C\",[[]],\"D\",null]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals("A", reader.nextString());
    reader.skipValue();
//ARGO_ORIGINAL
assertEquals("C", reader.nextString());
    reader.skipValue();
//ARGO_ORIGINAL
assertEquals("D", reader.nextString());
    reader.skipValue();
    reader.endArray();
  }

  public void testWrongType() throws IOException {
    JsonElement element = JsonParser.parseString("[[],\"A\"]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    try {
      reader.nextBoolean();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextNull();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextString();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextInt();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextLong();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextDouble();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextName();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.beginObject();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.endArray();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.endObject();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    reader.beginArray();
    reader.endArray();

    try {
      reader.nextBoolean();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextNull();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
    try {
      reader.nextInt();
//ARGO_ORIGINAL
fail();
    } catch (NumberFormatException expected) {
    }
    try {
      reader.nextLong();
//ARGO_ORIGINAL
fail();
    } catch (NumberFormatException expected) {
    }
    try {
      reader.nextDouble();
//ARGO_ORIGINAL
fail();
    } catch (NumberFormatException expected) {
    }
    try {
      reader.nextName();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
//ARGO_ORIGINAL
assertEquals("A", reader.nextString());
    reader.endArray();
  }

  public void testEarlyClose() throws IOException {
    JsonElement element = JsonParser.parseString("[1, 2, 3]");
    JsonTreeReader reader = new JsonTreeReader(element);
    reader.beginArray();
    reader.close();
    try {
      reader.peek();
//ARGO_ORIGINAL
fail();
    } catch (IllegalStateException expected) {
    }
  }
}
