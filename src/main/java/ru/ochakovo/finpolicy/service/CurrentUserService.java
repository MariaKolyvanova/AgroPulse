package ru.ochakovo.finpolicy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ochakovo.finpolicy.entity.organization.Company;
import ru.ochakovo.finpolicy.entity.organization.User;
import ru.ochakovo.finpolicy.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email);
    }

    public Company getCurrentCompany(){
        return getCurrentUser().getCompany();
    }
}