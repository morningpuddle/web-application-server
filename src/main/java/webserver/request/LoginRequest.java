package webserver.request;

import com.google.common.collect.Maps;
import db.DataBase;
import lombok.Value;
import model.User;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

@Value
public class LoginRequest extends Request {
    Map<String, String> args;
    DataBase db;

    public LoginRequest(DataBase userDatabase, Map<String, String> args) {
        this.db = userDatabase;
        this.args = args;
    }

    @Override
    public void handleResponse(OutputStream out) {
        DataOutputStream dos = new DataOutputStream(out);
        String userId = args.get("userId");
        String password = args.get("password");
        User user = db.findUserById(userId);
        if (user == null) {
            responseHeader(dos, 302, "Found",
                    Map.of("Location", "/user/login_failed.html", "Set-Cookie", "logined=false")
            );
            return;
        }
        if (user.getPassword() != null && user.getPassword().equals(password)) {
            responseHeader(dos, 302, "Found",
                    Map.of("Location", "/index.html","Set-Cookie", "logined=true")
            );
        } else {
            responseHeader(dos, 302, "Found",
                    Map.of("Location", "/user/login_failed.html", "Set-Cookie", "logined=false")
            );
        }
    }

    @Override
    public String getArguments() {
        return null;
    }
}
