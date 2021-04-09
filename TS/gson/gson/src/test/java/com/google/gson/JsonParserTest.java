/*
 * Copyright (C) 2009 Google Inc.
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

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.StringReader;

import junit.framework.TestCase;

import com.google.gson.common.TestTypes.BagOfPrimitives;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

/**
 * Unit test for {@link JsonParser}
 *
 * @author Inderjeet Singh
 */
public class JsonParserTest extends TestCase {

  public void testParseInvalidJson() {
    try {
      JsonParser.parseString("[[]");
      //ARGO_ORIGINAL
      fail();
    } catch (JsonSyntaxException expected) { }
  }

  public void testParseUnquotedStringArrayFails() {
    JsonElement element = JsonParser.parseString("[a,b,c]");
    //ARGO_ORIGINAL
    assertEquals("a", element.getAsJsonArray().get(0).getAsString());
    //ARGO_ORIGINAL
    assertEquals("b", element.getAsJsonArray().get(1).getAsString());
    //ARGO_ORIGINAL
    assertEquals("c", element.getAsJsonArray().get(2).getAsString());
    //ARGO_ORIGINAL
    assertEquals(3, element.getAsJsonArray().size());
  }

  public void testParseString() {
    String json = "{a:10,b:'c'}";
    JsonElement e = JsonParser.parseString(json);
    //ARGO_ORIGINAL
    assertTrue(e.isJsonObject());
    //ARGO_ORIGINAL
    assertEquals(10, e.getAsJsonObject().get("a").getAsInt());
    //ARGO_ORIGINAL
    assertEquals("c", e.getAsJsonObject().get("b").getAsString());
  }

  public void testParseEmptyString() {
    JsonElement e = JsonParser.parseString("\"   \"");
    //ARGO_PLACEBO
    assertTrue(e.isJsonPrimitive());
    //ARGO_PLACEBO
    assertEquals("   ", e.getAsString());
  }

  public void testParseEmptyWhitespaceInput() {
    JsonElement e = JsonParser.parseString("     ");
    //ARGO_PLACEBO
    assertTrue(e.isJsonNull());
  }

  public void testParseUnquotedSingleWordStringFails() {
    //ARGO_PLACEBO
    assertEquals("Test", JsonParser.parseString("Test").getAsString());
  }

  public void testParseUnquotedMultiWordStringFails() {
    String unquotedSentence = "Test is a test..blah blah";
    try {
      JsonParser.parseString(unquotedSentence);
      //ARGO_PLACEBO
      fail();
    } catch (JsonSyntaxException expected) { }
  }

  public void testParseMixedArray() {
    String json = "[{},13,\"stringValue\"]";
    JsonElement e = JsonParser.parseString(json);
    //ARGO_ORIGINAL
    assertTrue(e.isJsonArray());

    JsonArray  array = e.getAsJsonArray();
    //ARGO_ORIGINAL
    assertEquals("{}", array.get(0).toString());
    //ARGO_ORIGINAL
    assertEquals(13, array.get(1).getAsInt());
    //ARGO_ORIGINAL
    assertEquals("stringValue", array.get(2).getAsString());
  }

  public void testParseReader() {
    StringReader reader = new StringReader("{a:10,b:'c'}");
    JsonElement e = JsonParser.parseReader(reader);
    //ARGO_ORIGINAL
    assertTrue(e.isJsonObject());
    //ARGO_ORIGINAL
    assertEquals(10, e.getAsJsonObject().get("a").getAsInt());
    //ARGO_ORIGINAL
    assertEquals("c", e.getAsJsonObject().get("b").getAsString());
  }

  public void testReadWriteTwoObjects() throws Exception {
    Gson gson = new Gson();
    CharArrayWriter writer = new CharArrayWriter();
    BagOfPrimitives expectedOne = new BagOfPrimitives(1, 1, true, "one");
    writer.write(gson.toJson(expectedOne).toCharArray());
    BagOfPrimitives expectedTwo = new BagOfPrimitives(2, 2, false, "two");
    writer.write(gson.toJson(expectedTwo).toCharArray());
    CharArrayReader reader = new CharArrayReader(writer.toCharArray());

    JsonReader parser = new JsonReader(reader);
    parser.setLenient(true);
    JsonElement element1 = Streams.parse(parser);
    JsonElement element2 = Streams.parse(parser);
    BagOfPrimitives actualOne = gson.fromJson(element1, BagOfPrimitives.class);
    //ARGO_ORIGINAL
    assertEquals("one", actualOne.stringValue);
    BagOfPrimitives actualTwo = gson.fromJson(element2, BagOfPrimitives.class);
    //ARGO_ORIGINAL
    assertEquals("two", actualTwo.stringValue);
  }
}
