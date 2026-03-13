package com.github.database.rider.springboot.model.company;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CompanyRepository extends CrudRepository<Company, Long> {

    /**
     * @param name the company name.
     */
    Company findByName(String name);

    Company findByNameLike(String umbrella);
}
