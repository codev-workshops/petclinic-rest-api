package org.springframework.samples.petclinic.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {VisitMapperImpl.class, PetMapperImpl.class})
class VisitMapperTests {

    private static final LocalDate VISIT_DATE = LocalDate.of(2013, 1, 1);

    @Autowired
    private VisitMapper visitMapper;

    @Test
    void toVisitFromDtoMapsAllFieldsAndPetId() {
        Visit visit = visitMapper.toVisit(newVisitDto(1, 7));

        assertThat(visit.getId()).isEqualTo(1);
        assertThat(visit.getDate()).isEqualTo(VISIT_DATE);
        assertThat(visit.getDescription()).isEqualTo("rabies shot");
        assertThat(visit.getPet().getId()).isEqualTo(7);
    }

    @Test
    void toVisitFromDtoAlwaysCreatesPetEvenWithoutPetId() {
        Visit visit = visitMapper.toVisit(newVisitDto(1, null));

        assertThat(visit.getPet()).isNotNull();
        assertThat(visit.getPet().getId()).isNull();
    }

    @Test
    void toVisitFromDtoOverridesDefaultDateWithNull() {
        VisitDto dto = newVisitDto(1, 7);
        dto.setDate(null);

        assertThat(visitMapper.toVisit(dto).getDate()).isNull();
    }

    @Test
    void toVisitFromDtoReturnsNullForNull() {
        assertThat(visitMapper.toVisit((VisitDto) null)).isNull();
    }

    @Test
    void toVisitFromFieldsDtoMapsFieldsAndLeavesIdAndPetUnset() {
        VisitFieldsDto fields = new VisitFieldsDto();
        fields.setDate(VISIT_DATE);
        fields.setDescription("neutered");

        Visit visit = visitMapper.toVisit(fields);

        assertThat(visit.getId()).isNull();
        assertThat(visit.getPet()).isNull();
        assertThat(visit.getDate()).isEqualTo(VISIT_DATE);
        assertThat(visit.getDescription()).isEqualTo("neutered");
    }

    @Test
    void toVisitFromFieldsDtoReturnsNullForNull() {
        assertThat(visitMapper.toVisit((VisitFieldsDto) null)).isNull();
    }

    @Test
    void toVisitDtoMapsAllFieldsAndPetId() {
        Pet pet = new Pet();
        pet.setId(8);
        Visit visit = newVisit(2, pet);

        VisitDto dto = visitMapper.toVisitDto(visit);

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getDate()).isEqualTo(VISIT_DATE);
        assertThat(dto.getDescription()).isEqualTo("spayed");
        assertThat(dto.getPetId()).isEqualTo(8);
    }

    @Test
    void toVisitDtoLeavesPetIdNullWhenPetAbsent() {
        assertThat(visitMapper.toVisitDto(newVisit(2, null)).getPetId()).isNull();
    }

    @Test
    void toVisitDtoLeavesPetIdNullWhenPetHasNoId() {
        assertThat(visitMapper.toVisitDto(newVisit(2, new Pet())).getPetId()).isNull();
    }

    @Test
    void toVisitDtoReturnsNullForNull() {
        assertThat(visitMapper.toVisitDto(null)).isNull();
    }

    @Test
    void toVisitsDtoMapsEachVisitInOrder() {
        Collection<VisitDto> dtos = visitMapper.toVisitsDto(List.of(newVisit(1, null), newVisit(2, null)));

        assertThat(dtos).extracting(VisitDto::getId).containsExactly(1, 2);
    }

    @Test
    void toVisitsDtoHandlesEmptyAndNull() {
        assertThat(visitMapper.toVisitsDto(List.of())).isEmpty();
        assertThat(visitMapper.toVisitsDto(null)).isNull();
    }

    private static VisitDto newVisitDto(int id, Integer petId) {
        VisitDto dto = new VisitDto();
        dto.setId(id);
        dto.setPetId(petId);
        dto.setDate(VISIT_DATE);
        dto.setDescription("rabies shot");
        return dto;
    }

    private static Visit newVisit(int id, Pet pet) {
        Visit visit = new Visit();
        visit.setId(id);
        visit.setDate(VISIT_DATE);
        visit.setDescription("spayed");
        visit.setPet(pet);
        return visit;
    }
}
