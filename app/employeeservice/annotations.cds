using EmployeeService as service from '../../srv/services';
annotate service.Employee with @(
    UI.FieldGroup #GeneratedGroup : {
        $Type : 'UI.FieldGroupType',
        Data : [
            {
                $Type : 'UI.DataField',
                Label : 'Number',
                Value : Number,
            },
            {
                $Type : 'UI.DataField',
                Label : 'Name',
                Value : Name,
            },
            {
                $Type : 'UI.DataField',
                Label : 'LWD',
                Value : LWD,
            },
            {
                $Type : 'UI.DataField',
                Label : 'Salary',
                Value : Salary,
            },
            {
                $Type : 'UI.DataField',
                Label : 'City',
                Value : City,
            },
            {
                $Type : 'UI.DataField',
                Label : 'State',
                Value : State,
            },
            {
                $Type : 'UI.DataField',
                Label : 'email_Id',
                Value : email_Id,
            },
            {
                $Type : 'UI.DataField',
                Label : 'NOK_Phone',
                Value : NOK_Phone,
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
            Label : 'Number',
            Value : Number,
        },
        {
            $Type : 'UI.DataField',
            Label : 'Name',
            Value : Name,
        },
        {
            $Type : 'UI.DataField',
            Label : 'LWD',
            Value : LWD,
        },
        {
            $Type : 'UI.DataField',
            Label : 'Salary',
            Value : Salary,
        },
        {
            $Type : 'UI.DataField',
            Label : 'email_Id',
            Value : email_Id,
        },
        {
            $Type : 'UI.DataField',
            Value : City,
            Label : 'City',
        },
        {
            $Type : 'UI.DataField',
            Value : State,
            Label : 'State',
        },
    ],
);

annotate service.Employee with @(
    UI.SelectionFields : [
        City,
        State,
    ]
);
annotate service.Employee with {
    City @Common.Label : 'City'
};
annotate service.Employee with {
    State @Common.Label : 'State'
};
