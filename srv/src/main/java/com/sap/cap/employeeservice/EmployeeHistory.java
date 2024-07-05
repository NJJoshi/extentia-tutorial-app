package com.sap.cap.employeeservice;

import java.util.Map;

import com.sap.cds.ql.Insert;

import cds.gen.employeeservice.EECreationHistorySVC_;

public class EmployeeHistory {
    public void insertEmployeeHistory(Map<String, String> map){
        System.out.println(map);
        Insert.into(EECreationHistorySVC_.class).entry(map);
    }
}
