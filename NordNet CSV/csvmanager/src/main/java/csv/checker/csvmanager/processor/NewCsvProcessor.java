package csv.checker.csvmanager.processor;

import org.springframework.batch.item.ItemProcessor;

import csv.checker.csvmanager.entity.NewCsv;

public class NewCsvProcessor implements ItemProcessor<NewCsv, NewCsv>{
	@Override
    public NewCsv process(NewCsv newCsv) throws Exception {
    	
    	if (newCsv.getCsvId() != null && !newCsv.getCsvId().trim().isEmpty()) {
    		return newCsv;
    	}else {
    		return null;
    	}
    }
}
