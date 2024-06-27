using EmployeeService as service from '../../srv/services';
annotate service.EmployeeSVC with @(
    UI.FieldGroup #GeneratedGroup : {
        $Type : 'UI.FieldGroupType',
        Data : [
            {
                $Type : 'UI.DataField',
                Label : 'Employee Number',
                Value : emp_num,
            },
            {
                $Type : 'UI.DataField',
                Label : 'Employee Name',
                Value : name,
            },
            {
                $Type : 'UI.DataField',
                Label : 'Last Working Day',
                Value : lwd,
            },
            {
                $Type : 'UI.DataField',
                Label : 'Salary',
                Value : salary,
            },
            {
                $Type : 'UI.DataField',
                Label : 'Email Id',
                Value : email_id,
            },
            {
                $Type : 'UI.DataField',
                Label : 'nok',
                Value : nok,
            },
            {
                $Type : 'UI.DataField',
                Label : 'State',
                Value : state_state_id,
            },
            {
                $Type : 'UI.DataField',
                Label : 'City',
                Value : city_city_id,
            },
        ],
    },
    UI.Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            ID : 'GeneratedFacet1',
            Label : 'General Information',
            Target : '@UI.FieldGroup#GeneratedGroup',
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Leaves Info Table',
            ID : 'LeavesInfoTable',
            Target : 'leaves/@UI.LineItem#LeavesInfoTable',
        },
    ],
    UI.LineItem : [
        {
            $Type : 'UI.DataField',
            Label : 'Employee Number',
            Value : emp_num,
        },
        {
            $Type : 'UI.DataField',
            Label : 'Employee Name',
            Value : name,
        },
        {
            $Type : 'UI.DataField',
            Label : 'Last Working Day',
            Value : lwd,
        },
        {
            $Type : 'UI.DataField',
            Label : 'Salary',
            Value : salary,
        },
        {
            $Type : 'UI.DataField',
            Label : 'Email Id',
            Value : email_id,
        },
        {
            $Type : 'UI.DataField',
            Value : state_state_id,
            Label : 'State',
        },
        {
            $Type : 'UI.DataField',
            Value : city_city_id,
            Label : 'City',
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action : 'EmployeeService.calculateEmpStateWise',
            Label : 'Recalc Employee Count in one State',
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action : 'EmployeeService.zeroStateCount',
            Label : 'Zero State Count',
            Inline : true,
        },
    ],
);

annotate service.EmployeeSVC with {
    state @Common.ValueList : {
        $Type : 'Common.ValueListType',
        CollectionPath : 'StateSVC',
        Parameters : [
            {
                $Type : 'Common.ValueListParameterInOut',
                LocalDataProperty : state_state_id,
                ValueListProperty : 'state_id',
            },
        ],
        Label : 'lbl_state',
    }
};

annotate service.EmployeeSVC with {
    city @Common.ValueList : {
        $Type : 'Common.ValueListType',
        CollectionPath : 'CitySVC',
        Parameters : [
            {
                $Type : 'Common.ValueListParameterInOut',
                LocalDataProperty : city_city_id,
                ValueListProperty : 'city_id',
            },
            {
                $Type : 'Common.ValueListParameterIn',
                ValueListProperty : 'state_id',
                LocalDataProperty : state_state_id,
            },
        ],
        Label : 'lbl_city',
    }
};

annotate service.EmployeeSVC with @(
    UI.SelectionFields : [
        state_state_id,
        city_city_id,
    ]
);
annotate service.EmployeeSVC with {
    city @Common.Label : 'City'
};
annotate service.EmployeeSVC with {
    state @Common.Label : 'State'
};
annotate service.EmployeeSVC with {
    state @Common.Text : {
            $value : state.name,
            ![@UI.TextArrangement] : #TextOnly,
        }
};
annotate service.EmployeeSVC with {
    city @Common.ValueListWithFixedValues : true
};
annotate service.StateSVC with {
    state_id @Common.Text : {
        $value : name,
        ![@UI.TextArrangement] : #TextOnly,
    }
};
annotate service.EmployeeSVC with {
    city @Common.Text : {
            $value : city.name,
            ![@UI.TextArrangement] : #TextOnly,
        }
};
annotate service.CitySVC with {
    city_id @Common.Text : {
        $value : name,
        ![@UI.TextArrangement] : #TextOnly,
    }
};
annotate service.EmployeeSVC with {
    state @Common.ValueListWithFixedValues : true
};
annotate service.EmployeeSVC with @(
    UI.FieldGroup #LeaveInfo : {
        $Type : 'UI.FieldGroupType',
        Data : [
            {
                $Type : 'UI.DataField',
                Value : leaves.date,
                Label : 'Date',
            },{
                $Type : 'UI.DataField',
                Value : leaves.days,
                Label : 'Days',
            },{
                $Type : 'UI.DataField',
                Value : leaves.emp_id,
                Label : 'Employee Id',
            },],
    }
);
annotate service.LeaveSVC with @(
    UI.LineItem #LeavesInfoTable : [
        {
            $Type : 'UI.DataField',
            Value : emp_id,
            Label : 'Employee Name',
        },
        {
            $Type : 'UI.DataField',
            Value : date,
            Label : 'Leave Date',
        },
        {
            $Type : 'UI.DataField',
            Value : days,
            Label : 'No Of Days',
        },]
);
annotate service.LeaveSVC with {
    emp_id @(Common.ValueList : {
            $Type : 'Common.ValueListType',
            CollectionPath : 'EmployeeSVC',
            Parameters : [
                {
                    $Type : 'Common.ValueListParameterInOut',
                    LocalDataProperty : emp_id,
                    ValueListProperty : 'ID',
                },
                {
                    $Type : 'Common.ValueListParameterOut',
                    ValueListProperty : 'name',
                    LocalDataProperty : employee.name,
                },
            ],
            Label : 'lbl_emp_id_leaves',
        },
        Common.ValueListWithFixedValues : true
)};
annotate service.EmployeeSVC with {
    ID @Common.Text : {
        $value : name,
        ![@UI.TextArrangement] : #TextOnly,
    }
};
annotate service.LeaveSVC with @(
    UI.Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Leave Info',
            ID : 'LeaveInfo',
            Target : '@UI.FieldGroup#LeaveInfo',
        },
    ],
    UI.FieldGroup #LeaveInfo : {
        $Type : 'UI.FieldGroupType',
        Data : [
            {
                $Type : 'UI.DataField',
                Value : date,
                Label : 'date',
            },{
                $Type : 'UI.DataField',
                Value : days,
                Label : 'days',
            },],
    }
);
annotate service.EmployeeSVC with {
    emp_num @Common.FieldControl : #ReadOnly
};
annotate service.EmployeeSVC with @(
    UI.Identification : [
        {
            $Type : 'UI.DataFieldForAction',
            Action : 'EmployeeService.calculateEmpStateWise',
            Label : 'Recalc Employee Count in one State',
        },
    ]
);
