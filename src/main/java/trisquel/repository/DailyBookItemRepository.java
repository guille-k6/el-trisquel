package trisquel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import trisquel.model.DailyBookItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyBookItemRepository extends JpaRepository<DailyBookItem, Long>, JpaSpecificationExecutor<DailyBookItem> {

    @Query("SELECT dbi FROM DailyBookItem dbi WHERE dbi.voucherNumber = (SELECT MAX(dbis.voucherNumber) FROM DailyBookItem dbis WHERE dbis.voucherNumber IS NOT NULL)")
    Optional<DailyBookItem> findHighestVoucherNumber();

    @Query("SELECT dbi FROM DailyBookItem dbi WHERE dbi.id = (SELECT MAX(dbis.id) FROM DailyBookItem dbis WHERE dbis.xVoucher IS NOT NULL)")
    Optional<DailyBookItem> findLatestXVoucher();

    @Query("SELECT d FROM DailyBookItem d WHERE d.id IN :ids")
    List<DailyBookItem> findByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT d FROM DailyBookItem d WHERE d.client.id = :id")
    List<DailyBookItem> findByClient(Long id);

    @Query("SELECT d FROM DailyBookItem d WHERE d.product.id = :id")
    List<DailyBookItem> findByProduct(Long id);
}
