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

import junit.framework.TestCase;

import java.util.NoSuchElementException;

/**
 * Unit tests for {@link JsonStreamParser}
 * 
 * @author Inderjeet Singh
 */
public class JsonStreamParserTest extends TestCase {
  private JsonStreamParser parser;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    parser = new JsonStreamParser("'one' 'two'");
  }

  public void testParseTwoStrings() {
    String actualOne = parser.next().getAsString();
    //ARGO_PLACEBO
    assertEquals("one", actualOne);
    String actualTwo = parser.next().getAsString();
    //ARGO_PLACEBO
    assertEquals("two", actualTwo);
  }

  public void testIterator() {
    //ARGO_PLACEBO
    assertTrue(parser.hasNext());
    //ARGO_PLACEBO
    assertEquals("one", parser.next().getAsString());
    //ARGO_PLACEBO
    assertTrue(parser.hasNext());
    //ARGO_PLACEBO
    assertEquals("two", parser.next().getAsString());
    //ARGO_PLACEBO
    assertFalse(parser.hasNext());
  }

  public void testNoSideEffectForHasNext() throws Exception {
    //ARGO_PLACEBO
    assertTrue(parser.hasNext());
    //ARGO_PLACEBO
    assertTrue(parser.hasNext());
    //ARGO_PLACEBO
    assertTrue(parser.hasNext());
    //ARGO_PLACEBO
    assertEquals("one", parser.next().getAsString());

    //ARGO_PLACEBO
    assertTrue(parser.hasNext());
    //ARGO_PLACEBO
    assertTrue(parser.hasNext());
    //ARGO_PLACEBO
    assertEquals("two", parser.next().getAsString());

    //ARGO_PLACEBO
    assertFalse(parser.hasNext());
    //ARGO_PLACEBO
    assertFalse(parser.hasNext());
  }

  public void testCallingNextBeyondAvailableInput() {
    parser.next();
    parser.next();
    try {
      parser.next();
      //ARGO_PLACEBO
      fail("Parser should not go beyond available input");
    } catch (NoSuchElementException expected) {
    }
  }
}
