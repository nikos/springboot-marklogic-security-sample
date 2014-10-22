'use strict';

/* ~~ Controllers ~~ */

/** Helper */
var handleModalDialog = function($scope, $modalInstance, product) {
    $scope.product = product;
    $scope.ok = function () {
        $modalInstance.close(product);
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
};

angular.module('MarkLogicSampleApp.controllers', [])

    /* ---------------------------------------------------------------------- */
    /* Controller for user access control                                     */
    /* ---------------------------------------------------------------------- */

    .controller("UserController", function($rootScope, $scope, $log, $location, AuthenticationService) {
        $rootScope.user = ""; // TODO: handle also recoverage from session
        $scope.credentials = {username: "", password: ""};

        $scope.login = function() {
            $log.info("Log in", $scope.credentials);
            AuthenticationService.login($scope.credentials).success(function(authResponse) {
                $rootScope.user = authResponse;
                $log.info("Logged in user", $rootScope.user);
                $location.path('/products');
            });
        };
        $scope.logout = function() {
            AuthenticationService.logout().success(function() {
                $log.info("Logged out user " + $rootScope.user.name);
                $rootScope.user = "";
                $location.path('/login');
            });
        };
    })

    /* ---------------------------------------------------------------------- */
    /* Controller for Listing Products                                        */
    /* ---------------------------------------------------------------------- */
    .controller('ProductListController', function($scope, $modal, $log, MarkLogicService, toastr) {
        $scope.searchresult = MarkLogicService.getProducts();
        $scope.totalItems = 40;  // FIXME
        $scope.currentPage = 1;  // FIXME

        $scope.confirmDeletion = function (product) {
            var modalInstance = $modal.open({
                templateUrl: 'confirmDeletionModal.html',
                controller: handleModalDialog,
                size: 'sm',
                resolve: {
                    product: function () {
                        return product;
                    }
                }
            });
            // As soon the user made a decision
            modalInstance.result.then(function(product) {
                $log.info('Will now delete product: ' + product.sku);
                MarkLogicService.removeProduct({sku: product.sku});
                toastr.success("Deleted product '" + product.name + "'");
                // (in case paging is not an issue, delete directly)
                //  var index = $scope.products.indexOf(product);
                //  $scope.products.splice(index, 1);
                $scope.products = MarkLogicService.getProducts();
            }, function () {
                $log.info('Modal dismissed.');
            });
        }
    })

    /* ---------------------------------------------------------------------- */
    /* Controller for Searching Products                                      */
    /* ---------------------------------------------------------------------- */
    .controller('ProductSearchController', function($scope, $http, $log, MarkLogicService) {
        /* Method called by typeahead function */
        $scope.findMatchingProducts = function (val) {
            return MarkLogicService.getProducts({name: val}).$promise.then(function (result) {
                var products = [];
                angular.forEach(result.products, function (item) {
                    products.push(item.name);
                });
                $log.info(" findMatchingProducts returned: ", products);
                return products;
            });
        };
    })

    /* ---------------------------------------------------------------------- */
    /* Controller for Showing a single product                                */
    /* ---------------------------------------------------------------------- */
    .controller('ProductDetailController', function($scope, $routeParams, $location, MarkLogicService) {
        var sku = $routeParams.sku;
        $scope.product = MarkLogicService.getProduct({sku: sku});
    })

    /* ---------------------------------------------------------------------- */
    /* Controller for Creating a single product                               */
    /* ---------------------------------------------------------------------- */
    .controller('ProductCreateController', function($scope, $location, MarkLogicService, toastr) {
        $scope.save = function () {
            MarkLogicService.addProduct(function (product, headers) {
                toastr.success("Created new product");
                $location.path('/products');
            });
        };
    })

;
