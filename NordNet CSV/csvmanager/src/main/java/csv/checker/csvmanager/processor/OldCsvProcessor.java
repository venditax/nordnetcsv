package csv.checker.csvmanager.processor;

import org.springframework.batch.item.ItemProcessor;

import csv.checker.csvmanager.entity.OldCsv;

public class OldCsvProcessor implements ItemProcessor<OldCsv, OldCsv>{
	@Override
    public OldCsv process(OldCsv oldCsv) throws Exception {
    	if (oldCsv.getCsvId() != null && !oldCsv.getCsvId().trim().isEmpty()) {
    		return oldCsv;
    	}else {
    		return null;
    	}
    }
}
