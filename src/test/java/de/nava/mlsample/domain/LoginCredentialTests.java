package de.nava.mlsample.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Niko Schmuck
 */
public class LoginCredentialTests {

    private ObjectMapper mapper;

    @Before
    public void setUp() {
         mapper = new ObjectMapper();
    }

    @Test
    public void thatBindingToObjectWorks() throws IOException {
        String json = "{\"username\":\"fred\",\"password\":\"psst\",\"csrf_token\":\"TOPSECRET\"}";
        LoginCredential cred = mapper.readValue(json, LoginCredential.class);
        assertNotNull(cred);
        assertEquals("fred", cred.getUsername());
        assertEquals("psst", cred.getPassword());
    }

    @Test
    public void requirePassword() throws IOException {
        String json = "{\"username\":\"fred\",\"csrf_token\":\"TOPSECRET\"}";
        LoginCredential cred = mapper.readValue(json, LoginCredential.class);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<LoginCredential>> constraintViolations = validator.validate(cred);
        assertEquals(1, constraintViolations.size());
        ConstraintViolation<LoginCredential> violation = constraintViolations.iterator().next();
        assertEquals("password", violation.getPropertyPath().toString());
        assertEquals("{javax.validation.constraints.NotNull.message}", violation.getMessageTemplate());
    }

    @Test(expected = UnrecognizedPropertyException.class)
    public void unknownFieldLeadsToException() throws IOException {
        String json = "{\"user\":\"fred\",\"csrf_token\":\"TOPSECRET\"}";
        mapper.readValue(json, LoginCredential.class);
    }

}
