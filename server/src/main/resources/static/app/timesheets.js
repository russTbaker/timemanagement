app.controller('TimesheetController', function ($scope, $http, SpringDataRestAdapter, $location, $route) {
    console.log("");
    angular.element(document).ready(function () {
        getEmployeeId();
        console.log('page loading completed');
    });


    getJobsForEmployee();

    $scope.changeJob = function (job) {
        var i = 0
        for (i = 0; i < $scope.timesheetEntries.length; i++) {
            $scope.timesheetEntries[i].jobId = job;
        }
    };

    // TODO: This is hardcoded
    $scope.onClickCreateTimesheet = function (jobId) {
        SpringDataRestAdapter.process($http.put('/hydrated/employees/2/timesheets/' + jobId).success(
            function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
            console.log("Timesheet created.")
        });
    }

    // TODO: This is hardcoded
    $scope.updateTimesheet = function (jobs) {
        var i;
        for (i = 0; i < jobs.length; i++) {
            var url = '/hydrated/employees/timesheets/' + jobs[i].jobId + '/timesheetentries';
            console.log("Trying to update timesheet entries" + url);
            SpringDataRestAdapter.process($http.put(url, jobs[i].timeEntries,
                'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                })).then(function (processedResponse) {
                console.log("Time Entry updated.")
            });
        }
        //loadTimesheets(map);
        //loadTimesheetEntries();
        $route.reload();
    };

    // Functions

    function loadTimesheetEntries() {
        SpringDataRestAdapter.process($http.get('/api/jobs/1/timeEntries').success(function (response) {
            $scope.response = angular.toJson(response, true);
        })).then(function (processedResponse) {
            $scope.timesheetEntries = processedResponse._embeddedItems;
            //$scope.timesheetEntries = angular.toJson(processedResponse, true);
        });
    }

    function getJobsForEmployee() {
        SpringDataRestAdapter.process($http.get('/hydrated/employees/jobs').success(function (response) {
            $scope.response = angular.toJson(response, true);
        })).then(function (processedResponse) {
            $scope.jobs = processedResponse._embeddedItems;

            //var index;
            //for(index =0;index<processedResponse._embeddedItems.length;index++){
            //    console.log(processedResponse._embeddedItems[index].job.name);
            //}
            console.log("Got jobs ")
        });
    }

    function getEmployeeId() {
        var employeeId;
        SpringDataRestAdapter.process($http.get('/employee').success(function (response) {
            $scope.response = angular.toJson(response, true);
        })).then(function (processedResponse) {
            console.log("Processed response: " + processedResponse.dba);
            //employeeId = processedResponse.id;
            $scope.employeeId = processedResponse.id;
            //console.log("Got jobs ")
        });
        return employeeId;
    }

    function getEmployeeIdCallback(employeeId) {

    }
});