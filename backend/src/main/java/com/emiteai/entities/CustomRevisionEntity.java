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
@RevisionEntity(CustomRevisionListener.class) // 🔧 Adicionar listener
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
    
    // 🔧 Campos opcionais para auditoria avançada
    @Column(name = "usuario", length = 100)
    private String usuario; // Usuário que fez a modificação
    
    @Column(name = "ip_origem", length = 45)
    private String ipOrigem; // IP de onde veio a modificação
    
    @Column(name = "user_agent", length = 500)
    private String userAgent; // Navegador/cliente
    
    @Column(name = "motivo", length = 500)
    private String motivo; // Motivo da mudança (opcional)
    
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
