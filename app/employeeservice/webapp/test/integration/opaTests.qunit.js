sap.ui.require(
    [
        'sap/fe/test/JourneyRunner',
        'sap/capire/employees/employeeservice/test/integration/FirstJourney',
		'sap/capire/employees/employeeservice/test/integration/pages/EmployeeList',
		'sap/capire/employees/employeeservice/test/integration/pages/EmployeeObjectPage'
    ],
    function(JourneyRunner, opaJourney, EmployeeList, EmployeeObjectPage) {
        'use strict';
        var JourneyRunner = new JourneyRunner({
            // start index.html in web folder
            launchUrl: sap.ui.require.toUrl('sap/capire/employees/employeeservice') + '/index.html'
        });

       
        JourneyRunner.run(
            {
                pages: { 
					onTheEmployeeList: EmployeeList,
					onTheEmployeeObjectPage: EmployeeObjectPage
                }
            },
            opaJourney.run
        );
    }
);