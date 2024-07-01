package com.sap.cap.employeeservice.repository.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cap.employeeservice.repository.EmployeeRepository;
import com.sap.cap.employeeservice.repository.StateRepository;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.employeeservice.StateSVC_;
import cds.gen.sap.capire.employees.State;

@Component
public class StateRepositoryImpl implements StateRepository {

    @Autowired
    private PersistenceService db;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public State getState(Integer stateId) {
        CqnSelect fetchExistingStateEmpCount = Select.from(StateSVC_.class)
                .columns(s -> s.emp_count(), s -> s.state_id())
                .where(s -> s.state_id().eq(stateId));
        State state = db.run(fetchExistingStateEmpCount)
                .first(State.class)
                .orElseThrow(() -> new ServiceException(ErrorStatuses.NOT_FOUND, "State does not exist"));
        return state;
    }

    @Override
    public void updateState(State state) {
        CqnUpdate existingStateEmpCountUpdate = Update.entity(StateSVC_.class).data(state)
                .where(s -> s.state_id().eq(state.getStateId()));
        db.run(existingStateEmpCountUpdate);
    }

    @Override
    public void updateEntireStateEntity() {
        Map<Integer, Integer> employeeCountByStateIdMap = employeeRepository.getEmployeeCountbyState();
        // Get All State Rows order by State Id
        CqnSelect getAllStates = Select.from(StateSVC_.class)
                .columns(c -> c.get("state_id"), c -> c.get("name"))
                .orderBy("state_id");
        db.run(getAllStates).forEach(stateRow -> {
            Integer state_id = Integer.parseInt(stateRow.get("state_id").toString());
            String state_name = stateRow.get("name").toString();
            Integer empCount = -1;
            System.out.println("Updating for State Id:" + state_id + ", state_name:" + state_name);
            // Retrieve statewise employee count and update State Entity
            if ((empCount = employeeCountByStateIdMap.get(state_id)) != null) {
                State state = State.create();
                state.setEmpCount(empCount);
                CqnUpdate update = Update.entity(StateSVC_.class).data(state)
                        .where(s -> s.state_id().eq(state_id.intValue()));
                db.run(update);
                System.out.println("Update complete for State Id:" + state_id + ", state_name:" + state_name);
            }
        });
    }
}
