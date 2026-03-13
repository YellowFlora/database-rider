package com.github.database.rider.springboot.model.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserRepository extends CrudRepository<User, Long> {

  /**
   *
   * @param email the user email.
   */
   User findByEmail(String email);

}
