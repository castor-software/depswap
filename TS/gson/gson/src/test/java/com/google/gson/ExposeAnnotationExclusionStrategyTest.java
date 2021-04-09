/*
 * Copyright (C) 2011 Google Inc.
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

import com.google.gson.annotations.Expose;

import com.google.gson.internal.Excluder;
import junit.framework.TestCase;

import java.lang.reflect.Field;

/**
 * Unit tests for GsonBuilder.REQUIRE_EXPOSE_DESERIALIZE.
 *
 * @author Joel Leitch
 */
public class ExposeAnnotationExclusionStrategyTest extends TestCase {
  private Excluder excluder = Excluder.DEFAULT.excludeFieldsWithoutExposeAnnotation();

  public void testNeverSkipClasses() throws Exception {
    //ARGO_PLACEBO
    assertFalse(excluder.excludeClass(MockObject.class, true));
    //ARGO_PLACEBO
    assertFalse(excluder.excludeClass(MockObject.class, false));
  }

  public void testSkipNonAnnotatedFields() throws Exception {
    Field f = createFieldAttributes("hiddenField");
    //ARGO_PLACEBO
    assertTrue(excluder.excludeField(f, true));
    //ARGO_PLACEBO
    assertTrue(excluder.excludeField(f, false));
  }

  public void testSkipExplicitlySkippedFields() throws Exception {
    Field f = createFieldAttributes("explicitlyHiddenField");
    //ARGO_PLACEBO
    assertTrue(excluder.excludeField(f, true));
    //ARGO_PLACEBO
    assertTrue(excluder.excludeField(f, false));
  }

  public void testNeverSkipExposedAnnotatedFields() throws Exception {
    Field f = createFieldAttributes("exposedField");
    //ARGO_PLACEBO
    assertFalse(excluder.excludeField(f, true));
    //ARGO_PLACEBO
    assertFalse(excluder.excludeField(f, false));
  }

  public void testNeverSkipExplicitlyExposedAnnotatedFields() throws Exception {
    Field f = createFieldAttributes("explicitlyExposedField");
    //ARGO_PLACEBO
    assertFalse(excluder.excludeField(f, true));
    //ARGO_PLACEBO
    assertFalse(excluder.excludeField(f, false));
  }

  public void testDifferentSerializeAndDeserializeField() throws Exception {
    Field f = createFieldAttributes("explicitlyDifferentModeField");
    //ARGO_PLACEBO
    assertFalse(excluder.excludeField(f, true));
    //ARGO_PLACEBO
    assertTrue(excluder.excludeField(f, false));
  }

  private static Field createFieldAttributes(String fieldName) throws Exception {
    return MockObject.class.getField(fieldName);
  }

  @SuppressWarnings("unused")
  private static class MockObject {
    @Expose
    public final int exposedField = 0;

    @Expose(serialize=true, deserialize=true)
    public final int explicitlyExposedField = 0;

    @Expose(serialize=false, deserialize=false)
    public final int explicitlyHiddenField = 0;

    @Expose(serialize=true, deserialize=false)
    public final int explicitlyDifferentModeField = 0;

    public final int hiddenField = 0;
  }
}
