package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class VetTests {

    @Test
    void shouldHaveNoSpecialtiesByDefault() {
        Vet vet = new Vet();

        assertThat(vet.getSpecialties()).isEmpty();
        assertThat(vet.getNrOfSpecialties()).isZero();
    }

    @Test
    void shouldAddSpecialtiesAndReturnThemSortedIgnoringCase() {
        Specialty surgery = specialty("surgery");
        Specialty dentistry = specialty("Dentistry");
        Specialty radiology = specialty("radiology");

        Vet vet = new Vet();
        vet.addSpecialty(surgery);
        vet.addSpecialty(dentistry);
        vet.addSpecialty(radiology);

        assertThat(vet.getSpecialties()).containsExactly(dentistry, radiology, surgery);
        assertThat(vet.getNrOfSpecialties()).isEqualTo(3);
    }

    @Test
    void shouldNotDuplicateSameSpecialtyInstance() {
        Specialty surgery = specialty("surgery");

        Vet vet = new Vet();
        vet.addSpecialty(surgery);
        vet.addSpecialty(surgery);

        assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
    }

    @Test
    void shouldReturnUnmodifiableSpecialtyList() {
        Vet vet = new Vet();
        vet.addSpecialty(specialty("surgery"));

        List<Specialty> specialties = vet.getSpecialties();

        assertThatThrownBy(() -> specialties.add(specialty("dentistry")))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldReplaceSpecialtiesWithCopyOfGivenList() {
        Specialty surgery = specialty("surgery");
        Specialty dentistry = specialty("dentistry");
        List<Specialty> source = new ArrayList<>(List.of(surgery, dentistry));

        Vet vet = new Vet();
        vet.addSpecialty(specialty("radiology"));
        vet.setSpecialties(source);
        source.clear();

        assertThat(vet.getSpecialties()).containsExactly(dentistry, surgery);
    }

    @Test
    void shouldClearSpecialties() {
        Vet vet = new Vet();
        vet.addSpecialty(specialty("surgery"));
        vet.addSpecialty(specialty("dentistry"));

        vet.clearSpecialties();

        assertThat(vet.getSpecialties()).isEmpty();
        assertThat(vet.getNrOfSpecialties()).isZero();
    }

    @Test
    void shouldUseSpecialtiesInternalSetDirectly() {
        Set<Specialty> specialties = new HashSet<>();
        Specialty surgery = specialty("surgery");
        specialties.add(surgery);

        Vet vet = new Vet();
        vet.setSpecialtiesInternal(specialties);

        assertThat(vet.getSpecialtiesInternal()).isSameAs(specialties);
        assertThat(vet.getSpecialties()).containsExactly(surgery);
    }

    private static Specialty specialty(String name) {
        Specialty specialty = new Specialty();
        specialty.setName(name);
        return specialty;
    }
}
