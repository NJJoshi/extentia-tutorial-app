namespace sap.capire.employees;

entity Employee
{
    key ID : UUID;
    emp_num : Integer;
    name : String(100);
    lwd : Date;
    @mandatory
    salary : Integer;
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
    @mandatory
    date : Date;
    @mandatory
    days : Integer;
    emp_id : UUID;
    employee : Association to one Employee;
}

entity State
{
    name : String;
    key state_id : Integer;
    cities : Association to many City on cities.state_id = $self.state_id;
}

entity City
{
    name : String;
    key city_id : Integer;
    state_id : Integer;
    state : Association to one State on $self.state_id = state.state_id;
}

annotate Demands with {
    @error.mandatory: 'Demands.plantCode.mandatory'
    plantCode; //field name
}