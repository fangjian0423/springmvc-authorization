package org.format.demo.handler;


import java.util.List;

public interface AuthHandler {

    public boolean handleAuth(String userName, List<String> auth, List<String> roles);

    public void handleSuccess();

    public void handleError();

}
