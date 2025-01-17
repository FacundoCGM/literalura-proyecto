package com.alura.literalura.repository;

import com.alura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface LibroRepository extends JpaRepository<Libro,Long> {
    List<Libro> findByIdiomas(String idiomas);
}
