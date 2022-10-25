package prgms.lecture.order_management.user.dto;


import prgms.lecture.order_management.user.domain.User;

public class LoginResponse {

    private final String token;

    private final UserDto user;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = new UserDto(user);
    }

    public String getToken() {
        return token;
    }

    public UserDto getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", user=" + user +
                '}';
    }
}
