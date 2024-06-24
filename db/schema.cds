namespace sap.capire.employees;

using
{
    managed,
    cuid
}
from '@sap/cds/common';

entity Employee : cuid, managed
{
    Number : Integer;
    Name : String(111);
    LWD : Date;
    Salary : Decimal(9,2);
    email_Id : String;
    NOK_Phone : String
        @assert.format : '[0-9]+';
    leaves : Association to one Leave;
    City : String(100);
    State : String(100);
    city_id : Association to one City on city_id.ID = ID;
    state_id : Association to one State on state_id.ID = ID;
}

entity Leave : managed
{
    Leave_ID : UUID;
    employee : Association to many Employee on employee.leaves = $self;
    Leave_Date : Date
        @mandatory;
    No_Of_Days : Integer
        @manadatory;
}

/**
 * name
 */
entity State
{
    key ID : UUID;
    name : String(100);
}

entity City
{
    key ID : UUID;
    name : String(100);
    state_id : Association to one State;
}
