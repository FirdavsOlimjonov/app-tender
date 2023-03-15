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

    @Value("${app.basic-auth-offeror.username}")
    private String basicAuthUsernameOfferor;

    @Value("${app.basic-auth-offeror.password}")
    private String basicAuthPasswordOfferor;


    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (Objects.isNull(username) || Objects.isNull(password))
            throw new BadCredentialsException("Full authentication required!");

        if ((Objects.equals(username, basicAuthUsername) && Objects.equals(password, basicAuthPassword)))
            return new UsernamePasswordAuthenticationToken(null, null, null);

        if ((Objects.equals(username, basicAuthUsernameOfferor) && Objects.equals(password, basicAuthPasswordOfferor)))
            return new UsernamePasswordAuthenticationToken(null, null, null);

        throw new BadCredentialsException("Full authentication required!");
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return authenticationType.equals(UsernamePasswordAuthenticationToken.class);
    }
}