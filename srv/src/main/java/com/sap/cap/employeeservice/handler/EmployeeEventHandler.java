package com.sap.cap.employeeservice.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnElementRef;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.authorization.CalcWhereConditionEventContext;
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
import cds.gen.employeeservice.StateSVC_;
import cds.gen.employeeservice.ZeroStateCountContext;
import cds.gen.sap.capire.employees.Employee;
import cds.gen.sap.capire.employees.State;

@Component
@ServiceName(EmployeeService_.CDS_NAME)
public class EmployeeEventHandler implements EventHandler {
    
    @Autowired
    PersistenceService db;

    @Before(event = CqnService.EVENT_CREATE, entity=EmployeeSVC_.CDS_NAME )
    public void updateEmployeeNumber(Employee emp) {
        if(null != emp) {
            CqnElementRef number = CQL.get("emp_num");
            CqnSelect fetchMaximumEmpNumber = Select.from(EmployeeSVC_.class)
                                                    .columns(CQL.max(number).as("maxNumber"));
            Result result = db.run(fetchMaximumEmpNumber);
            System.out.println("Employee Created: " + result.first());
            Object maxNumber = result.single().get("maxNumber");
            int newEmpId = Integer.parseInt(null != maxNumber ? String.valueOf(maxNumber) : "0") + 1;
            System.out.println("Employee Updated with : " + newEmpId);
            emp.setEmpNum(newEmpId);
        }
    }

    @Before(event = {CqnService.EVENT_CREATE, CqnService.EVENT_UPDATE}, entity=EmployeeSVC_.CDS_NAME)
    public void updateEmployeeCountInState(Employee emp){
        if(null != emp) {
            System.out.println("#### Before Received new Emp State update:" + emp);
            validateSalary(emp);
            updateExistingStateEmployeeCount(emp);
        }
    }


 @After(event = {CqnService.EVENT_CREATE, CqnService.EVENT_UPDATE}, entity=EmployeeSVC_.CDS_NAME)
    public void afterUpdateEmployeeCountInState(Employee emp){
        System.out.println("#### After Received new Emp State update:" + emp);
        validateSalary(emp);
        updateNewStateEmpCount(emp);
    }  
    
    @Before(event = {CqnService.EVENT_DELETE}, entity=EmployeeSVC_.CDS_NAME)
    public void onDeleteUpdateEmployeeCountInState(CdsDeleteEventContext context){
        System.out.println("#### On Delete Received new Emp State update #####");
        CdsModel cdsModel = context.getModel();
        CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(cdsModel);
        String employeeId = (String) cqnAnalyzer.analyze(context.getCqn()).targetKeys().get(Employee.ID);
        CqnSelect employeeQuery = Select.from(EmployeeSVC_.class).where(e -> e.get("ID").eq(employeeId));
        Optional<Employee> empOptional= db.run(employeeQuery).first(Employee.class);
        if(empOptional.isPresent())
        {
            Employee oldEmp=empOptional.get();
            Integer stateId=oldEmp.getStateStateId();
            System.out.println("#### On Delete Received new Emp State update:" + oldEmp);
            handleExistingEmpStateEmpCount(oldEmp, stateId);
        }
    }    

    //Validate Salary. If Salary is more than 10,000 then raise Exception with message
    private void validateSalary(Employee emp) {
        if(emp.getSalary() > 10000) {
            throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Salary is higher than decided limit.") ;
        }
    }
    private void updateNewStateEmpCount(Employee emp) {
        System.out.println("#### Inside updateNewStateEmpCount ####");
        //Fetch data from employees to get employee count by state id
        CqnSelect getEmpCountByStateId = Select.from(EmployeeSVC_.class)
                                                .columns(c -> c.get("state_state_id"), 
                                                         c-> CQL.count(c.get("emp_num")).as("empCount")
                                                         )
                                                .groupBy(g -> g.get("state_state_id"));
        db.run(getEmpCountByStateId).forEach(row -> {
            Integer state_id = Integer.parseInt((null != row.get("state_state_id")) ? row.get("state_state_id").toString() : "0");
            Integer empCount = Integer.parseInt(row.get("empCount").toString());
            if(state_id > 0 &&  empCount > 0) {                
                //Update State Entity with employee count 
                State state = State.create();                    
                state.setEmpCount(empCount.intValue());
                CqnUpdate update = Update.entity(StateSVC_.class).data(state).where(s -> s.state_id().eq(state_id.intValue()));
                db.run(update);
            }
        });
        System.out.println("#### Updated new State Emp Count ####");
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
        if(existingEmp.isPresent() && (emp.getStateStateId() != null) && (existingEmp.get().getStateStateId() != null) && existingEmp.get().getStateStateId().equals(emp.getStateStateId())) {                        
            System.out.println("Found Same State Id: "+ existingEmp.get().getStateStateId() +" for update. Skipping Update.");
            return 0;
        } 
        System.out.println("#### Ends validateStateEmpUpdateCount ####");
        return (existingEmp.isPresent() && existingEmp.get().getStateStateId() != null) ? existingEmp.get().getStateStateId() : -1 ;
    }

    private void updateExistingStateEmployeeCount(Employee emp) {
        System.out.println("#### Inside updateExistingStateEmployeeCount ####");
        int exisistingStateId = returnAndValidateExistingStateEmpCount(emp);
        if(exisistingStateId > 0) {
            handleExistingEmpStateEmpCount(emp, exisistingStateId);
        } else {
            System.out.println("##### No existing State Data #####");
        }
    }

    private void handleExistingEmpStateEmpCount(Employee emp, int exisistingStateId) {
        System.out.println("#### Inside handleExistingEmpStateEmpCount ####");
            //Update existing State Emp Count
            CqnSelect fetchExistingStateEmpCount = Select.from(StateSVC_.class).columns(s -> s.emp_count()).where(s -> s.state_id().eq(exisistingStateId));
            State existingStateEmpCount = db.run(fetchExistingStateEmpCount)
                                            .first(State.class)
                                            .orElseThrow(() -> new ServiceException(ErrorStatuses.NOT_FOUND, "State does not exist"));
            System.out.println("Existing State Emp Count: " + existingStateEmpCount);
            existingStateEmpCount.setEmpCount(existingStateEmpCount.getEmpCount() - 1);
            CqnUpdate existingStateEmpCountUpdate = Update.entity(StateSVC_.class).data(existingStateEmpCount).where(s -> s.state_id().eq(exisistingStateId));
            db.run(existingStateEmpCountUpdate);
            System.out.println("##### Updated existing State Data #####");
    }

    @On(event = ZeroStateCountContext.CDS_NAME)    
    public void customActionUpdateZeroEmpCountforState(ZeroStateCountContext context) {
        System.out.println("#### Inside customActionUpdateZeroEmpCountforState ####");
        System.out.println("Event occurred for Emp Id:" + context.getEvent());
        CdsModel cdsModel = context.getModel();
        CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(cdsModel);
        String employeeId = (String) cqnAnalyzer.analyze(context.getCqn()).targetKeys().get(Employee.ID);
        CqnSelect employeeQuery = Select.from(EmployeeSVC_.class).where(e -> e.get("ID").eq(employeeId));
        Optional<Employee> empOptional= db.run(employeeQuery).first(Employee.class);
        if(empOptional.isPresent())
        {
            Employee oldEmp=empOptional.get();
            Integer stateId=oldEmp.getStateStateId();
            System.out.println("#### On ZeroStateCountContext Received new Emp State update:" + oldEmp);
            oldEmp.setCity(null);
            oldEmp.setCityCityId(0);
            oldEmp.setState(null);
            oldEmp.setStateStateId(0);
            CqnUpdate existingEmpUpdt = Update.entity(EmployeeSVC_.class).data(oldEmp).where(e -> e.ID().eq(employeeId));
            db.run(existingEmpUpdt);
            System.out.println("#### Updated Employee Entity ####");
            State state = State.create();                    
            state.setEmpCount(0);
            CqnUpdate existingStateEmpCountUpdate = Update.entity(StateSVC_.class).data(state).where(s -> s.state_id().eq(stateId));
            db.run(existingStateEmpCountUpdate);
            System.out.println("#### Updated State Entity ####");
        }        
        context.setCompleted();
        System.out.println("#### Ends customActionUpdateZeroEmpCountforState ####");
    }

    @On(event = CalculateEmpStateWiseContext.CDS_NAME)
    public void customActionRecalculateEmpCountByState(CalculateEmpStateWiseContext context) {
        System.out.println("#### Inside customActionRecalculateEmpCountByState ####");
        Map<Integer, Integer> employeeCountByStateIdMap = collectStatewiseEmployeeCount();
        updateEntireStateEntity(employeeCountByStateIdMap);
        context.setCompleted();
        System.out.println("#### Ends customActionRecalculateEmpCountByState ####");
    }

    private void updateEntireStateEntity(Map<Integer, Integer> employeeCountByStateIdMap) {
        //Get All State Rows order by State Id
        CqnSelect getAllStates = Select.from(StateSVC_.class)
                                       .columns(c ->c.get("state_id"), c-> c.get("name"))
                                       .orderBy("state_id");
        db.run(getAllStates).forEach(stateRow ->{
            Integer state_id = Integer.parseInt(stateRow.get("state_id").toString());
            String state_name = stateRow.get("name").toString();
            Integer empCount = -1;
            System.out.println("Updating for State Id:" + state_id+", state_name:"+state_name);
            // Retrieve statewise employee count and update State Entity
            if((empCount = employeeCountByStateIdMap.get(state_id)) != null) {
                State state = State.create();                    
                state.setEmpCount(empCount);
                CqnUpdate update = Update.entity(StateSVC_.class).data(state).where(s -> s.state_id().eq(state_id.intValue()));
                db.run(update);
                System.out.println("Update complete for State Id:" + state_id+", state_name:"+state_name);
            }
        });
    }

    private Map<Integer, Integer> collectStatewiseEmployeeCount() {
        Map<Integer, Integer> employeeCountByStateIdMap = new HashMap<>();

        //Fetch data from employees to get employee count by state id
        CqnSelect getEmpCountByStateId = Select.from(EmployeeSVC_.class)
                                                .columns(c -> c.get("state_state_id"), 
                                                         c-> CQL.count(c.get("emp_num")).as("empCount")
                                                         )
                                                .groupBy(g -> g.get("state_state_id"));
        db.run(getEmpCountByStateId).forEach(row -> {
            Integer state_id = Integer.parseInt((null != row.get("state_state_id")) ? row.get("state_state_id").toString() : "0");
            Integer empCount = Integer.parseInt(row.get("empCount").toString());
            if(state_id > 0 &&  empCount > 0) {                
                //Collect State wise employee count 
                employeeCountByStateIdMap.put(state_id, empCount);
            }
        });
        System.out.println("#### Total collected data into collection for statewise emp count:" + employeeCountByStateIdMap.size());
        return employeeCountByStateIdMap;
    }
}
