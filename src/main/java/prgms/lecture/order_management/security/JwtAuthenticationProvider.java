package prgms.lecture.order_management.security;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import prgms.lecture.order_management.exception.NotFoundException;
import prgms.lecture.order_management.user.domain.Email;
import prgms.lecture.order_management.user.domain.Role;
import prgms.lecture.order_management.user.domain.User;
import prgms.lecture.order_management.user.service.UserService;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.util.TypeUtils.isAssignable;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    public JwtAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        return processUserAuthentication(
                Email.of(String.valueOf(authenticationToken.getPrincipal())),
                authenticationToken.getCredentials()
        );
    }

    private Authentication processUserAuthentication(Email email, String password) {
        try {
            User user = userService.login(email, password);
            JwtAuthenticationToken authenticated =
                    new JwtAuthenticationToken(
                            new JwtAuthentication(user.getSeq(), user.getName()),
                            null,
                            createAuthorityList(Role.USER.value())
                    );
            authenticated.setDetails(user);
            return authenticated;
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (DataAccessException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return isAssignable(JwtAuthenticationToken.class, authentication);
    }

}