package ru.ochakovo.agropulse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.ochakovo.agropulse.entity.organization.Company;
import ru.ochakovo.agropulse.entity.organization.User;
import ru.ochakovo.agropulse.repository.UserRepository;

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