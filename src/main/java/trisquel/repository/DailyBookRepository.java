package trisquel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import trisquel.model.DailyBook;

import java.util.List;

@Repository
public interface DailyBookRepository extends JpaRepository<DailyBook, Long> {
    @Query("SELECT d FROM DailyBook d WHERE d.vehicle.id = :id")
    List<DailyBook> findByVehicle(Long id);
}
