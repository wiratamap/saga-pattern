package com.personal.sagapattern.application_user;

import javax.transaction.Transactional;

import com.personal.sagapattern.authentication.model.UserPrinciple;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ApplicationUserService implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {
        ApplicationUser applicationUser = applicationUserRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found with -> username or email: " + email));

        return UserPrinciple.build(applicationUser);
    }
}
