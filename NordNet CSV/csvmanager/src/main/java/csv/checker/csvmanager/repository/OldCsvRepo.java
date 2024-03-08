package csv.checker.csvmanager.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import csv.checker.csvmanager.entity.OldCsv;


public interface OldCsvRepo extends JpaRepository<OldCsv, Long> {
	
	@Query(value = "SELECT DISTINCT csv_id,nom_commune,numero,rep,nom_afnor,code_postal,lat,lon,status FROM ( SELECT combined_csv.id, combined_csv.csv_id, CASE WHEN old_csv.csv_id IS NULL THEN new_csv.nom_commune ELSE old_csv.nom_commune END AS nom_commune, CASE WHEN old_csv.csv_id IS NULL THEN new_csv.numero ELSE old_csv.numero END AS numero, CASE WHEN old_csv.csv_id IS NULL THEN new_csv.rep ELSE old_csv.rep END AS rep, CASE WHEN old_csv.csv_id IS NULL THEN new_csv.nom_afnor ELSE old_csv.nom_afnor END AS nom_afnor, CASE WHEN old_csv.csv_id IS NULL THEN new_csv.code_postal ELSE old_csv.code_postal END AS code_postal,combined_csv.lat, combined_csv.lon, CASE WHEN old_csv.csv_id IS NULL THEN 'Added' WHEN new_csv.csv_id IS NULL THEN 'Deleted' WHEN old_csv.nom_commune <> new_csv.nom_commune OR old_csv.numero <> new_csv.numero OR old_csv.rep <> new_csv.rep OR old_csv.nom_afnor <> new_csv.nom_afnor  OR old_csv.code_postal <> new_csv.code_postal OR old_csv.lat <> new_csv.lat OR old_csv.lon <> new_csv.lon THEN 'Updated' ELSE 'No Change' END AS status FROM (SELECT * FROM csv) AS combined_csv LEFT JOIN new_csv ON combined_csv.csv_id = new_csv.csv_id LEFT JOIN old_csv ON combined_csv.csv_id = old_csv.csv_id ) AS subquery WHERE status <> 'No Change'",
		    nativeQuery = true)
	List<Object[]> findUniqueCsvRecords();
	
	@Query(value = "delete from old_csv", nativeQuery = true)
    void deleteAllQuery();	
}
 