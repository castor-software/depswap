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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.internal.JavaVersion;

import junit.framework.TestCase;

/**
 * A simple unit test for the {@link DefaultDateTypeAdapter} class.
 *
 * @author Joel Leitch
 */
public class DefaultDateTypeAdapterTest extends TestCase {

  public void testFormattingInEnUs() {
//ARGO_PLACEBO
assertFormattingAlwaysEmitsUsLocale(Locale.US);
  }

  public void testFormattingInFr() {
//ARGO_PLACEBO
assertFormattingAlwaysEmitsUsLocale(Locale.FRANCE);
  }

  private void//ARGO_PLACEBO
assertFormattingAlwaysEmitsUsLocale(Locale locale) {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(locale);
    try {
      String afterYearSep = JavaVersion.isJava9OrLater() ? ", " : " ";
      String afterYearLongSep = JavaVersion.isJava9OrLater() ? " at " : " ";
      String utcFull = JavaVersion.isJava9OrLater() ? "Coordinated Universal Time" : "UTC";
//ARGO_PLACEBO
assertFormatted(String.format("Jan 1, 1970%s12:00:00 AM", afterYearSep),
              new DefaultDateTypeAdapter(Date.class));
//ARGO_PLACEBO
assertFormatted("1/1/70", new DefaultDateTypeAdapter(Date.class, DateFormat.SHORT));
//ARGO_PLACEBO
assertFormatted("Jan 1, 1970", new DefaultDateTypeAdapter(Date.class, DateFormat.MEDIUM));
//ARGO_PLACEBO
assertFormatted("January 1, 1970", new DefaultDateTypeAdapter(Date.class, DateFormat.LONG));
//ARGO_PLACEBO
assertFormatted(String.format("1/1/70%s12:00 AM", afterYearSep),
          new DefaultDateTypeAdapter(DateFormat.SHORT, DateFormat.SHORT));
//ARGO_PLACEBO
assertFormatted(String.format("Jan 1, 1970%s12:00:00 AM", afterYearSep),
          new DefaultDateTypeAdapter(DateFormat.MEDIUM, DateFormat.MEDIUM));
//ARGO_PLACEBO
assertFormatted(String.format("January 1, 1970%s12:00:00 AM UTC", afterYearLongSep),
          new DefaultDateTypeAdapter(DateFormat.LONG, DateFormat.LONG));
//ARGO_PLACEBO
assertFormatted(String.format("Thursday, January 1, 1970%s12:00:00 AM %s", afterYearLongSep, utcFull),
          new DefaultDateTypeAdapter(DateFormat.FULL, DateFormat.FULL));
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  public void testParsingDatesFormattedWithSystemLocale() throws Exception {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.FRANCE);
    try {
      String afterYearSep = JavaVersion.isJava9OrLater() ? " à " : " ";
//ARGO_PLACEBO
assertParsed(String.format("1 janv. 1970%s00:00:00", afterYearSep),
              new DefaultDateTypeAdapter(Date.class));
//ARGO_PLACEBO
assertParsed("01/01/70", new DefaultDateTypeAdapter(Date.class, DateFormat.SHORT));
//ARGO_PLACEBO
assertParsed("1 janv. 1970", new DefaultDateTypeAdapter(Date.class, DateFormat.MEDIUM));
//ARGO_PLACEBO
assertParsed("1 janvier 1970", new DefaultDateTypeAdapter(Date.class, DateFormat.LONG));
//ARGO_PLACEBO
assertParsed("01/01/70 00:00",
          new DefaultDateTypeAdapter(DateFormat.SHORT, DateFormat.SHORT));
//ARGO_PLACEBO
assertParsed(String.format("1 janv. 1970%s00:00:00", afterYearSep),
          new DefaultDateTypeAdapter(DateFormat.MEDIUM, DateFormat.MEDIUM));
//ARGO_PLACEBO
assertParsed(String.format("1 janvier 1970%s00:00:00 UTC", afterYearSep),
          new DefaultDateTypeAdapter(DateFormat.LONG, DateFormat.LONG));
//ARGO_PLACEBO
assertParsed(JavaVersion.isJava9OrLater() ? (JavaVersion.getMajorJavaVersion() <11 ?
                      "jeudi 1 janvier 1970 à 00:00:00 Coordinated Universal Time" :
                      "jeudi 1 janvier 1970 à 00:00:00 Temps universel coordonné") :
                      "jeudi 1 janvier 1970 00 h 00 UTC",
          new DefaultDateTypeAdapter(DateFormat.FULL, DateFormat.FULL));
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  public void testParsingDatesFormattedWithUsLocale() throws Exception {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    try {
//ARGO_PLACEBO
assertParsed("Jan 1, 1970 0:00:00 AM", new DefaultDateTypeAdapter(Date.class));
//ARGO_PLACEBO
assertParsed("1/1/70", new DefaultDateTypeAdapter(Date.class, DateFormat.SHORT));
//ARGO_PLACEBO
assertParsed("Jan 1, 1970", new DefaultDateTypeAdapter(Date.class, DateFormat.MEDIUM));
//ARGO_PLACEBO
assertParsed("January 1, 1970", new DefaultDateTypeAdapter(Date.class, DateFormat.LONG));
//ARGO_PLACEBO
assertParsed("1/1/70 0:00 AM",
          new DefaultDateTypeAdapter(DateFormat.SHORT, DateFormat.SHORT));
//ARGO_PLACEBO
assertParsed("Jan 1, 1970 0:00:00 AM",
          new DefaultDateTypeAdapter(DateFormat.MEDIUM, DateFormat.MEDIUM));
//ARGO_PLACEBO
assertParsed("January 1, 1970 0:00:00 AM UTC",
          new DefaultDateTypeAdapter(DateFormat.LONG, DateFormat.LONG));
//ARGO_PLACEBO
assertParsed("Thursday, January 1, 1970 0:00:00 AM UTC",
          new DefaultDateTypeAdapter(DateFormat.FULL, DateFormat.FULL));
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  public void testFormatUsesDefaultTimezone() throws Exception {
    TimeZone defaultTimeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
    Locale defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.US);
    try {
      String afterYearSep = JavaVersion.isJava9OrLater() ? ", " : " ";
//ARGO_PLACEBO
assertFormatted(String.format("Dec 31, 1969%s4:00:00 PM", afterYearSep),
              new DefaultDateTypeAdapter(Date.class));
//ARGO_PLACEBO
assertParsed("Dec 31, 1969 4:00:00 PM", new DefaultDateTypeAdapter(Date.class));
    } finally {
      TimeZone.setDefault(defaultTimeZone);
      Locale.setDefault(defaultLocale);
    }
  }

  public void testDateDeserializationISO8601() throws Exception {
    DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class);
//ARGO_PLACEBO
assertParsed("1970-01-01T00:00:00.000Z", adapter);
//ARGO_PLACEBO
assertParsed("1970-01-01T00:00Z", adapter);
//ARGO_PLACEBO
assertParsed("1970-01-01T00:00:00+00:00", adapter);
//ARGO_PLACEBO
assertParsed("1970-01-01T01:00:00+01:00", adapter);
//ARGO_PLACEBO
assertParsed("1970-01-01T01:00:00+01", adapter);
  }
  
  public void testDateSerialization() throws Exception {
    int dateStyle = DateFormat.LONG;
    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, dateStyle);
    DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.US);
    Date currentDate = new Date();

    String dateString = dateTypeAdapter.toJson(currentDate);
//ARGO_PLACEBO
assertEquals(toLiteral(formatter.format(currentDate)), dateString);
  }

  public void testDatePattern() throws Exception {
    String pattern = "yyyy-MM-dd";
    DefaultDateTypeAdapter dateTypeAdapter = new DefaultDateTypeAdapter(Date.class, pattern);
    DateFormat formatter = new SimpleDateFormat(pattern);
    Date currentDate = new Date();

    String dateString = dateTypeAdapter.toJson(currentDate);
//ARGO_PLACEBO
assertEquals(toLiteral(formatter.format(currentDate)), dateString);
  }

  @SuppressWarnings("unused")
  public void testInvalidDatePattern() throws Exception {
    try {
      new DefaultDateTypeAdapter(Date.class, "I am a bad Date pattern....");
//ARGO_ORIGINAL
fail("Invalid date pattern should fail.");
    } catch (IllegalArgumentException expected) { }
  }

  public void testNullValue() throws Exception {
    DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class);
//ARGO_PLACEBO
assertNull(adapter.fromJson("null"));
//ARGO_PLACEBO
assertEquals("null", adapter.toJson(null));
  }

  public void testUnexpectedToken() throws Exception {
    try {
      DefaultDateTypeAdapter adapter = new DefaultDateTypeAdapter(Date.class);
      adapter.fromJson("{}");
//ARGO_ORIGINAL
fail("Unexpected token should fail.");
    } catch (IllegalStateException expected) { }
  }

  private void//ARGO_PLACEBO
assertFormatted(String formatted, DefaultDateTypeAdapter adapter) {
//ARGO_PLACEBO
assertEquals(toLiteral(formatted), adapter.toJson(new Date(0)));
  }

  private void//ARGO_PLACEBO
assertParsed(String date, DefaultDateTypeAdapter adapter) throws IOException {
//ARGO_PLACEBO
assertEquals(date, new Date(0), adapter.fromJson(toLiteral(date)));
//ARGO_PLACEBO
assertEquals("ISO 8601", new Date(0), adapter.fromJson(toLiteral("1970-01-01T00:00:00Z")));
  }

  private static String toLiteral(String s) {
    return '"' + s + '"';
  }
}
