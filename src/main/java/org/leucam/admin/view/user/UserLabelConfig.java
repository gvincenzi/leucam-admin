package org.leucam.admin.view.user;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "label.user")
public class UserLabelConfig {
    private String firstname;
    private String lastname;
    private String mail;
    private String active;
    private String credit;
    private String oldCredit;
    private String newCredit;
    private String rechargeUserCreditType;
    private String rechargeDateTime;
    private String administrator;
    private String cashFund;
}
