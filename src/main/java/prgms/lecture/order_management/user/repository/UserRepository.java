package prgms.lecture.order_management.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prgms.lecture.order_management.user.domain.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    void update(User user);

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

}
