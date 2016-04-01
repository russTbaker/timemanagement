app.controller('TimesheetController', function ($scope, $http, SpringDataRestAdapter, $location, $route) {
    getCurrentUser($http,SpringDataRestAdapter,$scope);
    getJobsForEmployee();


    angular.element(document).ready(function () {
        // getEmployeeId();
        console.log('page loading completed');
    });

    $scope.startDate = {
        opened: false
    };
    $scope.openStartDate = function () {
        $scope.startDate.opened = true;
    };

    $scope.changeJob = function (job) {
        var i = 0
        for (i = 0; i < $scope.timesheetEntries.length; i++) {
            $scope.timesheetEntries[i].jobId = job;
        }
    };

    $scope.onClickCreateTimesheet = function (jobId,startDate) {
        var theDate = Date.parse(startDate);
        SpringDataRestAdapter.process($http.put('/hydrated/employees/'+$scope.userId+'/timesheets/' + jobId + '/?startDate=' + theDate.toString('yyyy-MM-dd')).success(
            function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
            console.log("Timesheet created.")
        });
    }

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
    // todo: hardcoded!!!!
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