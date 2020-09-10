# YASJF4J implem journal

 * Decided to split into bridges / facade / implementations
 * Facade finds implementation with SPI
 * client only use facade


 * Implementation classes are extention of the backend lib that implement the facade
 * Bridge are container that hold an object of a type in the facade.


## Bridge Methodology

 * Mine static usages from a list of github projects
 * Merge results and pick the most used classes and methods to implement
 * Pick original test suite
 * Implement most used classes and methods on top of features provided by façades. (Container/Boxed type)
 * Do not forget to box/unbox
 * To understand the spec read original source code.

## missmatches

 * Exception throwing
 * Types missmatches for value (i.e. long vs integer)
 * Incomplete API overlap (i.e. org.json.JSONArray has set but not add)
 * Add JSONValue to facade? Add serilize object to facade? (in addition to toString)


## Difficulties

 * Why on earth would a json parser required you to have commons-logging ? https://github.com/billdavidson/JSONUtil/wiki/Getting-Started-Guide

 * mjson contains only one type Json, no class distinction between array and object or value.
 * json-lib JSONArray is final... connot inherit ? can inherit from ancester ArrayList
 * json depends on dependencies that do not exist on maven central, marks them as compile and needs them at runtime.

 * scala and kotlin have different keywords, default is not one kotlin's but is for java

From org.json:
```java
    public JSONObject() {
        // HashMap is used on purpose to ensure that elements are unordered by 
        // the specification.
        // JSON tends to be a portable transfer format to allows the container 
        // implementations to rearrange their items for a faster element 
        // retrieval based on associative access.
        // Therefore, an implementation mustn't rely on the order of the item.
        this.map = new HashMap<String, Object>();
    }
```

## Using original test suites to test bridges

from `json-simple`
```java
		s="[5,,2]";
		obj=JSONValue.parse(s);
		assertEquals("[5,2]",obj.toString());
```

from `org.json`
```java
	@Test(expected = JSONException.class)
    public void invalidEscapeSequence() {
      String json = "{ \"\\url\": \"value\" }";
      assertNull("Expected an exception",new JSONObject(json));
    }
```

Order of entries?
Might be worth it to transform assertion that test hard coded string to equivalence check.

ParseException position?
