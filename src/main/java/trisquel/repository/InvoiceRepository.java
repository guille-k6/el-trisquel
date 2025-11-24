package trisquel.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import trisquel.model.Invoice;
import trisquel.model.InvoiceQueueStatus;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    @Modifying
    @Query("UPDATE Invoice i set i.status = :status where i.id = :id")
    void updateStatus(@Param("status") InvoiceQueueStatus status, @Param("id") Long id);

    @Modifying
    @Query("UPDATE Invoice i SET i.cae = :cae, i.vtoCae = :vtoCae WHERE i.id = :id")
    void updateAfipResponseFields(@Param("cae") String cae, @Param("vtoCae") LocalDate vtoCae, @Param("id") Long id);


    @Modifying
    @Query("UPDATE Invoice i SET i.numero = :lastAuthorizedComprobanteNumber WHERE i.id = :id")
    void updateNumber(@Param("lastAuthorizedComprobanteNumber") Long lastAuthorizedComprobanteNumber,
                      @Param("id") Long id);

    @Query("""
                SELECT DISTINCT i
                FROM Invoice i
                JOIN i.items item
                WHERE i.client.id = :clientId AND item.product.id = :productId AND i.status = 'COMPLETED'
                ORDER BY i.date DESC
            """)
    List<Invoice> findInvoicesByClientIdAndProductId(@Param("clientId") Long clientId,
                                                     @Param("productId") Long productId, Pageable pageable);

    @Query("""
                SELECT DISTINCT i
                FROM Invoice i
                JOIN i.items item
                WHERE item.product.id = :productId AND i.status = 'COMPLETED'
                ORDER BY i.date DESC
            """)
    List<Invoice> findInvoicesByProductId(@Param("productId") Long productId, Pageable pageable);
}
