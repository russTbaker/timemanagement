
app.controller('UserSelfServiceController', function ($scope, $http, SpringDataRestAdapter) {

    $scope.emailTypes = ['billing',
        'business',
        'both'];

    $scope.phoneTypes = ['fax',
        'mobile',
        'office'];

    $scope.roles = ['administrator'
        , 'employee'
        , 'customer'
        , 'guest'];
    getCurrentUser($http, SpringDataRestAdapter, $scope);
    

});