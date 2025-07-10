package com.emiteai.service;

import com.emiteai.entities.Pessoa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EnversAuditService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Busca todas as revisões de uma pessoa específica
     */
    public List<Number> findRevisionsByPessoa(Long pessoaId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.getRevisions(Pessoa.class, pessoaId);
    }

    /**
     * Busca o estado de uma pessoa em uma revisão específica
     */
    public Pessoa findPessoaAtRevision(Long pessoaId, Number revision) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.find(Pessoa.class, pessoaId, revision);
    }

    /**
     * Busca todas as pessoas modificadas em um período
     */
    @SuppressWarnings("unchecked")
    public List<Pessoa> findPessoasModifiedBetween(Date startDate, Date endDate) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        
        return auditReader.createQuery()
                .forRevisionsOfEntity(Pessoa.class, false, true)
                .add(AuditEntity.revisionProperty("timestamp").between(
                    startDate.getTime(), endDate.getTime()))
                .getResultList();
    }

    /**
     * Busca o histórico completo de uma pessoa com informações de revisão
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findPessoaHistoryWithRevisionInfo(Long pessoaId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        
        return auditReader.createQuery()
                .forRevisionsOfEntity(Pessoa.class, false, true)
                .add(AuditEntity.id().eq(pessoaId))
                .addOrder(AuditEntity.revisionNumber().desc())
                .getResultList();
    }

    /**
     * Busca todas as operações (INSERT, UPDATE, DELETE) de pessoas
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findAllPessoaOperations() {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        
        return auditReader.createQuery()
                .forRevisionsOfEntity(Pessoa.class, false, true)
                .addOrder(AuditEntity.revisionProperty("timestamp").desc())
                .setMaxResults(100) // Limitar para performance
                .getResultList();
    }

    /**
     * Conta quantas vezes uma pessoa foi modificada
     */
    public Long countPessoaRevisions(Long pessoaId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = auditReader.getRevisions(Pessoa.class, pessoaId);
        return (long) revisions.size();
    }

    /**
     * Busca a data da última modificação de uma pessoa
     */
    public Date findLastModificationDate(Long pessoaId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = auditReader.getRevisions(Pessoa.class, pessoaId);
        
        if (revisions.isEmpty()) {
            return null;
        }
        
        Number lastRevision = revisions.get(revisions.size() - 1);
        return auditReader.getRevisionDate(lastRevision);
    }
}
