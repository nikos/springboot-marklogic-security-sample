'use strict';

/* ~~ Services ~~ */

angular.module('MarkLogicSampleApp.services', [])

    /* ---------------------------------------------------------------------- */
    /* Provide access to REST API from Spring-Boot application                */
    /* ---------------------------------------------------------------------- */
    .factory('MarkLogicService', function ($resource) {
        var service = $resource('/products/:sku.json', {id: '@sku'}, {
            getProduct:    {method: 'GET'},
            /* Note: search results contain facets beside the products */
            getProducts:   {method: 'GET',  url: '/products.json', isArray: false},
            addProduct:    {method: 'POST', url: '/products' },
            removeProduct: {method: 'DELETE'}
        });
        service.prototype.isNew = function() {
            return (typeof(this.id) === 'undefined');
        };
        return service;
    })

;
