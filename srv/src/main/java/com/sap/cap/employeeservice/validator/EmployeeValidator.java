package com.sap.cap.employeeservice.validator;

import org.springframework.stereotype.Component;

import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;

import cds.gen.sap.capire.employees.Employee;

@Component
public class EmployeeValidator {
    
    //Validate Salary. If Salary is more than 10,000 then raise Exception with message
    public void validateSalary(Employee emp) {
        if(emp.getSalary() > 10000) {
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Salary is higher than decided limit.") ;
        }
    }
}
