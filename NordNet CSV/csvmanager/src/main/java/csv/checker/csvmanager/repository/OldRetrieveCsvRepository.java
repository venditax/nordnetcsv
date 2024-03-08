package csv.checker.csvmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import csv.checker.csvmanager.entity.OldCsv;

@Repository
public interface OldRetrieveCsvRepository extends JpaRepository<OldCsv, Long> {
	OldCsv findByCsvId(String csvId);
}