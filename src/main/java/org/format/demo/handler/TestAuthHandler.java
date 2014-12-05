package org.format.demo.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.format.demo.model.AuthMode;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TestAuthHandler implements AuthHandler, Ordered {

    private static Log log = LogFactory.getLog(TestAuthHandler.class);

    @Override
    public boolean handleAuth(String userName, Set<String> auth, Set<String> roles, AuthMode mode) {
        log.info(userName + ", " + auth + ", " + roles + ", " + mode);
        return true;
    }

    @Override
    public void authSuccess() {

    }

    @Override
    public void authError() {

    }

    @Override
    public int getOrder() {
        return -100;
    }
}
