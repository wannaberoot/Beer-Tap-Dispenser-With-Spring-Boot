package com.example.beertapdispenserwithspringboot.repository;

import com.example.beertapdispenserwithspringboot.model.Dispenser;
import com.example.beertapdispenserwithspringboot.model.Usage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsageRepository extends JpaRepository<Usage, Long> {

    List<Usage> findAllByDispenser(final Dispenser dispenser);
    List<Usage> findAllByDispenserOrderByOpenedAt(final Dispenser dispenser);
}
