package prgms.lecture.order_management.user.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import prgms.lecture.order_management.exception.NotFoundException;
import prgms.lecture.order_management.exception.UnauthorizedException;
import prgms.lecture.order_management.security.Jwt;
import prgms.lecture.order_management.security.JwtAuthentication;
import prgms.lecture.order_management.security.JwtAuthenticationToken;
import prgms.lecture.order_management.user.domain.User;
import prgms.lecture.order_management.user.dto.LoginRequest;
import prgms.lecture.order_management.user.dto.LoginResponse;
import prgms.lecture.order_management.user.dto.UserDto;
import prgms.lecture.order_management.user.service.UserService;
import prgms.lecture.order_management.utils.ApiUtils;

import javax.validation.Valid;

import static prgms.lecture.order_management.utils.ApiUtils.success;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final Jwt jwt;

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    public UserController(Jwt jwt, AuthenticationManager authenticationManager, UserService userService) {
        this.jwt = jwt;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping(path = "login")
    public ApiUtils.ApiResult<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) throws UnauthorizedException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new JwtAuthenticationToken(request.getPrincipal(), request.getCredentials())
            );
            final User user = (User) authentication.getDetails();
            final String token = user.newJwt(
                    jwt,
                    authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toArray(String[]::new)
            );
            return success(new LoginResponse(token, user));
        } catch (AuthenticationException e) {
            throw new UnauthorizedException(e.getMessage(), e);
        }
    }

    @GetMapping(path = "me")
    public ApiUtils.ApiResult<UserDto> me(
            // JwtAuthenticationTokenFilter 에서 JWT 값을 통해 사용자를 인증한다.
            // 사용자 인증이 정상으로 완료됐다면 @AuthenticationPrincipal 어노테이션을 사용하여 인증된 사용자 정보(JwtAuthentication)에 접근할 수 있다.
            @AuthenticationPrincipal JwtAuthentication authentication
    ) {
        return success(
                userService.findById(authentication.id)
                        .map(UserDto::new)
                        .orElseThrow(() -> new NotFoundException("Could nof found user for " + authentication.id))
        );
    }

}
