using EmployeeService as service from '../../srv/services';
annotate service.EmployeeSVC with @(
    UI.FieldGroup #GeneratedGroup : {
        $Type : 'UI.FieldGroupType',
        Data : [
            {
                $Type : 'UI.DataField',
                Label : 'emp_num',
                Value : emp_num,
            },
            {
                $Type : 'UI.DataField',
                Label : 'name',
                Value : name,
            },
            {
                $Type : 'UI.DataField',
                Label : 'lwd',
                Value : lwd,
            },
            {
                $Type : 'UI.DataField',
                Label : 'salary',
                Value : salary,
            },
            {
                $Type : 'UI.DataField',
                Label : 'email_id',
                Value : email_id,
            },
            {
                $Type : 'UI.DataField',
                Label : 'nok',
                Value : nok,
            },
            {
                $Type : 'UI.DataField',
                Label : 'state_state_id',
                Value : state_state_id,
            },
            {
                $Type : 'UI.DataField',
                Label : 'city_city_id',
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
    ],
    UI.LineItem : [
        {
            $Type : 'UI.DataField',
            Label : 'emp_num',
            Value : emp_num,
        },
        {
            $Type : 'UI.DataField',
            Label : 'name',
            Value : name,
        },
        {
            $Type : 'UI.DataField',
            Label : 'lwd',
            Value : lwd,
        },
        {
            $Type : 'UI.DataField',
            Label : 'salary',
            Value : salary,
        },
        {
            $Type : 'UI.DataField',
            Label : 'email_id',
            Value : email_id,
        },
        {
            $Type : 'UI.DataField',
            Value : state_state_id,
            Label : 'state_state_id',
        },
        {
            $Type : 'UI.DataField',
            Value : city_city_id,
            Label : 'city_city_id',
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
