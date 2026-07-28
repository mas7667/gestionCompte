package com.gestioncompte.gestion_compte.service;

import com.gestioncompte.gestion_compte.model.Client;
import com.gestioncompte.gestion_compte.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client getById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No client found with id: " + id));
    }

    public Client getByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No client found with email: " + email));
    }

    public boolean emailExists(String email) {
        return clientRepository.existsByEmail(email);
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }
}