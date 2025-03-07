package com.henrry.Api_crud.Controllers;

import com.henrry.Api_crud.Models.Producto;
import com.henrry.Api_crud.Service.ProductoService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    /**
     * Fuente de mensajes internacionalizados.
     * Esta inyección permite acceder a mensajes traducidos según el locale del usuario.
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * Método para agregar un nuevo producto.
     * Recibe un objeto Producto en el cuerpo de la solicitud y lo guarda en la base de datos.
     *
     * @param producto El objeto Producto que se desea agregar.
     * @return ResponseEntity con el producto creado y el código de estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<Mono<Producto>> agregarProducto(@RequestHeader(name = "Accept-Language", required = false) String acceptLanguage,@RequestBody @NotNull Producto producto) {
        Locale locale = parseLocale(acceptLanguage);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.agregarProducto(producto,locale));
    }

    /**
     * Método para listar todos los productos.
     * Retorna una lista de todos los productos disponibles en la base de datos.
     *
     * @return ResponseEntity con un Flux de Productos y el código de estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<Flux<Producto>> listarProductos(@RequestHeader(name = "Accept-Language", required = false) String acceptLanguage) {
        Locale locale = parseLocale(acceptLanguage);
        return ResponseEntity.ok(productoService.listarProductos(locale));
    }

    /**
     * Método para obtener un producto por su ID.
     * Busca un producto en la base de datos utilizando el ID proporcionado en la URL.
     *
     * @param id El ID del producto que se desea obtener.
     * @return ResponseEntity con el producto encontrado y el código de estado HTTP 200 (OK),
     *         o un código de estado HTTP 404 (NOT FOUND) si el producto no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mono<Producto>> obtenerProductoPorId(@RequestHeader(name = "Accept-Language", required = false) String acceptLanguage,@PathVariable @NotNull Long id) {
        Locale locale = parseLocale(acceptLanguage);
        Mono<Producto> producto = productoService.obtenerProductoPorId(id,locale);
        return ResponseEntity.ok(producto);
    }

    /**
     * Método para actualizar un producto existente.
     * Recibe un objeto Producto en el cuerpo de la solicitud y lo actualiza en la base de datos.
     * El ID del producto debe coincidir con el ID proporcionado en la URL.
     *
     * @param id El ID del producto que se desea actualizar.
     * @param producto El objeto Producto con los nuevos datos.
     * @return ResponseEntity con el producto actualizado y el código de estado HTTP 200 (OK),
     *         o un código de estado HTTP 404 (NOT FOUND) si el producto no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mono<Producto>> actualizarProducto(@RequestHeader(name = "Accept-Language", required = false) String acceptLanguage,@PathVariable @NotNull Long id, @RequestBody @NotNull Producto producto) {
        Locale locale = parseLocale(acceptLanguage);
        producto.setId(id); // Asegurarse de que el ID coincida con el del path
        Mono<Producto> productoActualizado = productoService.actualizarProducto(producto,locale);
        return productoActualizado != null
                ? ResponseEntity.ok(productoActualizado)
                : ResponseEntity.notFound().build();
    }

    /**
     * Método para eliminar un producto por su ID.
     * Elimina un producto de la base de datos utilizando el ID proporcionado en la URL.
     *
     * @param id El ID del producto que se desea eliminar.
     * @return ResponseEntity con el código de estado HTTP 204 (NO CONTENT) si la eliminación fue exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@RequestHeader(name = "Accept-Language", required = false) String acceptLanguage,@PathVariable Long id) {
        Locale locale = parseLocale(acceptLanguage);
        productoService.eliminarProducto(id,locale);
        return ResponseEntity.noContent().build();
    }

    /**
     * Convierte una cadena de locale en un objeto Locale.
     * Si la cadena es vacía o nula, se utiliza el locale por defecto del sistema.
     *
     * @param acceptLanguage Cadena de locale proporcionada en el encabezado 'Accept-Language'.
     * @return Objeto Locale correspondiente a la cadena proporcionada.
     */
    private Locale parseLocale(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isEmpty()) {
            return Locale.getDefault();
        }
        // Tomar solo la primera parte antes de la coma
        // Ej: Locale part "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3"
        String primaryLocale = acceptLanguage.split(",")[0].trim();
        try {
            return Locale.forLanguageTag(primaryLocale);
        } catch (IllegalArgumentException e) {
            // Manejar el caso donde el tag de locale no es válido
            return Locale.getDefault();
        }
    }
}
