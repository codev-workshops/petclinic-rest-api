package org.springframework.samples.petclinic.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class BindingErrorsResponseTests {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void testNoArgumentConstructorIsEmpty() {
        BindingErrorsResponse response = new BindingErrorsResponse();

        assertThat(response.toJSON()).isEqualTo("[]");
        assertThat(response.toString()).isEqualTo("BindingErrorsResponse [bindingErrors=[]]");
    }

    @Test
    void testBodyIdWithoutPathIdAddsError() throws Exception {
        BindingErrorsResponse response = new BindingErrorsResponse(5);
        JsonNode errors = jsonMapper.readTree(response.toJSON());

        assertThat(errors.size()).isEqualTo(1);
        assertBindingError(errors.get(0), "body", "id", "5", "must not be specified");
    }

    @Test
    void testMatchingAndMissingIdsAreEmpty() {
        assertThat(new BindingErrorsResponse(null, null).toJSON()).isEqualTo("[]");
        assertThat(new BindingErrorsResponse(1, 1).toJSON()).isEqualTo("[]");
        assertThat(new BindingErrorsResponse(1, null).toJSON()).isEqualTo("[]");
    }

    @Test
    void testMismatchedIdsAddError() throws Exception {
        BindingErrorsResponse response = new BindingErrorsResponse(1, 2);
        JsonNode errors = jsonMapper.readTree(response.toJSON());

        assertThat(errors.size()).isEqualTo(1);
        assertBindingError(errors.get(0), "body", "id", "2", "does not match pathId: 1");
    }

    @Test
    void testAddAllErrorsIncludesFieldErrorsOnly() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("");
        owner.setLastName(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(owner, "owner");
        bindingResult.rejectValue("firstName", "NotBlank", "must not be blank");
        bindingResult.rejectValue("lastName", "NotNull", "must not be null");
        bindingResult.reject("global", "ignored");

        BindingErrorsResponse response = new BindingErrorsResponse();
        response.addAllErrors(bindingResult);
        JsonNode errors = jsonMapper.readTree(response.toJSON());

        assertThat(errors.size()).isEqualTo(2);
        assertBindingError(errors.get(0), "owner", "firstName", "", "must not be blank");
        assertBindingError(errors.get(1), "owner", "lastName", "null", "must not be null");
    }

    @Test
    void testToStringIncludesBodyIdError() {
        BindingErrorsResponse response = new BindingErrorsResponse(5);

        assertThat(response.toString()).contains(
            "BindingError [objectName=body, fieldName=id, fieldValue=5, errorMessage=must not be specified]");
    }

    @Test
    void testSerializationFailureReturnsEmptyString() {
        BindingErrorsResponse response = new BindingErrorsResponse();
        response.addError(new ExplodingBindingError());

        assertThat(response.toJSON()).isEmpty();
    }

    private void assertBindingError(JsonNode error, String objectName, String fieldName, String fieldValue,
                                    String errorMessage) {
        assertThat(error.get("objectName").asText()).isEqualTo(objectName);
        assertThat(error.get("fieldName").asText()).isEqualTo(fieldName);
        assertThat(error.get("fieldValue").asText()).isEqualTo(fieldValue);
        assertThat(error.get("errorMessage").asText()).isEqualTo(errorMessage);
    }

    private static class ExplodingBindingError extends BindingErrorsResponse.BindingError {

        public String getExploding() {
            throw new IllegalStateException("cannot serialize");
        }
    }
}
