package com.emiteai.repository;

import com.emiteai.entities.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByEndpointContainingIgnoreCase(String endpoint);

    List<Auditoria> findByMetodoHttpAndEndpoint(String metodoHttp, String endpoint);

    List<Auditoria> findByTimestampRequisicaoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Auditoria> findByStatusResposta(Integer status);

    @Query("SELECT a FROM Auditoria a WHERE a.timestampRequisicao >= :inicio ORDER BY a.timestampRequisicao DESC")
    List<Auditoria> findRecentAudits(@Param("inicio") LocalDateTime inicio);

    @Query("SELECT COUNT(a) FROM Auditoria a WHERE a.timestampRequisicao >= :inicio")
    Long countRequestsSince(@Param("inicio") LocalDateTime inicio);

    @Query("SELECT a.statusResposta, COUNT(a) FROM Auditoria a WHERE a.timestampRequisicao >= :inicio GROUP BY a.statusResposta")
    List<Object[]> getStatusStatistics(@Param("inicio") LocalDateTime inicio);
}
