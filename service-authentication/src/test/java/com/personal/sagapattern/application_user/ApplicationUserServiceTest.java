package com.personal.sagapattern.application_user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class ApplicationUserServiceTest {

    @InjectMocks
    private ApplicationUserService applicationUserService;

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @Test
    void loadUserByUsername_expectReturnUserPrinciple_whenUsernameIsFound() {
        ApplicationUser existingApplicationUser = ApplicationUser.builder().email("john.doe@mail.com").name("John Doe")
                .email("johndoe@gmail.com").cif("ACCOUNT1").password("secret").build();
        existingApplicationUser.setId(UUID.randomUUID());
        Mockito.when(applicationUserRepository.findByEmail(existingApplicationUser.getEmail()))
                .thenReturn(Optional.of(existingApplicationUser));

        UserDetails userDetails = applicationUserService.loadUserByUsername(existingApplicationUser.getEmail());

        assertEquals(existingApplicationUser.getEmail(), userDetails.getUsername());
        assertEquals(existingApplicationUser.getPassword(), userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_expectThrowException_whenUsernameIsNotFound() {
        Mockito.when(applicationUserRepository.findByEmail("not.found.email@mail.com")).thenReturn(Optional.empty());

        Executable loadByUsernameAction = () -> applicationUserService.loadUserByUsername("not.found.email@mail.com");

        assertThrows(UsernameNotFoundException.class, loadByUsernameAction);
    }

}
