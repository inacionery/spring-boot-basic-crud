/**
 * 
 */
package com.inacionery.basic.service.impl;

import com.inacionery.basic.domain.Person;
import com.inacionery.basic.repository.PersonRepository;
import com.inacionery.basic.service.PersonService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Inácio Nery
 */
@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public void delete(Long id) {
        personRepository.deleteById(id);
    }
}
