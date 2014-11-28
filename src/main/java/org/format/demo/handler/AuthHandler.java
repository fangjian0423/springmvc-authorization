package org.format.demo.handler;


import org.format.demo.model.AuthMode;

import java.util.Set;

public interface AuthHandler {

    public boolean handleAuth(String userName, Set<String> auth, Set<String> roles, AuthMode mode);

    public void authSuccess();

    public void authError();

}
