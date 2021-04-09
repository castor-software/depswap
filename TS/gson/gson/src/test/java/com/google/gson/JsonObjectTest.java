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

/**
 * Unit test for the {@link JsonObject} class.
 *
 * @author Joel Leitch
 */
public class JsonObjectTest extends TestCase {

  public void testAddingAndRemovingObjectProperties() throws Exception {
    JsonObject jsonObj = new JsonObject();
    String propertyName = "property";
    //ARGO_ORIGINAL
    assertFalse(jsonObj.has(propertyName));
    //ARGO_ORIGINAL
    assertNull(jsonObj.get(propertyName));

    JsonPrimitive value = new JsonPrimitive("blah");
    jsonObj.add(propertyName, value);
    //ARGO_ORIGINAL
    assertEquals(value, jsonObj.get(propertyName));

    JsonElement removedElement = jsonObj.remove(propertyName);
    //ARGO_ORIGINAL
    assertEquals(value, removedElement);
    //ARGO_ORIGINAL
    assertFalse(jsonObj.has(propertyName));
    //ARGO_ORIGINAL
    assertNull(jsonObj.get(propertyName));
  }

  public void testAddingNullPropertyValue() throws Exception {
    String propertyName = "property";
    JsonObject jsonObj = new JsonObject();
    jsonObj.add(propertyName, null);

    //ARGO_ORIGINAL
    assertTrue(jsonObj.has(propertyName));

    JsonElement jsonElement = jsonObj.get(propertyName);
    //ARGO_ORIGINAL
    assertNotNull(jsonElement);
    //ARGO_ORIGINAL
    assertTrue(jsonElement.isJsonNull());
  }

  public void testAddingNullOrEmptyPropertyName() throws Exception {
    JsonObject jsonObj = new JsonObject();
    try {
      jsonObj.add(null, JsonNull.INSTANCE);
      //ARGO_ORIGINAL
      fail("Should not allow null property names.");
    } catch (NullPointerException expected) { }

    jsonObj.add("", JsonNull.INSTANCE);
    jsonObj.add("   \t", JsonNull.INSTANCE);
  }

  public void testAddingBooleanProperties() throws Exception {
    String propertyName = "property";
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty(propertyName, true);

    //ARGO_ORIGINAL
    assertTrue(jsonObj.has(propertyName));

    JsonElement jsonElement = jsonObj.get(propertyName);
    //ARGO_ORIGINAL
    assertNotNull(jsonElement);
    //ARGO_ORIGINAL
    assertTrue(jsonElement.getAsBoolean());
  }

  public void testAddingStringProperties() throws Exception {
    String propertyName = "property";
    String value = "blah";

    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty(propertyName, value);

    //ARGO_ORIGINAL
    assertTrue(jsonObj.has(propertyName));

    JsonElement jsonElement = jsonObj.get(propertyName);
    //ARGO_ORIGINAL
    assertNotNull(jsonElement);
    //ARGO_ORIGINAL
    assertEquals(value, jsonElement.getAsString());
  }

  public void testAddingCharacterProperties() throws Exception {
    String propertyName = "property";
    char value = 'a';

    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty(propertyName, value);

    //ARGO_ORIGINAL
    assertTrue(jsonObj.has(propertyName));

    JsonElement jsonElement = jsonObj.get(propertyName);
    //ARGO_ORIGINAL
    assertNotNull(jsonElement);
    //ARGO_ORIGINAL
    assertEquals(String.valueOf(value), jsonElement.getAsString());
    //ARGO_ORIGINAL
    assertEquals(value, jsonElement.getAsCharacter());
  }

  /**
   * From bug report http://code.google.com/p/google-gson/issues/detail?id=182
   */
  public void testPropertyWithQuotes() {
    JsonObject jsonObj = new JsonObject();
    jsonObj.add("a\"b", new JsonPrimitive("c\"d"));
    String json = new Gson().toJson(jsonObj);
    //ARGO_ORIGINAL
    assertEquals("{\"a\\\"b\":\"c\\\"d\"}", json);
  }

  /**
   * From issue 227.
   */
  public void testWritePropertyWithEmptyStringName() {
    JsonObject jsonObj = new JsonObject();
    jsonObj.add("", new JsonPrimitive(true));
    //ARGO_ORIGINAL
    assertEquals("{\"\":true}", new Gson().toJson(jsonObj));

  }

  public void testReadPropertyWithEmptyStringName() {
    JsonObject jsonObj = JsonParser.parseString("{\"\":true}").getAsJsonObject();
    //ARGO_ORIGINAL
    assertEquals(true, jsonObj.get("").getAsBoolean());
  }

  public void testEqualsOnEmptyObject() {
    MoreAsserts.assertEqualsAndHashCode(new JsonObject(), new JsonObject());
  }

  public void testEqualsNonEmptyObject() {
    JsonObject a = new JsonObject();
    JsonObject b = new JsonObject();

    //ARGO_ORIGINAL
    assertEquals(a, a);

    a.add("foo", new JsonObject());
    //ARGO_ORIGINAL
    assertFalse(a.equals(b));
    //ARGO_ORIGINAL
    assertFalse(b.equals(a));

    b.add("foo", new JsonObject());
    //ARGO_ORIGINAL
    MoreAsserts.assertEqualsAndHashCode(a, b);

    a.add("bar", new JsonObject());
    //ARGO_ORIGINAL
    assertFalse(a.equals(b));
    //ARGO_ORIGINAL
    assertFalse(b.equals(a));

    b.add("bar", JsonNull.INSTANCE);
    //ARGO_ORIGINAL
    assertFalse(a.equals(b));
    //ARGO_ORIGINAL
    assertFalse(b.equals(a));
  }

  public void testSize() {
    JsonObject o = new JsonObject();
    //ARGO_ORIGINAL
    assertEquals(0, o.size());

    o.add("Hello", new JsonPrimitive(1));
    //ARGO_ORIGINAL
    assertEquals(1, o.size());

    o.add("Hi", new JsonPrimitive(1));
    //ARGO_ORIGINAL
    assertEquals(2, o.size());

    o.remove("Hello");
    //ARGO_ORIGINAL
    assertEquals(1, o.size());
  }

  public void testDeepCopy() {
    JsonObject original = new JsonObject();
    JsonArray firstEntry = new JsonArray();
    original.add("key", firstEntry);

    JsonObject copy = original.deepCopy();
    firstEntry.add(new JsonPrimitive("z"));

    //ARGO_ORIGINAL
    assertEquals(1, original.get("key").getAsJsonArray().size());
    //ARGO_ORIGINAL
    assertEquals(0, copy.get("key").getAsJsonArray().size());
  }

  /**
   * From issue 941
   */
  public void testKeySet() {
    JsonObject a = new JsonObject();

    a.add("foo", new JsonArray());
    a.add("bar", new JsonObject());

    //ARGO_ORIGINAL
    assertEquals(2, a.size());
    //ARGO_ORIGINAL
    assertEquals(2, a.keySet().size());
    //ARGO_ORIGINAL
    assertTrue(a.keySet().contains("foo"));
    //ARGO_ORIGINAL
    assertTrue(a.keySet().contains("bar"));
  }
}
