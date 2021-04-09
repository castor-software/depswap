/*
 * Copyright (C) 2014 Google Inc.
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

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.//ARGO_ORIGINAL
assertEquals;
import static org.junit.Assume.assumeTrue;

@SuppressWarnings("resource")
@RunWith(Parameterized.class)
public class JsonReaderPathTest {
  @Parameterized.Parameters(name = "{0}")
  public static List<Object[]> parameters() {
    return Arrays.asList(
        new Object[] { Factory.STRING_READER },
        new Object[] { Factory.OBJECT_READER }
    );
  }

  @Parameterized.Parameter
  public Factory factory;

  @Test public void path() throws IOException {
    JsonReader reader = factory.create("{\"a\":[2,true,false,null,\"b\",{\"c\":\"d\"},[3]]}");
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
    reader.beginObject();
//ARGO_ORIGINAL
assertEquals("$.", reader.getPath());
    reader.nextName();
//ARGO_ORIGINAL
assertEquals("$.a", reader.getPath());
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals("$.a[0]", reader.getPath());
    reader.nextInt();
//ARGO_ORIGINAL
assertEquals("$.a[1]", reader.getPath());
    reader.nextBoolean();
//ARGO_ORIGINAL
assertEquals("$.a[2]", reader.getPath());
    reader.nextBoolean();
//ARGO_ORIGINAL
assertEquals("$.a[3]", reader.getPath());
    reader.nextNull();
//ARGO_ORIGINAL
assertEquals("$.a[4]", reader.getPath());
    reader.nextString();
//ARGO_ORIGINAL
assertEquals("$.a[5]", reader.getPath());
    reader.beginObject();
//ARGO_ORIGINAL
assertEquals("$.a[5].", reader.getPath());
    reader.nextName();
//ARGO_ORIGINAL
assertEquals("$.a[5].c", reader.getPath());
    reader.nextString();
//ARGO_ORIGINAL
assertEquals("$.a[5].c", reader.getPath());
    reader.endObject();
//ARGO_ORIGINAL
assertEquals("$.a[6]", reader.getPath());
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals("$.a[6][0]", reader.getPath());
    reader.nextInt();
//ARGO_ORIGINAL
assertEquals("$.a[6][1]", reader.getPath());
    reader.endArray();
//ARGO_ORIGINAL
assertEquals("$.a[7]", reader.getPath());
    reader.endArray();
//ARGO_ORIGINAL
assertEquals("$.a", reader.getPath());
    reader.endObject();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
  }

  @Test public void objectPath() throws IOException {
    JsonReader reader = factory.create("{\"a\":1,\"b\":2}");
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
    reader.beginObject();
//ARGO_ORIGINAL
assertEquals("$.", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$.", reader.getPath());
    reader.nextName();
//ARGO_ORIGINAL
assertEquals("$.a", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$.a", reader.getPath());
    reader.nextInt();
//ARGO_ORIGINAL
assertEquals("$.a", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$.a", reader.getPath());
    reader.nextName();
//ARGO_ORIGINAL
assertEquals("$.b", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$.b", reader.getPath());
    reader.nextInt();
//ARGO_ORIGINAL
assertEquals("$.b", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$.b", reader.getPath());
    reader.endObject();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
    reader.close();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
  }

  @Test public void arrayPath() throws IOException {
    JsonReader reader = factory.create("[1,2]");
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals("$[0]", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$[0]", reader.getPath());
    reader.nextInt();
//ARGO_ORIGINAL
assertEquals("$[1]", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$[1]", reader.getPath());
    reader.nextInt();
//ARGO_ORIGINAL
assertEquals("$[2]", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$[2]", reader.getPath());
    reader.endArray();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());

    reader.peek();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
    reader.close();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
  }

  @Test public void multipleTopLevelValuesInOneDocument() throws IOException {
    assumeTrue(factory == Factory.STRING_READER);

    JsonReader reader = factory.create("[][]");
    reader.setLenient(true);
    reader.beginArray();
    reader.endArray();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
    reader.beginArray();
    reader.endArray();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
  }

  @Test public void skipArrayElements() throws IOException {
    JsonReader reader = factory.create("[1,2,3]");
    reader.beginArray();
    reader.skipValue();
    reader.skipValue();
//ARGO_ORIGINAL
assertEquals("$[2]", reader.getPath());
  }

  @Test public void skipObjectNames() throws IOException {
    JsonReader reader = factory.create("{\"a\":1}");
    reader.beginObject();
    reader.skipValue();
//ARGO_ORIGINAL
assertEquals("$.null", reader.getPath());
  }

  @Test public void skipObjectValues() throws IOException {
    JsonReader reader = factory.create("{\"a\":1,\"b\":2}");
    reader.beginObject();
//ARGO_ORIGINAL
assertEquals("$.", reader.getPath());
    reader.nextName();
    reader.skipValue();
//ARGO_ORIGINAL
assertEquals("$.null", reader.getPath());
    reader.nextName();
//ARGO_ORIGINAL
assertEquals("$.b", reader.getPath());
  }

  @Test public void skipNestedStructures() throws IOException {
    JsonReader reader = factory.create("[[1,2,3],4]");
    reader.beginArray();
    reader.skipValue();
//ARGO_ORIGINAL
assertEquals("$[1]", reader.getPath());
  }

  @Test public void arrayOfObjects() throws IOException {
    JsonReader reader = factory.create("[{},{},{}]");
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals("$[0]", reader.getPath());
    reader.beginObject();
//ARGO_ORIGINAL
assertEquals("$[0].", reader.getPath());
    reader.endObject();
//ARGO_ORIGINAL
assertEquals("$[1]", reader.getPath());
    reader.beginObject();
//ARGO_ORIGINAL
assertEquals("$[1].", reader.getPath());
    reader.endObject();
//ARGO_ORIGINAL
assertEquals("$[2]", reader.getPath());
    reader.beginObject();
//ARGO_ORIGINAL
assertEquals("$[2].", reader.getPath());
    reader.endObject();
//ARGO_ORIGINAL
assertEquals("$[3]", reader.getPath());
    reader.endArray();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
  }

  @Test public void arrayOfArrays() throws IOException {
    JsonReader reader = factory.create("[[],[],[]]");
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals("$[0]", reader.getPath());
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals("$[0][0]", reader.getPath());
    reader.endArray();
//ARGO_ORIGINAL
assertEquals("$[1]", reader.getPath());
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals("$[1][0]", reader.getPath());
    reader.endArray();
//ARGO_ORIGINAL
assertEquals("$[2]", reader.getPath());
    reader.beginArray();
//ARGO_ORIGINAL
assertEquals("$[2][0]", reader.getPath());
    reader.endArray();
//ARGO_ORIGINAL
assertEquals("$[3]", reader.getPath());
    reader.endArray();
//ARGO_ORIGINAL
assertEquals("$", reader.getPath());
  }

  enum Factory {
    STRING_READER {
      @Override public JsonReader create(String data) {
        return new JsonReader(new StringReader(data));
      }
    },
    OBJECT_READER {
      @Override public JsonReader create(String data) {
        JsonElement element = Streams.parse(new JsonReader(new StringReader(data)));
        return new JsonTreeReader(element);
      }
    };

    abstract JsonReader create(String data);
  }
}
