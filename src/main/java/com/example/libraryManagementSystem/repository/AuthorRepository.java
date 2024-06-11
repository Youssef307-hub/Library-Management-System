package com.example.libraryManagementSystem.repository;

import com.example.libraryManagementSystem.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    boolean existsByNameAndBirthDateAndNationality(String name, String birthDate, String nationality);

    Optional<Author> findByNameAndBirthDateAndNationality(String name, String birthDate, String nationality);

}
