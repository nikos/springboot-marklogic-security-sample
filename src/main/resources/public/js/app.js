'use strict';

angular.module('MarkLogicSampleApp',
    // Declare app level module dependencies
    [   'ngResource',
        'ngRoute',
        'toastr',
        'ui.bootstrap',
        'ngTagsInput'
    ])

    .config(function($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: '/views/list.html',
                controller: 'ProductListController'
            })
            .when('/login', {
                templateUrl: '/views/login.html',
                controller: 'LoginController'
            })
            .when('/search', {
                templateUrl: '/views/search.html',
                controller: 'ApplicationController'
            })
            .when('/products/new', {
                templateUrl: '/views/create.html',
                controller: 'ProductCreateController'
            })
            .when('/products/:sku', {
                templateUrl: '/views/detail.html',
                controller: 'ProductDetailController'
            })
            .otherwise({
                redirectTo: '/'
            });
    })

;