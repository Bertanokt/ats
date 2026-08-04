package ats.repository;

import ats.model.Aktivite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AktiviteRepository extends JpaRepository<Aktivite, Long> {

    List<Aktivite> findByBasvuruIdOrderByTarihAsc(Long basvuruId);   //şu başvuruya ait aktiviteleri tarih sırasıyla getir
}