package com.contact_management_system.configurations;

import com.contact_management_system.entities.ContactProfile;
import com.contact_management_system.entities.EmailAddress;
import com.contact_management_system.entities.PhoneNumber;
import com.contact_management_system.services.UserService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

import static com.contact_management_system.enums.Label.PERSONAL;
import static com.contact_management_system.enums.Label.WORK;

@Configuration
public class CsvConfiguration {

    private Resource resource;

    @Bean
    FlatFileItemReader<CsvRow> csvFileReader() {
        return new FlatFileItemReaderBuilder<CsvRow>()
                .name("csvFileReader")
                .delimited().delimiter(",")
                .names("first name, last name, title, email, work email, number, work number".split(", "))
                .linesToSkip(1)
                .fieldSetMapper(fieldSet -> new CsvRow(
                        fieldSet.readString("first name"),
                        fieldSet.readString("last name"),
                        fieldSet.readString("title"),
                        fieldSet.readString("email"),
                        fieldSet.readString("work email"),
                        fieldSet.readString("number"),
                        fieldSet.readString("work number")
                )).build();
    }

    @Bean
    ItemWriter<CsvRow> itemWriter(UserService userService) {
        return chunk -> chunk.getItems()
                .forEach(csvRow -> userService.saveContact(ContactProfile.builder()
                        .firstName(csvRow.firstName())
                        .lastName(csvRow.lastName())
                        .title(csvRow.title())
                        .emailAddresses(List.of(
                                EmailAddress.builder().emailLabel(PERSONAL).email(csvRow.email()).build(),
                                EmailAddress.builder().emailLabel(WORK).email(csvRow.workEmail()).build()))
                        .phoneNumbers(List.of(
                                PhoneNumber.builder().phoneLabel(PERSONAL).number(csvRow.email()).build(),
                                PhoneNumber.builder().phoneLabel(WORK).number(csvRow.workEmail()).build()))
                        .build()));
    }

    @Bean
    Step step(JobRepository jobRepository,
              PlatformTransactionManager transactionManager, ItemWriter<CsvRow> csvFileWriter) {
        return new StepBuilder("CsvToTable", jobRepository)
                .<CsvRow, CsvRow>chunk(100, transactionManager)
                .reader(csvFileReader())
                .writer(csvFileWriter)
                .build();
    }

    @Bean
    Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .build();
    }

    public void setResource(Resource resource) {
        csvFileReader().setResource(resource);
    }
}

record CsvRow(String firstName, String lastName, String title,
              String email, String workEmail, String phoneNumber, String workPhoneNumber) {
}