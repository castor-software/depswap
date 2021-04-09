package com.google.gson.functional;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.common.TestTypes.BagOfPrimitives;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import junit.framework.TestCase;

import static com.google.gson.JSONTestUtils.assertEquivalent;

/**
 * Functional tests for {@link Gson#toJsonTree(Object)} and 
 * {@link Gson#toJsonTree(Object, java.lang.reflect.Type)}
 * 
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class JsonTreeTest extends TestCase {
  private Gson gson;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    gson = new Gson();
  }

  public void testToJsonTree() {
    BagOfPrimitives bag = new BagOfPrimitives(10L, 5, false, "foo");
    JsonElement json = gson.toJsonTree(bag);
    //ARGO_ORIGINAL
    assertTrue(json.isJsonObject());
    JsonObject obj = json.getAsJsonObject();
    Set<Entry<String, JsonElement>> children = obj.entrySet();
    //ARGO_ORIGINAL
    assertEquals(4, children.size());
    //ARGO_ORIGINAL
    assertContains(obj, new JsonPrimitive(10L));
    //ARGO_ORIGINAL
    assertContains(obj, new JsonPrimitive(5));
    //ARGO_ORIGINAL
    assertContains(obj, new JsonPrimitive(false));
    //ARGO_ORIGINAL
    assertContains(obj, new JsonPrimitive("foo"));
  }

  public void testToJsonTreeObjectType() {
    SubTypeOfBagOfPrimitives bag = new SubTypeOfBagOfPrimitives(10L, 5, false, "foo", 1.4F);
    JsonElement json = gson.toJsonTree(bag, BagOfPrimitives.class);
    //ARGO_ORIGINAL
    assertTrue(json.isJsonObject());
    JsonObject obj = json.getAsJsonObject();
    Set<Entry<String, JsonElement>> children = obj.entrySet();
    //ARGO_ORIGINAL
    assertEquals(4, children.size());
    //ARGO_ORIGINAL
    assertContains(obj, new JsonPrimitive(10L));
    //ARGO_ORIGINAL
    assertContains(obj, new JsonPrimitive(5));
    //ARGO_ORIGINAL
    assertContains(obj, new JsonPrimitive(false));
    //ARGO_ORIGINAL
    assertContains(obj, new JsonPrimitive("foo"));
  }

  public void testJsonTreeToString() {
    SubTypeOfBagOfPrimitives bag = new SubTypeOfBagOfPrimitives(10L, 5, false, "foo", 1.4F);
    String json1 = gson.toJson(bag);
    JsonElement jsonElement = gson.toJsonTree(bag, SubTypeOfBagOfPrimitives.class);
    String json2 = gson.toJson(jsonElement);
    //ARGO_EQUIVALENT
    assertEquivalent(json1, json2, gson);
  }

  public void testJsonTreeNull() {
    BagOfPrimitives bag = new BagOfPrimitives(10L, 5, false, null);
    JsonObject jsonElement = (JsonObject) gson.toJsonTree(bag, BagOfPrimitives.class);
    //ARGO_ORIGINAL
    assertFalse(jsonElement.has("stringValue"));
  }

  private void assertContains(JsonObject json, JsonPrimitive child) {
    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
      JsonElement node = entry.getValue();
      if (node.isJsonPrimitive()) {
        if (node.getAsJsonPrimitive().equals(child)) {
          return;
        }
      }
    }
    //ARGO_ORIGINAL
    fail();
  }
  
  private static class SubTypeOfBagOfPrimitives extends BagOfPrimitives {
    @SuppressWarnings("unused")
    float f = 1.2F;
    public SubTypeOfBagOfPrimitives(long l, int i, boolean b, String string, float f) {
      super(l, i, b, string);
      this.f = f;
    }
  }
}
