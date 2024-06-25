using {sap.capire.employees as db} from '../db/schema';

//Define Employee Services
service EmployeeService {
    //For create button enable
    @odata.draft.enabled
    entity EmployeeSVC as
        projection on db.Employee;
 
   @fiori.draft.enabled
    entity LeaveSVC as
        projection on db.Leave;
 
    @odata.draft.enabled
    entity StateSVC as
        projection on db.State;
 
    @odata.draft.enabled
    entity CitySVC as
        projection on db.City;
}