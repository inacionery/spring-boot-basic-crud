/**
 * 
 */
package com.inacionery.basic.service;

import com.inacionery.basic.domain.Person;

import java.util.List;
import java.util.Optional;

/**
 * @author Inácio Nery
 */
public interface PersonService {

    public Person save(Person person);

    public List<Person> findAll();

    public Optional<Person> findById(Long id);

    public void delete(Long id);

}
