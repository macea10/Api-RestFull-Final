package com.henrry.Api_crud.Service;

import com.henrry.Api_crud.Models.Pedido;
import com.henrry.Api_crud.Models.Producto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ProductoService {

    final List<Producto> productos = new ArrayList<>();

    public List<Producto> ProductosList(Locale locale) {

        switch (locale.getLanguage()) {
            case "es" -> {
                productos.add(new Producto(1L, "iPhone 14", 999.99));
                productos.add(new Producto(2L, "Samsung TV", 1299.99));
                productos.add(new Producto(3L, "Nike Air Max", 79.99));
            }
            case "fr" -> {
                productos.add(new Producto(1L, "iPhone 14", 999.99));
                productos.add(new Producto(2L, "Téléviseur Samsung", 1299.99));
                productos.add(new Producto(3L, "Nike Air Max", 79.99));
            }
            case "en" -> {
                productos.add(new Producto(1L, "iPhone 14", 999.99));
                productos.add(new Producto(2L, "Samsung TV", 1299.99));
                productos.add(new Producto(3L, "Nike Air Max", 79.99));
            }
        }

        return productos;
    }

    /***
     * agregar un producto
     *
     * @param producto
     * @return
     */
    public Mono<Producto> agregarProducto(Producto producto,Locale locale) {
        producto.setId((long) (ProductosList(locale).size() + 1)); // Simulamos un ID autoincrementable
        ProductosList(locale).add(producto);
        return Mono.just(producto);
    }

    /**
     * listar todo los productos
     *
     * @return
     */
    public Flux<Producto> listarProductos(Locale locale) {
        return Flux.fromIterable(ProductosList(locale));
    }

    /**
     * metodo para obtener un producto por id
     *
     * @param id
     * @return
     */

    public Mono<Producto> obtenerProductoPorId(Long id,Locale locale) {
        return Mono.justOrEmpty(
                ProductosList(locale).stream()
                        .filter(p -> p.getId().equals(id))
                        .findFirst()
        );
    }

    /**
     * Metodo para actualizar el prodcuto
     * Se utiliza Optional para manejar el caso en el que un producto no se encuentra.
     *
     * @param producto
     * @return
     */
    public Mono<Producto> actualizarProducto(Producto producto,Locale locale) {
        Mono<Producto> productoExistente = obtenerProductoPorId(producto.getId(),locale);
        if (productoExistente.blockOptional().isPresent()) {
            Mono<Producto> p = productoExistente;
            p.block().setNombre(producto.getNombre());
            p.block().setPrecio(producto.getPrecio());
            return p;
        } else {
            return null;
        }
    }

    /**
     * METODO PARA ELIMINAR EL PRODUCTO
     *
     * @param id
     */
    public void eliminarProducto(Long id,Locale locale) {
        ProductosList(locale).removeIf(p -> p.getId().equals(id));
    }
}

