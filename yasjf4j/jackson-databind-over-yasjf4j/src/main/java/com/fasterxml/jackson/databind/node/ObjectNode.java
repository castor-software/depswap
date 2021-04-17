package com.fasterxml.jackson.databind.node;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.RawValue;
import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JNull;
import se.kth.castor.yasjf4j.JObject;

/**
 * Node that maps to JSON Object structures in JSON content.
 *<p>
 * Note: class was <code>final</code> temporarily for Jackson 2.2.
 */
public class ObjectNode
    extends ContainerNode<ObjectNode>
    implements java.io.Serializable
{
    private static final long serialVersionUID = 1L; // since 2.10

    // Note: LinkedHashMap for backwards compatibility
    //protected final Map<String, JsonNode> _children;
    protected JObject json = JFactory.createJObject();

    public ObjectNode(Object o) {
        super(((JsonNodeFactory) (o instanceof JsonNodeFactory ? o : JsonNodeFactory.instance)));
        if(o instanceof JObject) {
            json = (JObject) o;
        } else if (o instanceof Map) {
            Map m = (Map) o;
            for(Object k: m.keySet()) {
                try {
                    json.YASJF4J_put(k.toString(), toObject(m.get(k)));
                } catch (JException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*public ObjectNode(Map m) {
        super(JsonNodeFactory.instance);
        for(Object k: m.keySet()) {
            try {
                json.YASJF4J_put(k.toString(), toObject(m.get(k)));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
    }*/


    public static JsonNode toJsonNode(Object o) {
        if(o instanceof JsonNode) return (JsonNode) o;
        else if (o instanceof JNull) return NullNode.getInstance();
        else if (o == null) return NullNode.getInstance();
        else if (o instanceof String) return new TextNode((String) o);
        else if (o instanceof Integer) return new IntNode((Integer) o);
        else if (o instanceof Long) return new LongNode((Long) o);
        else if (o instanceof Short) return new ShortNode((Short) o);
        else if (o instanceof Float) return new FloatNode((Float) o);
        else if (o instanceof Double) return new DoubleNode((Double) o);
        else if (o instanceof BigInteger) return new BigIntegerNode((BigInteger) o);
        else if (o instanceof BigDecimal) return new DecimalNode((BigDecimal) o);
        else if (o instanceof Boolean) return new BooleanNode((Boolean) o);
        else if (o instanceof Character) return new TextNode(((Character) o).toString());
        else if (o instanceof JObject) return new ObjectNode((JObject) o);
        else if (o instanceof JArray) return new ArrayNode((JArray) o);
        else if (o instanceof Map) return new ObjectNode((Map) o);
        else if (o instanceof List) return new ArrayNode((List) o);
        else return new POJONode(o);
        //return null;
    }

    public static Object toObject(Object e) {
        if(e instanceof NullNode) return JNull.getInstance();
        else if (e instanceof TextNode) return ((TextNode) e).asText();
        else if (e instanceof IntNode) return ((IntNode) e).intValue();
        else if (e instanceof LongNode) return ((LongNode) e).longValue();
        else if (e instanceof ShortNode) return ((ShortNode) e).shortValue();
        else if (e instanceof FloatNode) return ((FloatNode) e).floatValue();
        else if (e instanceof DoubleNode) return ((DoubleNode) e).doubleValue();
        else if (e instanceof BigIntegerNode) return ((BigIntegerNode) e).bigIntegerValue();
        else if (e instanceof DecimalNode) return ((DecimalNode) e).decimalValue();
        else if (e instanceof BooleanNode) return ((BooleanNode) e).booleanValue();
        else if (e instanceof ArrayNode) return ((ArrayNode) e).json;
        else if (e instanceof ObjectNode) return ((ObjectNode) e).json;
        else if (e instanceof POJONode) return ((POJONode) e)._value;
        return e;
    }


    public ObjectNode(JsonNodeFactory nc) {
        super(nc);
        //_children = new LinkedHashMap<String, JsonNode>();
    }

    /**
     * @since 2.4
     */
    public ObjectNode(JsonNodeFactory nc, Map<String, JsonNode> kids) {
        super(nc);
        //_children = kids;
        for(String key: kids.keySet()) {
            try {
                json.YASJF4J_put(key, toObject(kids.get(key)));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected JsonNode _at(JsonPointer ptr) {
        return get(ptr.getMatchingProperty());
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

    /* Question: should this delegate to `JsonNodeFactory`? It does not absolutely
     * have to, as long as sub-types override the method but...
     */
    // note: co-variant for type safety
    @SuppressWarnings("unchecked")
    @Override
    public ObjectNode deepCopy()
    {
        /*ObjectNode ret = new ObjectNode(_nodeFactory);

        for (Map.Entry<String, JsonNode> entry: _children.entrySet())
            ret._children.put(entry.getKey(), entry.getValue().deepCopy());

        return ret;*/
        ObjectNode result = new ObjectNode(_nodeFactory);
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

    /*
    /**********************************************************
    /* Overrides for JsonSerializable.Base
    /**********************************************************
     */

    @Override
    public boolean isEmpty(SerializerProvider serializers) {
        return json.YASJF4J_getKeys().size() == 0;
        //return _children.isEmpty();
    }

    /*
    /**********************************************************
    /* Implementation of core JsonNode API
    /**********************************************************
     */

    @Override
    public JsonNodeType getNodeType() {
        return JsonNodeType.OBJECT;
    }

    @Override
    public final boolean isObject() {
        return true;
    }
    
    @Override public JsonToken asToken() { return JsonToken.START_OBJECT; }

    @Override
    public int size() {
        return json.YASJF4J_getKeys().size();
        //return _children.size();
    }

    @Override // since 2.10
    public boolean isEmpty() {
        return json.YASJF4J_getKeys().size() == 0;
        // return _children.isEmpty();
    }
    
    @Override
    public Iterator<JsonNode> elements() {
        List<JsonNode> l = new ArrayList<JsonNode>();
        for(String key: json.YASJF4J_getKeys()) {
            try {
                l.add(toJsonNode(json.YASJF4J_get(key)));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return l.iterator();
        //return _children.values().iterator();
    }

    @Override
    public JsonNode get(int index) { return null; }

    @Override
    public JsonNode get(String fieldName) {
        try {
            if(!json.YASJF4J_getKeys().contains(fieldName)) {
                return null;
            }
            return toJsonNode(json.YASJF4J_get(fieldName));
        } catch (JException e) {
            e.printStackTrace();
        }
        return null;
        //return _children.get(fieldName);
    }

    @Override
    public Iterator<String> fieldNames() {
        return json.YASJF4J_getKeys().iterator();
        //return _children.keySet().iterator();
    }

    @Override
    public JsonNode path(int index) {
        return MissingNode.getInstance();
    }

    @Override
    public JsonNode path(String fieldName)
    {
        /*JsonNode n = _children.get(fieldName);
        if (n != null) {
            return n;
        }
        return MissingNode.getInstance();*/
        if(!json.YASJF4J_getKeys().contains(fieldName)) {
            return MissingNode.getInstance();
        }
        try {
            return toJsonNode(json.YASJF4J_get(fieldName));
        } catch (JException e) {
            e.printStackTrace();
        }
        return MissingNode.getInstance();
    }

    @Override
    public JsonNode required(String fieldName) {
        /*JsonNode n = _children.get(fieldName);
        if (n != null) {
            return n;
        }
        return _reportRequiredViolation("No value for property '%s' of `ObjectNode`", fieldName);*/
        try {
            if(json.YASJF4J_getKeys().contains(fieldName)) {
                return toJsonNode(json.YASJF4J_get(fieldName));
            }
        } catch (JException e) {
            e.printStackTrace();
        }
        return _reportRequiredViolation("No value for property '%s' of `ObjectNode`", fieldName);
    }

    /**
     * Method to use for accessing all fields (with both names
     * and values) of this JSON Object.
     */
    @Override
    public Iterator<Map.Entry<String, JsonNode>> fields() {
        /*Map<String, JsonNode> l = new LinkedHashMap<>();
        for(String key: json.YASJF4J_getKeys()) {
            try {
                l.put(key, toJsonNode(json.YASJF4J_get(key)));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return l.entrySet().iterator();*/
        //return _children.entrySet().iterator();
        return new Iterator<Map.Entry<String, JsonNode>>() {
            Iterator<String> properties = json.YASJF4J_getKeys().iterator();
            String cur;
            @Override
            public boolean hasNext() {
                return properties.hasNext();
            }

            @Override
            public Map.Entry<String, JsonNode> next() {
                cur = properties.next();
                JsonNode n = null;
                try {
                    n = toJsonNode(json.YASJF4J_get(cur));
                } catch (JException e) {
                }
                return new HashMap.SimpleEntry<>(cur, n);
            }

            @Override
            public void remove() {
                try {
                    json.YASJF4J_remove(cur);
                } catch (JException e) {
                }
            }
        };

    }

    @SuppressWarnings("unchecked")
    @Override
    public ObjectNode with(String propertyName) {
        /*JsonNode n = _children.get(propertyName);
        if (n != null) {
            if (n instanceof ObjectNode) {
                return (ObjectNode) n;
            }
            throw new UnsupportedOperationException("Property '" + propertyName
                + "' has value that is not of type ObjectNode (but " + n
                .getClass().getName() + ")");
        }
        ObjectNode result = objectNode();
        _children.put(propertyName, result);
        return result;*/

        try {
            Object o = json.YASJF4J_get(propertyName);
            if(o != null) {
                JsonNode n = toJsonNode(o);
                if (n instanceof ObjectNode) {
                    return (ObjectNode) n;
                }
                throw new UnsupportedOperationException("Property '" + propertyName
                        + "' has value that is not of type ObjectNode (but " + n
                        .getClass().getName() + ")");
            }
        } catch (JException e) {
        }
        ObjectNode result = objectNode();
        try {
            json.YASJF4J_put(propertyName, toObject(result));
        } catch (JException e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArrayNode withArray(String propertyName)
    {
        /*JsonNode n = _children.get(propertyName);
        if (n != null) {
            if (n instanceof ArrayNode) {
                return (ArrayNode) n;
            }
            throw new UnsupportedOperationException("Property '" + propertyName
                + "' has value that is not of type ArrayNode (but " + n
                .getClass().getName() + ")");
        }
        ArrayNode result = arrayNode();
        _children.put(propertyName, result);
        return result;*/

        try {
            Object o = json.YASJF4J_get(propertyName);
            if(o != null) {
                JsonNode n = toJsonNode(o);
                if (n instanceof ArrayNode) {
                    return (ArrayNode) n;
                }
                throw new UnsupportedOperationException("Property '" + propertyName
                        + "' has value that is not of type ArrayNode (but " + n
                        .getClass().getName() + ")");
            }
        } catch (JException e) {
        }
        ArrayNode result = new ArrayNode(_nodeFactory);
        try {
            json.YASJF4J_put(propertyName, toObject(result));
        } catch (JException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean equals(Comparator<JsonNode> comparator, JsonNode o)
    {
        /*if (!(o instanceof ObjectNode)) {
            return false;
        }
        ObjectNode other = (ObjectNode) o;
        Map<String, JsonNode> m1 = _children;
        Map<String, JsonNode> m2 = other._children;

        final int len = m1.size();
        if (m2.size() != len) {
            return false;
        }

        for (Map.Entry<String, JsonNode> entry : m1.entrySet()) {
            JsonNode v2 = m2.get(entry.getKey());
            if ((v2 == null) || !entry.getValue().equals(comparator, v2)) {
                return false;
            }
        }
        return true;*/

        if (!(o instanceof ObjectNode)) {
            return false;
        }
        ObjectNode other = (ObjectNode) o;
        JObject m1 = json;
        JObject m2 = other.json;

        final int len = m1.YASJF4J_getKeys().size();
        if (m2.YASJF4J_getKeys().size() != len) {
            return false;
        }

        for (String key: m1.YASJF4J_getKeys()) {
            JsonNode v2 = null;
            try {
                v2 = toJsonNode(m2.YASJF4J_get(key));
                if ((v2 == null) || !toJsonNode(m1.YASJF4J_get(key)).equals(comparator, v2)) {
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
    /* Public API, finding value nodes
    /**********************************************************
     */
    
    @Override
    public JsonNode findValue(String fieldName)
    {
        /*for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                return entry.getValue();
            }
            JsonNode value = entry.getValue().findValue(fieldName);
            if (value != null) {
                return value;
            }
        }
        return null;*/
        for (String key: json.YASJF4J_getKeys()) {
            if(key.equals(fieldName)) {
                try {
                    return toJsonNode(json.YASJF4J_get(key));
                } catch (JException e) {
                    return null;
                }

            }
            try {
                JsonNode value = toJsonNode(json.YASJF4J_get(key)).findValue(fieldName);
                if (value != null) {
                    return value;
                }
            } catch (JException e) {
            }
        }
        return null;
    }
    
    @Override
    public List<JsonNode> findValues(String fieldName, List<JsonNode> foundSoFar)
    {
        /*for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<JsonNode>();
                }
                foundSoFar.add(entry.getValue());
            } else { // only add children if parent not added
                foundSoFar = entry.getValue().findValues(fieldName, foundSoFar);
            }
        }
        return foundSoFar;*/
        for (String key: json.YASJF4J_getKeys()) {
            if(key.equals(fieldName)) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<JsonNode>();
                }
                try {
                    foundSoFar.add(toJsonNode(json.YASJF4J_get(key)));
                } catch (JException e) {
                }

            } else { // only add children if parent not added
                try {
                    JsonNode value = toJsonNode(json.YASJF4J_get(key));
                    foundSoFar = value.findValues(fieldName, foundSoFar);
                } catch (JException e) {
                }

            }
        }
        return foundSoFar;
    }

    @Override
    public List<String> findValuesAsText(String fieldName, List<String> foundSoFar)
    {
        /*for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<String>();
                }
                foundSoFar.add(entry.getValue().asText());
            } else { // only add children if parent not added
                foundSoFar = entry.getValue().findValuesAsText(fieldName,
                    foundSoFar);
            }
        }
        return foundSoFar;*/
        for (String key: json.YASJF4J_getKeys()) {
            if(key.equals(fieldName)) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<String>();
                }
                try {
                    foundSoFar.add(toJsonNode(json.YASJF4J_get(key)).asText());
                } catch (JException e) {
                }

            } else { // only add children if parent not added
                try {
                    JsonNode value = toJsonNode(json.YASJF4J_get(key));
                    foundSoFar = value.findValuesAsText(fieldName, foundSoFar);
                } catch (JException e) {
                }

            }
        }
        return foundSoFar;
    }
    
    @Override
    public ObjectNode findParent(String fieldName)
    {
        /*for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                return this;
            }
            JsonNode value = entry.getValue().findParent(fieldName);
            if (value != null) {
                return (ObjectNode) value;
            }
        }
        return null;*/
        for (String key: json.YASJF4J_getKeys()) {
            if (fieldName.equals(key)) {
                return this;
            }
            try {
                JsonNode value = toJsonNode(json.YASJF4J_get(key)).findParent(fieldName);
                if (value != null) {
                    return (ObjectNode) value;
                }
            } catch (JException e) {
            }
        }
        return null;
    }

    @Override
    public List<JsonNode> findParents(String fieldName, List<JsonNode> foundSoFar)
    {
        /*for (Map.Entry<String, JsonNode> entry : _children.entrySet()) {
            if (fieldName.equals(entry.getKey())) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<JsonNode>();
                }
                foundSoFar.add(this);
            } else { // only add children if parent not added
                foundSoFar = entry.getValue()
                    .findParents(fieldName, foundSoFar);
            }
        }
        return foundSoFar;*/
        for (String key: json.YASJF4J_getKeys()) {
            if (fieldName.equals(key)) {
                if (foundSoFar == null) {
                    foundSoFar = new ArrayList<JsonNode>();
                }
                foundSoFar.add(this);
            } else { // only add children if parent not added
                try {
                    JsonNode value = toJsonNode(json.YASJF4J_get(key)).findParent(fieldName);
                    if(value != null) {
                        foundSoFar = value
                                .findParents(fieldName, foundSoFar);
                    }
                } catch (JException e) {
                }
            }
        }
        return foundSoFar;
    }
    
    /*
    /**********************************************************
    /* Public API, serialization
    /**********************************************************
     */

    /**
     * Method that can be called to serialize this node and
     * all of its descendants using specified JSON generator.
     */
    @Override
    public void serialize(JsonGenerator g, SerializerProvider provider)
        throws IOException
    {
        /*@SuppressWarnings("deprecation")
        boolean trimEmptyArray = (provider != null) &&
                !provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        g.writeStartObject(this);
        for (Map.Entry<String, JsonNode> en : _children.entrySet()) {
            BaseJsonNode value = (BaseJsonNode) en.getValue();

            // as per [databind#867], see if WRITE_EMPTY_JSON_ARRAYS feature is disabled,
            // if the feature is disabled, then should not write an empty array
            // to the output, so continue to the next element in the iteration
            if (trimEmptyArray && value.isArray() && value.isEmpty(provider)) {
            	continue;
            }
            g.writeFieldName(en.getKey());
            value.serialize(g, provider);
        }
        g.writeEndObject();*/
        @SuppressWarnings("deprecation")
        boolean trimEmptyArray = (provider != null) &&
                !provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        g.writeStartObject(this);
        for (String key: json.YASJF4J_getKeys()) {
        //for (Map.Entry<String, JsonNode> en : _children.entrySet()) {
            /* 17-Feb-2009, tatu: Can we trust that all nodes will always
             *   extend BaseJsonNode? Or if not, at least implement
             *   JsonSerializable? Let's start with former, change if
             *   we must.
             */
            try {
                JsonNode val = toJsonNode(json.YASJF4J_get(key));
                BaseJsonNode value = (BaseJsonNode) val;

                // as per [databind#867], see if WRITE_EMPTY_JSON_ARRAYS feature is disabled,
                // if the feature is disabled, then should not write an empty array
                // to the output, so continue to the next element in the iteration
                if (trimEmptyArray && value.isArray() && value.isEmpty(provider)) {
                    continue;
                }
                g.writeFieldName(key);
                value.serialize(g, provider);

            } catch (JException e) {
            }
        }
        g.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator g, SerializerProvider provider,
            TypeSerializer typeSer)
        throws IOException
    {
        /*@SuppressWarnings("deprecation")
        boolean trimEmptyArray = (provider != null) &&
                !provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);

        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(this, JsonToken.START_OBJECT));
        for (Map.Entry<String, JsonNode> en : _children.entrySet()) {
            BaseJsonNode value = (BaseJsonNode) en.getValue();

            // check if WRITE_EMPTY_JSON_ARRAYS feature is disabled,
            // if the feature is disabled, then should not write an empty array
            // to the output, so continue to the next element in the iteration
            if (trimEmptyArray && value.isArray() && value.isEmpty(provider)) {
                continue;
            }
            
            g.writeFieldName(en.getKey());
            value.serialize(g, provider);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);*/
        @SuppressWarnings("deprecation")
        boolean trimEmptyArray = (provider != null) &&
                !provider.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);

        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(this, JsonToken.START_OBJECT));
        for (String key: json.YASJF4J_getKeys()) {
            try {
                JsonNode val = toJsonNode(json.YASJF4J_get(key));
                BaseJsonNode value = (BaseJsonNode) val;

                // check if WRITE_EMPTY_JSON_ARRAYS feature is disabled,
                // if the feature is disabled, then should not write an empty array
                // to the output, so continue to the next element in the iteration
                if (trimEmptyArray && value.isArray() && value.isEmpty(provider)) {
                    continue;
                }

                g.writeFieldName(key);
                value.serialize(g, provider);

            } catch (JException e) {
            }
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    /*
    /**********************************************************
    /* Extended ObjectNode API, mutators, since 2.1
    /**********************************************************
     */

    /**
     * Method that will set specified field, replacing old value, if any.
     * Note that this is identical to {@link #replace(String, JsonNode)},
     * except for return value.
     *<p>
     * NOTE: added to replace those uses of {@link #put(String, JsonNode)}
     * where chaining with 'this' is desired.
     *<p>
     * NOTE: co-variant return type since 2.10
     *
     * @param value to set field to; if null, will be converted
     *   to a {@link NullNode} first  (to remove field entry, call
     *   {@link #remove} instead)
     *
     * @return This node after adding/replacing property value (to allow chaining)
     *
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T set(String fieldName, JsonNode value)
    {
        /*if (value == null) {
            value = nullNode();
        }
        _children.put(fieldName, value);
        return (T) this;*/
        if (value == null) {
            value = nullNode();
        }
        try {
            json.YASJF4J_put(fieldName, toObject(value));
        } catch (JException e) {
            e.printStackTrace();
        }
        return (T) this;
    }

    /**
     * Method for adding given properties to this object node, overriding
     * any existing values for those properties.
     *<p>
     * NOTE: co-variant return type since 2.10
     * 
     * @param properties Properties to add
     * 
     * @return This node after adding/replacing property values (to allow chaining)
     *
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T setAll(Map<String,? extends JsonNode> properties)
    {
        /*for (Map.Entry<String,? extends JsonNode> en : properties.entrySet()) {
            JsonNode n = en.getValue();
            if (n == null) {
                n = nullNode();
            }
            _children.put(en.getKey(), n);
        }
        return (T) this;*/
        for (Map.Entry<String,? extends JsonNode> en : properties.entrySet()) {
            JsonNode n = en.getValue();
            if (n == null) {
                n = nullNode();
            }
            try {
                json.YASJF4J_put(en.getKey(), toObject(n));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return (T) this;
    }

    /**
     * Method for adding all properties of the given Object, overriding
     * any existing values for those properties.
     *<p>
     * NOTE: co-variant return type since 2.10
     * 
     * @param other Object of which properties to add to this object
     *
     * @return This node after addition (to allow chaining)
     *
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T setAll(ObjectNode other)
    {
        /*_children.putAll(other._children);
        return (T) this;*/
        for(String key: other.json.YASJF4J_getKeys()) {
            try {
                json.YASJF4J_put(key, other.json.YASJF4J_get(key));
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return (T) this;
    }

    /**
     * Method for replacing value of specific property with passed
     * value, and returning value (or null if none).
     *
     * @param fieldName Property of which value to replace
     * @param value Value to set property to, replacing old value if any
     * 
     * @return Old value of the property; null if there was no such property
     *   with value
     * 
     * @since 2.1
     */
    public JsonNode replace(String fieldName, JsonNode value)
    {
        /*if (value == null) { // let's not store 'raw' nulls but nodes
            value = nullNode();
        }
        return _children.put(fieldName, value);*/

        if (value == null) { // let's not store 'raw' nulls but nodes
            value = nullNode();
        }

        JsonNode old = null;
        try {
            if(json.YASJF4J_getKeys().contains(fieldName)) {
                old = toJsonNode(json.YASJF4J_get(fieldName));
            }
        } catch (JException e) {
            //e.printStackTrace();
        }
        try {
            json.YASJF4J_put(fieldName, toObject(value));
        } catch (JException e) {
            e.printStackTrace();
        }
        return old;
    }

    /**
     * Method for removing field entry from this ObjectNode, and
     * returning instance after removal.
     *<p>
     * NOTE: co-variant return type since 2.10
     * 
     * @return This node after removing entry (if any)
     * 
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T without(String fieldName)
    {
        /*_children.remove(fieldName);
        return (T) this;*/
        try {
            json.YASJF4J_remove(fieldName);
        } catch (JException e) {
            e.printStackTrace();
        }
        return (T) this;
    }

    /**
     * Method for removing specified field properties out of
     * this ObjectNode.
     *<p>
     * NOTE: co-variant return type since 2.10
     * 
     * @param fieldNames Names of fields to remove
     * 
     * @return This node after removing entries
     * 
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
    public <T extends JsonNode> T without(Collection<String> fieldNames)
    {
        /*_children.keySet().removeAll(fieldNames);
        return (T) this;*/
        for(String key: fieldNames) {
            try {
                json.YASJF4J_remove(key);
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return (T) this;
    }
    
    /*
    /**********************************************************
    /* Extended ObjectNode API, mutators, generic
    /**********************************************************
     */
    
    /**
     * Method that will set specified field, replacing old value, if any.
     *
     * @param value to set field to; if null, will be converted
     *   to a {@link NullNode} first  (to remove field entry, call
     *   {@link #remove} instead)
     *   
     * @return Old value of the field, if any; null if there was no
     *   old value.
     *   
     * @deprecated Since 2.4 use either {@link #set(String,JsonNode)} or {@link #replace(String,JsonNode)},
     */
    @Deprecated
    public JsonNode put(String fieldName, JsonNode value)
    {
        /*if (value == null) { // let's not store 'raw' nulls but nodes
            value = nullNode();
        }
        return _children.put(fieldName, value);*/
        if (value == null) { // let's not store 'raw' nulls but nodes
            value = nullNode();
        }
        JsonNode old = null;
        try {
            old = toJsonNode(json .YASJF4J_get(fieldName));
        } catch (JException e) {
            e.printStackTrace();
        }
        try {
            json.YASJF4J_put(fieldName, toObject(value));
        } catch (JException e) {
            e.printStackTrace();
        }
        return old;
    }

    /**
     * Method for removing field entry from this ObjectNode.
     * Will return value of the field, if such field existed;
     * null if not.
     * 
     * @return Value of specified field, if it existed; null if not
     */
    public JsonNode remove(String fieldName) {
        //return _children.remove(fieldName);
        JsonNode old = null;
        try {
            old = toJsonNode(json .YASJF4J_get(fieldName));
        } catch (JException e) {
            e.printStackTrace();
        }
        try {
            json.YASJF4J_remove(fieldName);
        } catch (JException e) {
            e.printStackTrace();
        }
        return old;
    }

    /**
     * Method for removing specified field properties out of
     * this ObjectNode.
     * 
     * @param fieldNames Names of fields to remove
     * 
     * @return This node after removing entries
     */
    public ObjectNode remove(Collection<String> fieldNames)
    {
        /*_children.keySet().removeAll(fieldNames);
        return this;*/
        for(String key: fieldNames) {
            try {
                json.YASJF4J_remove(key);
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return this;
    }
    
    /**
     * Method for removing all field properties, such that this
     * ObjectNode will contain no properties after call.
     * 
     * @return This node after removing all entries
     */
    @Override
    public ObjectNode removeAll()
    {
        /*_children.clear();
        return this;*/
        json = JFactory.createJObject();
        return this;
    }

    /**
     * Method for adding given properties to this object node, overriding
     * any existing values for those properties.
     * 
     * @param properties Properties to add
     * 
     * @return This node after adding/replacing property values (to allow chaining)
     * 
     * @deprecated Since 2.4 use {@link #setAll(Map)},
     */
    @Deprecated
    public JsonNode putAll(Map<String,? extends JsonNode> properties) {
        return setAll(properties);
    }

    /**
     * Method for adding all properties of the given Object, overriding
     * any existing values for those properties.
     * 
     * @param other Object of which properties to add to this object
     * 
     * @return This node (to allow chaining)
     * 
     * @deprecated Since 2.4 use {@link #setAll(ObjectNode)},
     */
    @Deprecated
    public JsonNode putAll(ObjectNode other) {
        return setAll(other);
    }

    /**
     * Method for removing all field properties out of this ObjectNode
     * <b>except</b> for ones specified in argument.
     * 
     * @param fieldNames Fields to <b>retain</b> in this ObjectNode
     * 
     * @return This node (to allow call chaining)
     */
    public ObjectNode retain(Collection<String> fieldNames)
    {
        /*_children.keySet().retainAll(fieldNames);
        return this;*/
        Set<String> toRemove = new HashSet<>();
        for(String key: json.YASJF4J_getKeys()) {
                if(!fieldNames.contains(key)) {
                    toRemove.add(key);
                }
        }
        for(String k: toRemove) {
            try {
                json.YASJF4J_remove(k);
            } catch (JException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * Method for removing all field properties out of this ObjectNode
     * <b>except</b> for ones specified in argument.
     * 
     * @param fieldNames Fields to <b>retain</b> in this ObjectNode
     * 
     * @return This node (to allow call chaining)
     */
    public ObjectNode retain(String... fieldNames) {
        return retain(Arrays.asList(fieldNames));
    }
    
    /*
    /**********************************************************
    /* Extended ObjectNode API, mutators, typed
    /**********************************************************
     */

    /**
     * Method that will construct an ArrayNode and add it as a
     * field of this ObjectNode, replacing old value, if any.
     *<p>
     * <b>NOTE</b>: Unlike all <b>put(...)</b> methods, return value
     * is <b>NOT</b> this <code>ObjectNode</code>, but the
     * <b>newly created</b> <code>ArrayNode</code> instance.
     *
     * @return Newly constructed ArrayNode (NOT the old value,
     *   which could be of any type)
     */
    public ArrayNode putArray(String fieldName)
    {
        ArrayNode n  = arrayNode();
        _put(fieldName, n);
        return n;
    }

    /**
     * Method that will construct an ObjectNode and add it as a
     * field of this ObjectNode, replacing old value, if any.
     *<p>
     * <b>NOTE</b>: Unlike all <b>put(...)</b> methods, return value
     * is <b>NOT</b> this <code>ObjectNode</code>, but the
     * <b>newly created</b> <code>ObjectNode</code> instance.
     *
     * @return Newly constructed ObjectNode (NOT the old value,
     *   which could be of any type)
     */
    public ObjectNode putObject(String fieldName)
    {
        ObjectNode n = objectNode();
        _put(fieldName, n);
        return n;
    }

    /**
     * @return This node (to allow chaining)
     */
    public ObjectNode putPOJO(String fieldName, Object pojo) {
        return _put(fieldName, pojoNode(pojo));
    }

    /**
     * @since 2.6
     */
    public ObjectNode putRawValue(String fieldName, RawValue raw) {
        return _put(fieldName, rawValueNode(raw));
    }
    
    /**
     * @return This node (to allow chaining)
     */
    public ObjectNode putNull(String fieldName)
    {
        /*_children.put(fieldName, nullNode());
        return this;*/
        try {
            json.YASJF4J_put(fieldName, toObject(nullNode()));
        } catch (JException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Method for setting value of a field to specified numeric value.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, short v) {
        return _put(fieldName, numberNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, Short v) {
        return _put(fieldName, (v == null) ? nullNode()
                : numberNode(v.shortValue()));
    }

    /**
     * Method for setting value of a field to specified numeric value.
     * The underlying {@link JsonNode} that will be added is constructed
     * using {@link JsonNodeFactory#numberNode(int)}, and may be
     *  "smaller" (like {@link ShortNode}) in cases where value fits within
     *  range of a smaller integral numeric value.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, int v) {
        return _put(fieldName, numberNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, Integer v) {
        return _put(fieldName, (v == null) ? nullNode()
                : numberNode(v.intValue()));
    }
    
    /**
     * Method for setting value of a field to specified numeric value.
     * The underlying {@link JsonNode} that will be added is constructed
     * using {@link JsonNodeFactory#numberNode(long)}, and may be
     *  "smaller" (like {@link IntNode}) in cases where value fits within
     *  range of a smaller integral numeric value.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, long v) {
        return _put(fieldName, numberNode(v));
    }

    /**
     * Method for setting value of a field to specified numeric value.
     * The underlying {@link JsonNode} that will be added is constructed
     * using {@link JsonNodeFactory#numberNode(Long)}, and may be
     *  "smaller" (like {@link IntNode}) in cases where value fits within
     *  range of a smaller integral numeric value.
     * <p>
     * Note that this is alternative to {@link #put(String, long)} needed to avoid
     * bumping into NPE issues with auto-unboxing.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, Long v) {
        return _put(fieldName, (v == null) ? nullNode()
                : numberNode(v.longValue()));
    }
    
    /**
     * Method for setting value of a field to specified numeric value.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, float v) {
        return _put(fieldName, numberNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, Float v) {
        return _put(fieldName, (v == null) ? nullNode()
                : numberNode(v.floatValue()));
    }
    
    /**
     * Method for setting value of a field to specified numeric value.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, double v) {
        return _put(fieldName, numberNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, Double v) {
        return _put(fieldName, (v == null) ? nullNode()
                : numberNode(v.doubleValue()));
    }
    
    /**
     * Method for setting value of a field to specified numeric value.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, BigDecimal v) {
        return _put(fieldName, (v == null) ? nullNode()
                : numberNode(v));
    }

    /**
     * Method for setting value of a field to specified numeric value.
     * 
     * @return This node (to allow chaining)
     *
     * @since 2.9
     */
    public ObjectNode put(String fieldName, BigInteger v) {
        return _put(fieldName, (v == null) ? nullNode()
                : numberNode(v));
    }

    /**
     * Method for setting value of a field to specified String value.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, String v) {
        return _put(fieldName, (v == null) ? nullNode()
                : textNode(v));
    }

    /**
     * Method for setting value of a field to specified String value.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, boolean v) {
        return _put(fieldName, booleanNode(v));
    }

    /**
     * Alternative method that we need to avoid bumping into NPE issues
     * with auto-unboxing.
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, Boolean v) {
        return _put(fieldName, (v == null) ? nullNode()
                : booleanNode(v.booleanValue()));
    }
    
    /**
     * Method for setting value of a field to specified binary value
     * 
     * @return This node (to allow chaining)
     */
    public ObjectNode put(String fieldName, byte[] v) {
        return _put(fieldName, (v == null) ? nullNode()
                : binaryNode(v));
    }
    
    /*
    /**********************************************************
    /* Standard methods
    /**********************************************************
     */

    @Override
    public boolean equals(Object o)
    {
        if (o == this) return true;
        if (o == null) return false;
        if (o instanceof ObjectNode) {
            return _childrenEqual((ObjectNode) o);
        }
        return false;
    }

    /**
     * @since 2.3
     */
    protected boolean _childrenEqual(ObjectNode other)
    {
        //return _children.equals(other._children);
        return json.equals(other.json);
    }
    
    @Override
    public int hashCode()
    {
        //return _children.hashCode();
        return json.hashCode();
    }

    /*
    /**********************************************************
    /* Internal methods (overridable)
    /**********************************************************
     */

    protected ObjectNode _put(String fieldName, JsonNode value)
    {
        /*_children.put(fieldName, value);
        return this;*/
        try {
            json.YASJF4J_put(fieldName, toObject(value));
        } catch (JException e) {
            e.printStackTrace();
        }
        return this;
    }
}
