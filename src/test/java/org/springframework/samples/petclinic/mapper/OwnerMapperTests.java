package org.springframework.samples.petclinic.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.OwnerFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {OwnerMapperImpl.class, PetMapperImpl.class, VisitMapperImpl.class})
class OwnerMapperTests {

    @Autowired
    private OwnerMapper ownerMapper;

    @Test
    void toOwnerDtoMapsAllFieldsAndPets() {
        Owner owner = newOwner(1, "George");
        Pet pet = new Pet();
        pet.setId(7);
        pet.setName("Leo");
        pet.setBirthDate(LocalDate.of(2020, 9, 7));
        PetType cat = new PetType();
        cat.setId(1);
        cat.setName("cat");
        pet.setType(cat);
        owner.addPet(pet);

        OwnerDto dto = ownerMapper.toOwnerDto(owner);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("George");
        assertThat(dto.getLastName()).isEqualTo("Franklin");
        assertThat(dto.getAddress()).isEqualTo("110 W. Liberty St.");
        assertThat(dto.getCity()).isEqualTo("Madison");
        assertThat(dto.getTelephone()).isEqualTo("6085551023");
        assertThat(dto.getPets()).singleElement().satisfies(petDto -> {
            assertThat(petDto.getId()).isEqualTo(7);
            assertThat(petDto.getName()).isEqualTo("Leo");
            assertThat(petDto.getOwnerId()).isEqualTo(1);
            assertThat(petDto.getType().getName()).isEqualTo("cat");
        });
    }

    @Test
    void toOwnerDtoReturnsEmptyPetsWhenOwnerHasNoPets() {
        OwnerDto dto = ownerMapper.toOwnerDto(newOwner(2, "Betty"));

        assertThat(dto.getPets()).isEmpty();
    }

    @Test
    void toOwnerDtoReturnsNullForNullOwner() {
        assertThat(ownerMapper.toOwnerDto(null)).isNull();
    }

    @Test
    void toOwnerFromOwnerDtoMapsAllFieldsAndPets() {
        PetTypeDto dog = new PetTypeDto();
        dog.setId(2);
        dog.setName("dog");
        PetDto petDto = new PetDto();
        petDto.setId(3);
        petDto.setName("Rosy");
        petDto.setOwnerId(4);
        petDto.setType(dog);
        OwnerDto dto = newOwnerDto(4, "Jean");
        dto.setPets(List.of(petDto));

        Owner owner = ownerMapper.toOwner(dto);

        assertThat(owner.getId()).isEqualTo(4);
        assertThat(owner.getFirstName()).isEqualTo("Jean");
        assertThat(owner.getLastName()).isEqualTo("Coleman");
        assertThat(owner.getAddress()).isEqualTo("105 N. Lake St.");
        assertThat(owner.getCity()).isEqualTo("Monona");
        assertThat(owner.getTelephone()).isEqualTo("6085552654");
        assertThat(owner.getPets()).singleElement().satisfies(pet -> {
            assertThat(pet.getId()).isEqualTo(3);
            assertThat(pet.getName()).isEqualTo("Rosy");
            assertThat(pet.getOwner().getId()).isEqualTo(4);
            assertThat(pet.getType().getName()).isEqualTo("dog");
        });
    }

    @Test
    void toOwnerFromOwnerDtoReturnsNullForNullDto() {
        assertThat(ownerMapper.toOwner((OwnerDto) null)).isNull();
    }

    @Test
    void toOwnerFromOwnerDtoWithNullPetsThrowsBecauseOwnerRejectsNullPetList() {
        OwnerDto dto = newOwnerDto(4, "Jean");
        dto.setPets(null);

        assertThatNullPointerException().isThrownBy(() -> ownerMapper.toOwner(dto));
    }

    @Test
    void toOwnerFromFieldsDtoMapsFieldsAndLeavesIdAndPetsUnset() {
        OwnerFieldsDto fields = new OwnerFieldsDto();
        fields.setFirstName("Harold");
        fields.setLastName("Davis");
        fields.setAddress("563 Friendly St.");
        fields.setCity("Windsor");
        fields.setTelephone("6085553198");

        Owner owner = ownerMapper.toOwner(fields);

        assertThat(owner.getId()).isNull();
        assertThat(owner.isNew()).isTrue();
        assertThat(owner.getFirstName()).isEqualTo("Harold");
        assertThat(owner.getLastName()).isEqualTo("Davis");
        assertThat(owner.getAddress()).isEqualTo("563 Friendly St.");
        assertThat(owner.getCity()).isEqualTo("Windsor");
        assertThat(owner.getTelephone()).isEqualTo("6085553198");
        assertThat(owner.getPets()).isEmpty();
    }

    @Test
    void toOwnerFromFieldsDtoReturnsNullForNullDto() {
        assertThat(ownerMapper.toOwner((OwnerFieldsDto) null)).isNull();
    }

    @Test
    void toOwnerDtoCollectionMapsEachOwnerInOrder() {
        List<OwnerDto> dtos = ownerMapper.toOwnerDtoCollection(
            List.of(newOwner(1, "George"), newOwner(2, "Betty")));

        assertThat(dtos).extracting(OwnerDto::getId).containsExactly(1, 2);
        assertThat(dtos).extracting(OwnerDto::getFirstName).containsExactly("George", "Betty");
    }

    @Test
    void toOwnerDtoCollectionHandlesEmptyNullAndNullElements() {
        assertThat(ownerMapper.toOwnerDtoCollection(List.of())).isEmpty();
        assertThat(ownerMapper.toOwnerDtoCollection(null)).isNull();

        List<Owner> withNull = new ArrayList<>();
        withNull.add(null);
        assertThat(ownerMapper.toOwnerDtoCollection(withNull)).containsExactly((OwnerDto) null);
    }

    @Test
    void toOwnersMapsEachDto() {
        Collection<Owner> owners = ownerMapper.toOwners(
            List.of(newOwnerDto(4, "Jean"), newOwnerDto(5, "Peter")));

        assertThat(owners).extracting(Owner::getId).containsExactly(4, 5);
        assertThat(owners).extracting(Owner::getFirstName).containsExactly("Jean", "Peter");
    }

    @Test
    void toOwnersHandlesEmptyAndNull() {
        assertThat(ownerMapper.toOwners(List.of())).isEmpty();
        assertThat(ownerMapper.toOwners(null)).isNull();
    }

    private static Owner newOwner(int id, String firstName) {
        Owner owner = new Owner();
        owner.setId(id);
        owner.setFirstName(firstName);
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");
        return owner;
    }

    private static OwnerDto newOwnerDto(int id, String firstName) {
        OwnerDto dto = new OwnerDto();
        dto.setId(id);
        dto.setFirstName(firstName);
        dto.setLastName("Coleman");
        dto.setAddress("105 N. Lake St.");
        dto.setCity("Monona");
        dto.setTelephone("6085552654");
        return dto;
    }
}
