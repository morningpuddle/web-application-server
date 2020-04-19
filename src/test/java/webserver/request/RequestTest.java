package webserver.request;

import db.DataBase;
import model.User;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;
import static webserver.request.Request.responseHeader;

public class RequestTest {

    public static final Path TEST_RESOURCE_DIR = Paths.get("src", "test", "resources");
    TestDatabaseImpl db;

    public class TestDatabaseImpl implements DataBase {
        public Map<String, User> users = new HashMap<>();

        @Override
        public void addUser(User user) { users.put(user.getUserId(), user); }

        @Override
        public User findUserById(String userId) { return users.get(userId); }

        @Override
        public Collection<User> findAll() { return users.values(); }
    }

    @Before
    public void setup() {
        db = new TestDatabaseImpl();
    }

    @Test
    public void testLoginRequestHandleResponse() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        User user = new User("id", "pass", null, null);
        db.addUser(user);

        LoginRequest getRequest = new LoginRequest(
                db,
                Map.of("userId", "id", "password", "pass")
        );
        getRequest.handleResponse(stream);

        String res = new String(stream.toByteArray());

        assertThat(res, containsString("302 Found"));
        assertThat(res, containsString("Location: /index.html"));
        assertThat(res, containsString("Cookie: logined=true"));
    }

    @Test
    public void testLoginRequestHandleResponseForFailed() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        User user = new User("id", "fail", null, null);
        db.addUser(user);

        LoginRequest getRequest = new LoginRequest(
                db,
                Map.of("userId", "id", "password", "pass")
        );
        getRequest.handleResponse(stream);

        String res = new String(stream.toByteArray());

        assertThat(res, containsString("302 Found"));
        assertThat(res, containsString("Location: /user/login_failed.html"));
        assertThat(res, containsString("Cookie: logined=false"));
    }

    @Test
    public void testLoginRequestHandleResponseForNonexistentUser() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        LoginRequest getRequest = new LoginRequest(
                db,
                Map.of("userId", "id", "password", "pass")
        );
        getRequest.handleResponse(stream);

        String res = new String(stream.toByteArray());

        assertThat(res, containsString("302 Found"));
        assertThat(res, containsString("Location: /user/login_failed.html"));
        assertThat(res, containsString("Cookie: logined=false"));
    }

    @Test
    public void testGetRequestHandleResponse() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        GetRequest getRequest = new GetRequest(TEST_RESOURCE_DIR.toString(), "/test-index.html");
        getRequest.handleResponse(stream);

        Path filePath = TEST_RESOURCE_DIR.resolve("test-index.html");
        String content = new String(Files.readAllBytes(filePath));
        String res = new String(stream.toByteArray());

        assertThat(res, containsString("200 OK"));
        assertThat(res, containsString("Content-Type: text/html"));
        assertThat(res, containsString(content));
    }


    @Test
    public void testGetRequestHandleResponseForCss() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        GetRequest getRequest = new GetRequest(TEST_RESOURCE_DIR.toString(), "/test.css");
        getRequest.handleResponse(stream);

        String res = new String(stream.toByteArray());

        assertThat(res, containsString("200 OK"));
        assertThat(res, containsString("Content-Type: text/css"));
    }

    @Test
    public void testListRequestHandleResponse() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        db.addUser(new User("id", null, "foobar", "a@b.c"));
        ListRequest getRequest = new ListRequest(TEST_RESOURCE_DIR.toString(), true, db);
        getRequest.handleResponse(stream);

        String res = new String(stream.toByteArray());

        assertThat(res, containsString("200 OK"));
        assertThat(res, containsString("<td>id</td>"));
        assertThat(res, containsString("<td>foobar</td>"));
        assertThat(res, containsString("<td>a@b.c</td>"));
    }

    @Test
    public void testListRequestHandleResponseIfNotLoggedIn() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ListRequest getRequest = new ListRequest(TEST_RESOURCE_DIR.toString(), false, db);
        getRequest.handleResponse(stream);

        String res = new String(stream.toByteArray());

        assertThat(res, containsString("302 Found"));
        assertThat(res, containsString("Location: /user/login.html"));
        assertThat(res, containsString("Set-Cookie: logined=false"));
    }

    @Test
    public void testNewUserRequestHandleResponse() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        Map<String, String> params = Collections.singletonMap("name", "world");
        NewUserRequest getRequest = new NewUserRequest(db, params);
        getRequest.handleResponse(stream);

        String res = new String(stream.toByteArray());

        assertThat(res, containsString("302 Found"));
        assertThat(db.users.values(), hasItem(new User(null, null, "world", null)));
    }

    @Test
    public void testGetArguments() {
        GetRequest getRequest = new GetRequest("/index.html", "/index.html");
        assertThat(getRequest.getArguments(), equalTo("/index.html"));

        Map<String, String> params = Collections.singletonMap("name", "world");
        NewUserRequest newUserRequest = new NewUserRequest(db, params);
        assertThat(newUserRequest.getArguments(), equalTo(params.toString()));
    }

    @Test
    public void testResponseHeader() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(stream);

        responseHeader(dos, 200, "OK", Collections.singletonMap("Content-Length", "20"));

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