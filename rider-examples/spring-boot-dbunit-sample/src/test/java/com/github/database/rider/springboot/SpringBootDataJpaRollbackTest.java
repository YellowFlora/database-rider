package com.github.database.rider.springboot;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.configuration.DataSetConfig;
import com.github.database.rider.core.configuration.ExpectedDataSetConfig;
import com.github.database.rider.junit5.api.DBRider;
import com.github.database.rider.springboot.model.user.UserRepository;
import org.dbunit.DatabaseUnitException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.transaction.AfterTransaction;

import javax.sql.DataSource;
import java.sql.SQLException;

import static com.github.database.rider.core.dsl.RiderDSL.withConnection;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@DBRider
public class SpringBootDataJpaRollbackTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DataSource dataSource;

    @Test
    @DataSet("users.yml")
    public void shouldRollbackChangesMadeInsideDataJpaTestTransaction() {
        assertThat(userRepository).isNotNull();
        assertThat(userRepository.count()).isEqualTo(3);
        userRepository.findById(3L).ifPresent(userRepository::delete);
        assertThat(userRepository.count()).isEqualTo(2);
    }

    @AfterTransaction
    void afterTransaction() throws SQLException, DatabaseUnitException {
        withConnection(dataSource.getConnection())
                .withDataSetConfig(new DataSetConfig("users.yml"))
                .expectDataSet(new ExpectedDataSetConfig());
    }
}
