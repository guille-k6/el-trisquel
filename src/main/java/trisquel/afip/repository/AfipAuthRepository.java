package trisquel.afip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import trisquel.afip.model.AfipAuth;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface AfipAuthRepository extends JpaRepository<AfipAuth, Long> {
    @Query("SELECT aa FROM AfipAuth aa WHERE aa.errorMessage IS NULL AND :now < aa.expirationTime ORDER BY aa.id ASC")
    Optional<AfipAuth> findLastAuth(@Param("now") OffsetDateTime now);
}
