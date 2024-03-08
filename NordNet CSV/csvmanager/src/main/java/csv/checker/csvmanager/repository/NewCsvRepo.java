package csv.checker.csvmanager.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import csv.checker.csvmanager.entity.NewCsv;

public interface NewCsvRepo  extends JpaRepository<NewCsv,Long> {

}