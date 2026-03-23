package com.swiftbite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEditPasswordDTO {
    private Integer empId;
    private String newPassword;
    private String oldPassword;
}
