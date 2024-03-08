package csv.checker.csvmanager.entity;

import java.util.List;

import lombok.Data;

@Data
public class UniqueCsvRecordsResponse {
	 private List<OldCsv> records;
	 private Long count;
}
