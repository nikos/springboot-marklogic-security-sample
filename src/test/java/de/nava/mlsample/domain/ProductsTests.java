package de.nava.mlsample.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Niko Schmuck
 */
public class ProductsTests {

    @Test
    public void thatProductsCanBeReadFromJSON() throws JAXBException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = Products.class.getResourceAsStream("/sampledata/products.json");
        Product[] products = mapper.readValue(inputStream, Product[].class);
        assertEquals(40, products.length);
    }

    @Test
    public void thatProductCanBeWrittenToJSON() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.convertValue(generateTestProduct(4712L, "JsonManiac"), JsonNode.class);
        assertThat(json.toString(), containsString("\"sku\":4712"));
        assertThat(json.toString(), containsString("\"name\":\"JsonManiac\""));
        assertThat(json.toString(), containsString("\"categories\":[\"Book\",\"Test\"]}"));
    }

    // ~~

    private Product generateTestProduct(Long sku, String name) {
        return new Product(sku, name, 9.99,
                Arrays.asList(new Category("Book"), new Category("Test")));
    }

}
