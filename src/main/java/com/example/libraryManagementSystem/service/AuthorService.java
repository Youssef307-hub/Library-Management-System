package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.exceptionhandling.DataAlreadyExistException;
import com.example.libraryManagementSystem.exceptionhandling.DataNotFoundException;
import com.example.libraryManagementSystem.model.Author;
import com.example.libraryManagementSystem.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository repository;

    @Cacheable("authors")
    public ResponseEntity<List<Author>> getAuthors(int pageNumber, int pageSize, String field){

        if(repository.findAll().isEmpty())
            throw new DataNotFoundException("No Authors Found!");

        if(pageNumber <= 0)
            pageNumber = 0;

        if (pageSize <=0)
            pageSize = 5;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(field));

        Page<Author> authorsPage = repository.findAll(pageable);

        return new ResponseEntity<>(authorsPage.getContent(), HttpStatus.OK);
    }

    @Cacheable("authors")
    public ResponseEntity<Author> getAuthorById(Long id){
        if(repository.findById(id).isEmpty())
            throw new DataNotFoundException("No Author With The ID: " + id + " Found!");

        return new ResponseEntity<>(repository.findById(id).get(), HttpStatus.OK);
    }

    @CacheEvict(value = "authors", allEntries = true)
    public ResponseEntity<Author> addAuthor(Author author){
        if (repository.existsByNameAndBirthDateAndNationality(author.getName(), author.getBirthDate(), author.getNationality()))
            throw new DataAlreadyExistException("This Author Already Exists!");

        Author newAuthor = Author
                .builder()
                .name(author.getName())
                .birthDate(author.getBirthDate())
                .nationality(author.getNationality())
                .build();

        return new ResponseEntity<>(repository.save(newAuthor), HttpStatus.OK);
    }

    @CacheEvict(value = "authors", allEntries = true)
    public ResponseEntity<Author> updateAuthor(Long id, Author author) {
        if(repository.findById(id).isEmpty())
            throw new DataNotFoundException("No Author With The ID: " + id + " Found!");

        Author updatedAuthor = repository.findById(id).get();
        updatedAuthor.setName(author.getName());
        updatedAuthor.setBirthDate(author.getBirthDate());
        updatedAuthor.setNationality(author.getNationality());

        return new ResponseEntity<>(repository.save(updatedAuthor), HttpStatus.OK);
    }

    @CacheEvict(value = "authors", allEntries = true)
    public ResponseEntity<String> deleteAuthor(Long id) {
        if(repository.findById(id).isEmpty())
            throw new DataNotFoundException("No Author With The ID: " + id + " Found!");

        repository.deleteById(id);

        return new ResponseEntity<>("Author With ID: " + id + " Deleted Successfully!", HttpStatus.OK);
    }

}
