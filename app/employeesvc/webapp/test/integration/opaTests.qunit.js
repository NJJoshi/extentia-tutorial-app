sap.ui.require(
    [
        'sap/fe/test/JourneyRunner',
        'sap/capire/employees/employeesvc/test/integration/FirstJourney',
		'sap/capire/employees/employeesvc/test/integration/pages/EmployeeSVCList',
		'sap/capire/employees/employeesvc/test/integration/pages/EmployeeSVCObjectPage',
		'sap/capire/employees/employeesvc/test/integration/pages/LeaveSVCObjectPage'
    ],
    function(JourneyRunner, opaJourney, EmployeeSVCList, EmployeeSVCObjectPage, LeaveSVCObjectPage) {
        'use strict';
        var JourneyRunner = new JourneyRunner({
            // start index.html in web folder
            launchUrl: sap.ui.require.toUrl('sap/capire/employees/employeesvc') + '/index.html'
        });

       
        JourneyRunner.run(
            {
                pages: { 
					onTheEmployeeSVCList: EmployeeSVCList,
					onTheEmployeeSVCObjectPage: EmployeeSVCObjectPage,
					onTheLeaveSVCObjectPage: LeaveSVCObjectPage
                }
            },
            opaJourney.run
        );
    }
);