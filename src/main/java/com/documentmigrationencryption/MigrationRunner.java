package com.documentmigrationencryption;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MigrationRunner implements CommandLineRunner {
   @Override
    public  void run(String... args)throws Exception{
        Migration.consoleRunner();
    }
}
