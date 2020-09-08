# YASJF4J implem journal

 * Decided to split into bridges / facade / implementations
 * Facade finds implementation with SPI
 * client only use facade


 * Implementation classes are extention of the backend lib that implement the facade
 * Bridge are container that hold an object of a type in the facade.

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

## Using original test suites to test bridges

```java
		s="[5,,2]";
		obj=JSONValue.parse(s);
		assertEquals("[5,2]",obj.toString());
```

Order of entries?
ParseException position?
