package com.sap.cap.employeeservice.repository.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cap.employeeservice.repository.EmployeeRepository;
import com.sap.cap.employeeservice.repository.StateRepository;
import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnElementRef;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.employeeservice.EmployeeSVC_;
import cds.gen.sap.capire.employees.Employee;
import cds.gen.sap.capire.employees.State;

@Component
public class EmployeeRepositoryImpl implements EmployeeRepository {

    @Autowired
    private PersistenceService db;

    @Autowired
    private StateRepository stateRepository;

    @Override
    public void updateEmpId(Employee emp) {
        System.out.println("EmployeeRepository : updateEmpId: Starts");
        CqnElementRef number = CQL.get("emp_num");
        CqnSelect fetchMaximumEmpNumber = Select.from(EmployeeSVC_.class)
                                        .columns(CQL.max(number).as("maxNumber"));
                                                    Result result = db.run(fetchMaximumEmpNumber);
        Object maxNumber = result.single().get("maxNumber");
        int newEmpId = Integer.parseInt(null != maxNumber ? String.valueOf(maxNumber) : "0") + 1;
        System.out.println("Employee Updated with : " + newEmpId);
        emp.setEmpNum(newEmpId);
        System.out.println("EmployeeRepository : updateEmpId: Ends");
    }

    @Override
    public void updateExistingStateEmployeeCount(Employee emp) {
        System.out.println("#### Inside updateExistingStateEmployeeCount ####");
        int exisistingStateId = returnAndValidateExistingStateEmpCount(emp);
        if(exisistingStateId > 0) {
            handleExistingEmpStateEmpCount(emp, exisistingStateId);
        } else {
            System.out.println("##### No existing State Data #####");
        }
    }

    private int returnAndValidateExistingStateEmpCount(Employee emp) {
        System.out.println("#### Inside validateStateEmpUpdateCount ####");
        //Get existing emp object
        CqnSelect fetchExistingEmp = Select.from(EmployeeSVC_.class)
                                           .columns(e -> e.state_state_id())
                                           .where(e -> e.ID().eq(emp.getId()));
        Optional<Employee> existingEmp = db.run(fetchExistingEmp)
                                            .first(Employee.class);
        System.out.println("Existing emp: " + existingEmp);
        System.out.println("New emp: " + emp);
        //validation before update
        if(existingEmp.isPresent() && 
            ObjectUtils.anyNotNull(emp.getStateStateId()) && 
            ObjectUtils.anyNotNull(existingEmp.get().getStateStateId()) && 
            existingEmp.get().getStateStateId().equals(emp.getStateStateId())) {                        
            System.out.println("Found Same State Id: "+ existingEmp.get().getStateStateId() +" for update. Skipping Update.");
            return 0;
        } 
        System.out.println("#### Ends validateStateEmpUpdateCount ####");
        return (existingEmp.isPresent() && ObjectUtils.anyNotNull(existingEmp.get().getStateStateId())) ? existingEmp.get().getStateStateId() : -1 ;
    }
    
    private void handleExistingEmpStateEmpCount(Employee emp, int exisistingStateId) {
        System.out.println("#### Inside handleExistingEmpStateEmpCount ####");
        //Update existing State Emp Count
        State existingStateEmpCount = stateRepository.getState(exisistingStateId);
        System.out.println("Existing State Emp Count: " + existingStateEmpCount);
        existingStateEmpCount.setEmpCount(existingStateEmpCount.getEmpCount() - 1);
        stateRepository.updateState(existingStateEmpCount);
        System.out.println("##### Updated existing State Data #####");
    }

    @Override
    public Map<Integer, Integer> getEmployeeCountbyState() {
        Map<Integer, Integer> result = new HashMap<>();
        //Fetch data from employees to get employee count by state id
        CqnSelect getEmpCountByStateId = Select.from(EmployeeSVC_.class)
                                                .columns(c -> c.get("state_state_id"), 
                                                         c-> CQL.count(c.get("emp_num")).as("empCount")
                                                         )
                                                .groupBy(g -> g.get("state_state_id"));
        db.run(getEmpCountByStateId).forEach(row -> {
            Integer state_id = Integer.parseInt((null != row.get("state_state_id")) ? row.get("state_state_id").toString() : "0");
            Integer empCount = Integer.parseInt(row.get("empCount").toString());
            result.put(state_id, empCount);
        });
        return result;
    }

    @Override
    public Optional<Employee> getEmployee(String ID) {
        CqnSelect employeeQuery = Select.from(EmployeeSVC_.class).where(e -> e.get("ID").eq(ID));
        Optional<Employee> empOptional= db.run(employeeQuery).first(Employee.class);
        return empOptional;
    }

    @Override
    public void updateEmployee(Employee emp) {
         CqnUpdate existingEmpUpdt = Update.entity(EmployeeSVC_.class).data(emp).where(e -> e.ID().eq(emp.getId()));
         db.run(existingEmpUpdt);
    }
}
