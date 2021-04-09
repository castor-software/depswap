package org.json.simple.parser;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

public class YylexTest extends TestCase {

	public void testYylex() throws Exception{

	}

/*
	public void testYylex() throws Exception{
		String s="\"\\/\"";
		System.out.println(s);
		StringReader in = new StringReader(s);
		Yylex lexer=new Yylex(in);
		Yytoken token=lexer.yylex();
		//ARGO_NOT_COVERED_FEATURE
		assertEquals(Yytoken.TYPE_VALUE,token.type);
		//ARGO_NOT_COVERED_FEATURE
		assertEquals("/",token.value);
		
		s="\"abc\\/\\r\\b\\n\\t\\f\\\\\"";
		System.out.println(s);
		in = new StringReader(s);
		lexer=new Yylex(in);
		token=lexer.yylex();
		//ARGO_NOT_COVERED_FEATURE
		assertEquals(Yytoken.TYPE_VALUE,token.type);
		//ARGO_NOT_COVERED_FEATURE
		assertEquals("abc/\r\b\n\t\f\\",token.value);
		
		s="[\t \n\r\n{ \t \t\n\r}";
		System.out.println(s);
		in = new StringReader(s);
		lexer=new Yylex(in);
		token=lexer.yylex();
		//ARGO_NOT_COVERED_FEATURE
		assertEquals(Yytoken.TYPE_LEFT_SQUARE,token.type);
		token=lexer.yylex();
		//ARGO_NOT_COVERED_FEATURE
		assertEquals(Yytoken.TYPE_LEFT_BRACE,token.type);
		token=lexer.yylex();
		//ARGO_NOT_COVERED_FEATURE
		assertEquals(Yytoken.TYPE_RIGHT_BRACE,token.type);
		
		s="\b\f{";
		System.out.println(s);
		in = new StringReader(s);
		lexer=new Yylex(in);
		ParseException err=null;
		try{
			token=lexer.yylex();
		}
		catch(ParseException e){
			err=e;
			System.out.println("error:"+err);
		//ARGO_NOT_COVERED_FEATURE
			assertEquals(ParseException.ERROR_UNEXPECTED_CHAR, e.getErrorType());
		//ARGO_NOT_COVERED_FEATURE
			assertEquals(0,e.getPosition());
		//ARGO_NOT_COVERED_FEATURE
			assertEquals(new Character('\b'),e.getUnexpectedObject());
		}
		catch(IOException ie){
			throw ie;
		}
		//ARGO_NOT_COVERED_FEATURE
		assertTrue(err!=null);
		
		s="{a : b}";
		System.out.println(s);
		in = new StringReader(s);
		lexer=new Yylex(in);
		err=null;
		try{
			lexer.yylex();
			token=lexer.yylex();
		}
		catch(ParseException e){
			err=e;
			System.out.println("error:"+err);
		//ARGO_NOT_COVERED_FEATURE
			assertEquals(ParseException.ERROR_UNEXPECTED_CHAR, e.getErrorType());
		//ARGO_NOT_COVERED_FEATURE
			assertEquals(new Character('a'),e.getUnexpectedObject());
		//ARGO_NOT_COVERED_FEATURE
			assertEquals(1,e.getPosition());
		}
		catch(IOException ie){
			throw ie;
		}
		//ARGO_NOT_COVERED_FEATURE
		assertTrue(err!=null);
	}
*/
}
