package io.gromit.uaparser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ParserTest {

    private String agentString1 = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; fr; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5,gzip(gfe),gzip(gfe)";
    private String agentString2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3";

    private static Parser parser;
	
    @BeforeClass	
    public static void beforeClass() throws IOException{
    	parser = new Parser();
    }
    		
	@Test
	public void testParse() throws IOException {
		System.out.println(new ObjectMapper().writeValueAsString(parser.parse(agentString1)));
		System.out.println(new ObjectMapper().writeValueAsString(parser.parse(agentString2)));
	}

}
