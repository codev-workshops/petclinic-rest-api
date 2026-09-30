package org.springframework.samples.petclinic.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;

class SpecialtyMapperTests {

    private final SpecialtyMapper specialtyMapper = Mappers.getMapper(SpecialtyMapper.class);

    @Test
    void toSpecialtyMapsIdAndName() {
        Specialty specialty = specialtyMapper.toSpecialty(newSpecialtyDto(1, "radiology"));

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    void toSpecialtyReturnsNullForNull() {
        assertThat(specialtyMapper.toSpecialty(null)).isNull();
    }

    @Test
    void toSpecialtyDtoMapsIdAndName() {
        SpecialtyDto dto = specialtyMapper.toSpecialtyDto(newSpecialty(2, "surgery"));

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    @Test
    void toSpecialtyDtoReturnsNullForNull() {
        assertThat(specialtyMapper.toSpecialtyDto(null)).isNull();
    }

    @Test
    void toSpecialtyDtosMapsEachSpecialtyInOrder() {
        Collection<SpecialtyDto> dtos = specialtyMapper.toSpecialtyDtos(
            List.of(newSpecialty(1, "radiology"), newSpecialty(3, "dentistry")));

        assertThat(dtos).extracting(SpecialtyDto::getId).containsExactly(1, 3);
        assertThat(dtos).extracting(SpecialtyDto::getName).containsExactly("radiology", "dentistry");
    }

    @Test
    void toSpecialtyDtosHandlesEmptyAndNull() {
        assertThat(specialtyMapper.toSpecialtyDtos(List.of())).isEmpty();
        assertThat(specialtyMapper.toSpecialtyDtos(null)).isNull();
    }

    @Test
    void toSpecialtysMapsEachDtoInOrder() {
        Collection<Specialty> specialties = specialtyMapper.toSpecialtys(
            List.of(newSpecialtyDto(2, "surgery"), newSpecialtyDto(3, "dentistry")));

        assertThat(specialties).extracting(Specialty::getId).containsExactly(2, 3);
        assertThat(specialties).extracting(Specialty::getName).containsExactly("surgery", "dentistry");
    }

    @Test
    void toSpecialtysHandlesEmptyAndNull() {
        assertThat(specialtyMapper.toSpecialtys(List.of())).isEmpty();
        assertThat(specialtyMapper.toSpecialtys(null)).isNull();
    }

    private static Specialty newSpecialty(int id, String name) {
        Specialty specialty = new Specialty();
        specialty.setId(id);
        specialty.setName(name);
        return specialty;
    }

    private static SpecialtyDto newSpecialtyDto(int id, String name) {
        SpecialtyDto dto = new SpecialtyDto();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }
}
