package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NamedEntityTests {

    @Test
    void shouldExposeNameAndUseItAsStringRepresentation() {
        NamedEntity entity = new NamedEntity();
        entity.setName("dog");

        assertThat(entity.getName()).isEqualTo("dog");
        assertThat(entity).hasToString("dog");
    }

    @Test
    void shouldReturnNullStringRepresentationWhenNameIsUnset() {
        NamedEntity entity = new NamedEntity();

        assertThat(entity.getName()).isNull();
        assertThat(entity.toString()).isNull();
    }

    @Test
    void shouldInheritIdentityBehaviourFromBaseEntity() {
        PetType type = new PetType();
        type.setId(3);
        type.setName("lizard");

        Specialty specialty = new Specialty();
        specialty.setName("surgery");

        assertThat(type.isNew()).isFalse();
        assertThat(type).hasToString("lizard");
        assertThat(specialty.isNew()).isTrue();
        assertThat(specialty).hasToString("surgery");
    }
}
