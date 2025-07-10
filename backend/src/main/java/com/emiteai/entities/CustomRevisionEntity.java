package com.emiteai.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

/**
 * Entidade customizada para revisões do Hibernate Envers
 * Resolve o problema da sequência padrão 'revinfo_seq' vs 'hibernate_sequence'
 */
@Entity
@RevisionEntity
@Table(name = "revinfo")
@Getter
@Setter
public class CustomRevisionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revinfo_seq")
    @SequenceGenerator(name = "revinfo_seq", sequenceName = "hibernate_sequence", allocationSize = 1)
    @RevisionNumber
    @Column(name = "rev")
    private int id;
    
    @RevisionTimestamp
    @Column(name = "revtstmp")
    private long timestamp;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomRevisionEntity)) return false;
        CustomRevisionEntity that = (CustomRevisionEntity) o;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
}
