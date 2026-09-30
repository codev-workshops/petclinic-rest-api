package org.springframework.samples.petclinic.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeFieldsDto;

class PetTypeMapperTests {

    private final PetTypeMapper petTypeMapper = Mappers.getMapper(PetTypeMapper.class);

    @Test
    void toPetTypeFromDtoMapsIdAndName() {
        PetTypeDto dto = new PetTypeDto();
        dto.setId(3);
        dto.setName("lizard");

        PetType petType = petTypeMapper.toPetType(dto);

        assertThat(petType.getId()).isEqualTo(3);
        assertThat(petType.getName()).isEqualTo("lizard");
    }

    @Test
    void toPetTypeFromDtoReturnsNullForNull() {
        assertThat(petTypeMapper.toPetType((PetTypeDto) null)).isNull();
    }

    @Test
    void toPetTypeFromFieldsDtoMapsNameAndLeavesIdUnset() {
        PetTypeFieldsDto fields = new PetTypeFieldsDto();
        fields.setName("snake");

        PetType petType = petTypeMapper.toPetType(fields);

        assertThat(petType.getId()).isNull();
        assertThat(petType.isNew()).isTrue();
        assertThat(petType.getName()).isEqualTo("snake");
    }

    @Test
    void toPetTypeFromFieldsDtoReturnsNullForNull() {
        assertThat(petTypeMapper.toPetType((PetTypeFieldsDto) null)).isNull();
    }

    @Test
    void toPetTypeDtoMapsIdAndName() {
        PetTypeDto dto = petTypeMapper.toPetTypeDto(newPetType(1, "cat"));

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("cat");
    }

    @Test
    void toPetTypeDtoReturnsNullForNull() {
        assertThat(petTypeMapper.toPetTypeDto(null)).isNull();
    }

    @Test
    void toPetTypeFieldsDtoMapsName() {
        PetTypeFieldsDto fields = petTypeMapper.toPetTypeFieldsDto(newPetType(2, "dog"));

        assertThat(fields.getName()).isEqualTo("dog");
    }

    @Test
    void toPetTypeFieldsDtoReturnsNullForNull() {
        assertThat(petTypeMapper.toPetTypeFieldsDto(null)).isNull();
    }

    @Test
    void toPetTypeDtosMapsEachPetTypeInOrder() {
        List<PetTypeDto> dtos = petTypeMapper.toPetTypeDtos(
            List.of(newPetType(1, "cat"), newPetType(2, "dog")));

        assertThat(dtos).extracting(PetTypeDto::getId).containsExactly(1, 2);
        assertThat(dtos).extracting(PetTypeDto::getName).containsExactly("cat", "dog");
    }

    @Test
    void toPetTypeDtosHandlesEmptyAndNull() {
        assertThat(petTypeMapper.toPetTypeDtos(List.of())).isEmpty();
        assertThat(petTypeMapper.toPetTypeDtos(null)).isNull();
    }

    private static PetType newPetType(int id, String name) {
        PetType petType = new PetType();
        petType.setId(id);
        petType.setName(name);
        return petType;
    }
}
