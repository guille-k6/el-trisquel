package trisquel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trisquel.model.ConfigurationMap;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<ConfigurationMap, Long> {
    Optional<ConfigurationMap> findByKey(String key);
}
