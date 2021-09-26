package com.smart.repository;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.smart.entity.Contact;
import com.smart.entity.User;
@EnableJpaRepositories
public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	//pagination
	/*
	 * @Query("from Contact as c where c.users.id=:usersId") public List<Contact>
	 * findContactsByUser(@Param("usersId")int usersId);
	 */
	@Query("from Contact as c where c.users.id=:usersId")
	public Page<Contact> findContactsByUser(@Param("usersId")int usersId,Pageable pageable);
    //search
	public List<Contact> findByNameContainingAndUsers(String name,User user);
}
