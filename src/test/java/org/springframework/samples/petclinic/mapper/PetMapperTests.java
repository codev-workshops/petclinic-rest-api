package org.springframework.samples.petclinic.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {PetMapperImpl.class, VisitMapperImpl.class})
class PetMapperTests {

    private static final LocalDate BIRTH_DATE = LocalDate.of(2019, 3, 14);

    @Autowired
    private PetMapper petMapper;

    @Test
    void toPetDtoMapsAllFieldsOwnerIdTypeAndVisits() {
        Owner owner = new Owner();
        owner.setId(10);
        Pet pet = newPet(5, "Max");
        owner.addPet(pet);
        Visit older = newVisit(1, LocalDate.of(2024, 1, 1), "shots");
        Visit newer = newVisit(2, LocalDate.of(2025, 6, 1), "checkup");
        pet.addVisit(older);
        pet.addVisit(newer);

        PetDto dto = petMapper.toPetDto(pet);

        assertThat(dto.getId()).isEqualTo(5);
        assertThat(dto.getName()).isEqualTo("Max");
        assertThat(dto.getBirthDate()).isEqualTo(BIRTH_DATE);
        assertThat(dto.getOwnerId()).isEqualTo(10);
        assertThat(dto.getType().getId()).isEqualTo(2);
        assertThat(dto.getType().getName()).isEqualTo("dog");
        assertThat(dto.getVisits()).extracting(VisitDto::getId).containsExactly(2, 1);
        assertThat(dto.getVisits()).extracting(VisitDto::getPetId).containsOnly(5);
    }

    @Test
    void toPetDtoLeavesOwnerIdAndTypeNullWhenAbsent() {
        Pet pet = new Pet();
        pet.setName("Stray");

        PetDto dto = petMapper.toPetDto(pet);

        assertThat(dto.getOwnerId()).isNull();
        assertThat(dto.getType()).isNull();
        assertThat(dto.getVisits()).isEmpty();
    }

    @Test
    void toPetDtoLeavesOwnerIdNullWhenOwnerHasNoId() {
        Pet pet = newPet(5, "Max");
        pet.setOwner(new Owner());

        assertThat(petMapper.toPetDto(pet).getOwnerId()).isNull();
    }

    @Test
    void toPetDtoReturnsNullForNull() {
        assertThat(petMapper.toPetDto(null)).isNull();
    }

    @Test
    void toPetsDtoMapsEachPetInOrder() {
        Collection<PetDto> dtos = petMapper.toPetsDto(List.of(newPet(1, "Leo"), newPet(2, "Basil")));

        assertThat(dtos).extracting(PetDto::getName).containsExactly("Leo", "Basil");
    }

    @Test
    void toPetsDtoHandlesEmptyAndNull() {
        assertThat(petMapper.toPetsDto(List.of())).isEmpty();
        assertThat(petMapper.toPetsDto(null)).isNull();
    }

    @Test
    void toPetFromDtoMapsAllFieldsOwnerTypeAndVisits() {
        PetDto dto = newPetDto(8, "Samantha", 6);
        VisitDto visitDto = new VisitDto();
        visitDto.setId(4);
        visitDto.setPetId(8);
        visitDto.setDate(LocalDate.of(2013, 1, 4));
        visitDto.setDescription("spayed");
        dto.setVisits(List.of(visitDto));

        Pet pet = petMapper.toPet(dto);

        assertThat(pet.getId()).isEqualTo(8);
        assertThat(pet.getName()).isEqualTo("Samantha");
        assertThat(pet.getBirthDate()).isEqualTo(BIRTH_DATE);
        assertThat(pet.getOwner().getId()).isEqualTo(6);
        assertThat(pet.getType().getId()).isEqualTo(1);
        assertThat(pet.getType().getName()).isEqualTo("cat");
        assertThat(pet.getVisits()).singleElement().satisfies(visit -> {
            assertThat(visit.getId()).isEqualTo(4);
            assertThat(visit.getDescription()).isEqualTo("spayed");
            assertThat(visit.getPet().getId()).isEqualTo(8);
        });
    }

    @Test
    void toPetFromDtoAlwaysCreatesOwnerEvenWithoutOwnerId() {
        PetDto dto = newPetDto(8, "Samantha", null);
        dto.setType(null);

        Pet pet = petMapper.toPet(dto);

        assertThat(pet.getOwner()).isNotNull();
        assertThat(pet.getOwner().getId()).isNull();
        assertThat(pet.getType()).isNull();
        assertThat(pet.getVisits()).isEmpty();
    }

    @Test
    void toPetFromDtoWithNullVisitsThrowsBecausePetRejectsNullVisitList() {
        PetDto dto = newPetDto(8, "Samantha", 6);
        dto.setVisits(null);

        assertThatNullPointerException().isThrownBy(() -> petMapper.toPet(dto));
    }

    @Test
    void toPetFromDtoReturnsNullForNull() {
        assertThat(petMapper.toPet((PetDto) null)).isNull();
    }

    @Test
    void toPetFromFieldsDtoMapsFieldsAndLeavesIdOwnerAndVisitsUnset() {
        PetFieldsDto fields = new PetFieldsDto();
        fields.setName("Lucky");
        fields.setBirthDate(BIRTH_DATE);
        fields.setType(newPetTypeDto(3, "lizard"));

        Pet pet = petMapper.toPet(fields);

        assertThat(pet.getId()).isNull();
        assertThat(pet.getName()).isEqualTo("Lucky");
        assertThat(pet.getBirthDate()).isEqualTo(BIRTH_DATE);
        assertThat(pet.getType().getId()).isEqualTo(3);
        assertThat(pet.getType().getName()).isEqualTo("lizard");
        assertThat(pet.getOwner()).isNull();
        assertThat(pet.getVisits()).isEmpty();
    }

    @Test
    void toPetFromFieldsDtoReturnsNullForNull() {
        assertThat(petMapper.toPet((PetFieldsDto) null)).isNull();
    }

    @Test
    void toPetsMapsEachDtoInOrder() {
        Collection<Pet> pets = petMapper.toPets(List.of(newPetDto(1, "Leo", 1), newPetDto(2, "Basil", 2)));

        assertThat(pets).extracting(Pet::getId).containsExactly(1, 2);
        assertThat(pets).extracting(pet -> pet.getOwner().getId()).containsExactly(1, 2);
    }

    @Test
    void toPetsHandlesEmptyAndNull() {
        assertThat(petMapper.toPets(List.of())).isEmpty();
        assertThat(petMapper.toPets(null)).isNull();
    }

    @Test
    void toPetTypeDtoAndBackRoundTrips() {
        PetType petType = newPetType(4, "snake");

        PetTypeDto dto = petMapper.toPetTypeDto(petType);
        PetType mappedBack = petMapper.toPetType(dto);

        assertThat(dto.getId()).isEqualTo(4);
        assertThat(dto.getName()).isEqualTo("snake");
        assertThat(mappedBack.getId()).isEqualTo(4);
        assertThat(mappedBack.getName()).isEqualTo("snake");
    }

    @Test
    void petTypeConversionsReturnNullForNull() {
        assertThat(petMapper.toPetTypeDto(null)).isNull();
        assertThat(petMapper.toPetType(null)).isNull();
    }

    @Test
    void toPetTypeDtosMapsEachPetType() {
        Collection<PetTypeDto> dtos = petMapper.toPetTypeDtos(
            List.of(newPetType(1, "cat"), newPetType(2, "dog")));

        assertThat(dtos).extracting(PetTypeDto::getName).containsExactly("cat", "dog");
    }

    @Test
    void toPetTypeDtosHandlesEmptyAndNull() {
        assertThat(petMapper.toPetTypeDtos(List.of())).isEmpty();
        assertThat(petMapper.toPetTypeDtos(null)).isNull();
    }

    private static Pet newPet(int id, String name) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setBirthDate(BIRTH_DATE);
        pet.setType(newPetType(2, "dog"));
        return pet;
    }

    private static PetDto newPetDto(int id, String name, Integer ownerId) {
        PetDto dto = new PetDto();
        dto.setId(id);
        dto.setName(name);
        dto.setBirthDate(BIRTH_DATE);
        dto.setOwnerId(ownerId);
        dto.setType(newPetTypeDto(1, "cat"));
        return dto;
    }

    private static PetType newPetType(int id, String name) {
        PetType petType = new PetType();
        petType.setId(id);
        petType.setName(name);
        return petType;
    }

    private static PetTypeDto newPetTypeDto(int id, String name) {
        PetTypeDto dto = new PetTypeDto();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    private static Visit newVisit(int id, LocalDate date, String description) {
        Visit visit = new Visit();
        visit.setId(id);
        visit.setDate(date);
        visit.setDescription(description);
        return visit;
    }
}
