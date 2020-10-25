package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.RawValue;
import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Node class that represents Arrays mapped from JSON content.
 *<p>
 * Note: class was <code>final</code> temporarily for Jackson 2.2.
 */
public class ArrayNode
    extends ContainerNode<ArrayNode>
    implements java.io.Serializable // since 2.10
{
    private static final long serialVersionUID = 1L;

    //private final List<JsonNode> _children;
    protected JArray json = JFactory.createJArray();

    public ArrayNode(Object o) {
        super(((JsonNodeFactory) (o instanceof JsonNodeFactory ? o : JsonNodeFactory.instance)));
        //super(JsonNodeFactory.instance);
        if(o instanceof JArray) {
            json = (JArray) o;
        } else if (o instanceof List) {
            List l = (List) o;
            json = JFactory.createJArray();
            for(Object e: l) {
                try {
                    json.YASJF4J_add(ObjectNode.toObject(e));
                } catch (JException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /*public ArrayNode(JsonNodeFactory nf) {
        super(nf);
        //_children = new ArrayList<JsonNode>();
    }*/
    /*public ArrayNode(JArray a) {
        super(JsonNodeFactory.instance);
        json = a;
    }*/

    /*public ArrayNode(List l) {
        super(JsonNodeFactory.instance);
        json = JFactory.createJArray();
        for(Object o: l) {
            try {
                json.YASJF4J_add(ObjectNode.toObject(o));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
    }*/

    /**
     * @since 2.8
     */
    public ArrayNode(JsonNodeFactory nf, int capacity) {
        super(nf);
        //_children = new ArrayList<JsonNode>(capacity);
    }

    /**
     * @since 2.7
     */
    public ArrayNode(JsonNodeFactory nf, List<JsonNode> children) {
        super(nf);
        //_children = children;
        for(JsonNode c: children) {
            try {
                json.YASJF4J_add(ObjectNode.toObject(c));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected JsonNode _at(JsonPointer ptr) {
        return get(ptr.getMatchingIndex());
    }

    // note: co-variant to allow caller-side type safety
    @SuppressWarnings("unchecked")
    @Override
    public ArrayNode deepCopy()
    {
        /*ArrayNode ret = new ArrayNode(_nodeFactory);

        for (JsonNode element: _children)
            ret._children.add(element.deepCopy());

        return ret;*/
        ArrayNode result = new ArrayNode(_nodeFactory);
        for(int i = 0; i < json.YASJF4J_size(); i++) {
            try {
                result.json.YASJF4J_add(ObjectNode.deepCopy(json.YASJF4J_get(i)));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /*
    /**********************************************************
    /* Overrides for JsonSerializable.Base
    /**********************************************************
     */

    @Override
    public boolean isEmpty(SerializerProvider serializers) {
        //return _children.isEmpty();
        return json.YASJF4J_size() == 0;
    }

    /*
    /**********************************************************
    /* Implementation of core JsonNode API
    /**********************************************************
     */

    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.ARRAY;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override public JsonToken asToken() { return JsonToken.START_ARRAY; }

    @Override
    public int size() {
        //return _children.size();
        return json.YASJF4J_size();
    }

    @Override // since 2.10
    public boolean isEmpty() {
        //return _children.isEmpty();
        return json.YASJF4J_size() == 0;
        }

    @Override
    public Iterator<JsonNode> elements() {
        //return _children.iterator();
        List<JsonNode> res = new ArrayList<>();
        for(int i = 0; i < json.YASJF4J_size(); i++) {
            try {
                res.add(ObjectNode.toJsonNode(json.YASJF4J_get(i)));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return res.iterator();
    }

    @Override
    public JsonNode get(int index) {
        /*if ((index >= 0) && (index < _children.size())) {
            return _children.get(index);
        }
        return null;*/

        if ((index >= 0) && (index < json.YASJF4J_size())) {
            try {
                return ObjectNode.toJsonNode(json.YASJF4J_get(index));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public JsonNode get(String fieldName) { return null; }

    @Override
    public JsonNode path(String fieldName) { return MissingNode.getInstance(); }

    @Override
    public JsonNode path(int index) {
       /* if (index >= 0 && index < _children.size()) {
            return _children.get(index);
        }
        return MissingNode.getInstance();*/
        if ((index >= 0) && (index < json.YASJF4J_size())) {
            try {
                return ObjectNode.toJsonNode(json.YASJF4J_get(index));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return MissingNode.getInstance();
    }

    @Override
    public JsonNode required(int index) {
        /*if ((index >= 0) && (index < _children.size())) {
            return _children.get(index);
        }
        return _reportRequiredViolation("No value at index #%d [0, %d) of `ArrayNode`",
                index, _children.size());*/

        if ((index >= 0) && (index < json.YASJF4J_size())) {
            try {
                return ObjectNode.toJsonNode(json.YASJF4J_get(index));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return _reportRequiredViolation("No value at index #%d [0, %d) of `ArrayNode`",
                index, json.YASJF4J_size());
    }

    @Override
    public boolean equals(Comparator<JsonNode> comparator, JsonNode o)
    {
        /*if (!(o instanceof ArrayNode)) {
            return false;
        }
        ArrayNode other = (ArrayNode) o;
        final int len = _children.size();
        if (other.size() != len) {
            return false;
        }
        List<JsonNode> l1 = _children;
        List<JsonNode> l2 = other._children;
        for (int i = 0; i < len; ++i) {
            if (!l1.get(i).equals(comparator, l2.get(i))) {
                return false;
            }
        }
        return true;*/
        if (!(o instanceof ArrayNode)) {
            return false;
        }
        ArrayNode other = (ArrayNode) o;
        final int len = json.YASJF4J_size();
        if (other.size() != len) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            try {
                if (!ObjectNode.toJsonNode(json.YASJF4J_get(i)).equals(comparator, ObjectNode.toJsonNode(other.json.YASJF4J_get(i)))) {
                    return false;
                }
            } catch (JException e) {
                return false;
            }
        }
        return true;
    }

    /*
    /**********************************************************
    /* Public API, serialization
    /**********************************************************
     */

    @Override
    public void serialize(JsonGenerator f, SerializerProvider provider) throws IOException
    {
        /*final List<JsonNode> c = _children;
        final int size = c.size();
        f.writeStartArray(this, size);
        for (int i = 0; i < size; ++i) { // we'll typically have array list
            // For now, assuming it's either BaseJsonNode, JsonSerializable
            JsonNode n = c.get(i);
            ((BaseJsonNode) n).serialize(f, provider);
        }
        f.writeEndArray();*/
        final int size = json.YASJF4J_size();
        f.writeStartArray(this, size);
        for (int i = 0; i < size; ++i) { // we'll typically have array list
            // For now, assuming it's either BaseJsonNode, JsonSerializable
            try {
                JsonNode n = ObjectNode.toJsonNode(json.YASJF4J_get(i));
                ((BaseJsonNode) n).serialize(f, provider);
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        f.writeEndArray();
    }

    @Override
    public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
        throws IOException
    {
        /*WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(this, JsonToken.START_ARRAY));
        for (JsonNode n : _children) {
            ((BaseJsonNode)n).serialize(g, provider);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);*/
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(this, JsonToken.START_ARRAY));
        final int size = json.YASJF4J_size();
        for (int i = 0; i < size; ++i) { // we'll typically have array list
            // For now, assuming it's either BaseJsonNode, JsonSerializable
            try {
                JsonNode n = ObjectNode.toJsonNode(json.YASJF4J_get(i));
                ((BaseJsonNode) n).serialize(g, provider);
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    /*
    /**********************************************************
    /* Public API, finding value nodes
    /**********************************************************
     */

    @Override
    public JsonNode findValue(String fieldName)
    {
        /*for (JsonNode node : _children) {
            JsonNode value = node.findValue(fieldName);
            if (value != null) {
                return value;
            }
        }
        return null;*/
        for (int i = 0; i < json.YASJF4J_size(); ++i) { // we'll typically have array list
            // For now, assuming it's either BaseJsonNode, JsonSerializable
            try {
                JsonNode node = ObjectNode.toJsonNode(json.YASJF4J_get(i));
                JsonNode value = node.findValue(fieldName);
                if (value != null) {
                    return value;
                }
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar)
    {
        /*for (JsonNode node : _children) {
            foundSoFar = node.findValues(fieldName, foundSoFar);
        }
        return foundSoFar;*/
        for (int i = 0; i < json.YASJF4J_size(); ++i) {
            try {
                JsonNode node = ObjectNode.toJsonNode(json.YASJF4J_get(i));
                foundSoFar = node.findValues(fieldName, foundSoFar);
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return foundSoFar;
    }

    @Override
    public List<String> findValuesAsText(String fieldName, List<String> foundSoFar)
    {
        /*for (JsonNode node : _children) {
            foundSoFar = node.findValuesAsText(fieldName, foundSoFar);
        }
        return foundSoFar;*/
        for (int i = 0; i < json.YASJF4J_size(); ++i) {
            try {
                JsonNode node = ObjectNode.toJsonNode(json.YASJF4J_get(i));
                foundSoFar = node.findValuesAsText(fieldName, foundSoFar);
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return foundSoFar;
    }

    @Override
    public ObjectNode findParent(String fieldName)
    {
        /*for (JsonNode node : _children) {
            JsonNode parent = node.findParent(fieldName);
            if (parent != null) {
                return (ObjectNode) parent;
            }
        }
        return null;*/
        for (int i = 0; i < json.YASJF4J_size(); ++i) {
            try {
                JsonNode node = ObjectNode.toJsonNode(json.YASJF4J_get(i));
                JsonNode parent = node.findParent(fieldName);
                if (parent != null) {
                    return (ObjectNode) parent;
                }
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar)
    {
        /*for (JsonNode node : _children) {
            foundSoFar = node.findParents(fieldName, foundSoFar);
        }
        return foundSoFar;*/
        for (int i = 0; i < json.YASJF4J_size(); ++i) {
            try {
                JsonNode node = ObjectNode.toJsonNode(json.YASJF4J_get(i));
                foundSoFar = node.findParents(fieldName, foundSoFar);
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return foundSoFar;
    }

    /*
    /**********************************************************
    /* Extended ObjectNode API, accessors
    /**********************************************************
     */

    /**
     * Method that will set specified field, replacing old value,
     * if any.
     *
     * @param value to set field to; if null, will be converted
     *   to a {@link NullNode} first  (to remove field entry, call
     *   {@link #remove} instead)
     *
     * @return Old value of the field, if any; null if there was no
     *   old value.
     */
    public JsonNode set(int index, JsonNode value)
    {
        /*if (value == null) { // let's not store 'raw' nulls but nodes
            value = nullNode();
        }
        if (index < 0 || index >= _children.size()) {
            throw new IndexOutOfBoundsException("Illegal index " + index + ", array size "+size());
        }
        return _children.set(index, value);*/
        if (value == null) { // let's not store 'raw' nulls but nodes
            value = nullNode();
        }
        if (index < 0 || index >= json.YASJF4J_size()) {
            throw new IndexOutOfBoundsException("Illegal index " + index + ", array size "+size());
        }
        JsonNode old = null;
        try {
            old = ObjectNode.toJsonNode(json.YASJF4J_get(index));
        } catch (JException e) { }
        try {
            json.YASJF4J_set(index, ObjectNode.toObject(value));
        } catch (JException e) { }
        return old;
    }

    /**
     * Method for adding specified node at the end of this array.
     *
     * @return This node, to allow chaining
     */
    public ArrayNode add(JsonNode value)
    {
        if (value == null) { // let's not store 'raw' nulls but nodes
            value = nullNode();
        }
        _add(value);
        return this;
    }

    /**
     * Method for adding all child nodes of given Array, appending to
     * child nodes this array contains
     *
     * @param other Array to add contents from
     *
     * @return This node (to allow chaining)
     */
    public ArrayNode addAll(ArrayNode other)
    {
        /*_children.addAll(other._children);
        return this;*/
        for(int i = 0; i < other.json.YASJF4J_size(); i++) {
            try {
                json.YASJF4J_add(other.json.YASJF4J_get(i));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * Method for adding given nodes as child nodes of this array node.
     *
     * @param nodes Nodes to add
     *
     * @return This node (to allow chaining)
     */
    public ArrayNode addAll(Collection<? extends JsonNode> nodes)
    {
        for (JsonNode node : nodes) {
            add(node);
        }
        return this;
    }

    /**
     * Method for inserting specified child node as an element
     * of this Array. If index is 0 or less, it will be inserted as
     * the first element; if {@code >= size()}, appended at the end, and otherwise
     * inserted before existing element in specified index.
     * No exceptions are thrown for any index.
     *
     * @return This node (to allow chaining)
     */
    public ArrayNode insert(int index, JsonNode value)
    {
        if (value == null) {
            value = nullNode();
        }
        _insert(index, value);
        return this;
    }

    /**
     * Method for removing an entry from this ArrayNode.
     * Will return value of the entry at specified index, if entry existed;
     * null if not.
     *
     * @return Node removed, if any; null if none
     */
    public JsonNode remove(int index)
    {
        /*if (index >= 0 && index < _children.size()) {
            return _children.remove(index);
        }
        return null;*/
        if (index >= 0 && index < json.YASJF4J_size()) {
            JsonNode old = null;
            try {
                old = ObjectNode.toJsonNode(json.YASJF4J_get(index));
            } catch (JException e) { }
            try {
                json.YASJF4J_remove(index);
            } catch (JException e) { }
            return old;
        }
        return null;
    }

    /**
     * Method for removing all elements of this array, leaving the
     * array empty.
     *
     * @return This node (to allow chaining)
     */
    @Override
    public ArrayNode removeAll()
    {
        /*_children.clear();
        return this;*/
        json = JFactory.createJArray();
        return this;
    }

    /*
    /**********************************************************
    /* Extended ObjectNode API, mutators, generic; addXxx()/insertXxx()
    /**********************************************************
     */

    /**
     * Method that will construct an ArrayNode and add it at the end
     * of this array node.
     *
     * @return Newly constructed ArrayNode
     */
    public ArrayNode addArray()
    {
        ArrayNode n  = arrayNode();
        _add(n);
        return n;
    }

    /**
     * Method that will construct an ObjectNode and add it at the end
     * of this array node.
     *
     * @return Newly constructed ObjectNode
     */
    public ObjectNode addObject()
    {
        ObjectNode n  = objectNode();
        _add(n);
        return n;
    }

    /**
     * Method that will construct a POJONode and add it at the end
     * of this array node.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode addPOJO(Object value)
    {
        if (value == null) {
            addNull();
        } else {
            _add(pojoNode(value));
        }
        return this;
    }

    /**
     * @return This array node, to allow chaining
     *
     * @since 2.6
     */
    public ArrayNode addRawValue(RawValue raw) {
        if (raw == null) {
            addNull();
        } else {
            _add(rawValueNode(raw));
        }
        return this;
    }

    /**
     * Method that will add a null value at the end of this array node.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode addNull()
    {
        _add(nullNode());
        return this;
    }

    /**
     * Method for adding specified number at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(int v) {
        _add(numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(Integer value) {
        if (value == null) {
            return addNull();
        }
        return _add(numberNode(value.intValue()));
    }

    /**
     * Method for adding specified number at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(long v) { return _add(numberNode(v)); }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(Long value) {
        if (value == null) {
            return addNull();
        }
        return _add(numberNode(value.longValue()));
    }

    /**
     * Method for adding specified number at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(float v) {
        return _add(numberNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(Float value) {
        if (value == null) {
            return addNull();
        }
        return _add(numberNode(value.floatValue()));
    }

    /**
     * Method for adding specified number at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(double v) {
        return _add(numberNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(Double value) {
        if (value == null) {
            return addNull();
        }
        return _add(numberNode(value.doubleValue()));
    }

    /**
     * Method for adding specified number at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(BigDecimal v) {
        if (v == null) {
            return addNull();
        }
        return _add(numberNode(v));
    }

    /**
     * Method for adding specified number at the end of this array.
     *
     * @return This array node, to allow chaining
     *
     * @since 2.9
     */
    public ArrayNode add(BigInteger v) {
        if (v == null) {
            return addNull();
        }
        return _add(numberNode(v));
    }
    
    /**
     * Method for adding specified String value at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(String v) {
        if (v == null) {
            return addNull();
        }
        return _add(textNode(v));
    }

    /**
     * Method for adding specified boolean value at the end of this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(boolean v) {
        return _add(booleanNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(Boolean value) {
        if (value == null) {
            return addNull();
        }
        return _add(booleanNode(value.booleanValue()));
    }

    /**
     * Method for adding specified binary value at the end of this array
     * (note: when serializing as JSON, will be output Base64 encoded)
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode add(byte[] v) {
        if (v == null) {
            return addNull();
        }
        return _add(binaryNode(v));
    }

    /**
     * Method for creating an array node, inserting it at the
     * specified point in the array,
     * and returning the <b>newly created array</b>
     * (note: NOT 'this' array)
     */
    public ArrayNode insertArray(int index)
    {
        ArrayNode n  = arrayNode();
        _insert(index, n);
        return n;
    }

    /**
     * Method for creating an {@link ObjectNode}, appending it at the end
     * of this array, and returning the <b>newly created node</b>
     * (note: NOT 'this' array)
     *
     * @return Newly constructed ObjectNode
     */
    public ObjectNode insertObject(int index)
    {
        ObjectNode n  = objectNode();
        _insert(index, n);
        return n;
    }

    /**
     * Method that will construct a POJONode and
     * insert it at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insertPOJO(int index, Object value)
    {
        if (value == null) {
            return insertNull(index);
        }
        return _insert(index, pojoNode(value));
    }

    /**
     * Method that will insert a null value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insertNull(int index)
    {
        _insert(index, nullNode());
        return this;
    }

    /**
     * Method that will insert specified numeric value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, int v) {
        _insert(index, numberNode(v));
        return this;
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, Integer value) {
        if (value == null) {
            insertNull(index);
        } else {
            _insert(index, numberNode(value.intValue()));
        }
        return this;
    }

    /**
     * Method that will insert specified numeric value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, long v) {
        return _insert(index, numberNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, Long value) {
        if (value == null) {
            return insertNull(index);
        }
        return _insert(index, numberNode(value.longValue()));
    }

    /**
     * Method that will insert specified numeric value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, float v) {
        return _insert(index, numberNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, Float value) {
        if (value == null) {
            return insertNull(index);
        }
        return _insert(index, numberNode(value.floatValue()));
    }

    /**
     * Method that will insert specified numeric value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, double v) {
        return _insert(index, numberNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, Double value) {
        if (value == null) {
            return insertNull(index);
        }
        return _insert(index, numberNode(value.doubleValue()));
    }

    /**
     * Method that will insert specified numeric value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, BigDecimal v) {
        if (v == null) {
            return insertNull(index);
        }
        return _insert(index, numberNode(v));
    }

    /**
     * Method that will insert specified numeric value
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     *
     * @since 2.9
     */
    public ArrayNode insert(int index, BigInteger v) {
        if (v == null) {
            return insertNull(index);
        }
        return _insert(index, numberNode(v));
    }
    
    /**
     * Method that will insert specified String
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, String v) {
        if (v == null) {
            return insertNull(index);
        }
        return _insert(index, textNode(v));
    }

    /**
     * Method that will insert specified String
     * at specified position in this array.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, boolean v) {
        return _insert(index, booleanNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, Boolean value) {
        if (value == null) {
            return insertNull(index);
        }
        return _insert(index, booleanNode(value.booleanValue()));
    }

    /**
     * Method that will insert specified binary value
     * at specified position in this array
     * (note: when written as JSON, will be Base64 encoded)
     *
     * @return This array node, to allow chaining
     */
    public ArrayNode insert(int index, byte[] v) {
        if (v == null) {
            return insertNull(index);
        }
        return _insert(index, binaryNode(v));
    }

    /*
    /**********************************************************
    /* Standard methods
    /**********************************************************
     */

    @Override
    public boolean equals(Object o)
    {
        /*if (o == this) return true;
        if (o == null) return false;
        if (o instanceof ArrayNode) {
            return _children.equals(((ArrayNode) o)._children);
        }
        return false;*/
        if (o == this) return true;
        if (o == null) return false;
        if (o instanceof ArrayNode) {
            return json.equals(((ArrayNode) o).json);
        }
        return false;
    }

    /**
     * @since 2.3
     */
    protected boolean _childrenEqual(ArrayNode other) {
        //return _children.equals(other._children);
        return json.equals(other.json);
    }

    @Override
    public int hashCode() {
        //return _children.hashCode();
        return json.hashCode();
    }

    /*
    /**********************************************************
    /* Internal methods (overridable)
    /**********************************************************
     */

    protected ArrayNode _add(JsonNode node) {
        /*_children.add(node);
        return this;*/
        try {
            json.YASJF4J_add(ObjectNode.toObject(node));
        } catch (JException e) {
            e.printStackTrace();
        }
        return this;
    }

    protected ArrayNode _insert(int index, JsonNode node)
    {
        /*if (index < 0) {
            _children.add(0, node);
        } else if (index >= _children.size()) {
            _children.add(node);
        } else {
            _children.add(index, node);
        }
        return this;*/
        if (index < 0 || index < json.YASJF4J_size()) {
            if(index < 0) {
                index = 0;
            }
            Object prev, cur;
            prev = ObjectNode.toObject(node);

            try {
                for(int i = index; i < json.YASJF4J_size(); i++) {
                        cur = json.YASJF4J_get(i);
                        json.YASJF4J_set(i, prev);
                        prev = cur;
                }
                json.YASJF4J_add(prev);
            } catch (JException e) {
                e.printStackTrace();
            }
        } else {
            try {
                json.YASJF4J_add(ObjectNode.toObject(node));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
}
