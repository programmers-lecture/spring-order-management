package prgms.lecture.order_management.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import prgms.lecture.order_management.exception.NotFoundException;
import prgms.lecture.order_management.user.domain.Email;
import prgms.lecture.order_management.user.domain.User;
import prgms.lecture.order_management.user.repository.UserRepository;

import java.util.Optional;


@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    public User login(Email email, String password) {
        Assert.notNull(password, "password must be provided");

        User user = findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Could not found user for " + email));
        user.login(passwordEncoder, password);
        user.afterLoginSuccess();
        userRepository.update(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        Assert.notNull(userId, "userId must be provided");

        return userRepository.findById(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(Email email) {
        Assert.notNull(email, "email must be provided");

        return userRepository.findByEmail(email.getAddress());
    }

}
