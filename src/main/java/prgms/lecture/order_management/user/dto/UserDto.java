package prgms.lecture.order_management.user.dto;

import prgms.lecture.order_management.user.domain.Email;
import prgms.lecture.order_management.user.domain.User;

import java.time.LocalDateTime;

import static org.springframework.beans.BeanUtils.copyProperties;

public class UserDto {

    private String name;

    private Email email;

    private int loginCount;

    private final LocalDateTime lastLoginAt;

    private LocalDateTime createAt;

    public UserDto(User source) {
        copyProperties(source, this);

        this.lastLoginAt = source.getLastLoginAt().orElse(null);
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "name='" + name + '\'' +
                ", email=" + email +
                ", loginCount=" + loginCount +
                ", lastLoginAt=" + lastLoginAt +
                ", createAt=" + createAt +
                '}';
    }
}