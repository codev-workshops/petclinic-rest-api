package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class PetTests {

    @Test
    void shouldExposeBasicProperties() {
        PetType cat = new PetType();
        cat.setName("cat");
        Owner owner = new Owner();
        LocalDate birthDate = LocalDate.of(2010, 9, 7);

        Pet pet = new Pet();
        pet.setName("Leo");
        pet.setBirthDate(birthDate);
        pet.setType(cat);
        pet.setOwner(owner);

        assertThat(pet.getName()).isEqualTo("Leo");
        assertThat(pet.getBirthDate()).isEqualTo(birthDate);
        assertThat(pet.getType()).isSameAs(cat);
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void shouldReturnEmptyVisitsWhenNoneAssigned() {
        Pet pet = new Pet();

        assertThat(pet.getVisits()).isEmpty();
        assertThat(pet.getVisitsInternal()).isEmpty();
    }

    @Test
    void shouldAddVisitAndLinkBackToPet() {
        Pet pet = new Pet();
        Visit visit = visit(LocalDate.of(2020, 1, 1));

        pet.addVisit(visit);

        assertThat(pet.getVisits()).containsExactly(visit);
        assertThat(visit.getPet()).isSameAs(pet);
    }

    @Test
    void shouldReturnVisitsSortedByDateDescending() {
        Visit oldest = visit(LocalDate.of(2019, 3, 4));
        Visit newest = visit(LocalDate.of(2021, 6, 1));
        Visit middle = visit(LocalDate.of(2020, 11, 20));

        Pet pet = new Pet();
        pet.setVisits(List.of(oldest, newest, middle));

        assertThat(pet.getVisits()).containsExactly(newest, middle, oldest);
    }

    @Test
    void shouldReturnUnmodifiableVisitList() {
        Pet pet = new Pet();
        pet.addVisit(visit(LocalDate.of(2020, 1, 1)));

        List<Visit> visits = pet.getVisits();

        assertThatThrownBy(() -> visits.add(new Visit()))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldCopyVisitListOnSet() {
        Visit visit = visit(LocalDate.of(2020, 1, 1));
        List<Visit> source = new ArrayList<>(List.of(visit));

        Pet pet = new Pet();
        pet.setVisits(source);
        source.clear();

        assertThat(pet.getVisits()).containsExactly(visit);
    }

    @Test
    void shouldUseVisitsInternalSetDirectly() {
        Set<Visit> visits = new HashSet<>();
        Visit visit = visit(LocalDate.of(2020, 1, 1));
        visits.add(visit);

        Pet pet = new Pet();
        pet.setVisitsInternal(visits);

        assertThat(pet.getVisitsInternal()).isSameAs(visits);
        assertThat(pet.getVisits()).containsExactly(visit);
    }

    private static Visit visit(LocalDate date) {
        Visit visit = new Visit();
        visit.setDate(date);
        return visit;
    }
}
