package de.nava.mlsample.controller;

import com.marklogic.client.ResourceNotFoundException;
import de.nava.mlsample.domain.Product;
import de.nava.mlsample.domain.ProductSearchResult;
import de.nava.mlsample.service.ProductRepositoryJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class ProductJSONController {

    private static final Logger logger = LoggerFactory.getLogger(ProductJSONController.class);

    @Autowired
    protected ProductRepositoryJSON productRepositoryJSON;

    @RequestMapping(
            value = "/products",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createProduct(@RequestBody Product product, UriComponentsBuilder builder) {
        productRepositoryJSON.add(product);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                builder.path("/products/{id}.json")
                        .buildAndExpand(product.getSku()).toUri());

        return new ResponseEntity<>("", headers, HttpStatus.CREATED);
    }

    @RequestMapping(
            value = "/products/{sku}.json",
            method = RequestMethod.DELETE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("sku") Long sku) {
        productRepositoryJSON.remove(sku);
    }

    @RequestMapping(
            value = "/products/{sku}.json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Product readProduct(@PathVariable("sku") Long sku) {
        return productRepositoryJSON.findBySku(sku);
    }

    @RequestMapping(
            value = "/products.json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ProductSearchResult searchProducts(@RequestParam(required = false, value = "name") String name) {
        if (StringUtils.isEmpty(name)) {
            logger.info("Lookup all {} products...", productRepositoryJSON.count());
            return productRepositoryJSON.findAll();
        } else {
            logger.info("Lookup products by name: {}", name);
            return productRepositoryJSON.findByName(name);
        }
    }

    // Example on how to register custom exception handler in case lookup does not return
    // anything, and avoids HTTP status 500.
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleMarkLogicResourceNotFoundException() {
    }

}
