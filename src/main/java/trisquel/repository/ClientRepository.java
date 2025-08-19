package trisquel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import trisquel.model.Client;
import trisquel.model.ClientForCombo;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("SELECT c FROM Client c WHERE " + "(:searchText IS NULL OR :searchText = '' OR " + "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " + "LOWER(c.address) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " + "LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " + "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " + "CAST(c.docNumber AS string) LIKE CONCAT('%', :searchText, '%'))")
    Page<Client> findBySearchText(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT new trisquel.model.ClientForCombo(c.id, c.name) FROM Client c ORDER BY c.name ASC")
    List<ClientForCombo> getAllForCombo();
}
