package com.sap.cap.employeeservice.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnElementRef;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.employeeservice.EmployeeSVC_;
import cds.gen.employeeservice.EmployeeService_;
import cds.gen.employeeservice.StateSVC;
import cds.gen.employeeservice.StateSVC_;
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
            Result result = db.run(Select.from(EmployeeSVC_.class).columns(
                CQL.max(number).as("maxNumber")));
            System.out.println("Employee Created: " + result.first());
            Object maxNumber = result.single().get("maxNumber");
            int newEmpId = Integer.parseInt(null != maxNumber ? String.valueOf(maxNumber) : "0") + 1;
            System.out.println("Employee Updated with : " + newEmpId);
            emp.setEmpNum(newEmpId);
        }
    }

    @After(event = {CqnService.EVENT_CREATE, CqnService.EVENT_UPDATE}, entity=EmployeeSVC_.CDS_NAME)
    public void updateEmployeeCountInState(){
        CqnSelect getEmpCountByStateId = Select.from(EmployeeSVC_.class)
                                                .columns(c -> c.get("state_state_id"), c-> CQL.count(c.get("emp_num")).as("empCount"))
                                                .groupBy(g -> g.get("state_state_id"));
        db.run(getEmpCountByStateId).forEach(row -> {
            System.out.println("Row Value:" + row);
            Integer state_id = Integer.parseInt((null != row.get("state_state_id")) ? row.get("state_state_id").toString() : "0");
            Integer empCount = Integer.parseInt(row.get("empCount").toString());
            System.out.println("*****state_id:" +state_id+ "******");
            System.out.println("*****empCount:" +empCount+ "******");
            if(state_id > 0 &&  empCount > 0) {
                State state = State.create();                    
                state.setEmpCount(empCount.intValue());
                CqnUpdate update = Update.entity(StateSVC_.class).data(state).where(s -> s.state_id().eq(state_id.intValue()));
                db.run(update);
                System.out.println("Custom State: " + state);
            }
        });
    }
}
