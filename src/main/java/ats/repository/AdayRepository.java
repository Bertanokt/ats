package ats.repository;

import ats.model.Aday;
import ats.model.Ilan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdayRepository extends JpaRepository<Aday, Long> {

    Optional<Aday> findByEmail(String email);
}
