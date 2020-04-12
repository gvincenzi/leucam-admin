package org.leucam.admin.view.product;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "label.product")
public class ProductLabelConfig {
    private String name;
    private String description;
    private String fileId;
    private String filePath;
    private String active;
    private String showHistory;
    private String urlOrderPublic;
}
