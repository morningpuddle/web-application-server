package webserver;

import org.junit.Ignore;
import org.junit.Test;
import webserver.request.GetRequest;
import webserver.request.NewUserRequest;
import webserver.request.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class RequestUtilsTest {

    @Test
    public void createsGetRequest() throws IOException {
        String test = "GET /index.html HTTP/1.1";
        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);

        Request req = RequestUtils.createRequest(reader);

        assertThat(req, instanceOf(GetRequest.class));
    }

    @Test
    public void createsNewUserRequest() throws IOException {
        String test =
                "POST /user/create HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 48\n" +
                "\n" +
                "userId=abc&password=def&name=ghi&email=y%40a.com\n";
        Reader inputString = new StringReader(test);
        BufferedReader reader = new BufferedReader(inputString);

        Request req = RequestUtils.createRequest(reader);

        assertThat(req, instanceOf(NewUserRequest.class));
    }

    @Test
    @Ignore("TODO")
    public void creates404() throws IOException { }
}