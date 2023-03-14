package uz.mc.apptender.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomIdentityAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;

    @Value("${app.basic-auth.username}")
    private String basicAuthUsername;

    @Value("${app.basic-auth.password}")
    private String basicAuthPassword;


    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (Objects.isNull(username) || !Objects.equals(username,basicAuthUsername))
            throw new BadCredentialsException("Full authentication required!");

        if (Objects.isNull(password) || !Objects.equals(password,basicAuthPassword))
            throw new BadCredentialsException("Full authentication required!");

        return new UsernamePasswordAuthenticationToken(null, null, null);
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return authenticationType.equals(UsernamePasswordAuthenticationToken.class);
    }
}