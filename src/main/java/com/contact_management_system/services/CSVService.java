package com.contact_management_system.services;

import com.contact_management_system.configurations.CsvConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
@Slf4j
public class CSVService {

    private final JobLauncher jobLauncher;
    private final CsvConfiguration csvConfiguration;
    private final Job job;

    public CSVService(JobLauncher jobLauncher, CsvConfiguration csvConfiguration, Job job) {
        this.jobLauncher = jobLauncher;
        this.csvConfiguration = csvConfiguration;
        this.job = job;
    }

    @SneakyThrows
    public void importCsv(MultipartFile file) {
        log.info("Importing contacts from CSV file: {}", file.getOriginalFilename());
        csvConfiguration.setCsv(file.getResource());

        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("date", new Date())
                .toJobParameters();
        jobLauncher.run(job, jobParameters);
    }


}
