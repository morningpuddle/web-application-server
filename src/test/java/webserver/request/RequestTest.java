package webserver.request;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static webserver.request.Request.responseHeader;

public class RequestTest {

    public static final Path TEST_RESOURCE_DIR = Paths.get("src", "test", "resources");

    @Test
    public void testGetRequestHandleResponse() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        GetRequest getRequest = new GetRequest(TEST_RESOURCE_DIR.toString(), "/test-index.html");
        getRequest.handleResponse(stream);

        Path filePath = TEST_RESOURCE_DIR.resolve("test-index.html");
        String content = new String(Files.readAllBytes(filePath));
        String res = new String(stream.toByteArray());

        assertThat(res, containsString("200 OK"));
        assertThat(res, containsString(content));
    }


    @Test
    public void testNewUserRequestHandleResponse() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Map<String, String> params = Collections.singletonMap("name", "world");
        NewUserRequest getRequest = new NewUserRequest(params);
        getRequest.handleResponse(stream);

        String res = new String(stream.toByteArray());

        assertThat(res, containsString("200 OK"));
    }

    @Test
    public void testGetArguments() {
        GetRequest getRequest = new GetRequest("/index.html");
        assertThat(getRequest.getArguments(), equalTo("/index.html"));

        Map<String, String> params = Collections.singletonMap("name", "world");
        NewUserRequest newUserRequest = new NewUserRequest(params);
        assertThat(newUserRequest.getArguments(), equalTo(params.toString()));
    }

    @Test
    public void testResponseHeader() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(stream);

        responseHeader(dos, "200", 20);

        String res = new String(stream.toByteArray());
        assertThat(res, containsString("200 OK"));
        assertThat(res, containsString("Content-Length: 20"));
    }

    @Test
    public void testResponseBody() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(stream);
        byte[] helloWorld = "Hello World".getBytes();

        Request.responseBody(dos, helloWorld);

        String res = new String(stream.toByteArray());
        assertThat(res, containsString("Hello World"));
    }
}