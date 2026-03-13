package com.github.database.rider.springboot;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.github.database.rider.springboot.model.user.User;
import com.github.database.rider.springboot.model.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pestano on 13/09/16.
 * Declare @SpringBootTest first to ensure Spring context loads prior to DBRider extension execution.
 */
@SpringBootTest
@DBRider
@DBUnit(cacheConnection = false, leakHunter = true)
public class SpringBootDBUnitTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DataSet("users.yml")
    public void shouldListUsers() {
        assertThat(userRepository).isNotNull();
        assertThat(userRepository.count()).isEqualTo(3);
        assertThat(userRepository.findByEmail("springboot@gmail.com")).isEqualTo(new User(3));
    }
}
