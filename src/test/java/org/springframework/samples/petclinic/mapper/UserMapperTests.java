package org.springframework.samples.petclinic.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.samples.petclinic.model.Role;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.rest.dto.RoleDto;
import org.springframework.samples.petclinic.rest.dto.UserDto;

class UserMapperTests {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toRoleMapsNameAndLeavesIdAndUserUnset() {
        Role role = userMapper.toRole(newRoleDto("OWNER_ADMIN"));

        assertThat(role.getName()).isEqualTo("OWNER_ADMIN");
        assertThat(role.getId()).isNull();
        assertThat(role.getUser()).isNull();
    }

    @Test
    void toRoleReturnsNullForNull() {
        assertThat(userMapper.toRole(null)).isNull();
    }

    @Test
    void toRoleDtoMapsName() {
        assertThat(userMapper.toRoleDto(newRole("VET_ADMIN")).getName()).isEqualTo("VET_ADMIN");
    }

    @Test
    void toRoleDtoReturnsNullForNull() {
        assertThat(userMapper.toRoleDto(null)).isNull();
    }

    @Test
    void toRoleDtosMapsEachRoleInOrder() {
        Collection<RoleDto> dtos = userMapper.toRoleDtos(List.of(newRole("ADMIN"), newRole("VET_ADMIN")));

        assertThat(dtos).extracting(RoleDto::getName).containsExactly("ADMIN", "VET_ADMIN");
    }

    @Test
    void toRoleDtosHandlesEmptyAndNull() {
        assertThat(userMapper.toRoleDtos(List.of())).isEmpty();
        assertThat(userMapper.toRoleDtos(null)).isNull();
    }

    @Test
    void toRolesMapsEachDtoInOrder() {
        Collection<Role> roles = userMapper.toRoles(List.of(newRoleDto("ADMIN"), newRoleDto("OWNER_ADMIN")));

        assertThat(roles).extracting(Role::getName).containsExactly("ADMIN", "OWNER_ADMIN");
    }

    @Test
    void toRolesHandlesEmptyAndNull() {
        assertThat(userMapper.toRoles(List.of())).isEmpty();
        assertThat(userMapper.toRoles(null)).isNull();
    }

    @Test
    void toUserMapsAllFieldsAndPreservesRoleOrder() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setPassword("secret");
        dto.setEnabled(Boolean.TRUE);
        dto.setRoles(List.of(newRoleDto("OWNER_ADMIN"), newRoleDto("VET_ADMIN"), newRoleDto("ADMIN")));

        User user = userMapper.toUser(dto);

        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getPassword()).isEqualTo("secret");
        assertThat(user.getEnabled()).isTrue();
        assertThat(user.getRoles()).extracting(Role::getName)
            .containsExactly("OWNER_ADMIN", "VET_ADMIN", "ADMIN");
    }

    @Test
    void toUserKeepsNullEnabledAndNullRoles() {
        UserDto dto = new UserDto();
        dto.setUsername("nobody");
        dto.setRoles(null);

        User user = userMapper.toUser(dto);

        assertThat(user.getEnabled()).isNull();
        assertThat(user.getRoles()).isNull();
    }

    @Test
    void toUserMapsEmptyRolesToEmptySet() {
        UserDto dto = new UserDto();
        dto.setUsername("empty");
        dto.setRoles(new ArrayList<>());

        assertThat(userMapper.toUser(dto).getRoles()).isEmpty();
    }

    @Test
    void toUserReturnsNullForNull() {
        assertThat(userMapper.toUser(null)).isNull();
    }

    @Test
    void toUserDtoMapsAllFieldsAndRoles() {
        User user = new User();
        user.setUsername("vet");
        user.setPassword("pwd");
        user.setEnabled(Boolean.FALSE);
        Set<Role> roles = new LinkedHashSet<>();
        roles.add(newRole("VET_ADMIN"));
        roles.add(newRole("ADMIN"));
        user.setRoles(roles);

        UserDto dto = userMapper.toUserDto(user);

        assertThat(dto.getUsername()).isEqualTo("vet");
        assertThat(dto.getPassword()).isEqualTo("pwd");
        assertThat(dto.getEnabled()).isFalse();
        assertThat(dto.getRoles()).extracting(RoleDto::getName).containsExactly("VET_ADMIN", "ADMIN");
    }

    @Test
    void toUserDtoKeepsNullRoles() {
        User user = new User();
        user.setUsername("noroles");

        UserDto dto = userMapper.toUserDto(user);

        assertThat(dto.getRoles()).isNull();
        assertThat(dto.getEnabled()).isNull();
    }

    @Test
    void toUserDtoReturnsNullForNull() {
        assertThat(userMapper.toUserDto(null)).isNull();
    }

    private static Role newRole(String name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }

    private static RoleDto newRoleDto(String name) {
        RoleDto dto = new RoleDto();
        dto.setName(name);
        return dto;
    }
}
