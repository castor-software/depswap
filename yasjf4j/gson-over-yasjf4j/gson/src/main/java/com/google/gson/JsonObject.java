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

import com.google.gson.internal.LinkedTreeMap;
import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JNull;
import se.kth.castor.yasjf4j.JObject;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class representing an object type in Json. An object consists of name-value pairs where names
 * are strings, and values are any other type of {@link JsonElement}. This allows for a creating a
 * tree of JsonElements. The member elements of this object are maintained in order they were added.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public final class JsonObject extends JsonElement {
  //private final LinkedTreeMap<String, JsonElement> members =
   //   new LinkedTreeMap<String, JsonElement>();

  JObject json = JFactory.createJObject();

  public JsonObject() {

  }

  public static JObject deepCopy(JObject o) {
    JObject res = JFactory.createJObject();
    for(String key: o.YASJF4J_getKeys()) {
      try {
        Object val = o.YASJF4J_get(key);
        if(val instanceof JObject) {
          res.YASJF4J_put(key, deepCopy((JObject) val));
        } else if(val instanceof JArray) {
          res.YASJF4J_put(key, deepCopy((JArray) val));
        } else {
          res.YASJF4J_put(key, val);
        }
      } catch (JException e) {
        e.printStackTrace();
      }

    }
    return res;
  }

  public static JArray deepCopy(JArray o) {
    JArray res = JFactory.createJArray();
    for(int i = 0; i < o.YASJF4J_size(); i++) {
      try {
        Object val = o.YASJF4J_get(i);
        if(val instanceof JObject) {
          res.YASJF4J_add(deepCopy((JObject) val));
        } else if(val instanceof JArray) {
          res.YASJF4J_add(deepCopy((JArray) val));
        } else {
          res.YASJF4J_add(val);
        }
      } catch (JException e) {
        e.printStackTrace();
      }
    }
    return res;
  }

  public static Object deepCopy(Object o) {
    if(o instanceof JObject) return deepCopy((JObject) o);
    else if(o instanceof JArray) return deepCopy((JArray) o);
    else return o;
  }

  /**
   * Creates a deep copy of this element and all its children
   * @since 2.8.2
   */
  @Override
  public JsonObject deepCopy() {
    JsonObject result = new JsonObject();
    try {
      for (String key : json.YASJF4J_getKeys()) {
        result.json.YASJF4J_put(key, deepCopy(json.YASJF4J_get(key)));
        //result.add(key, toJsonElement(json.YASJF4J_get(key)));
      }
    } catch (JException e) {
      e.printStackTrace();
    }
    /*for (Map.Entry<String, JsonElement> entry : members.entrySet()) {
      result.add(entry.getKey(), entry.getValue().deepCopy());
    }*/
    return result;
  }

  /**
   * Adds a member, which is a name-value pair, to self. The name must be a String, but the value
   * can be an arbitrary JsonElement, thereby allowing you to build a full tree of JsonElements
   * rooted at this node.
   *
   * @param property name of the member.
   * @param value the member object.
   */
  public void add(String property, JsonElement value) {
    if (property == null) {
      throw new NullPointerException("key == null");
    }
    //members.put(property, value == null ? JsonNull.INSTANCE : value);
    try {

      json.YASJF4J_put(property,toObject(value));
      //json.YASJF4J_put(property, value == null ? JsonNull.INSTANCE : value);
    } catch (JException e) {
      e.printStackTrace();
    }
  }

  /**
   * Removes the {@code property} from this {@link JsonObject}.
   *
   * @param property name of the member that should be removed.
   * @return the {@link JsonElement} object that is being removed.
   * @since 1.3
   */
  public JsonElement remove(String property) {
    //return members.remove(property);
    JsonElement e = null;
    try {
      e = toJsonElement(json.YASJF4J_get(property));
    } catch (JException e1) {
      e1.printStackTrace();
    }
    try {
      json.YASJF4J_remove(property);
    } catch (JException e1) {
      e1.printStackTrace();
    }
    return e;
  }

  /**
   * Convenience method to add a primitive member. The specified value is converted to a
   * JsonPrimitive of String.
   *
   * @param property name of the member.
   * @param value the string value associated with the member.
   */
  public void addProperty(String property, String value) {
    //add(property, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    try {
      json.YASJF4J_put(property,toObject(value));
      //json.YASJF4J_put(property, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    } catch (JException e) {
      e.printStackTrace();
    }
  }

  /**
   * Convenience method to add a primitive member. The specified value is converted to a
   * JsonPrimitive of Number.
   *
   * @param property name of the member.
   * @param value the number value associated with the member.
   */
  public void addProperty(String property, Number value) {
    //add(property, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    try {
      json.YASJF4J_put(property,toObject(value));
      //json.YASJF4J_put(property, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    } catch (JException e) {
      e.printStackTrace();
    }
  }

  /**
   * Convenience method to add a boolean member. The specified value is converted to a
   * JsonPrimitive of Boolean.
   *
   * @param property name of the member.
   * @param value the number value associated with the member.
   */
  public void addProperty(String property, Boolean value) {
    //add(property, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    try {
      json.YASJF4J_put(property,toObject(value));
      //json.YASJF4J_put(property, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    } catch (JException e) {
      e.printStackTrace();
    }
  }

  /**
   * Convenience method to add a char member. The specified value is converted to a
   * JsonPrimitive of Character.
   *
   * @param property name of the member.
   * @param value the number value associated with the member.
   */
  public void addProperty(String property, Character value) {
    //add(property, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    try {
      json.YASJF4J_put(property,toObject(value));
      //json.YASJF4J_put(property, value == null ? JsonNull.INSTANCE : new JsonPrimitive(value));
    } catch (JException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns a set of members of this object. The set is ordered, and the order is in which the
   * elements were added.
   *
   * @return a set of members of this object.
   */
  public Set<Map.Entry<String, JsonElement>> entrySet() {
    //return members.entrySet();
    Set<Map.Entry<String, JsonElement>> set = new LinkedHashSet<Map.Entry<String, JsonElement>>();
    for(String key: json.YASJF4J_getKeys()) {
      try {
        set.add(new HashMap.SimpleEntry<String, JsonElement>(key, toJsonElement(json.YASJF4J_get(key))));
      } catch (JException e) {
        e.printStackTrace();
      }
    }
    return set;
  }

  /**
   * Returns a set of members key values.
   *
   * @return a set of member keys as Strings
   * @since 2.8.1
   */
  public Set<String> keySet() {
    //return members.keySet();
    return json.YASJF4J_getKeys();
  }

  /**
   * Returns the number of key/value pairs in the object.
   *
   * @return the number of key/value pairs in the object.
   */
  public int size() {
    //return members.size();
    return json.YASJF4J_getKeys().size();
  }

  /**
   * Convenience method to check if a member with the specified name is present in this object.
   *
   * @param memberName name of the member that is being checked for presence.
   * @return true if there is a member with the specified name, false otherwise.
   */
  public boolean has(String memberName) {
    //return members.containsKey(memberName);
    return json.YASJF4J_getKeys().contains(memberName);
  }

  /**
   * Returns the member with the specified name.
   *
   * @param memberName name of the member that is being requested.
   * @return the member matching the name. Null if no such member exists.
   */
  public JsonElement get(String memberName) {
    //return members.get(memberName);
    try {
      if(!json.YASJF4J_getKeys().contains(memberName)) return null;
      return toJsonElement(json.YASJF4J_get(memberName));
    } catch (JException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Convenience method to get the specified member as a JsonPrimitive element.
   *
   * @param memberName name of the member being requested.
   * @return the JsonPrimitive corresponding to the specified member.
   */
  public JsonPrimitive getAsJsonPrimitive(String memberName) {
    //return (JsonPrimitive) members.get(memberName);
    try {
      return (JsonPrimitive) toJsonElement(json.YASJF4J_get(memberName));
    } catch (JException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Convenience method to get the specified member as a JsonArray.
   *
   * @param memberName name of the member being requested.
   * @return the JsonArray corresponding to the specified member.
   */
  public JsonArray getAsJsonArray(String memberName) {
    //return (JsonArray) members.get(memberName);
    try {
      return (JsonArray) toJsonElement(json.YASJF4J_get(memberName));
    } catch (JException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Convenience method to get the specified member as a JsonObject.
   *
   * @param memberName name of the member being requested.
   * @return the JsonObject corresponding to the specified member.
   */
  public JsonObject getAsJsonObject(String memberName) {
    //return (JsonObject) members.get(memberName);
    try {
      return (JsonObject) toJsonElement(json.YASJF4J_get(memberName));
    } catch (JException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    //return (o == this) || (o instanceof JsonObject
    //    && ((JsonObject) o).members.equals(members));
    //return (o == this) || (o instanceof JsonObject
    //        && ((JsonObject) o).json.equals(json));
    return (o == this) || (o instanceof JsonObject && JsonObject.equivalence(json, ((JsonObject) o).json));
  }

  @Override
  public int hashCode() {
    //return members.hashCode();
    int h = 0;
    for(String key: json.YASJF4J_getKeys()) {
      h += key.hashCode();
    }
    return h;
  }

  public JsonObject(JObject o) {
    json = o;
  }

  public JsonObject(Map m) {
    for(Object k: m.keySet()) {
      try {
        json.YASJF4J_put(k.toString(), toObject(m.get(k)));
      } catch (JException e) {
        e.printStackTrace();
      }
    }
  }


  public static JsonElement toJsonElement(Object o) {
    if(o instanceof JsonElement) return (JsonElement) o;
    else if (o == null) return JsonNull.INSTANCE;
    else if (o instanceof JNull) return JsonNull.INSTANCE;
    else if (o instanceof String) return new JsonPrimitive((String) o);
    else if (o instanceof Number) return new JsonPrimitive((Number) o);
    else if (o instanceof Boolean) return new JsonPrimitive((Boolean) o);
    else if (o instanceof Character) return new JsonPrimitive((Character) o);
    else if (o instanceof JObject) return new JsonObject((JObject) o);
    else if (o instanceof JArray) return new JsonArray((JArray) o);
    else if (o instanceof Map) return new JsonObject((Map) o);
    else if (o instanceof List) return new JsonArray((List) o);
    return null;
  }

  public static Object toObject(Object e) {
    if(e instanceof JsonNull) return JNull.getInstance();
    else if (e == null) return JNull.getInstance();
    else if (e instanceof JsonPrimitive) {
      JsonPrimitive p = (JsonPrimitive) e;
      if (p.isBoolean()) return p.getAsBoolean();
      else if (p.isString()) return p.getAsString();
      else if (p.isNumber()) return p.getAsNumber();
    } else if (e instanceof JsonArray) return ((JsonArray) e).json;
    else if (e instanceof JsonObject) return ((JsonObject) e).json;
    return e;
  }

  public static boolean equivalence(Object a, Object b) {
    if(a == null) return b == null;
    if(b == null) return false;
    if(a.getClass() != b.getClass()) {
      return false;
    } else if(a.equals(b)) {
      return true;
    } else if(a instanceof JsonObject) {
      JsonObject ao, bo;
      ao = (JsonObject) a;
      bo = (JsonObject) b;
      if(ao.size() != bo.size()) {
        return false;
      }
      for(String key: ao.keySet()) {
        if(!bo.has(key)) return false;
        if(!equivalence(ao.get(key), bo.get(key))) return false;
      }
      return true;
    } else if(a instanceof JObject) {
      JObject ao, bo;
      ao = (JObject) a;
      bo = (JObject) b;
      if(ao.YASJF4J_getKeys().size() != bo.YASJF4J_getKeys().size()) {
        return false;
      }
      for(String key: ao.YASJF4J_getKeys()) {
        if(!bo.YASJF4J_getKeys().contains(key)) return false;
        try {
          if(!equivalence(ao.YASJF4J_get(key), bo.YASJF4J_get(key))) return false;
        } catch (JException e) {
          return false;
        }
      }
      return true;

    } else if(a instanceof JsonArray) {
      JsonArray ao, bo;
      ao = (JsonArray) a;
      bo = (JsonArray) b;
      if(ao.size() != bo.size()) {
        return false;
      }
      for(int i = 0; i < ao.size(); i++) {
        if (!equivalence(ao.get(i), bo.get(i))) return false;
      }
      return true;
    } else if(a instanceof JArray) {
      JArray ao, bo;
      ao = (JArray) a;
      bo = (JArray) b;
      if(ao.YASJF4J_size() != bo.YASJF4J_size()) {
        return false;
      }
      for(int i = 0; i < ao.YASJF4J_size(); i++) {
        try {
          if (!equivalence(ao.YASJF4J_get(i), bo.YASJF4J_get(i))) return false;
        } catch (JException e) {
          return false;
        }
      }
      return true;

    }
    return false;
  }
}
