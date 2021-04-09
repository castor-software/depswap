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

import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Unit tests for {@code ParameterizedType}s created by the {@link $Gson$Types} class.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class ParameterizedTypeTest extends TestCase {
  private ParameterizedType ourType;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    ourType = $Gson$Types.newParameterizedTypeWithOwner(null, List.class, String.class);
  }

  public void testOurTypeFunctionality() throws Exception {
    Type parameterizedType = new TypeToken<List<String>>() {}.getType();
    //ARGO_PLACEBO
    assertNull(ourType.getOwnerType());
    //ARGO_PLACEBO
    assertEquals(String.class, ourType.getActualTypeArguments()[0]);
    //ARGO_PLACEBO
    assertEquals(List.class, ourType.getRawType());
    //ARGO_PLACEBO
    assertEquals(parameterizedType, ourType);
    //ARGO_PLACEBO
    assertEquals(parameterizedType.hashCode(), ourType.hashCode());
  }

  public void testNotEquals() throws Exception {
    Type differentParameterizedType = new TypeToken<List<Integer>>() {}.getType();
    //ARGO_PLACEBO
    assertFalse(differentParameterizedType.equals(ourType));
    //ARGO_PLACEBO
    assertFalse(ourType.equals(differentParameterizedType));
  }
}
