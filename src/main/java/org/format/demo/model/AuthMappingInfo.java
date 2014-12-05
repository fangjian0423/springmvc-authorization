package org.format.demo.model;

import java.util.List;

public class AuthMappingInfo {

    private String url;
    private List<String> roles;
    private List<String> auth;
    private AuthMode mode;

    public AuthMappingInfo(String url, List<String> roles, List<String> auth, AuthMode mode) {
        this.url = url;
        this.roles = roles;
        this.auth = auth;
        this.mode = mode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getAuth() {
        return auth;
    }

    public void setAuth(List<String> auth) {
        this.auth = auth;
    }

    public AuthMode getMode() {
        return mode;
    }

    public void setMode(AuthMode mode) {
        this.mode = mode;
    }

}
