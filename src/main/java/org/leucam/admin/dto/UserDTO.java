package org.leucam.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String surname;
    private String mail;
    private Double credit = 0D;
    private Integer telegramUserId;
    private Boolean active = Boolean.TRUE;
    private Boolean administrator = Boolean.FALSE;

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
