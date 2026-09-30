package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RoleTests {

    @Test
    void shouldExposeNameAndUser() {
        User user = new User();
        user.setUsername("admin");

        Role role = new Role();
        role.setId(5);
        role.setName("ROLE_ADMIN");
        role.setUser(user);

        assertThat(role.getId()).isEqualTo(5);
        assertThat(role.getName()).isEqualTo("ROLE_ADMIN");
        assertThat(role.getUser()).isSameAs(user);
        assertThat(role.isNew()).isFalse();
    }
}
