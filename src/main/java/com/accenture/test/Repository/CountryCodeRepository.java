package com.accenture.test.Repository;

import com.accenture.test.model.AirportData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryCodeRepository extends JpaRepository<AirportData, String> {
}
