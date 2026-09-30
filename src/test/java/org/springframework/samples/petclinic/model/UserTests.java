package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class UserTests {

    @Test
    void shouldExposeCredentialsAndEnabledFlag() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("secret");
        user.setEnabled(Boolean.TRUE);

        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getPassword()).isEqualTo("secret");
        assertThat(user.getEnabled()).isTrue();
    }

    @Test
    void shouldHaveNullRolesByDefault() {
        assertThat(new User().getRoles()).isNull();
    }

    @Test
    void shouldCreateRoleSetWhenAddingFirstRole() {
        User user = new User();

        user.addRole("ROLE_ADMIN");

        assertThat(user.getRoles())
            .singleElement()
            .satisfies(role -> {
                assertThat(role.getName()).isEqualTo("ROLE_ADMIN");
                assertThat(role.getUser()).isNull();
            });
    }

    @Test
    void shouldAppendToExistingRoles() {
        Role existing = new Role();
        existing.setName("ROLE_OWNER_ADMIN");
        Set<Role> roles = new HashSet<>();
        roles.add(existing);

        User user = new User();
        user.setRoles(roles);
        user.addRole("ROLE_VET_ADMIN");

        assertThat(user.getRoles()).isSameAs(roles);
        assertThat(user.getRoles())
            .extracting(Role::getName)
            .containsExactlyInAnyOrder("ROLE_OWNER_ADMIN", "ROLE_VET_ADMIN");
    }
}
