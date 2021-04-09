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
package com.google.gson.functional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.JavaVersion;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;

import junit.framework.TestCase;

import static com.google.gson.JSONTestUtils.assertEquivalent;

/**
 * Functional test for Json serialization and deserialization for common classes for which default
 * support is provided in Gson. The tests for Map types are available in {@link MapTest}.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public class DefaultTypeAdaptersTest extends TestCase {
  private Gson gson;
  private TimeZone oldTimeZone;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.oldTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
    Locale.setDefault(Locale.US);
    gson = new Gson();
  }

  @Override
  protected void tearDown() throws Exception {
    TimeZone.setDefault(oldTimeZone);
    super.tearDown();
  }

  public void testClassSerialization() {
    try {
      gson.toJson(String.class);
    } catch (UnsupportedOperationException expected) {}
    // Override with a custom type adapter for class.
    gson = new GsonBuilder().registerTypeAdapter(Class.class, new MyClassTypeAdapter()).create();
    //ARGO_PLACEBO
    assertEquals("\"java.lang.String\"", gson.toJson(String.class));
  }

  public void testClassDeserialization() {
    try {
      gson.fromJson("String.class", String.class.getClass());
    } catch (UnsupportedOperationException expected) {}
    // Override with a custom type adapter for class.
    gson = new GsonBuilder().registerTypeAdapter(Class.class, new MyClassTypeAdapter()).create();
    //ARGO_PLACEBO
    assertEquals(String.class, gson.fromJson("java.lang.String", Class.class));
  }

  public void testUrlSerialization() throws Exception {
    String urlValue = "http://google.com/";
    URL url = new URL(urlValue);
    //ARGO_PLACEBO
    assertEquals("\"http://google.com/\"", gson.toJson(url));
  }

  public void testUrlDeserialization() {
    String urlValue = "http://google.com/";
    String json = "'http:\\/\\/google.com\\/'";
    URL target = gson.fromJson(json, URL.class);
    //ARGO_PLACEBO
    assertEquals(urlValue, target.toExternalForm());

    gson.fromJson('"' + urlValue + '"', URL.class);
    //ARGO_PLACEBO
    assertEquals(urlValue, target.toExternalForm());
  }

  public void testUrlNullSerialization() throws Exception {
    ClassWithUrlField target = new ClassWithUrlField();
    //ARGO_PLACEBO
    assertEquals("{}", gson.toJson(target));
  }

  public void testUrlNullDeserialization() {
    String json = "{}";
    ClassWithUrlField target = gson.fromJson(json, ClassWithUrlField.class);
    //ARGO_PLACEBO
    assertNull(target.url);
  }

  private static class ClassWithUrlField {
    URL url;
  }

  public void testUriSerialization() throws Exception {
    String uriValue = "http://google.com/";
    URI uri = new URI(uriValue);
    //ARGO_PLACEBO
    assertEquals("\"http://google.com/\"", gson.toJson(uri));
  }

  public void testUriDeserialization() {
    String uriValue = "http://google.com/";
    String json = '"' + uriValue + '"';
    URI target = gson.fromJson(json, URI.class);
    //ARGO_PLACEBO
    assertEquals(uriValue, target.toASCIIString());
  }
  
  public void testNullSerialization() throws Exception {
    testNullSerializationAndDeserialization(Boolean.class);
    testNullSerializationAndDeserialization(Byte.class);
    testNullSerializationAndDeserialization(Short.class);
    testNullSerializationAndDeserialization(Integer.class);
    testNullSerializationAndDeserialization(Long.class);
    testNullSerializationAndDeserialization(Double.class);
    testNullSerializationAndDeserialization(Float.class);
    testNullSerializationAndDeserialization(Number.class);
    testNullSerializationAndDeserialization(Character.class);
    testNullSerializationAndDeserialization(String.class);
    testNullSerializationAndDeserialization(StringBuilder.class);
    testNullSerializationAndDeserialization(StringBuffer.class);
    testNullSerializationAndDeserialization(BigDecimal.class);
    testNullSerializationAndDeserialization(BigInteger.class);
    testNullSerializationAndDeserialization(TreeSet.class);
    testNullSerializationAndDeserialization(ArrayList.class);
    testNullSerializationAndDeserialization(HashSet.class);
    testNullSerializationAndDeserialization(Properties.class);
    testNullSerializationAndDeserialization(URL.class);
    testNullSerializationAndDeserialization(URI.class);
    testNullSerializationAndDeserialization(UUID.class);
    testNullSerializationAndDeserialization(Locale.class);
    testNullSerializationAndDeserialization(InetAddress.class);
    testNullSerializationAndDeserialization(BitSet.class);
    testNullSerializationAndDeserialization(Date.class);
    testNullSerializationAndDeserialization(GregorianCalendar.class);
    testNullSerializationAndDeserialization(Calendar.class);
    testNullSerializationAndDeserialization(Time.class);
    testNullSerializationAndDeserialization(Timestamp.class);
    testNullSerializationAndDeserialization(java.sql.Date.class);
    testNullSerializationAndDeserialization(Enum.class);
    testNullSerializationAndDeserialization(Class.class);
  }

  private void testNullSerializationAndDeserialization(Class<?> c) {
    //ARGO_PLACEBO
    assertEquals("null", gson.toJson(null, c));
    //ARGO_PLACEBO
    assertEquals(null, gson.fromJson("null", c));
  }

  public void testUuidSerialization() throws Exception {
    String uuidValue = "c237bec1-19ef-4858-a98e-521cf0aad4c0";
    UUID uuid = UUID.fromString(uuidValue);
    //ARGO_PLACEBO
    assertEquals('"' + uuidValue + '"', gson.toJson(uuid));
  }

  public void testUuidDeserialization() {
    String uuidValue = "c237bec1-19ef-4858-a98e-521cf0aad4c0";
    String json = '"' + uuidValue + '"';
    UUID target = gson.fromJson(json, UUID.class);
    //ARGO_PLACEBO
    assertEquals(uuidValue, target.toString());
  }

  public void testLocaleSerializationWithLanguage() {
    Locale target = new Locale("en");
    //ARGO_PLACEBO
    assertEquals("\"en\"", gson.toJson(target));
  }

  public void testLocaleDeserializationWithLanguage() {
    String json = "\"en\"";
    Locale locale = gson.fromJson(json, Locale.class);
    //ARGO_PLACEBO
    assertEquals("en", locale.getLanguage());
  }

  public void testLocaleSerializationWithLanguageCountry() {
    Locale target = Locale.CANADA_FRENCH;
    //ARGO_PLACEBO
    assertEquals("\"fr_CA\"", gson.toJson(target));
  }

  public void testLocaleDeserializationWithLanguageCountry() {
    String json = "\"fr_CA\"";
    Locale locale = gson.fromJson(json, Locale.class);
    //ARGO_PLACEBO
    assertEquals(Locale.CANADA_FRENCH, locale);
  }

  public void testLocaleSerializationWithLanguageCountryVariant() {
    Locale target = new Locale("de", "DE", "EURO");
    String json = gson.toJson(target);
    //ARGO_PLACEBO
    assertEquals("\"de_DE_EURO\"", json);
  }

  public void testLocaleDeserializationWithLanguageCountryVariant() {
    String json = "\"de_DE_EURO\"";
    Locale locale = gson.fromJson(json, Locale.class);
    //ARGO_PLACEBO
    assertEquals("de", locale.getLanguage());
    //ARGO_PLACEBO
    assertEquals("DE", locale.getCountry());
    //ARGO_PLACEBO
    assertEquals("EURO", locale.getVariant());
  }

  public void testBigDecimalFieldSerialization() {
    ClassWithBigDecimal target = new ClassWithBigDecimal("-122.01e-21");
    String json = gson.toJson(target);
    String actual = json.substring(json.indexOf(':') + 1, json.indexOf('}'));
    //ARGO_PLACEBO
    assertEquals(target.value, new BigDecimal(actual));
  }

  public void testBigDecimalFieldDeserialization() {
    ClassWithBigDecimal expected = new ClassWithBigDecimal("-122.01e-21");
    String json = expected.getExpectedJson();
    ClassWithBigDecimal actual = gson.fromJson(json, ClassWithBigDecimal.class);
    //ARGO_PLACEBO
    assertEquals(expected.value, actual.value);
  }

  public void testBadValueForBigDecimalDeserialization() {
    try {
      gson.fromJson("{\"value\"=1.5e-1.0031}", ClassWithBigDecimal.class);
      //ARGO_PLACEBO
      fail("Exponent of a BigDecimal must be an integer value.");
    } catch (JsonParseException expected) { }
  }

  public void testBigIntegerFieldSerialization() {
    ClassWithBigInteger target = new ClassWithBigInteger("23232323215323234234324324324324324324");
    String json = gson.toJson(target);
    //ARGO_PLACEBO
    assertEquals(target.getExpectedJson(), json);
  }

  public void testBigIntegerFieldDeserialization() {
    ClassWithBigInteger expected = new ClassWithBigInteger("879697697697697697697697697697697697");
    String json = expected.getExpectedJson();
    ClassWithBigInteger actual = gson.fromJson(json, ClassWithBigInteger.class);
    //ARGO_PLACEBO
    assertEquals(expected.value, actual.value);
  }
  
  public void testOverrideBigIntegerTypeAdapter() throws Exception {
    gson = new GsonBuilder()
        .registerTypeAdapter(BigInteger.class, new NumberAsStringAdapter(BigInteger.class))
        .create();
    //ARGO_PLACEBO
    assertEquals("\"123\"", gson.toJson(new BigInteger("123"), BigInteger.class));
    //ARGO_PLACEBO
    assertEquals(new BigInteger("123"), gson.fromJson("\"123\"", BigInteger.class));
  }

  public void testOverrideBigDecimalTypeAdapter() throws Exception {
    gson = new GsonBuilder()
        .registerTypeAdapter(BigDecimal.class, new NumberAsStringAdapter(BigDecimal.class))
        .create();
    //ARGO_PLACEBO
    assertEquals("\"1.1\"", gson.toJson(new BigDecimal("1.1"), BigDecimal.class));
    //ARGO_PLACEBO
    assertEquals(new BigDecimal("1.1"), gson.fromJson("\"1.1\"", BigDecimal.class));
  }

  public void testSetSerialization() throws Exception {
    Gson gson = new Gson();
    HashSet<String> s = new HashSet<String>();
    s.add("blah");
    String json = gson.toJson(s);
    //ARGO_PLACEBO
    assertEquals("[\"blah\"]", json);

    json = gson.toJson(s, Set.class);
    //ARGO_PLACEBO
    assertEquals("[\"blah\"]", json);
  }

  public void testBitSetSerialization() throws Exception {
    Gson gson = new Gson();
    BitSet bits = new BitSet();
    bits.set(1);
    bits.set(3, 6);
    bits.set(9);
    String json = gson.toJson(bits);
    //ARGO_PLACEBO
    assertEquals("[0,1,0,1,1,1,0,0,0,1]", json);
  }

  public void testBitSetDeserialization() throws Exception {
    BitSet expected = new BitSet();
    expected.set(0);
    expected.set(2, 6);
    expected.set(8);

    Gson gson = new Gson();
    String json = gson.toJson(expected);
    //ARGO_PLACEBO
    assertEquals(expected, gson.fromJson(json, BitSet.class));

    json = "[1,0,1,1,1,1,0,0,1,0,0,0]";
    //ARGO_PLACEBO
    assertEquals(expected, gson.fromJson(json, BitSet.class));

    json = "[\"1\",\"0\",\"1\",\"1\",\"1\",\"1\",\"0\",\"0\",\"1\"]";
    //ARGO_PLACEBO
    assertEquals(expected, gson.fromJson(json, BitSet.class));

    json = "[true,false,true,true,true,true,false,false,true,false,false]";
    //ARGO_PLACEBO
    assertEquals(expected, gson.fromJson(json, BitSet.class));
  }

  public void testDefaultDateSerialization() {
    Date now = new Date(1315806903103L);
    String json = gson.toJson(now);
    if (JavaVersion.isJava9OrLater()) {
      //ARGO_PLACEBO
      assertEquals("\"Sep 11, 2011, 10:55:03 PM\"", json);
    } else {
      //ARGO_PLACEBO
      assertEquals("\"Sep 11, 2011 10:55:03 PM\"", json);
    }
  }

  public void testDefaultDateDeserialization() {
    String json = "'Dec 13, 2009 07:18:02 AM'";
    Date extracted = gson.fromJson(json, Date.class);
    //ARGO_PLACEBO
    assertEqualsDate(extracted, 2009, 11, 13);
    //ARGO_PLACEBO
    assertEqualsTime(extracted, 7, 18, 2);
  }

  // Date can not directly be compared with another instance since the deserialization loses the
  // millisecond portion.
  @SuppressWarnings("deprecation")
  private void assertEqualsDate(Date date, int year, int month, int day) {
    assertEquals(year-1900, date.getYear());
    assertEquals(month, date.getMonth());
    assertEquals(day, date.getDate());
  }

  @SuppressWarnings("deprecation")
  private void assertEqualsTime(Date date, int hours, int minutes, int seconds) {
    assertEquals(hours, date.getHours());
    assertEquals(minutes, date.getMinutes());
    assertEquals(seconds, date.getSeconds());
  }

  public void testDefaultJavaSqlDateSerialization() {
    java.sql.Date instant = new java.sql.Date(1259875082000L);
    String json = gson.toJson(instant);
    //ARGO_PLACEBO
    assertEquals("\"Dec 3, 2009\"", json);
  }

  public void testDefaultJavaSqlDateDeserialization() {
    String json = "'Dec 3, 2009'";
    java.sql.Date extracted = gson.fromJson(json, java.sql.Date.class);
    //ARGO_PLACEBO
    assertEqualsDate(extracted, 2009, 11, 3);
  }

  public void testDefaultJavaSqlTimestampSerialization() {
    Timestamp now = new java.sql.Timestamp(1259875082000L);
    String json = gson.toJson(now);
    if (JavaVersion.isJava9OrLater()) {
      //ARGO_PLACEBO
      assertEquals("\"Dec 3, 2009, 1:18:02 PM\"", json);
    } else {
      //ARGO_PLACEBO
      assertEquals("\"Dec 3, 2009 1:18:02 PM\"", json);
    }
  }

  public void testDefaultJavaSqlTimestampDeserialization() {
    String json = "'Dec 3, 2009 1:18:02 PM'";
    Timestamp extracted = gson.fromJson(json, Timestamp.class);
    //ARGO_PLACEBO
    assertEqualsDate(extracted, 2009, 11, 3);
    //ARGO_PLACEBO
    assertEqualsTime(extracted, 13, 18, 2);
  }

  public void testDefaultJavaSqlTimeSerialization() {
    Time now = new Time(1259875082000L);
    String json = gson.toJson(now);
    //ARGO_PLACEBO
    assertEquals("\"01:18:02 PM\"", json);
  }

  public void testDefaultJavaSqlTimeDeserialization() {
    String json = "'1:18:02 PM'";
    Time extracted = gson.fromJson(json, Time.class);
    //ARGO_PLACEBO
    assertEqualsTime(extracted, 13, 18, 2);
  }

  public void testDefaultDateSerializationUsingBuilder() throws Exception {
    Gson gson = new GsonBuilder().create();
    Date now = new Date(1315806903103L);
    String json = gson.toJson(now);
    if (JavaVersion.isJava9OrLater()) {
      //ARGO_PLACEBO
      assertEquals("\"Sep 11, 2011, 10:55:03 PM\"", json);
    } else {
      //ARGO_PLACEBO
      assertEquals("\"Sep 11, 2011 10:55:03 PM\"", json);
    }
  }

  public void testDefaultDateDeserializationUsingBuilder() throws Exception {
    Gson gson = new GsonBuilder().create();
    Date now = new Date(1315806903103L);
    String json = gson.toJson(now);
    Date extracted = gson.fromJson(json, Date.class);
    //ARGO_PLACEBO
    assertEquals(now.toString(), extracted.toString());
  }

  public void testDefaultCalendarSerialization() throws Exception {
    Gson gson = new GsonBuilder().create();
    String json = gson.toJson(Calendar.getInstance());
    //ARGO_PLACEBO
    assertTrue(json.contains("year"));
    //ARGO_PLACEBO
    assertTrue(json.contains("month"));
    //ARGO_PLACEBO
    assertTrue(json.contains("dayOfMonth"));
    //ARGO_PLACEBO
    assertTrue(json.contains("hourOfDay"));
    //ARGO_PLACEBO
    assertTrue(json.contains("minute"));
    //ARGO_PLACEBO
    assertTrue(json.contains("second"));
  }

  public void testDefaultCalendarDeserialization() throws Exception {
    Gson gson = new GsonBuilder().create();
    String json = "{year:2009,month:2,dayOfMonth:11,hourOfDay:14,minute:29,second:23}";
    Calendar cal = gson.fromJson(json, Calendar.class);
    //ARGO_PLACEBO
    assertEquals(2009, cal.get(Calendar.YEAR));
    //ARGO_PLACEBO
    assertEquals(2, cal.get(Calendar.MONTH));
    //ARGO_PLACEBO
    assertEquals(11, cal.get(Calendar.DAY_OF_MONTH));
    //ARGO_PLACEBO
    assertEquals(14, cal.get(Calendar.HOUR_OF_DAY));
    //ARGO_PLACEBO
    assertEquals(29, cal.get(Calendar.MINUTE));
    //ARGO_PLACEBO
    assertEquals(23, cal.get(Calendar.SECOND));
  }

  public void testDefaultGregorianCalendarSerialization() throws Exception {
    Gson gson = new GsonBuilder().create();
    GregorianCalendar cal = new GregorianCalendar();
    String json = gson.toJson(cal);
    //ARGO_PLACEBO
    assertTrue(json.contains("year"));
    //ARGO_PLACEBO
    assertTrue(json.contains("month"));
    //ARGO_PLACEBO
    assertTrue(json.contains("dayOfMonth"));
    //ARGO_PLACEBO
    assertTrue(json.contains("hourOfDay"));
    //ARGO_PLACEBO
    assertTrue(json.contains("minute"));
    //ARGO_PLACEBO
    assertTrue(json.contains("second"));
  }

  public void testDefaultGregorianCalendarDeserialization() throws Exception {
    Gson gson = new GsonBuilder().create();
    String json = "{year:2009,month:2,dayOfMonth:11,hourOfDay:14,minute:29,second:23}";
    GregorianCalendar cal = gson.fromJson(json, GregorianCalendar.class);
    //ARGO_PLACEBO
    assertEquals(2009, cal.get(Calendar.YEAR));
    //ARGO_PLACEBO
    assertEquals(2, cal.get(Calendar.MONTH));
    //ARGO_PLACEBO
    assertEquals(11, cal.get(Calendar.DAY_OF_MONTH));
    //ARGO_PLACEBO
    assertEquals(14, cal.get(Calendar.HOUR_OF_DAY));
    //ARGO_PLACEBO
    assertEquals(29, cal.get(Calendar.MINUTE));
    //ARGO_PLACEBO
    assertEquals(23, cal.get(Calendar.SECOND));
  }

  public void testDateSerializationWithPattern() throws Exception {
    String pattern = "yyyy-MM-dd";
    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
    Date now = new Date(1315806903103L);
    String json = gson.toJson(now);
    //ARGO_PLACEBO
    assertEquals("\"2011-09-11\"", json);
  }

  @SuppressWarnings("deprecation")
  public void testDateDeserializationWithPattern() throws Exception {
    String pattern = "yyyy-MM-dd";
    Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL).setDateFormat(pattern).create();
    Date now = new Date(1315806903103L);
    String json = gson.toJson(now);
    Date extracted = gson.fromJson(json, Date.class);
    //ARGO_PLACEBO
    assertEquals(now.getYear(), extracted.getYear());
    //ARGO_PLACEBO
    assertEquals(now.getMonth(), extracted.getMonth());
    //ARGO_PLACEBO
    assertEquals(now.getDay(), extracted.getDay());
  }

  public void testDateSerializationWithPatternNotOverridenByTypeAdapter() throws Exception {
    String pattern = "yyyy-MM-dd";
    Gson gson = new GsonBuilder()
        .setDateFormat(pattern)
        .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
          public Date deserialize(JsonElement json, Type typeOfT,
              JsonDeserializationContext context)
              throws JsonParseException {
            return new Date(1315806903103L);
          }
        })
        .create();

    Date now = new Date(1315806903103L);
    String json = gson.toJson(now);
    //ARGO_PLACEBO
    assertEquals("\"2011-09-11\"", json);
  }

  // http://code.google.com/p/google-gson/issues/detail?id=230
  public void testDateSerializationInCollection() throws Exception {
    Type listOfDates = new TypeToken<List<Date>>() {}.getType();
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    try {
      Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
      List<Date> dates = Arrays.asList(new Date(0));
      String json = gson.toJson(dates, listOfDates);
      //ARGO_PLACEBO
      assertEquals("[\"1970-01-01\"]", json);
      //ARGO_PLACEBO
      assertEquals(0L, gson.<List<Date>>fromJson("[\"1970-01-01\"]", listOfDates).get(0).getTime());
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  // http://code.google.com/p/google-gson/issues/detail?id=230
  public void testTimestampSerialization() throws Exception {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    try {
      Timestamp timestamp = new Timestamp(0L);
      Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
      String json = gson.toJson(timestamp, Timestamp.class);
      //ARGO_PLACEBO
      assertEquals("\"1970-01-01\"", json);
      //ARGO_PLACEBO
      assertEquals(0, gson.fromJson("\"1970-01-01\"", Timestamp.class).getTime());
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  // http://code.google.com/p/google-gson/issues/detail?id=230
  public void testSqlDateSerialization() throws Exception {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    try {
      java.sql.Date sqlDate = new java.sql.Date(0L);
      Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
      String json = gson.toJson(sqlDate, Timestamp.class);
      //ARGO_PLACEBO
      assertEquals("\"1970-01-01\"", json);
      //ARGO_PLACEBO
      assertEquals(0, gson.fromJson("\"1970-01-01\"", java.sql.Date.class).getTime());
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  public void testJsonPrimitiveSerialization() {
    //ARGO_PLACEBO
    assertEquals("5", gson.toJson(new JsonPrimitive(5), JsonElement.class));
    //ARGO_PLACEBO
    assertEquals("true", gson.toJson(new JsonPrimitive(true), JsonElement.class));
    //ARGO_PLACEBO
    assertEquals("\"foo\"", gson.toJson(new JsonPrimitive("foo"), JsonElement.class));
    //ARGO_PLACEBO
    assertEquals("\"a\"", gson.toJson(new JsonPrimitive('a'), JsonElement.class));
  }

  public void testJsonPrimitiveDeserialization() {
    //ARGO_PLACEBO
    assertEquals(new JsonPrimitive(5), gson.fromJson("5", JsonElement.class));
    //ARGO_PLACEBO
    assertEquals(new JsonPrimitive(5), gson.fromJson("5", JsonPrimitive.class));
    //ARGO_PLACEBO
    assertEquals(new JsonPrimitive(true), gson.fromJson("true", JsonElement.class));
    //ARGO_PLACEBO
    assertEquals(new JsonPrimitive(true), gson.fromJson("true", JsonPrimitive.class));
    //ARGO_PLACEBO
    assertEquals(new JsonPrimitive("foo"), gson.fromJson("\"foo\"", JsonElement.class));
    //ARGO_PLACEBO
    assertEquals(new JsonPrimitive("foo"), gson.fromJson("\"foo\"", JsonPrimitive.class));
    //ARGO_PLACEBO
    assertEquals(new JsonPrimitive('a'), gson.fromJson("\"a\"", JsonElement.class));
    //ARGO_PLACEBO
    assertEquals(new JsonPrimitive('a'), gson.fromJson("\"a\"", JsonPrimitive.class));
  }

  public void testJsonNullSerialization() {
    //ARGO_PLACEBO
    assertEquals("null", gson.toJson(JsonNull.INSTANCE, JsonElement.class));
    //ARGO_PLACEBO
    assertEquals("null", gson.toJson(JsonNull.INSTANCE, JsonNull.class));
  }

  public void testNullJsonElementSerialization() {
    //ARGO_PLACEBO
    assertEquals("null", gson.toJson(null, JsonElement.class));
    //ARGO_PLACEBO
    assertEquals("null", gson.toJson(null, JsonNull.class));
  }

  public void testJsonArraySerialization() {
    JsonArray array = new JsonArray();
    array.add(new JsonPrimitive(1));
    array.add(new JsonPrimitive(2));
    array.add(new JsonPrimitive(3));
    //ARGO_ORIGINAL
    assertEquals("[1,2,3]", gson.toJson(array, JsonElement.class));
  }

  public void testJsonArrayDeserialization() {
    JsonArray array = new JsonArray();
    array.add(new JsonPrimitive(1));
    array.add(new JsonPrimitive(2));
    array.add(new JsonPrimitive(3));

    String json = "[1,2,3]";
    //ARGO_EQUIVALENT
    assertEquivalent(array, gson.fromJson(json, JsonElement.class));
    //ARGO_EQUIVALENT
    assertEquivalent(array, gson.fromJson(json, JsonArray.class));
  }

  public void testJsonObjectSerialization() {
    JsonObject object = new JsonObject();
    object.add("foo", new JsonPrimitive(1));
    object.add("bar", new JsonPrimitive(2));
    //ARGO_EQUIVALENT
    assertEquivalent("{\"foo\":1,\"bar\":2}", gson.toJson(object, JsonElement.class), gson);
  }

  public void testJsonObjectDeserialization() {
    JsonObject object = new JsonObject();
    object.add("foo", new JsonPrimitive(1));
    object.add("bar", new JsonPrimitive(2));

    String json = "{\"foo\":1,\"bar\":2}";
    JsonElement actual = gson.fromJson(json, JsonElement.class);
    //ARGO_EQUIVALENT
    assertEquivalent(object, actual);

    JsonObject actualObj = gson.fromJson(json, JsonObject.class);
    //ARGO_EQUIVALENT
    assertEquivalent(object, actualObj);
  }

  public void testJsonNullDeserialization() {
    //ARGO_PLACEBO
    assertEquals(JsonNull.INSTANCE, gson.fromJson("null", JsonElement.class));
    //ARGO_PLACEBO
    assertEquals(JsonNull.INSTANCE, gson.fromJson("null", JsonNull.class));
  }

  public void testJsonElementTypeMismatch() {
    try {
      gson.fromJson("\"abc\"", JsonObject.class);
      //ARGO_PLACEBO
      fail();
    } catch (JsonSyntaxException expected) {
      assertEquals("Expected a com.google.gson.JsonObject but was com.google.gson.JsonPrimitive",
          expected.getMessage());
    }
  }

  private static class ClassWithBigDecimal {
    BigDecimal value;
    ClassWithBigDecimal(String value) {
      this.value = new BigDecimal(value);
    }
    String getExpectedJson() {
      return "{\"value\":" + value.toEngineeringString() + "}";
    }
  }

  private static class ClassWithBigInteger {
    BigInteger value;
    ClassWithBigInteger(String value) {
      this.value = new BigInteger(value);
    }
    String getExpectedJson() {
      return "{\"value\":" + value + "}";
    }
  }

  public void testPropertiesSerialization() {
    Properties props = new Properties();
    props.setProperty("foo", "bar");
    String json = gson.toJson(props);
    String expected = "{\"foo\":\"bar\"}";
    //ARGO_PLACEBO
    assertEquals(expected, json);
  }

  public void testPropertiesDeserialization() {
    String json = "{foo:'bar'}";
    Properties props = gson.fromJson(json, Properties.class);
    //ARGO_PLACEBO
    assertEquals("bar", props.getProperty("foo"));
  }

  public void testTreeSetSerialization() {
    TreeSet<String> treeSet = new TreeSet<String>();
    treeSet.add("Value1");
    String json = gson.toJson(treeSet);
    //ARGO_PLACEBO
    assertEquals("[\"Value1\"]", json);
  }

  public void testTreeSetDeserialization() {
    String json = "['Value1']";
    Type type = new TypeToken<TreeSet<String>>() {}.getType();
    TreeSet<String> treeSet = gson.fromJson(json, type);
    //ARGO_PLACEBO
    assertTrue(treeSet.contains("Value1"));
  }

  public void testStringBuilderSerialization() {
    StringBuilder sb = new StringBuilder("abc");
    String json = gson.toJson(sb);
    //ARGO_PLACEBO
    assertEquals("\"abc\"", json);
  }

  public void testStringBuilderDeserialization() {
    StringBuilder sb = gson.fromJson("'abc'", StringBuilder.class);
    //ARGO_PLACEBO
    assertEquals("abc", sb.toString());
  }

  public void testStringBufferSerialization() {
    StringBuffer sb = new StringBuffer("abc");
    String json = gson.toJson(sb);
    //ARGO_PLACEBO
    assertEquals("\"abc\"", json);
  }

  public void testStringBufferDeserialization() {
    StringBuffer sb = gson.fromJson("'abc'", StringBuffer.class);
    //ARGO_PLACEBO
    assertEquals("abc", sb.toString());
  }

  @SuppressWarnings("rawtypes")
  private static class MyClassTypeAdapter extends TypeAdapter<Class> {
    @Override
    public void write(JsonWriter out, Class value) throws IOException {
      out.value(value.getName());
    }
    @Override
    public Class read(JsonReader in) throws IOException {
      String className = in.nextString();
      try {
        return Class.forName(className);
      } catch (ClassNotFoundException e) {
        throw new IOException(e);
      }
    }
  }

  static class NumberAsStringAdapter extends TypeAdapter<Number> {
    private final Constructor<? extends Number> constructor;
    NumberAsStringAdapter(Class<? extends Number> type) throws Exception {
      this.constructor = type.getConstructor(String.class);
    }
    @Override public void write(JsonWriter out, Number value) throws IOException {
      out.value(value.toString());
    }
    @Override public Number read(JsonReader in) throws IOException {
      try {
        return constructor.newInstance(in.nextString());
      } catch (Exception e) {
        throw new AssertionError(e);
      }
    }
  }
}
