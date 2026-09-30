package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OwnerTests {

    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");
    }

    @Test
    void shouldExposeContactDetails() {
        assertThat(owner.getFirstName()).isEqualTo("George");
        assertThat(owner.getLastName()).isEqualTo("Franklin");
        assertThat(owner.getAddress()).isEqualTo("110 W. Liberty St.");
        assertThat(owner.getCity()).isEqualTo("Madison");
        assertThat(owner.getTelephone()).isEqualTo("6085551023");
    }

    @Test
    void shouldReturnEmptyPetsWhenNoneAssigned() {
        assertThat(owner.getPets()).isEmpty();
        assertThat(owner.getPetsInternal()).isEmpty();
    }

    @Test
    void shouldAddPetAndLinkBackToOwner() {
        Pet pet = pet(null, "Leo");

        owner.addPet(pet);

        assertThat(owner.getPets()).containsExactly(pet);
        assertThat(pet.getOwner()).isSameAs(owner);
    }

    @Test
    void shouldReturnPetsSortedByNameIgnoringCase() {
        Pet charlie = pet(1, "charlie");
        Pet bella = pet(2, "Bella");
        Pet alfie = pet(3, "alfie");

        owner.setPets(List.of(charlie, bella, alfie));

        assertThat(owner.getPets()).containsExactly(alfie, bella, charlie);
    }

    @Test
    void shouldReturnUnmodifiablePetList() {
        owner.addPet(pet(1, "Leo"));

        List<Pet> pets = owner.getPets();

        assertThatThrownBy(() -> pets.add(pet(2, "Max")))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldCopyPetListOnSet() {
        Pet leo = pet(1, "Leo");
        List<Pet> source = new ArrayList<>(List.of(leo));

        owner.setPets(source);
        source.clear();

        assertThat(owner.getPets()).containsExactly(leo);
    }

    @Test
    void shouldUsePetsInternalSetDirectly() {
        Set<Pet> pets = new HashSet<>();
        Pet leo = pet(1, "Leo");
        pets.add(leo);

        owner.setPetsInternal(pets);

        assertThat(owner.getPetsInternal()).isSameAs(pets);
        assertThat(owner.getPets()).containsExactly(leo);
    }

    @Test
    void shouldFindPetByNameIgnoringCase() {
        Pet leo = pet(1, "Leo");
        owner.addPet(pet(2, "Basil"));
        owner.addPet(leo);

        assertThat(owner.getPet("leo")).isSameAs(leo);
        assertThat(owner.getPet("LEO")).isSameAs(leo);
    }

    @Test
    void shouldReturnNullWhenNoPetHasGivenName() {
        owner.addPet(pet(1, "Leo"));

        assertThat(owner.getPet("Max")).isNull();
    }

    @Test
    void shouldIncludeNewPetsWhenNotIgnoringNew() {
        Pet newPet = pet(null, "Leo");
        owner.addPet(newPet);

        assertThat(owner.getPet("Leo", false)).isSameAs(newPet);
    }

    @Test
    void shouldSkipNewPetsWhenIgnoringNew() {
        Pet newPet = pet(null, "Leo");
        owner.addPet(newPet);

        assertThat(owner.getPet("Leo", true)).isNull();
    }

    @Test
    void shouldFindPersistedPetWhenIgnoringNew() {
        Pet savedPet = pet(7, "Leo");
        owner.addPet(savedPet);

        assertThat(owner.getPet("leo", true)).isSameAs(savedPet);
    }

    @Test
    void shouldFindPetById() {
        Pet leo = pet(1, "Leo");
        Pet basil = pet(2, "Basil");
        owner.addPet(leo);
        owner.addPet(basil);

        assertThat(owner.getPet(2)).isSameAs(basil);
    }

    @Test
    void shouldReturnNullWhenNoPetHasGivenId() {
        owner.addPet(pet(1, "Leo"));

        assertThat(owner.getPet(99)).isNull();
    }

    @Test
    void shouldIncludeKeyFieldsInToString() {
        String text = owner.toString();

        assertThat(text)
            .contains("id = 1")
            .contains("new = false")
            .contains("lastName = 'Franklin'")
            .contains("firstName = 'George'")
            .contains("address = '110 W. Liberty St.'")
            .contains("city = 'Madison'")
            .contains("telephone = '6085551023'");
    }

    private static Pet pet(Integer id, String name) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        return pet;
    }
}
