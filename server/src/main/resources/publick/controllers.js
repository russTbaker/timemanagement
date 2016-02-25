(function(angular) {
    var AppController = function($scope, Employee) {
        Employee.query(function(response) {
            $scope.employees = response ? response : [];
        });

        $scope.addEmployee = function(employee) {
            new Employee({
                firstName: employee.newEmployee,
                username: employee.username,
                password: employee.password
            }).$save(function(employee) {
                $scope.employees.push(employee);
            });
            $scope.newEmployee = "";
        };

        $scope.updateEmployee = function(employee) {
            employee.$update(function(){
                alert("Updated employee");
            });
        };

        $scope.deleteEmployee = function(employee) {
            employee.$remove(function() {
                $scope.employees.splice($scope.employees.indexOf(employee), 1);
            });
        };
    };

    AppController.$inject = ['$scope', 'Employee'];
    angular.module("timesheetApp.controllers").controller("AppController", AppController);
}(angular));