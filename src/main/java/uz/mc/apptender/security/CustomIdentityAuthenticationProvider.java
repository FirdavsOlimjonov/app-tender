package uz.mc.apptender.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.mc.apptender.exeptions.RestException;
import uz.mc.apptender.modules.enums.RoleEnum;
import uz.mc.apptender.repositories.UserRepository;
import uz.mc.apptender.utils.MessageConstants;

@Component
@RequiredArgsConstructor
public class CustomIdentityAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    // TODO:
    // In this function we need to connect with identity provider
    // and validate the user
    // we are hardcoding for a single user for demo purposes
    UserDetails isValidUser(String username, String password, uz.mc.apptender.modules.User user) {
        if (passwordEncoder.matches(password, user.getPassword())) {
                if (username.equalsIgnoreCase(adminUsername) && password.equalsIgnoreCase(adminPassword))
                    return User
                            .withUsername(username)
                            .password(password)
                            .roles(RoleEnum.ADMIN.name())
                            .build();
                return User
                        .withUsername(username)
                        .password(password)
                        .roles(RoleEnum.USER.name())
                        .build();
        }
        throw RestException.restThrow(MessageConstants.FULL_AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        uz.mc.apptender.modules.User user = userRepository.findByUsernameEqualsIgnoreCase(username).orElseThrow(
                () -> RestException.restThrow(MessageConstants.USER_NOT_FOUND, HttpStatus.FORBIDDEN));

        UserDetails userDetails = isValidUser(username, password, user);
        if (userDetails != null) {
            return new UsernamePasswordAuthenticationToken(
                    user, user.getAuthorities(), null);
        } else {
            throw new BadCredentialsException("Incorrect user credentials !!");
        }
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return authenticationType
                .equals(UsernamePasswordAuthenticationToken.class);
    }
}