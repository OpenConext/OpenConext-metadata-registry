package mr.conf;

import mr.migration.EntityType;
import org.apache.commons.io.IOUtils;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.internal.URIFormatValidator;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MetaDataAutoConfigurationTest {

    private MetaDataAutoConfiguration subject = new MetaDataAutoConfiguration(new ClassPathResource("metadata_configuration"));

    public MetaDataAutoConfigurationTest() throws IOException {
    }

    @Test
    public void testSpSchema() throws Exception {
        String json = readFile("json/valid_service_provider.json");
        subject.validate(json, EntityType.SP.getType());
    }

    @Test
    public void testSchemaSpInvalid() throws Exception {
        testErrors("json/invalid_service_provider.json", EntityType.SP, 4);
    }

    @Test
    public void testSchemaSpForUpdateIsValid() throws Exception {
        String json = readFile("json/updated_metadata.json");
        subject.validate(json, EntityType.SP.getType());
    }

    @Test
    public void testIdpSchema() throws Exception {
        String json = readFile("json/valid_identity_provider.json");
        subject.validate(json, EntityType.IDP.getType());
    }

    @Test
    public void testSchemaIdpInvalid() throws Exception {
        testErrors("json/invalid_identity_provider.json", EntityType.IDP, 11);
    }

    private void testErrors(String path, EntityType type, int errorsExpected) throws IOException {
        String json = readFile(path);
        try {
            subject.validate(json, type.getType());
            fail();
        } catch (ValidationException e) {
            assertEquals(errorsExpected, e.getAllMessages().size());
        }

    }

    @Test
    public void testIndexConfiguration() {
        List<IndexConfiguration> indexConfigurations = subject.indexConfigurations(EntityType.SP.getType());
        assertEquals(1, indexConfigurations.size());

        IndexConfiguration indexConfiguration = indexConfigurations.get(0);
        assertEquals("text", indexConfiguration.getType());
        assertEquals("autocomplete_text_query", indexConfiguration.getName());
        assertEquals(
            asList("entityid", "metaDataFields.name:en", "metaDataFields.name:nl", "metaDataFields.description:en", "metaDataFields.description:nl"),
            indexConfiguration.getFields());

    }

    @Test
    public void testRegularExpression() {
        boolean matches = Pattern.compile("^contacts:([0-3]{1}):emailAddress$").matcher("contacts:0:emailAddress").matches();
        assertTrue(matches);
    }

    @Test
    public void testUriValidator() {
        URIFormatValidator uriFormatValidator = new URIFormatValidator();
        String uri = "http://www.crossknowledge.com ";
        assertTrue(uriFormatValidator.validate(uri).isPresent());
        assertFalse(uriFormatValidator.validate(uri.trim()).isPresent());
    }

    private String readFile(String path) throws IOException {
        return IOUtils.toString(new ClassPathResource(path).getInputStream(), Charset.defaultCharset());
    }


}