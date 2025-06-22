package com.trisquel.repository;

import com.trisquel.model.DailyBookItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyBookItemRepository extends JpaRepository<DailyBookItem, Long> {

    @Query("SELECT dbi FROM DailyBookItem dbi WHERE dbi.voucherNumber = (SELECT MAX(dbis.voucherNumber) FROM DailyBookItem dbis WHERE dbis.voucherNumber IS NOT NULL)")
    Optional<DailyBookItem> findHighestVoucherNumber();

    @Query("SELECT dbi FROM DailyBookItem dbi WHERE dbi.id = (SELECT MAX(dbis.id) FROM DailyBookItem dbis WHERE dbis.xVoucher IS NOT NULL)")
    Optional<DailyBookItem> findLatestXVoucher();

    @Query("SELECT d FROM DailyBookItem d WHERE d.invoiceId IS NULL AND :clientId IS NULL OR d.client.id = :clientId AND :startDate IS NULL OR d.date >= :startDate AND :endDate IS NULL OR d.date <= :endDate")
    Page<DailyBookItem> findInvoiceableWithFilters(Pageable pageable, @Param("clientId") Long clientId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT d FROM DailyBookItem d WHERE d.id IN :ids")
    List<DailyBookItem> findByIdIn(@Param("ids") List<Long> ids);
}
