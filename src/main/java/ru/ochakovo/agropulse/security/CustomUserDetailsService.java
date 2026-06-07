package ru.ochakovo.agropulse.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ochakovo.agropulse.entity.organization.User;
import ru.ochakovo.agropulse.entity.organization.UserStatus;
import ru.ochakovo.agropulse.repository.organization.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (user.getUserStatus() == UserStatus.ARCHIVE) {
            throw new DisabledException("User is archived");
        }

        return new CustomUserDetails(user);
    }
}
