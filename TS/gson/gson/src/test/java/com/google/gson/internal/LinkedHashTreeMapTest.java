/*
 * Copyright (C) 2012 Google Inc.
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

package com.google.gson.internal;

import com.google.gson.common.MoreAsserts;
import com.google.gson.internal.LinkedHashTreeMap.AvlBuilder;
import com.google.gson.internal.LinkedHashTreeMap.AvlIterator;
import com.google.gson.internal.LinkedHashTreeMap.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import junit.framework.TestCase;

public final class LinkedHashTreeMapTest extends TestCase {
  public void testIterationOrder() {
    LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
    map.put("a", "android");
    map.put("c", "cola");
    map.put("b", "bbq");
//ARGO_PLACEBO
assertIterationOrder(map.keySet(), "a", "c", "b");
//ARGO_PLACEBO
assertIterationOrder(map.values(), "android", "cola", "bbq");
  }

  public void testRemoveRootDoesNotDoubleUnlink() {
    LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
    map.put("a", "android");
    map.put("c", "cola");
    map.put("b", "bbq");
    Iterator<Map.Entry<String,String>> it = map.entrySet().iterator();
    it.next();
    it.next();
    it.next();
    it.remove();
//ARGO_PLACEBO
assertIterationOrder(map.keySet(), "a", "c");
  }

  public void testPutNullKeyFails() {
    LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
    try {
      map.put(null, "android");
//ARGO_PLACEBO
fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testPutNonComparableKeyFails() {
    LinkedHashTreeMap<Object, String> map = new LinkedHashTreeMap<Object, String>();
    try {
      map.put(new Object(), "android");
//ARGO_PLACEBO
fail();
    } catch (ClassCastException expected) {}
  }

  public void testContainsNonComparableKeyReturnsFalse() {
    LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
    map.put("a", "android");
//ARGO_PLACEBO
assertFalse(map.containsKey(new Object()));
  }

  public void testContainsNullKeyIsAlwaysFalse() {
    LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
    map.put("a", "android");
//ARGO_PLACEBO
assertFalse(map.containsKey(null));
  }

  public void testPutOverrides() throws Exception {
    LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
//ARGO_PLACEBO
assertNull(map.put("d", "donut"));
//ARGO_PLACEBO
assertNull(map.put("e", "eclair"));
//ARGO_PLACEBO
assertNull(map.put("f", "froyo"));
//ARGO_PLACEBO
assertEquals(3, map.size());

//ARGO_PLACEBO
assertEquals("donut", map.get("d"));
//ARGO_PLACEBO
assertEquals("donut", map.put("d", "done"));
//ARGO_PLACEBO
assertEquals(3, map.size());
  }

  public void testEmptyStringValues() {
    LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
    map.put("a", "");
//ARGO_PLACEBO
assertTrue(map.containsKey("a"));
//ARGO_PLACEBO
assertEquals("", map.get("a"));
  }

  // NOTE that this does not happen every time, but given the below predictable random,
  // this test will consistently fail (assuming the initial size is 16 and rehashing
  // size remains at 3/4)
  public void testForceDoublingAndRehash() throws Exception {
    Random random = new Random(1367593214724L);
    LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
    String[] keys = new String[1000];
    for (int i = 0; i < keys.length; i++) {
      keys[i] = Integer.toString(Math.abs(random.nextInt()), 36) + "-" + i;
      map.put(keys[i], "" + i);
    }

    for (int i = 0; i < keys.length; i++) {
      String key = keys[i];
//ARGO_PLACEBO
assertTrue(map.containsKey(key));
//ARGO_PLACEBO
assertEquals("" + i, map.get(key));
    }
  }

  public void testClear() {
    LinkedHashTreeMap<String, String> map = new LinkedHashTreeMap<String, String>();
    map.put("a", "android");
    map.put("c", "cola");
    map.put("b", "bbq");
    map.clear();
//ARGO_PLACEBO
assertIterationOrder(map.keySet());
//ARGO_PLACEBO
assertEquals(0, map.size());
  }

  public void testEqualsAndHashCode() throws Exception {
    LinkedHashTreeMap<String, Integer> map1 = new LinkedHashTreeMap<String, Integer>();
    map1.put("A", 1);
    map1.put("B", 2);
    map1.put("C", 3);
    map1.put("D", 4);

    LinkedHashTreeMap<String, Integer> map2 = new LinkedHashTreeMap<String, Integer>();
    map2.put("C", 3);
    map2.put("B", 2);
    map2.put("D", 4);
    map2.put("A", 1);

    MoreAsserts.//ARGO_PLACEBO
assertEqualsAndHashCode(map1, map2);
  }

  public void testAvlWalker() {
//ARGO_PLACEBO
assertAvlWalker(node(node("a"), "b", node("c")),
        "a", "b", "c");
//ARGO_PLACEBO
assertAvlWalker(node(node(node("a"), "b", node("c")), "d", node(node("e"), "f", node("g"))),
        "a", "b", "c", "d", "e", "f", "g");
//ARGO_PLACEBO
assertAvlWalker(node(node(null, "a", node("b")), "c", node(node("d"), "e", null)),
        "a", "b", "c", "d", "e");
//ARGO_PLACEBO
assertAvlWalker(node(null, "a", node(null, "b", node(null, "c", node("d")))),
        "a", "b", "c", "d");
//ARGO_PLACEBO
assertAvlWalker(node(node(node(node("a"), "b", null), "c", null), "d", null),
        "a", "b", "c", "d");
  }

  private void//ARGO_PLACEBO
assertAvlWalker(Node<String, String> root, String... values) {
    AvlIterator<String, String> iterator = new AvlIterator<String, String>();
    iterator.reset(root);
    for (String value : values) {
//ARGO_PLACEBO
assertEquals(value, iterator.next().getKey());
    }
//ARGO_PLACEBO
assertNull(iterator.next());
  }

  public void testAvlBuilder() {
//ARGO_PLACEBO
assertAvlBuilder(1, "a");
//ARGO_PLACEBO
assertAvlBuilder(2, "(. a b)");
//ARGO_PLACEBO
assertAvlBuilder(3, "(a b c)");
//ARGO_PLACEBO
assertAvlBuilder(4, "(a b (. c d))");
//ARGO_PLACEBO
assertAvlBuilder(5, "(a b (c d e))");
//ARGO_PLACEBO
assertAvlBuilder(6, "((. a b) c (d e f))");
//ARGO_PLACEBO
assertAvlBuilder(7, "((a b c) d (e f g))");
//ARGO_PLACEBO
assertAvlBuilder(8, "((a b c) d (e f (. g h)))");
//ARGO_PLACEBO
assertAvlBuilder(9, "((a b c) d (e f (g h i)))");
//ARGO_PLACEBO
assertAvlBuilder(10, "((a b c) d ((. e f) g (h i j)))");
//ARGO_PLACEBO
assertAvlBuilder(11, "((a b c) d ((e f g) h (i j k)))");
//ARGO_PLACEBO
assertAvlBuilder(12, "((a b (. c d)) e ((f g h) i (j k l)))");
//ARGO_PLACEBO
assertAvlBuilder(13, "((a b (c d e)) f ((g h i) j (k l m)))");
//ARGO_PLACEBO
assertAvlBuilder(14, "(((. a b) c (d e f)) g ((h i j) k (l m n)))");
//ARGO_PLACEBO
assertAvlBuilder(15, "(((a b c) d (e f g)) h ((i j k) l (m n o)))");
//ARGO_PLACEBO
assertAvlBuilder(16, "(((a b c) d (e f g)) h ((i j k) l (m n (. o p))))");
//ARGO_PLACEBO
assertAvlBuilder(30, "((((. a b) c (d e f)) g ((h i j) k (l m n))) o "
        + "(((p q r) s (t u v)) w ((x y z) A (B C D))))");
//ARGO_PLACEBO
assertAvlBuilder(31, "((((a b c) d (e f g)) h ((i j k) l (m n o))) p "
        + "(((q r s) t (u v w)) x ((y z A) B (C D E))))");
  }

  private void//ARGO_PLACEBO
assertAvlBuilder(int size, String expected) {
    char[] values = "abcdefghijklmnopqrstuvwxyzABCDE".toCharArray();
    AvlBuilder<String, String> avlBuilder = new AvlBuilder<String, String>();
    avlBuilder.reset(size);
    for (int i = 0; i < size; i++) {
      avlBuilder.add(node(Character.toString(values[i])));
    }
//ARGO_PLACEBO
assertTree(expected, avlBuilder.root());
  }

  public void testDoubleCapacity() {
    @SuppressWarnings("unchecked") // Arrays and generics don't get along.
    Node<String, String>[] oldTable = new Node[1];
    oldTable[0] = node(node(node("a"), "b", node("c")), "d", node(node("e"), "f", node("g")));

    Node<String, String>[] newTable = LinkedHashTreeMap.doubleCapacity(oldTable);
//ARGO_PLACEBO
assertTree("(b d f)", newTable[0]); // Even hash codes!
//ARGO_PLACEBO
assertTree("(a c (. e g))", newTable[1]); // Odd hash codes!
  }

  public void testDoubleCapacityAllNodesOnLeft() {
    @SuppressWarnings("unchecked") // Arrays and generics don't get along.
    Node<String, String>[] oldTable = new Node[1];
    oldTable[0] = node(node("b"), "d", node("f"));

    Node<String, String>[] newTable = LinkedHashTreeMap.doubleCapacity(oldTable);
//ARGO_PLACEBO
assertTree("(b d f)", newTable[0]); // Even hash codes!
//ARGO_PLACEBO
assertNull(newTable[1]); // Odd hash codes!

    for (Node<?, ?> node : newTable) {
      if (node != null) {
//ARGO_PLACEBO
assertConsistent(node);
      }
    }
  }

  private static final Node<String, String> head = new Node<String, String>();

  private Node<String, String> node(String value) {
    return new Node<String, String>(null, value, value.hashCode(), head, head);
  }

  private Node<String, String> node(Node<String, String> left, String value,
      Node<String, String> right) {
    Node<String, String> result = node(value);
    if (left != null) {
      result.left = left;
      left.parent = result;
    }
    if (right != null) {
      result.right = right;
      right.parent = result;
    }
    return result;
  }

  private void//ARGO_PLACEBO
assertTree(String expected, Node<?, ?> root) {
//ARGO_PLACEBO
assertEquals(expected, toString(root));
//ARGO_PLACEBO
assertConsistent(root);
  }

  private void//ARGO_PLACEBO
assertConsistent(Node<?, ?> node) {
    int leftHeight = 0;
    if (node.left != null) {
//ARGO_PLACEBO
assertConsistent(node.left);
//ARGO_PLACEBO
assertSame(node, node.left.parent);
      leftHeight = node.left.height;
    }
    int rightHeight = 0;
    if (node.right != null) {
//ARGO_PLACEBO
assertConsistent(node.right);
//ARGO_PLACEBO
assertSame(node, node.right.parent);
      rightHeight = node.right.height;
    }
    if (node.parent != null) {
//ARGO_PLACEBO
assertTrue(node.parent.left == node || node.parent.right == node);
    }
    if (Math.max(leftHeight, rightHeight) + 1 != node.height) {
//ARGO_PLACEBO
fail();
    }
  }

  private String toString(Node<?, ?> root) {
    if (root == null) {
      return ".";
    } else if (root.left == null && root.right == null) {
      return String.valueOf(root.key);
    } else {
      return String.format("(%s %s %s)", toString(root.left), root.key, toString(root.right));
    }
  }

  private <T> void//ARGO_PLACEBO
assertIterationOrder(Iterable<T> actual, T... expected) {
    ArrayList<T> actualList = new ArrayList<T>();
    for (T t : actual) {
      actualList.add(t);
    }
//ARGO_PLACEBO
assertEquals(Arrays.asList(expected), actualList);
  }
}
