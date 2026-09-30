package org.springframework.samples.petclinic.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.rest.dto.VetFieldsDto;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {VetMapperImpl.class, SpecialtyMapperImpl.class})
class VetMapperTests {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toVetFromDtoMapsAllFieldsAndSpecialties() {
        VetDto dto = new VetDto();
        dto.setId(3);
        dto.setFirstName("Linda");
        dto.setLastName("Douglas");
        dto.setSpecialties(List.of(newSpecialtyDto(2, "surgery"), newSpecialtyDto(3, "dentistry")));

        Vet vet = vetMapper.toVet(dto);

        assertThat(vet.getId()).isEqualTo(3);
        assertThat(vet.getFirstName()).isEqualTo("Linda");
        assertThat(vet.getLastName()).isEqualTo("Douglas");
        assertThat(vet.getSpecialties()).extracting(Specialty::getName)
            .containsExactly("dentistry", "surgery");
    }

    @Test
    void toVetFromDtoWithNullSpecialtiesThrowsBecauseVetRejectsNullList() {
        VetDto dto = new VetDto();
        dto.setFirstName("James");
        dto.setSpecialties(null);

        assertThatNullPointerException().isThrownBy(() -> vetMapper.toVet(dto));
    }

    @Test
    void toVetFromDtoReturnsNullForNull() {
        assertThat(vetMapper.toVet((VetDto) null)).isNull();
    }

    @Test
    void toVetFromFieldsDtoMapsFieldsAndLeavesIdUnset() {
        VetFieldsDto fields = new VetFieldsDto();
        fields.setFirstName("Helen");
        fields.setLastName("Leary");
        fields.setSpecialties(List.of(newSpecialtyDto(1, "radiology")));

        Vet vet = vetMapper.toVet(fields);

        assertThat(vet.getId()).isNull();
        assertThat(vet.isNew()).isTrue();
        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getLastName()).isEqualTo("Leary");
        assertThat(vet.getSpecialties()).singleElement().satisfies(specialty -> {
            assertThat(specialty.getId()).isEqualTo(1);
            assertThat(specialty.getName()).isEqualTo("radiology");
        });
    }

    @Test
    void toVetFromFieldsDtoWithEmptySpecialtiesHasNoSpecialties() {
        VetFieldsDto fields = new VetFieldsDto();
        fields.setFirstName("James");
        fields.setLastName("Carter");
        fields.setSpecialties(List.of());

        assertThat(vetMapper.toVet(fields).getNrOfSpecialties()).isZero();
    }

    @Test
    void toVetFromFieldsDtoWithNullSpecialtiesThrowsBecauseVetRejectsNullList() {
        VetFieldsDto fields = new VetFieldsDto();
        fields.setFirstName("James");
        fields.setSpecialties(null);

        assertThatNullPointerException().isThrownBy(() -> vetMapper.toVet(fields));
    }

    @Test
    void toVetFromFieldsDtoReturnsNullForNull() {
        assertThat(vetMapper.toVet((VetFieldsDto) null)).isNull();
    }

    @Test
    void toVetDtoMapsAllFieldsAndSortedSpecialties() {
        Vet vet = newVet(2, "Rafael");
        vet.addSpecialty(newSpecialty(2, "surgery"));
        vet.addSpecialty(newSpecialty(1, "radiology"));

        VetDto dto = vetMapper.toVetDto(vet);

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getFirstName()).isEqualTo("Rafael");
        assertThat(dto.getLastName()).isEqualTo("Ortega");
        assertThat(dto.getSpecialties()).extracting(SpecialtyDto::getName)
            .containsExactly("radiology", "surgery");
    }

    @Test
    void toVetDtoWithoutSpecialtiesHasEmptyList() {
        assertThat(vetMapper.toVetDto(newVet(1, "James")).getSpecialties()).isEmpty();
    }

    @Test
    void toVetDtoReturnsNullForNull() {
        assertThat(vetMapper.toVetDto(null)).isNull();
    }

    @Test
    void toVetDtosMapsEachVetInOrder() {
        Collection<VetDto> dtos = vetMapper.toVetDtos(List.of(newVet(1, "James"), newVet(2, "Helen")));

        assertThat(dtos).extracting(VetDto::getId).containsExactly(1, 2);
        assertThat(dtos).extracting(VetDto::getFirstName).containsExactly("James", "Helen");
    }

    @Test
    void toVetDtosHandlesEmptyAndNull() {
        assertThat(vetMapper.toVetDtos(List.of())).isEmpty();
        assertThat(vetMapper.toVetDtos(null)).isNull();
    }

    private static Vet newVet(int id, String firstName) {
        Vet vet = new Vet();
        vet.setId(id);
        vet.setFirstName(firstName);
        vet.setLastName("Ortega");
        return vet;
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
