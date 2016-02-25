(function (angular) {
    var HATEOAS_URL = './hydrated/employee/{employeeId}/timesheet';
    var TimeSheetEntryFactory = function ($http, SpringDataRestAdapter) {
        function TimeSheetEntry(timesheetentry) {

            if (timesheetentry._resources) {
                timesheetentry.resources = timesheetentry._resources("self", {}, {
                    update: {
                        method: 'PUT'
                    }
                });
                timesheetentry.save = function (callback) {
                    timesheetentry.resources.update(timesheetentry, function () {
                        callback && callback(timesheetentry);
                    });
                };

                timesheetentry.remove = function (callback) {
                    timesheetentry.resources.remove(function () {
                        callback && callback(timesheetentry);
                    });
                };
            } else {
                timesheetentry.save = function (callback) {
                    Item.resources.save(timesheetentry, function (timesheetentry, headers) {
                        var deferred = $http.get(headers().location);
                        return SpringDataRestAdapter.processWithPromise(deferred).then(function (timesheetentry) {
                            callback && callback(new TimeSheetEntry(timesheetentry));
                        });
                    });
                };
            }

            return timesheetentry;
        }

        TimeSheetEntry.query = function (callback) {
            var deferred = $http.get(HATEOAS_URL);
            return SpringDataRestAdapter.processWithPromise(deferred).then(function (data) {
                Item.resources = data._resources("self");
                callback && callback(_.map(data._embeddedItems, function (item) {
                    return new Item(item);
                }));
            });
        };

        TimeSheetEntry.resources = null;

        return Item;
    };
    TimeSheetEntryFactory.$inject = ['$http', 'SpringDataRestAdapter'];
    angular.module("timesheetApp.timesheetEntryServices").factory("TimeSheetEntry", TimeSheetEntryFactory);
}(angular));