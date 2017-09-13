/**
 * 
 */
package com.inacionery.basic.repository;

import com.inacionery.basic.domain.Person;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Inácio Nery
 */
public interface PersonRepository extends JpaRepository<Person, Long> {

}
