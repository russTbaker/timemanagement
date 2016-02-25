(function(angular) {
    var AppController = function($scope, Item) {
        Item.query(function(response) {
            $scope.employees = response ? response : [];
        });

        $scope.addEmployee = function(username,password) {
            new Employee({
                username: description,
                password: password
            }).save(function(item) {
                $scope.employees.push(item);
            });
            $scope.newEmployee = "";
        };

        $scope.updateItem = function(item) {
            item.save();
        };

        $scope.deleteItem = function(item) {
            item.remove(function() {
                $scope.items.splice($scope.items.indexOf(item), 1);
            });
        };
    };

    AppController.$inject = ['$scope', 'Employee'];
    angular.module("timesheetApp.controllers").controller("AppController", AppController);
}(angular));