package com.sap.cap.employeeservice.repository;

import java.util.Map;
import java.util.Optional;

import cds.gen.sap.capire.employees.Employee;

public interface EmployeeRepository {
    public Optional<Employee> getEmployee(String ID);
    public void updateEmpId(Employee emp) ;
    public void updateExistingStateEmployeeCount(Employee emp);
    public Map<Integer, Integer> getEmployeeCountbyState();
    public void updateEmployee(Employee emp);
}
