package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;



public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombre(String nombre);

    @Query(value = "SELECT * FROM autor WHERE fecha_de_nacimiento < :fechaDeEstarVivo AND fecha_de_fallecimiento < :fechaDeEstarVivo", nativeQuery = true)
    List<Autor> autorEstaVivo(int fechaDeEstarVivo);
}
