package com.trisquel.service;

import com.trisquel.model.Client;
import com.trisquel.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class ClientService {
    @Autowired
    ClientService(ClientRepository repository){
        this.repository = repository;
    }
    private final ClientRepository repository;

    public List<Client> findAll() { return repository.findAll(); }
    public Optional<Client> findById(Long id) { return repository.findById(id); }
    public Client save(Client client) { return repository.save(client); }
    public void delete(Long id) { repository.deleteById(id); }
}
