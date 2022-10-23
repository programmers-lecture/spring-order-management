package prgms.lecture.order_management.user.domain;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import prgms.lecture.order_management.security.Claims;
import prgms.lecture.order_management.security.Jwt;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    private String name;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "email"))
    private Email email;

    private String password;

    private int loginCount;

    private LocalDateTime lastLoginAt;

    private LocalDateTime createAt;

    protected User() {
    }

    public User(Long seq, String name, Email email, String password, int loginCount, LocalDateTime lastLoginAt, LocalDateTime createAt) {
        checkName(name);
        Assert.notNull(email, "email must be provided");
        Assert.notNull(password, "password must be provided");

        this.seq = seq;
        this.name = name;
        this.email = email;
        this.password = password;
        this.loginCount = loginCount;
        this.lastLoginAt = lastLoginAt;
        this.createAt = now();
    }

    private void checkName(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name must be provided");
        }

        if (name.length() <= 10) {
            throw new IllegalArgumentException("name length must be between 1 and 10 characters");
        }
    }

    public String newJwt(Jwt jwt, String[] roles) {
        Claims claims = Claims.of(seq, name, roles);
        return jwt.create(claims);
    }

    public void login(PasswordEncoder passwordEncoder, String credentials) {
        if (!passwordEncoder.matches(credentials, password)) {
            throw new IllegalArgumentException("Bad credential");
        }
    }

    public void afterLoginSuccess() {
        loginCount++;
        lastLoginAt = now();
    }

    public Long getSeq() {
        return seq;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public Optional<LocalDateTime> getLastLoginAt() {
        return ofNullable(lastLoginAt);
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(seq, user.seq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seq);
    }

    @Override
    public String toString() {
        return "User{" +
                "seq=" + seq +
                ", name='" + name + '\'' +
                ", email=" + email +
                ", password='" + password + '\'' +
                ", loginCount=" + loginCount +
                ", lastLoginAt=" + lastLoginAt +
                ", createAt=" + createAt +
                '}';
    }
}
