package com.sap.cap.employeeservice;

import java.util.Map;

import com.sap.cds.ql.Insert;

import cds.gen.employeeservice.EmployeeSVC_;

public class EmployeeHistory {
    public void insertEmployeeHistory(Map<String, String> map){
        Insert.into(EmployeeSVC_.class).entry(map);
        System.out.println(map);
    }
}
