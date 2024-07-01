package com.sap.cap.employeeservice.repository;

import cds.gen.sap.capire.employees.State;

public interface StateRepository {
    public State getState(Integer stateId);
    public void updateState(State state);
    public void updateEntireStateEntity();
}
