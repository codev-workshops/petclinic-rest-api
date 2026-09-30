package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BaseEntityTests {

    @Test
    void shouldBeNewWhenIdIsNull() {
        BaseEntity entity = new BaseEntity();

        assertThat(entity.getId()).isNull();
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    void shouldNotBeNewWhenIdIsSet() {
        BaseEntity entity = new BaseEntity();
        entity.setId(42);

        assertThat(entity.getId()).isEqualTo(42);
        assertThat(entity.isNew()).isFalse();
    }

    @Test
    void shouldBecomeNewAgainWhenIdIsCleared() {
        BaseEntity entity = new BaseEntity();
        entity.setId(1);
        entity.setId(null);

        assertThat(entity.isNew()).isTrue();
    }
}
