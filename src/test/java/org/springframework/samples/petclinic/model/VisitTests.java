package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class VisitTests {

    @Test
    void shouldDefaultDateToToday() {
        LocalDate before = LocalDate.now();
        Visit visit = new Visit();
        LocalDate after = LocalDate.now();

        assertThat(visit.getDate()).isBetween(before, after);
        assertThat(visit.getDescription()).isNull();
        assertThat(visit.getPet()).isNull();
        assertThat(visit.isNew()).isTrue();
    }

    @Test
    void shouldExposeProperties() {
        LocalDate date = LocalDate.of(2013, 1, 1);
        Pet pet = new Pet();

        Visit visit = new Visit();
        visit.setDate(date);
        visit.setDescription("rabies shot");
        visit.setPet(pet);

        assertThat(visit.getDate()).isEqualTo(date);
        assertThat(visit.getDescription()).isEqualTo("rabies shot");
        assertThat(visit.getPet()).isSameAs(pet);
    }
}
