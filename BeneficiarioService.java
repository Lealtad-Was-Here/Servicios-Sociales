package org601.service;

import org601.entity.Beneficiario;
import org601.repository.BeneficiarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BeneficiarioService {
    private final BeneficiarioRepository repo;
    public BeneficiarioService(BeneficiarioRepository repo) { this.repo = repo; }

    public Beneficiario save(Beneficiario b) { return repo.save(b); }
    public List<Beneficiario> list() { return repo.findAll(); }
    public Optional<Beneficiario> get(Long id) { return repo.findById(id); }
    public void delete(Long id) { repo.deleteById(id); }
}
