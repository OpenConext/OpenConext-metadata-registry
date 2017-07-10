package mr.validations;

import org.junit.Test;

import static org.junit.Assert.*;

public class SAMLEmailValidatorTest {

    private SAMLEmailValidator subject = new SAMLEmailValidator();

    @Test
    public void validate() throws Exception {
        assertFalse(subject.validate("MAILTO:john.doe@example.org").isPresent());
        assertFalse(subject.validate("mailto:john.doe@example.org").isPresent());
        assertFalse(subject.validate("john.doe@example.org").isPresent());

        assertTrue(subject.validate("invalid").isPresent());
        assertTrue(subject.validate("mailto:invalid").isPresent());
    }

}