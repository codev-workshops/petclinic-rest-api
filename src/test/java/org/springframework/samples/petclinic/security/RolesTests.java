package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RolesTests {

    @Test
    void roleNamesIncludeRolePrefix() {
        Roles roles = new Roles();

        assertThat(roles.OWNER_ADMIN).isEqualTo("ROLE_OWNER_ADMIN");
        assertThat(roles.VET_ADMIN).isEqualTo("ROLE_VET_ADMIN");
        assertThat(roles.ADMIN).isEqualTo("ROLE_ADMIN");
    }
}
