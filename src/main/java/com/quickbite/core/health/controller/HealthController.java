package com.quickbite.core.health.controller;

import com.quickbite.core.common.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {
    private final AppConfig appConfig;
    private final DataSource dataSource;
    private static final Logger log = LoggerFactory.getLogger(HealthController.class);


    @Autowired
    public HealthController(DataSource dataSource, AppConfig appConfig) {
        this.dataSource = dataSource;
        this.appConfig = appConfig;
    }

    @GetMapping
    public Map<String, Object> checkHealth() {
        Map<String, Object> response = new HashMap<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // Execute a simple query to verify connection
            stmt.executeQuery("SELECT 1");

            response.put("status", "UP");
            response.put("database", "CONNECTED");
            log.info("Health check passed");

        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage());
            response.put("status", "DOWN");
            response.put("database", "DISCONNECTED");
            response.put("error", e.getMessage());
        }

        return response;
    }

    @GetMapping("test-config")
    public Map<String, String> dumpConfig() {
        Map<String, String> response = new HashMap<>();
        response.put("db host", appConfig.db().host());
        response.put("db port", String.valueOf(appConfig.db().port()));
        response.put("db name", appConfig.db().name());
        response.put("db username", appConfig.db().username());
        response.put("db password", appConfig.db().password());
        response.put("db pool max", String.valueOf(appConfig.db().poolMax()));
        response.put("db pool min", String.valueOf(appConfig.db().poolMin()));
        response.put("jwt access", appConfig.jwt().accessSecret());
        response.put("jwt access expires", String.valueOf(appConfig.jwt().accessExpiresIn()));
        response.put("jwt refresh", appConfig.jwt().refreshSecret());
        response.put("jwt refresh expires", String.valueOf(appConfig.jwt().refreshExpiresIn()));
        response.put("password encoder salt", String.valueOf(appConfig.passwordEncoder().salt()));
        response.put("server port", String.valueOf(appConfig.serverPort()));
        response.put("environment", appConfig.environment());

        return response;
    }
}