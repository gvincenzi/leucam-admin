package org.leucam.admin.view;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "label.button")
public class ButtonLabelConfig {
    private String userManagement;
    private String productManagement;
    private String quickPrintManagement;
    private String userNew;
    private String productNew;
    private String save;
    private String reset;
    private String delete;
    private String openDocument;
    private String putInCatalog;
    private String removeFromCatalog;
    private String prepared;
    private String notPrepared;
    private String delivered;
    private String notDelivered;
}
