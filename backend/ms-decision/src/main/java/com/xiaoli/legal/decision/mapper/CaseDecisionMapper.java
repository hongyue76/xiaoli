package com.xiaoli.legal.decision.mapper;

import com.xiaoli.legal.decision.model.entity.CaseDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 案件决策Mapper (JPA)
 */
@Repository
public interface CaseDecisionMapper extends JpaRepository<CaseDecision, Long> {
    
    List<CaseDecision> findByCaseType(String caseType);
}
