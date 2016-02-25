(function (angular) {
    var EmployeeFactory = function ($resource) {
        return $resource('/api/employees/:id', {
            id: '@id'
        }, {
            update: {
                method: "PUT"
            },
            remove: {
                method: "DELETE"
            },
            query: {
                method: "GET", isArray: false
            }
        });
    };

    EmployeeFactory.$inject = ['$resource'];
    angular.module("timesheetApp.employeeServices").factory("Employee", EmployeeFactory);
}(angular));