namespace sap.capire.employees;

entity Employee
{
    key ID : UUID
        @Core.Computed;
    emp_num : Integer;
    name : String(100);
    lwd : Date;
    salary : Integer
        @mandatory
        @validation_error_message.message : 'Please provide salary.';
    email_id : String(100);
    nok : Integer;
    state : Association to one State;
    city : Association to one City;
    leaves : Composition of many Leave on leaves.employee = $self;
}

annotate Employee with @assert.unique :
{
    emp_num : [ emp_num ],
};

entity Leave
{
    key leave_id : UUID;
    date : Date
        @mandatory;
    days : Integer
        @mandatory;
    emp_id : UUID;
    employee : Association to one Employee;
}

entity State
{
    name : String;
    key state_id : Integer;
    cities : Association to many City on cities.state_id = $self.state_id;
    emp_count : Integer not null default 0;
}

entity City
{
    name : String;
    key city_id : Integer;
    state_id : Integer;
    state : Association to one State on $self.state_id = state.state_id;
}

entity EECreationHistory
{
    key ID : UUID;
    createdAt : DateTime;
    employeeId : String(100);
    employeeName : String(100);
    Status : String(100);
}
