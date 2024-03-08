package csv.checker.csvmanager.service;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import csv.checker.csvmanager.repository.OldCsvRepo;

@Service
public class CsvService {

    private final OldCsvRepo oldCsvRepo;

    public CsvService(OldCsvRepo oldCsvRepo) {
        this.oldCsvRepo = oldCsvRepo;
    }

    @Cacheable(cacheNames = "csv")
    public List<Object[]> getUniqueCsvRecords() {
        return oldCsvRepo.findUniqueCsvRecords();
    }
    
}