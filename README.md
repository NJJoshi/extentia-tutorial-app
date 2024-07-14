## Tutorials for SAP CAP Concept understanding
### Tutorial 1: 
Create Entities
Employee(EE)=PK, Number, Name, LWD, Salary, emailId (AK), NOK Phone (NOK = Next of Kin)
Leave = PK, EE PK, Date, Days, Add Associtaion - EE -> Leave(1:m)
EE PK = UUID, Leave ID = UUID

### Tutorial 2:
Create EmployeeService

### Tutorial 3: 
Generate Fiori Elements List and Create screens for EE
Enable Create button and Filter on list.

### Tutorial 4:
#### 1 
Use Entity graphical Modeller for this. 
Load the already created Employee entity in graphical modeller. 
You may have to create a new diagram and then cut-paste Employee entity source lines in the schema.cds file.
Create two entities: State, City. 
Create association between the two, 
load data via XLS

##### Special Instructions: 
Associate State & City with Employee entity; 
Add State, City to Employee create and Employee Display screens
You may have to erase the generated code of Employee and re-generate it

#### 2 
Add State, City filter in the EE List and also add list columns

### Tutorial 5:
#### 1 
Delete Fiori Elements code
Now generate Fiori Elements code and Create screens for EE - in the EE Object screen add Leave List
Add Leave create screen (Object screen)

#### 2 
Make Salary mandatory field. Add error message via annotation

#### 3 
Make Date and Days mandatory in Leave. Add error message via annotation

### Tutorial 6: 

#### 1 - Event Handler 
Generate Employee NR via max + 1 logic in the Before Create event handler of Employee entity
Special Instructions: Refer Bookstore event Handlers See: public void beforeCreateOrder(Stream<Orders> orders, EventContext context)

####  2 - Event Handler 
Add a field Employee Count to State. Update it to maintain Statewise Employee count (a) creation of EE and (b) on change of State.
On Employee Create, State.EE Count ++
On Update,  
EE Count ++  and (new state) 
EE Count -- (old State)
on Employee Delete, State.EE Count --

#### 3 - Event Handler 
 Add a button [Recalc Employee Count in one State] above Employee List. On click of this button - call an event handler
    For each State (in a loop)
    Count the number of employees from that state 
    and save the count in the State entity

#### 4 - Event Handler 
Add a button [Recalc Employee Count in all States] in Employee Object page, On click of this button - call an event handler

#### 5 - Event Handler 
Add a button [Zero State Count] in Employee List screen on each row. On click of this button - call an event handler
"For the Employee ID in each row - get the associated State Id, For that state 
    update the Employee total for that state to zero.
    Test if after refresh the total for the state is zero or one (it should be zero)"

#### 6 - Event Handler 
When employee is created or updated if salary is > 10000, abort the transaction and show an error message in the UI and abort the save transaction


## Add Employee History record into separate Entity called EEEECreationHistory
Add separate functionality to capture Employee History when we create/update employee record