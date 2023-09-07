package com.example.beertapdispenserwithspringboot.repository;

import com.example.beertapdispenserwithspringboot.model.Dispenser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DispenserRepository extends JpaRepository<Dispenser, String> {

    Optional<Dispenser> findDispenserById(final String id);
}
