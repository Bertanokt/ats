package ats.repository;

import ats.model.CvDosya;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CvDosyaRepository extends JpaRepository<CvDosya, Long> {

    Optional<CvDosya> findByAdayId(Long adayId);
}