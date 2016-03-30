app.controller('AddUserController', function ($scope, $http, SpringDataRestAdapter, UserService) {
    $scope.roles = [
        {name: 'administrator', value: 'administrator'},
        {name: 'employee', value: 'employee'},
        {name: 'customer', value: 'customer'},
        {name: 'guest', value: 'guest'}
    ];

    $scope.emailTypes = ['billing',
        'business',
        'both'];

    $scope.phoneTypes = ['fax',
        'mobile',
        'office'];

    $scope.isCustomer = true;


    $scope.enableFields = function (role) {
        $scope.isCustomer = role == 'customers';
    };

    $scope.addUser = function (user) {
        UserService.addUser(user, $scope, this.addUserForm);
        $scope.user = UserService.getUser();
        $scope.error = UserService.getError();
        console.log($scope.error);
    };

    $scope.addAddress = function (address) {
        UserService.addAddress($scope.user, address);
    };

    $scope.addEmail = function (email) {
        UserService.addEmail($scope.user, email)
    };

    $scope.addPhone = function (phone) {
        UserService.addPhone($scope.user, phone);
    };
});