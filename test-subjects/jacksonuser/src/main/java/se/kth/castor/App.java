package se.kth.castor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.kth.castor.data.Simple;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World!" );
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

        System.out.println("s2");
    }
}
