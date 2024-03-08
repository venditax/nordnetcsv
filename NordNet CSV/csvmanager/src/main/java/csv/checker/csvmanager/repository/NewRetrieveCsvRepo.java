package csv.checker.csvmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import csv.checker.csvmanager.entity.NewCsv;

public interface NewRetrieveCsvRepo   extends JpaRepository<NewCsv,Long>{
	NewCsv findByCsvId(String csv_id);
}
