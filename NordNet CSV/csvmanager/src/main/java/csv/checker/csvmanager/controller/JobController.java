package csv.checker.csvmanager.controller;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;


import csv.checker.csvmanager.entity.HouseSearchResponse;
import csv.checker.csvmanager.entity.OldCsv;
import csv.checker.csvmanager.repository.NewCsvRepo;
import csv.checker.csvmanager.repository.OldCsvRepo;
import csv.checker.csvmanager.repository.OldRetrieveCsvRepository;
import csv.checker.csvmanager.service.CsvService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;
    
    @Autowired
    @Qualifier("runJob")
    private Job job;
    
    @Autowired
    private Job runJob1;
    
    @Autowired
    private OldRetrieveCsvRepository oldCsvRepository;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private CacheManager cacheManager;


    
    @Value("${graphql.api.endpoint}")
    private String apiEndpoint;

    @Value("${graphql.api.apikey}")
    private String apiKey;
     
    private final OldCsvRepo oldCsvRepo;
    private final NewCsvRepo newCsvRepo;
    private final CsvService csvService;
    
    private boolean isJobRunning = false;
    
    @Autowired
    public JobController(OldCsvRepo oldCsvRepo, CsvService csvService, NewCsvRepo newCsvRepo) {
        this.oldCsvRepo = oldCsvRepo;
        this.csvService = csvService;
        this.newCsvRepo = newCsvRepo;
    }

    @GetMapping("/compare-old")
    public void importCsvToDBJob() {
    	
    	 String sqlQuery = "DELETE FROM old_csv";

         try {
        	 jdbcTemplate.update(sqlQuery);
         } catch (Exception e) {
             e.printStackTrace();
         }
    	
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
        	
        	JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            
            if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            	isJobRunning = false;
            }
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
        	 isJobRunning = false;
            e.printStackTrace();
        }
    }
    
    @GetMapping("/get-progress-old")
    public long getRowOldCsv() {
    	return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM old_csv", Long.class);
    }
    @GetMapping("/compare-new")
    public void importNewCsv() {
    	
    	String sqlQuery = "DELETE FROM new_csv";

        try {
       	 jdbcTemplate.update(sqlQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(runJob1, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
    
    @GetMapping("/get-progress-new")
    public long getRowNewCsv() {
    	return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM new_csv", Long.class);
    }
    
    
    
    @PostMapping("/upload-first")
    @CacheEvict(cacheNames = "csv", allEntries = true)
	public ResponseEntity<String> uploadFileFirst(@RequestParam("file") MultipartFile file) {
    	
		if (file != null && !file.isEmpty()) {
            try {
                String uploadDirectory = "src/main/resources/";

                File directory = new File(uploadDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadDirectory + "old.csv");
                Files.write(path, bytes);

                return ResponseEntity.ok("File uploaded successfully"+uploadDirectory + file.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.ok("Failed to upload file.");
            }
        } else {
        	return ResponseEntity.ok("File is empty.");
        }
    }
    
    @PostMapping("/upload-second")
	public ResponseEntity<String> uploadFileSecond(@RequestParam("file") MultipartFile file) {
		if (file != null && !file.isEmpty()) {
            try {
                String uploadDirectory = "src/main/resources/";

                File directory = new File(uploadDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadDirectory + "new.csv");
                Files.write(path, bytes);

                return ResponseEntity.ok("File uploaded successfully"+uploadDirectory + "new.csv");
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.ok("Failed to upload file.");
            }
        } else {
        	return ResponseEntity.ok("File is empty.");
        }
    }
    
    @PutMapping("/check-unique-records/{id}")
    public ResponseEntity<?> updateData(@PathVariable String id, @RequestBody Object[] item) {
    	
        Object resultObject = null;
        
        OldCsv oldCsvFromDb = oldCsvRepository.findByCsvId(item[0].toString());
        
        
        
        if (oldCsvFromDb != null) {
            String queryTemplate = "query searchHouse($q: String!, $l: Int!) { "
        	        + "  searchHouse(query: $q, limit: $l) { "
        	        + "    score "
        	        + "    addressLabel "
        	        + "    gps { "
        	        + "      latitude "
        	        + "      longitude "
        	        + "    } "
        	        + "    house { "
        	        + "      gps { "
        	        + "        latitude "
        	        + "        longitude "
        	        + "      } "
        	        + "    } "
        	        + "    city { "
        	        + "      gps { "
        	        + "        latitude "
        	        + "        longitude "
        	        + "      } "
        	        + "    } "
        	        + "  } "
        	        + "} ";

        	Gson gson = new Gson();
        	
        	String variableQuery = "[\"" + item[1] + " " + item[2] + " " + item[3] + " " + item[4] + " " + item[7] + "\"]";
    	    String variables = "{\"q\":" + variableQuery + ",\"l\":5}";
    	    String body = "{\"query\":\"" + queryTemplate + "\",\"variables\":" + variables + "}";
    	    
    	    try {
	            HttpResponse<String> response = Unirest.post(apiEndpoint)
	                    .header("Content-Type", "application/json")
	                    .header("apikey", apiKey)
	                    .body(body)
	                    .asString();

	            String responseData = response.getBody();
	            HouseSearchResponse data = gson.fromJson(responseData, HouseSearchResponse.class);
	            
               System.out.print(responseData);
	            
	            if (response != null && data != null && data.getData() != null && data.getData().getSearchHouses() != null) {
	                for (HouseSearchResponse.SearchHouse house : data.getData().getSearchHouses()) {
	                	double latitude = 0.00;
	                	double longitude = 0.00;
	                	if(house.getGps() != null) {
	                		 latitude = house.getGps().getLatitude();
	 	                     longitude = house.getGps().getLongitude();
	                	}
	                	
	                   
	                    oldCsvFromDb.setNordLat(latitude);
	                    oldCsvFromDb.setNordLon(longitude);
	                }
	            }
	            
	            resultObject = oldCsvFromDb;
	        } catch (UnirestException e) {
	            e.printStackTrace();
	        }
        }
        
        
        if (resultObject != null) {
            return ResponseEntity.ok(resultObject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
        
    @GetMapping("/unique-records")
    public Page<Object[]> getUniqueCsvRecords(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    	
    	List<Object[]> allRecords = csvService.getUniqueCsvRecords();
        
        int start = Math.min((int) page * size, allRecords.size());
        int end = Math.min((start + size), allRecords.size());

        return new PageImpl<>(allRecords.subList(start, end), PageRequest.of(page, size), allRecords.size());
    }

    @GetMapping("/unique-records-paginate")
    public Page<Object[]> getCachedData(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String searchString) {
        
        Cache cache = cacheManager.getCache("csv");

        if (cache != null && cache.get("uniqueCsvRecords") != null) {
            List<Object[]> allRecords = cache.get("uniqueCsvRecords", List.class);

            if (searchString != null && !searchString.isEmpty()) {
                allRecords = filterRecords(allRecords, searchString);
            }

            int start = Math.min((int) page * size, allRecords.size());
            int end = Math.min((start + size), allRecords.size());

            return new PageImpl<>(allRecords.subList(start, end), PageRequest.of(page, size), allRecords.size());
        } else {
            List<Object[]> allRecords = csvService.getUniqueCsvRecords();
            cache.put("uniqueCsvRecords", allRecords);

            int start = Math.min((int) page * size, allRecords.size());
            int end = Math.min((start + size), allRecords.size());

            return new PageImpl<>(allRecords.subList(start, end), PageRequest.of(page, size), allRecords.size());
        }
    }
    
    private List<Object[]> filterRecords(List<Object[]> records, String searchString) {
    	List<Object[]> filteredRecords = new ArrayList<>();
        for (Object[] record : records) {
            boolean found = false;
            for (Object field : record) {
                if (field != null && field.toString().toLowerCase().contains(searchString)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                filteredRecords.add(record);
            }
        }
        return filteredRecords;
    }
    
    @GetMapping("/start-over")
    @CacheEvict(cacheNames = "csv", allEntries = true)
    public String  startOver() {
    	return null;
    }
    
    
}
