package com.trisquel.repository;

import com.trisquel.model.DailyBookItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyBookItemRepository extends JpaRepository<DailyBookItem, Long> {

    @Query("SELECT dbi FROM DailyBookItem dbi WHERE dbi.voucherNumber = (SELECT MAX(dbis.voucherNumber) FROM DailyBookItem dbis WHERE dbis.xVoucher is null)")
    Optional<DailyBookItem> findHighestVoucherNumber();

    @Query("SELECT dbi FROM DailyBookItem dbi WHERE dbi.id = (SELECT MAX(dbis.id) FROM DailyBookItem dbis WHERE dbis.voucherNumber IS NULL)")
    Optional<DailyBookItem> findLatestXVoucher();
}
