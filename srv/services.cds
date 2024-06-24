using {sap.capire.employees as db} from '../db/schema';

//Define Employee Services
service EmployeeService {
    @odata.draft.enabled
    entity Employee as projection on db.Employee;

    @odata.draft.enabled
    entity Leave as projection on db.Leave;

    entity City as projection on db.City;

    entity State as projection on db.State;
}