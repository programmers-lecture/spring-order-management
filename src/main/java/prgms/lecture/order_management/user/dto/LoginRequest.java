package prgms.lecture.order_management.user.dto;

import javax.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "principal must be provided")
    private String principal;

    @NotBlank(message = "credentials must be provided")
    private String credentials;

    protected LoginRequest() {
    }

    public LoginRequest(String principal, String credentials) {
        this.principal = principal;
        this.credentials = credentials;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getCredentials() {
        return credentials;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "principal='" + principal + '\'' +
                ", credentials='" + credentials + '\'' +
                '}';
    }
}