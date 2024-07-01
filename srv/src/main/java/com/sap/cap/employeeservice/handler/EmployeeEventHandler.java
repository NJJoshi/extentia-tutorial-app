package com.sap.cap.employeeservice.handler;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cap.employeeservice.repository.EmployeeRepository;
import com.sap.cap.employeeservice.repository.StateRepository;
import com.sap.cap.employeeservice.validator.EmployeeValidator;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.employeeservice.CalculateEmpStateWiseContext;
import cds.gen.employeeservice.EmployeeSVC_;
import cds.gen.employeeservice.EmployeeService_;
import cds.gen.employeeservice.ZeroStateCountContext;
import cds.gen.sap.capire.employees.Employee;
import cds.gen.sap.capire.employees.State;

@Component
@ServiceName(EmployeeService_.CDS_NAME)
public class EmployeeEventHandler implements EventHandler {
    
    @Autowired
    PersistenceService db;

    @Autowired
    private EmployeeRepository empRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private EmployeeValidator validator;

    //T6.1 : Generate Employee Number using max(current_employee_Id) + 1 and set it into Employee Entity
    @Before(event = CqnService.EVENT_CREATE, entity=EmployeeSVC_.CDS_NAME )
    public void getAutoGeneratedEmployeeNumber(Employee emp) {
        if(null != emp) {
            validator.validateSalary(emp);
            empRepository.updateEmpId(emp);
        }
    }

    //T6.2 - Part 1: Update Total Statewise Employee Count
    //1) Validation applied: if salary > 10000 then show error message and reject transaction by raising ServiceException.
    //2) If It's UPDATE Employee scenario then get existing statewise total employee count from State and update it with present_value - 1. 
    //   In order to perform clean UPDATE operation, You need to perform it BEFORE actual STATE entity update.Hence You will be able to grab older state value.
    @Before(event = {CqnService.EVENT_CREATE, CqnService.EVENT_UPDATE}, entity=EmployeeSVC_.CDS_NAME)
    public void updateEmployeeCountInState(Employee emp){
        if(null != emp) {
            System.out.println("#### Before CREATE/UPDATE Event received. Existing Emp State:" + emp);
            empRepository.updateExistingStateEmployeeCount(emp);
        }
    }

    //T6.2 - Part 2: Update Total Statewise Employee Count
    //In order to perform clean UPDATE operation, Statewise Total existing employee count, We are capturing new/edited STATE value from EMPLOYEE entity.
    @After(event = {CqnService.EVENT_CREATE, CqnService.EVENT_UPDATE}, entity=EmployeeSVC_.CDS_NAME)
    public void afterUpdateEmployeeCountInState(Employee emp){
        System.out.println("#### After CREATE/UPDATE Event received. New Emp State:" + emp);
        Map<Integer, Integer> result = empRepository.getEmployeeCountbyState();
        result.forEach((key, value) -> {
            System.out.println("#### key,value = " + key + " ,"+ value);
            State state = State.create();  
            state.setStateId(key.intValue());                  
            state.setEmpCount(value.intValue());
            stateRepository.updateState(state);
        });
    }  
    
    //T6.2 - Part 3: Update Total Statewise Employee Count into STATE Entity
    // If Employee is getting DELETED then recalibrate STATE entity column total employee count to update it.
    @Before(event = {CqnService.EVENT_DELETE}, entity=EmployeeSVC_.CDS_NAME)
    public void onDeleteUpdateEmployeeCountInState(CdsDeleteEventContext context){
        System.out.println("#### On Delete Received new Emp State update #####");
        CdsModel cdsModel = context.getModel();
        CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(cdsModel);
        String employeeId = (String) cqnAnalyzer.analyze(context.getCqn()).targetKeys().get(Employee.ID);
        Optional<Employee> empOptional= empRepository.getEmployee(employeeId);
        if(empOptional.isPresent())
        {
            Employee oldEmp=empOptional.get();
            Integer stateId=oldEmp.getStateStateId();
            System.out.println("#### On Delete Received new Emp State update:" + oldEmp);
            handleExistingEmpStateEmpCount(oldEmp, stateId);
        }
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

    //T6.5: Custom action addition and Addition of Button which will trigger custom action handler logic at each table row level to reset total employee count Zero and UPDATE into STATE entity
    @On(event = ZeroStateCountContext.CDS_NAME)    
    public void customActionUpdateZeroEmpCountforState(ZeroStateCountContext context) {
        System.out.println("#### Inside customActionUpdateZeroEmpCountforState ####");
        System.out.println("Event occurred for Emp Id:" + context.getEvent());
        CdsModel cdsModel = context.getModel();
        CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(cdsModel);
        String employeeId = (String) cqnAnalyzer.analyze(context.getCqn()).targetKeys().get(Employee.ID);
        Optional<Employee> empOptional= empRepository.getEmployee(employeeId);
        if(empOptional.isPresent())
        {
            Employee oldEmp=empOptional.get();
            Integer stateId=oldEmp.getStateStateId();
            System.out.println("#### On ZeroStateCountContext Received new Emp State update:" + oldEmp);
            oldEmp.setCity(null);
            oldEmp.setCityCityId(0);
            oldEmp.setState(null);
            oldEmp.setStateStateId(0);
            empRepository.updateEmployee(oldEmp);
            System.out.println("#### Updated Employee Entity ####");
            State state = State.create();
            state.setStateId(stateId);                    
            state.setEmpCount(0);
            stateRepository.updateState(state);
            System.out.println("#### Updated State Entity ####");
        }        
        context.setCompleted(); // it's neccessary to mark custom action as 'setCompleted()' otherwise it'll throw error & event won't be complete.
        System.out.println("#### Ends customActionUpdateZeroEmpCountforState ####");
    }

    //T6.3, T6.4 : Custom action addition and Addition of Button at Table Toolbar level to recalculate total employee count and UPDATE into STATE entity
    @On(event = CalculateEmpStateWiseContext.CDS_NAME)
    public void customActionRecalculateEmpCountByState(CalculateEmpStateWiseContext context) {
        System.out.println("#### Inside customActionRecalculateEmpCountByState ####");
        stateRepository.updateEntireStateEntity();
        context.setCompleted(); // it's neccessary to mark custom action as 'setCompleted()' otherwise it'll throw error & event won't be complete.
        System.out.println("#### Ends customActionRecalculateEmpCountByState ####");
    }
    
}
