package com.trisquel.repository;

import com.trisquel.model.DailyBookItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyBookItemRepository extends JpaRepository<DailyBookItem, Long> {}
