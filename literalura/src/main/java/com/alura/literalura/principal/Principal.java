package com.alura.literalura.principal;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepositorio;
    private AutorRepository autorRepositorio;
    private List<Libro> libros;

    @Autowired
    public Principal(LibroRepository libroRepositorio, AutorRepository autorRepositorio) {
        this.libroRepositorio = libroRepositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    Por favor, elija una opción para continuar
                    1 - Buscar libros
                    2 - Mostrar todos los libros buscados
                    3 - Buscar autores vivos en determinado año
                    4 - Buscar libros por idioma
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    mostrarLibrosBuscados();
                    break;
                case 3:
                    mostrarAutoresVivosEnAnio();
                    break;
                case 4:
                    mostrarLibrosIdioma();
                    break;
                case 0:
                    System.out.println("Gracias por usar nuestra aplicación.");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private DatosLibros getDatosLibro() {
        System.out.println("Por favor, escriba el nombre del libro.");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos("https://gutendex.com//books/?search=" + nombreLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()) {
            System.out.println("Libro Encontrado ");
            return libroBuscado.get();
        } else {
            System.out.println("Libro no encontrado");
            System.out.println(json);
            return null;
        }
    }

    private void buscarLibro() {
        DatosLibros datos = getDatosLibro();
        if (datos != null) {
            Autor autor = null;
            if (datos.autor() != null && !datos.autor().isEmpty()) {
                DatosAutor datosAutor = datos.autor().get(0);
                Optional<Autor> autorExistente = autorRepositorio.findByNombre(datosAutor.nombre());
                if (autorExistente.isPresent()) {
                    autor = autorExistente.get();
                } else {
                    autor = new Autor(datosAutor);
                    autorRepositorio.save(autor);
                }
            }

            Libro libro = new Libro(datos);
            libro.setAutor(autor);
            autor.addLibro(libro);
            libroRepositorio.save(libro);
            System.out.println(datos);
        }
    }

    private void mostrarLibrosBuscados() {
        libros = libroRepositorio.findAll();

        libros.forEach(System.out::println);
    }

    private void mostrarAutoresVivosEnAnio() {
        System.out.println("por favor, indique el año en que desea verificar si su autor estaba vivo");
        var fecha = teclado.nextInt();
        teclado.nextLine();
        List<Autor> autoresVivos =autorRepositorio.autorEstaVivo(fecha);
        if (autoresVivos != null) {
            autoresVivos.forEach(System.out::println);
        } else {
            System.out.println("No había autores vivos en esa fecha.");
        }
    }

    private void mostrarLibrosIdioma() {
        var opcionesIdioma = """
                Por favor, elija un idioma
                1 - Español
                2 - Ingles
                3 - Frances
                """;
        System.out.println(opcionesIdioma);
        var idiomaElegido = teclado.nextInt();
        teclado.nextLine();
        switch (idiomaElegido) {
            case 1:
                List<Libro> librosEnIdiomaEs = libroRepositorio.findByIdiomas("es");
                if (librosEnIdiomaEs != null) {
                    librosEnIdiomaEs.forEach(System.out::println);
                } else {
                    System.out.println("Libro no encontrado.");
                }
                break;
            case 2:
                List<Libro> librosEnIdiomaEn = libroRepositorio.findByIdiomas("en");
                if (librosEnIdiomaEn != null) {
                    librosEnIdiomaEn.forEach(System.out::println);
                } else {
                    System.out.println("Libro no encontrado.");
                }
                break;
            case 3:
                List<Libro> librosEnIdiomaFr = libroRepositorio.findByIdiomas("fr");
                if (librosEnIdiomaFr != null) {
                    librosEnIdiomaFr.forEach(System.out::println);
                } else {
                    System.out.println("Libro no encontrado.");
                }
                break;
            default:
                System.out.println("opción inválida.");
        }
    }
}
