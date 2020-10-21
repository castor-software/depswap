package se.kth.castor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import se.kth.castor.data.CollectionFields;
import se.kth.castor.data.Composite;
import se.kth.castor.data.Simple;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testSimple() throws IOException {
        Simple s = new Simple();
        s.setBi(new BigInteger("348930"));
        s.setD(0.0004);
        s.setF(3.14F);
        s.setI(2);
        s.setL(-2L);
        s.setStr("tutu");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(s);

        System.out.println("json: " + json);

        Simple s2 = mapper.readValue(json,Simple.class);

        assertEquals(s,s2);
    }

    @Test
    public void testComposite() throws IOException {
        Simple s = new Simple();
        s.setBi(new BigInteger("348930"));
        s.setD(0.0004);
        s.setF(3.14F);
        s.setI(2);
        s.setL(-2L);
        s.setStr("tutu");
        Composite c = new Composite();
        c.s1 = s;
        c.s2 = s;
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(c);

        System.out.println("json: " + json);

        Composite c2 = mapper.readValue(json, Composite.class);

        assertEquals(c,c2);
    }

    @Test
    public void testCollectionFields() throws IOException {
        Simple s = new Simple();
        s.setBi(new BigInteger("348930"));
        s.setD(0.0004);
        s.setF(3.14F);
        s.setI(2);
        s.setL(-2L);
        s.setStr("tutu");
        Composite c = new Composite();
        c.s1 = s;
        c.s2 = s;
        CollectionFields col = new CollectionFields();
        col.l.add(s);
        col.m.put("t", c);


        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(col);

        System.out.println("json: " + json);

        CollectionFields col2 = mapper.readValue(json, CollectionFields.class);

        assertEquals(col,col2);
    }
}
