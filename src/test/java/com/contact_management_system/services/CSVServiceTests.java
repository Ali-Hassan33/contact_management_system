package com.contact_management_system.services;

import com.contact_management_system.configurations.CsvConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CSVServiceTests {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private CsvConfiguration csvConfiguration;

    @Mock
    private Job job;

    @Mock
    private JobExecution jobExecution;

    @Mock
    private org.springframework.batch.core.JobInstance jobInstance;

    @InjectMocks
    private CSVService csvService;

    private MultipartFile mockCsvFile;

    @BeforeEach
    void setUp() {
        mockCsvFile = new MockMultipartFile(
                "contacts.csv",
                "contacts.csv",
                "text/csv",
                "firstName,lastName,email\nJohn,Doe,john@example.com".getBytes()
        );

        when(jobExecution.getJobInstance()).thenReturn(jobInstance);
        when(jobInstance.getInstanceId()).thenReturn(123L);
    }

    @Test
    @DisplayName("Should import CSV file successfully")
    void testImportCsv() throws Exception {
        when(jobLauncher.run(eq(job), any(JobParameters.class))).thenReturn(jobExecution);

        csvService.importCsv(mockCsvFile);

        verify(csvConfiguration).setCsv(any(Resource.class));
        verify(jobLauncher).run(eq(job), any(JobParameters.class));
    }

    @Test
    @DisplayName("Should handle empty CSV file")
    void testImportEmptyCsv() throws Exception {
        MultipartFile emptyFile = new MockMultipartFile(
                "empty.csv",
                "empty.csv",
                "text/csv",
                "".getBytes()
        );
        when(jobLauncher.run(eq(job), any(JobParameters.class))).thenReturn(jobExecution);

        csvService.importCsv(emptyFile);

        verify(csvConfiguration).setCsv(any(Resource.class));
        verify(jobLauncher).run(eq(job), any(JobParameters.class));
    }

    @Test
    @DisplayName("Should handle CSV file with invalid format")
    void testImportInvalidCsv() throws Exception {
        MultipartFile invalidFile = new MockMultipartFile(
                "invalid.csv",
                "invalid.csv",
                "text/csv",
                "invalid,format,without,headers".getBytes()
        );
        when(jobLauncher.run(eq(job), any(JobParameters.class))).thenReturn(jobExecution);

        csvService.importCsv(invalidFile);

        verify(csvConfiguration).setCsv(any(Resource.class));
        verify(jobLauncher).run(eq(job), any(JobParameters.class));
    }
}
