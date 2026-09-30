package org.springframework.samples.petclinic.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Vet;

class EntityUtilsTests {

    @Test
    void getByIdReturnsEntityWithMatchingId() {
        Pet firstPet = petWithId(1);
        Pet matchingPet = petWithId(2);
        Pet thirdPet = petWithId(3);

        Pet result = EntityUtils.getById(List.of(firstPet, matchingPet, thirdPet), Pet.class, 2);

        assertThat(result).isSameAs(matchingPet);
    }

    @Test
    void getByIdThrowsWhenIdIsNotFound() {
        ObjectRetrievalFailureException exception =
            assertThrows(ObjectRetrievalFailureException.class,
                () -> EntityUtils.getById(List.of(petWithId(1)), Pet.class, 2));

        assertThat(exception.getPersistentClass()).isEqualTo(Pet.class);
        assertThat(exception.getIdentifier()).isEqualTo(2);
    }

    @Test
    void getByIdThrowsForEmptyCollection() {
        ObjectRetrievalFailureException exception =
            assertThrows(ObjectRetrievalFailureException.class,
                () -> EntityUtils.getById(List.of(), Pet.class, 2));

        assertThat(exception.getPersistentClass()).isEqualTo(Pet.class);
        assertThat(exception.getIdentifier()).isEqualTo(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getByIdSkipsEntitiesOfDifferentType() {
        Vet vet = new Vet();
        vet.setId(1);
        Owner owner = new Owner();
        owner.setId(1);
        Collection<Person> entities = List.of(vet, owner);
        Class<Person> ownerClass = (Class<Person>) (Class<?>) Owner.class;

        Person result = EntityUtils.getById(entities, ownerClass, 1);

        assertThat(result).isSameAs(owner);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getByIdThrowsWhenOnlyMatchingIdHasDifferentType() {
        Vet vet = new Vet();
        vet.setId(1);
        Collection<Person> entities = List.of(vet);
        Class<Person> ownerClass = (Class<Person>) (Class<?>) Owner.class;

        ObjectRetrievalFailureException exception =
            assertThrows(ObjectRetrievalFailureException.class,
                () -> EntityUtils.getById(entities, ownerClass, 1));

        assertThat(exception.getPersistentClass()).isEqualTo(Owner.class);
        assertThat(exception.getIdentifier()).isEqualTo(1);
    }

    @Test
    void hasImplicitConstructor() {
        assertThat(new EntityUtils() { }).isNotNull();
    }

    private Pet petWithId(int id) {
        Pet pet = new Pet();
        pet.setId(id);
        return pet;
    }
}
