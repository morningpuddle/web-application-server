package webserver.request;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;

public class ListRequest extends Request {
    private static final Logger log = LoggerFactory.getLogger(GetRequest.class);

    private final boolean logged;
    private final String baseDir;
    private final DataBase db;

    public ListRequest(String baseDir, boolean logged, DataBase db) {
        this.logged = logged;
        this.baseDir = baseDir;
        this.db = db;
    }

    @Override
    public void handleResponse(OutputStream out) {
        DataOutputStream dos = new DataOutputStream(out);
        if (!logged) {
            responseHeader(dos, 302, "Found",
                    Map.of("Location", "/user/login.html", "Set-Cookie", "logined=false")
            );
        } else {
            try {
                String bodyString = Files.readString(new File(baseDir + "/user/list.html").toPath());
                bodyString = bodyString.replace("$tbody", generateList());
                byte[] bytes = bodyString.getBytes();
                responseHeader(dos, 200, "OK", bytes.length);
                responseBody(dos, bytes);
            } catch (IOException e) {
                log.error("Failed to handle response for GetRequest", e);
            }
        }
    }

    private String generateList() {
        StringBuilder builder = new StringBuilder();
        for (User user : db.findAll()) {
            builder.append("<tr>\n" +
                    "<th scope=\"row\">2</th> <td>" + user.getUserId() + "</td> <td>" + user.getName() + "</td> <td>" + user.getEmail() + "</td><td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>\n" +
                    "</tr>\n"
            );
        }
        return builder.toString();
    }

    @Override
    public String getArguments() {
        return null;
    }
}
