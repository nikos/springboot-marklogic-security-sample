'use strict';

angular.module('MarkLogicSampleApp',
    // Declare app level module dependencies
    [   'ngResource',
        'ngRoute',
        'toastr',
        'ui.bootstrap',
        'ngTagsInput',
        'MarkLogicSampleApp.services',
        'MarkLogicSampleApp.controllers'
    ])

    .config(function($routeProvider) {
        $routeProvider
            .when('/login', {
                templateUrl: '/public/views/login.html',
                controller: 'LoginController'
            })
            .when('/products', {
                templateUrl: '/public/views/list.html',
                controller: 'ProductListController'
            })
            .when('/search', {
                templateUrl: '/public/views/search.html',
                controller: 'ProductSearchController'
            })
            .when('/products/new', {
                templateUrl: '/public/views/create.html',
                controller: 'ProductCreateController'
            })
            .when('/products/:sku', {
                templateUrl: '/public/views/detail.html',
                controller: 'ProductDetailController'
            })
            .otherwise({
                redirectTo: '/login'
            });
    })

;