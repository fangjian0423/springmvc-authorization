package org.format.demo.handler;


import org.format.demo.model.AuthMode;

import java.util.Set;

public interface AuthHandler {

    /**
     * 权限验证逻辑
     * @param userName
     * @param auth
     * @param roles
     * @param mode
     * @return
     */
    public boolean handleAuth(String userName, Set<String> auth, Set<String> roles, AuthMode mode);

    /**
     * 权限验证成功后的操作
     */
    public void authSuccess();

    /**
     * 权限验证失败后的操作
     */
    public void authError();

}
