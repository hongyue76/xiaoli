package com.xiaoli.legal.compliance.mapper;

import com.xiaoli.legal.compliance.model.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 企业Mapper (JPA)
 */
@Repository
public interface CompanyMapper extends JpaRepository<Company, Long> {
    
    List<Company> findByIndustry(String industry);
    
    List<Company> findByRiskLevel(String riskLevel);
}
