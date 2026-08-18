package ats.repository;

import ats.dto.AsamaSayimDto;
import ats.model.Basvuru;
import ats.model.BasvuruAsamasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BasvuruRepository extends JpaRepository<Basvuru, Long> {

    // Adayin bu ilanda verilen asamalardan birinde basvurusu var mi?
    boolean existsByAdayIdAndIlanIdAndAsamaIn(Long adayId, Long ilanId, List<BasvuruAsamasi> asamalar);

    // Aday silinebilir mi kontrolu icin
    long countByAdayId(Long adayId);

    // Bir ilana ait basvurulari asamaya gore grupla ve say
    @Query("""
           SELECT new ats.dto.AsamaSayimDto(b.asama, COUNT(b))
           FROM Basvuru b
           WHERE b.ilan.id = :ilanId
           GROUP BY b.asama
           """)
    List<AsamaSayimDto> ilanAsamaSayimlari(@Param("ilanId") Long ilanId);

    // Tum sistemdeki basvurulari asamaya gore grupla ve say
    @Query("""
           SELECT new ats.dto.AsamaSayimDto(b.asama, COUNT(b))
           FROM Basvuru b
           GROUP BY b.asama
           """)
    List<AsamaSayimDto> genelAsamaSayimlari();

    // Bir ilana ait tum basvurular (listeleme icin)
    List<Basvuru> findByIlanId(Long ilanId);

    // Belirli asamadaki basvurular (ise alinanlar raporu icin)
    List<Basvuru> findByAsama(BasvuruAsamasi asama);

    // Belirli asamadaki basvurular, ilanin departmanina gore filtreli
    List<Basvuru> findByAsamaAndIlan_Departman(BasvuruAsamasi asama, String departman);
}