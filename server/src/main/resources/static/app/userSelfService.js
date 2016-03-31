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

 var usernameSearch ='api/users/search/findByUsername';
    $http.get('user').success(function (data) {
        var username;
        if (data.name) {
            username = data.name;
        }
        SpringDataRestAdapter.process($http.get(usernameSearch + '?username=' + username).success(function (response) {
            $scope.response = angular.toJson(response, true);
        })).then(function (processedResponse) {
            var usersUri = processedResponse._links.self.href;
            init(SpringDataRestAdapter, $http, $scope, usersUri);
        });
    }).error(function (response) {
        console.error("Could not find current user.");
    });

});