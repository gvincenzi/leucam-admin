package org.leucam.admin.view.product;

import lombok.Data;
import org.leucam.admin.dto.type.ActionType;
import org.leucam.admin.dto.type.ColorType;
import org.leucam.admin.dto.type.FrontBackType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "label.order")
public class OrderLabelConfig {
    private String user;
    private String actionType;
    private String frontBackType;
    private String colorType;
    private String numberOfCopies;
    private String pagesPerSheet;
    private String amount;
    private String paid;
    private String paymentExternalReference;
    private String paymentExternalDateTime;
}
