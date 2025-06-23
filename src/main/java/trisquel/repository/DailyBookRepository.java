package trisquel.repository;

import trisquel.model.DailyBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyBookRepository extends JpaRepository<DailyBook, Long> {
}
