app.controller('EditUserController', function ($scope, $routeParams, $http, SpringDataRestAdapter, UserService, $uibModal, $log, $route) {
    // Constants/Variables
    var usersUri = '/api/users/' + $routeParams.userId;
    const delay = 100;



    // Local function definitions

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


    init(SpringDataRestAdapter, $http, $scope, usersUri);


    $scope.showAddAddressForm = function () {
        $scope.message = "Show Form Button Clicked";
        console.log($scope.message);
        openModal($uibModal, AddAddressModalInstanceCtrl, $scope, $log, 'views/user/modal/addAddressModal.html');
    };

    var AddAddressModalInstanceCtrl = function ($scope, $uibModalInstance, UserService) {
        $scope.submitForm = function (address) {
            if ($scope.form.$valid) {
                console.log('user form is in scope');
                UserService.addAddress($scope.user, address);
                $scope.response = UserService.getResponse();
                $scope.processedResponse = UserService.getProcessedResponse();
                $uibModalInstance.close('closed');
            } else {
                console.log('userform is not in scope');
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    };


    $scope.showAddPhoneForm = function () {
        $scope.message = "Show Form Button Clicked";
        console.log($scope.message);
        openModal($uibModal, AddPhoneModalInstanceCtrl, $scope, $log, 'views/user/modal/addPhoneModal.html');
    };

    var AddPhoneModalInstanceCtrl = function ($scope, $uibModalInstance, UserService) {
        $scope.submitForm = function (phone) {
            if ($scope.form.$valid) {
                console.log('user form is in scope');
                UserService.addPhone($scope.user, phone);
                $scope.response = UserService.getResponse();
                $scope.processedResponse = UserService.getProcessedResponse();
                $uibModalInstance.close('closed');
            } else {
                console.log('userform is not in scope');
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    };

    $scope.showAddEmailForm = function () {
        $scope.message = "Show Form Button Clicked";
        console.log($scope.message);
        openModal($uibModal, AddEmailModalInstanceCtrl, $scope, $log, 'views/user/modal/addEmailModal.html');
    };

    var AddEmailModalInstanceCtrl = function ($scope, $uibModalInstance, UserService) {
        $scope.submitForm = function (email) {
            if ($scope.form.$valid) {
                console.log('user form is in scope');
                UserService.addEmail($scope.user, email);
                $scope.response = UserService.getResponse();
                $scope.processedResponse = UserService.getProcessedResponse();
                $uibModalInstance.close('closed');
            } else {
                console.log('userform is not in scope');
            }
        };

        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    };







    $scope.checkRole = function (role, user) {
        if (user != null) {
            var roles = user.role;
            return roles == role;
        }
    };

    $scope.editUser = function (user) {
        UserService.editUser(user);
        refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
    };


    $scope.addAddress = function(address){
        UserService.addAddress($scope.user, address);
        refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
    };

    $scope.updateAddress = function (address) {
        UserService.updateAddress(address);
        refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
    };



    $scope.deleteAddress = function (address) {
        // Take address URI and remove it from the list, then update the user with the new uri-list
        var remainingUris = '';
        _.each($scope.user.addresses, function (addr, key) {
            if (addr.street1 != address.street1) {
                remainingUris += addr._links.self.href;
                remainingUris += '\n';
            }
        });
        UserService.removeAssociation(remainingUris, $scope.user, 'addresses', address._links.self.href);
        init(SpringDataRestAdapter, $http, $scope, usersUri);
    };


    $scope.addPhone = function (phone) {
        UserService.addPhone($scope.user, phone);
        refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
    };

    $scope.updatePhone = function (phone) {
        UserService.updatePhone(phone);
        refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
    };

    $scope.deletePhone = function (phone) {
        // Take phone URI and remove it from the list, then update the user with the new uri-list
        var remainingUris = '';
        _.each($scope.user.phones, function (pho, key) {
            if (pho.phone != phone.phone) {
                remainingUris += pho._links.self.href;
                remainingUris += '\n';
            }
        });
        UserService.removeAssociation(remainingUris, $scope.user, 'phones', phone._links.self.href);
        refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
    };

    $scope.addEmail = function (email) {
        UserService.addEmail($scope.user, email);
        refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);

    };

    $scope.updateEmail = function (email) {
        UserService.updateEmail(email);
        refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
    };

    $scope.deleteEmail = function (email) {
        // Take emails URI and remove it from the list, then update the user with the new uri-list
        var remainingUris = '';
        _.each($scope.user.emails, function (eml, key) {
            if (eml.email != email.email) {
                remainingUris += eml._links.self.href;
                remainingUris += '\n';
            }
        });
        UserService.removeAssociation(remainingUris, $scope.user, 'emails', email._links.self.href);
        refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
    };



});