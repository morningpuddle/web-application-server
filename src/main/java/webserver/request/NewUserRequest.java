package webserver.request;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

public class NewUserRequest extends Request {
    private static final Logger log = LoggerFactory.getLogger(NewUserRequest.class);

    Map<String, String> formData;

    public NewUserRequest(Map<String, String> formData) {
        this.type = WebRequestType.POST;
        this.formData = formData;
    }

    @Override
    public void handleResponse(OutputStream out) {
        DataOutputStream dos = new DataOutputStream(out);
        User user = new User(
                formData.get("userId"),
                formData.get("password"),
                formData.get("name"),
                formData.get("email")
        );
        log.debug("Created user {}", user);

        responseHeader(dos, 302, "Found",
                Collections.singletonMap("Location", "/index.html")
        );
    }

    @Override
    public String getArguments() {
        return formData.toString();
    }
}
