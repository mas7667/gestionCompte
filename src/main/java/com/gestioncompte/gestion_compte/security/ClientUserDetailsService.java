package com.gestioncompte.gestion_compte.security;

import com.gestioncompte.gestion_compte.repository.ClientRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ClientUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;

    public ClientUserDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return clientRepository.findByEmail(email)
                .map(client -> new User(client.getEmail(), client.getPasswordHash(), Collections.emptyList()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}