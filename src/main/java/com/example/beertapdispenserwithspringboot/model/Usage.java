package com.example.beertapdispenserwithspringboot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage")
@Getter
@Setter
@NoArgsConstructor
public class Usage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column(name = "opened_at")
    private LocalDateTime openedAt;
    @Column(name = "closed_at")
    private LocalDateTime closedAt;
    @Column(name = "flow_volume")
    private String flowVolume;
    @Column(name = "total_spent")
    private String totalSpent;
    @ManyToOne
    @JoinColumn(name = "dispenser_id")
    @JsonBackReference
    private Dispenser dispenser;
}
