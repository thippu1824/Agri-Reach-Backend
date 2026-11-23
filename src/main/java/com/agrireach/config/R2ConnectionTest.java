//package com.agrireach.config;
//
//import com.agrireach.storage.r2.R2StorageProvider;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class R2ConnectionTest implements CommandLineRunner {
//    private static final Logger logger =
//        LoggerFactory.getLogger(R2ConnectionTest.class);
//    private final R2StorageProvider r2StorageProvider;
//
//    public R2ConnectionTest(R2StorageProvider r2StorageProvider) {
//        this.r2StorageProvider = r2StorageProvider;
//    }
//
//    @Override
//    public void run(String... args) {
//        logger.info("Starting Cloudflare R2 connectivity test");
//        r2StorageProvider.checkConnection();
//        logger.info("Cloudflare R2 connectivity test completed successfully");
//    }
//}