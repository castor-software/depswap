package com.fasterxml.jackson.databind.util;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.BaseMapTest;

public class TestStdDateFormat
    extends BaseMapTest
{
    @SuppressWarnings("deprecation")
    public void testFactories() {
        TimeZone tz = TimeZone.getTimeZone("GMT");
        Locale loc = Locale.US;
//ARGO_PLACEBO
assertNotNull(StdDateFormat.getISO8601Format(tz, loc));
//ARGO_PLACEBO
assertNotNull(StdDateFormat.getRFC1123Format(tz, loc));
    }

    // [databind#803]
    public void testLenientDefaults() throws Exception
    {
        StdDateFormat f = StdDateFormat.instance;

        // default should be lenient
//ARGO_PLACEBO
assertTrue(f.isLenient());

        StdDateFormat f2 = f.clone();
//ARGO_PLACEBO
assertTrue(f2.isLenient());

        f2.setLenient(false);
//ARGO_PLACEBO
assertFalse(f2.isLenient());

        f2.setLenient(true);
//ARGO_PLACEBO
assertTrue(f2.isLenient());

        // and for testing, finally, leave as non-lenient
        f2.setLenient(false);
//ARGO_PLACEBO
assertFalse(f2.isLenient());
        StdDateFormat f3 = f2.clone();
//ARGO_PLACEBO
assertFalse(f3.isLenient());
    }

    public void testISO8601RegexpDateOnly() throws Exception
    {
        Pattern p = StdDateFormat.PATTERN_PLAIN;
        Matcher m = p.matcher("1997-07-16");
//ARGO_PLACEBO
assertTrue(m.matches());
        // no matching groups...
    }

    public void testISO8601RegexpFull() throws Exception
    {
        /*
        String PATTERN_PLAIN_STR = "\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d";
        Pattern PATTERN_ISO8601 = Pattern.compile(PATTERN_PLAIN_STR
                +"[T]\\d\\d[:]\\d\\d(?:[:]\\d\\d)?" // hours, minutes, optional seconds
                +"(\\.\\d+)?" // optional second fractions
                +"(Z|[+-]\\d\\d(?:[:]?\\d\\d)?)?" // optional timeoffset/Z
                );
        final Pattern p = PATTERN_ISO8601;
        */
        final Pattern p = StdDateFormat.PATTERN_ISO8601;
        Matcher m;

        // First simple full representation (except no millisecs)
        m = p.matcher("1997-07-16T19:20:00+01:00");
//ARGO_PLACEBO
assertTrue(m.matches());
//ARGO_PLACEBO
assertEquals(2, m.groupCount());
//ARGO_PLACEBO
assertNull(m.group(1)); // no match (why not empty String)
//ARGO_PLACEBO
assertEquals("+01:00", m.group(2));

        // Then with 'Z' instead
        m = p.matcher("1997-07-16T19:20:00Z");
//ARGO_PLACEBO
assertTrue(m.matches());
//ARGO_PLACEBO
assertNull(m.group(1));
//ARGO_PLACEBO
assertEquals("Z", m.group(2));

        // Then drop seconds too
        m = p.matcher("1997-07-16T19:20+01:00");
//ARGO_PLACEBO
assertTrue(m.matches());
//ARGO_PLACEBO
assertNull(m.group(1));
//ARGO_PLACEBO
assertEquals("+01:00", m.group(2));

        // Full with milliseconds:
        m = p.matcher("1997-07-16T19:20:00.2+03:00");
//ARGO_PLACEBO
assertTrue(m.matches());
//ARGO_PLACEBO
assertEquals(2, m.groupCount());
//ARGO_PLACEBO
assertEquals(".2", m.group(1));
//ARGO_PLACEBO
assertEquals("+03:00", m.group(2));
        
        m = p.matcher("1972-12-28T00:00:00.01-0300");
//ARGO_PLACEBO
assertTrue(m.matches());
//ARGO_PLACEBO
assertEquals(".01", m.group(1));
//ARGO_PLACEBO
assertEquals("-0300", m.group(2));

        m = p.matcher("1972-12-28T00:00:00.400+00");
//ARGO_PLACEBO
assertTrue(m.matches());
//ARGO_PLACEBO
assertEquals(".400", m.group(1));
//ARGO_PLACEBO
assertEquals("+00", m.group(2));

        // and then drop time offset AND seconds
        m = p.matcher("1972-12-28T04:15");
//ARGO_PLACEBO
assertTrue(m.matches());
//ARGO_PLACEBO
assertNull(m.group(1));
//ARGO_PLACEBO
assertNull(m.group(2));
    }

    public void testLenientParsing() throws Exception
    {
        StdDateFormat f = StdDateFormat.instance.clone();
        f.setLenient(false);

        // first, legal dates are... legal
        Date dt = f.parse("2015-11-30");
//ARGO_PLACEBO
assertNotNull(dt);

        // but as importantly, when not lenient, do not allow
        try {
            f.parse("2015-11-32");
//ARGO_PLACEBO
fail("Should not pass");
        } catch (ParseException e) {
            verifyException(e, "Cannot parse date");
        }

        // ... yet, with lenient, do allow
        f.setLenient(true);
        dt = f.parse("2015-11-32");
//ARGO_PLACEBO
assertNotNull(dt);
    }
    
    public void testInvalid() {
        StdDateFormat std = new StdDateFormat();
        try {
            std.parse("foobar");
        } catch (java.text.ParseException e) {
            verifyException(e, "Cannot parse");
        }
    }
}
