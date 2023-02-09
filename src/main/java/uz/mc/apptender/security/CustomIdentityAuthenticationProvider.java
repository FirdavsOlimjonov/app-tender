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

import java.util.Objects;
import java.util.Optional;

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
        if (passwordEncoder.matches(password, user.getPassword()) && Objects.equals(user.getUsername(), username))
            return user;
        return null;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        Optional<uz.mc.apptender.modules.User> optionalUser = userRepository.findByUsernameEqualsIgnoreCase(username);
        if (optionalUser.isEmpty())
            throw new BadCredentialsException("User not found");

        uz.mc.apptender.modules.User user = optionalUser.get();
        UserDetails userDetails = isValidUser(username, password, user);

        if (userDetails == null)
            throw new BadCredentialsException("Incorrect user credentials !!");

        return new UsernamePasswordAuthenticationToken(
                user, user.getAuthorities(), null);
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return authenticationType.equals(UsernamePasswordAuthenticationToken.class);
    }
}