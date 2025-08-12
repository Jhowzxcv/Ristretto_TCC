package com.Ristretto.API.infra.security;

import com.Ristretto.API.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username){
        var user = repository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        return user; // ← Agora retorna direto, pois User implementa UserDetails
    }
}