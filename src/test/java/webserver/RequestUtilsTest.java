package webserver;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class RequestUtilsTest {

    @Test
    public void returnNull() throws IOException {
        String test = "Host: localhost:8080";
        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);

        assertThat(RequestUtils.getRequestedFile(reader), nullValue());
    }

    @Test
    public void returnRequestedFile() throws IOException {
        String test = "GET /index.html HTTP/1.1";
        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);

        assertThat(RequestUtils.getRequestedFile(reader), is("/index.html"));
    }
}