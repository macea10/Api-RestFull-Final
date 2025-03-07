package com.henrry.Api_crud.Controllers;

import com.henrry.Api_crud.Models.Pedido;
import com.henrry.Api_crud.Service.PedidoService;
import com.henrry.Api_crud.Service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Locale;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * Fuente de mensajes internacionalizados.
     * Esta inyección permite acceder a mensajes traducidos según el locale del usuario.
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * Método para agregar un nuevo Pedido.
     * Recibe un objeto Pedido en el cuerpo de la solicitud y lo guarda en la base de datos.
     *
     * @param Pedido El objeto Pedido que se desea agregar.
     * @return ResponseEntity con el Pedido creado y el código de estado HTTP 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<Mono<Pedido>> agregarPedido(@RequestHeader(name = "Accept-Language", required = false) String acceptLanguage,@RequestBody Pedido Pedido) {
        Locale locale = parseLocale(acceptLanguage);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.agregarPedido(Pedido,locale));
    }


    /**
     * Método para listar todos los Pedidos según el idioma solicitado.
     * Retorna una lista de todos los Pedidos disponibles en la base de datos filtrados por idioma.
     *
     * @param acceptLanguage El encabezado 'Accept-Language' que indica el idioma solicitado.
     * @return ResponseEntity con un Flux de Pedidos y el código de estado HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<Flux<Pedido>> listarPedidos(
            @RequestHeader(name = "Accept-Language", required = false) String acceptLanguage) {
        Locale locale = parseLocale(acceptLanguage); // Obtener el Locale del encabezado
        Flux<Pedido> pedidos = pedidoService.listarPedidos(locale);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Método para obtener un Pedido por su ID.
     * Busca un Pedido en la base de datos utilizando el ID proporcionado en la URL.
     *
     * @param id El ID del Pedido que se desea obtener.
     * @return ResponseEntity con el Pedido encontrado y el código de estado HTTP 200 (OK),
     *         o un código de estado HTTP 404 (NOT FOUND) si el Pedido no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mono<Pedido>> obtenerPedidoPorId(@RequestHeader(name = "Accept-Language", required = false) String acceptLanguage,@PathVariable Long id) {
        Locale locale = parseLocale(acceptLanguage);
        Mono<Pedido> Pedido = pedidoService.obtenerPedidoPorId(id,locale);
        return ResponseEntity.ok(Pedido);
    }

    /**
     * Método para actualizar un Pedido existente.
     * Recibe un objeto Pedido en el cuerpo de la solicitud y lo actualiza en la base de datos.
     * El ID del Pedido debe coincidir con el ID proporcionado en la URL.
     *
     * @param id El ID del Pedido que se desea actualizar.
     * @param Pedido El objeto Pedido con los nuevos datos.
     * @return ResponseEntity con el Pedido actualizado y el código de estado HTTP 200 (OK),
     *         o un código de estado HTTP 404 (NOT FOUND) si el Pedido no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mono<Pedido>> actualizarPedido(@RequestHeader(name = "Accept-Language", required = false) String acceptLanguage,@PathVariable Long id, @RequestBody Pedido Pedido) {
        Locale locale = parseLocale(acceptLanguage);
        Pedido.setId(id); // Asegurarse de que el ID coincida con el del path
        Mono<Pedido> PedidoActualizado = pedidoService.actualizarPedido(Pedido,locale);
        return PedidoActualizado != null
                ? ResponseEntity.ok(PedidoActualizado)
                : ResponseEntity.notFound().build();
    }

    /**
     * Método para eliminar un Pedido por su ID.
     * Elimina un Pedido de la base de datos utilizando el ID proporcionado en la URL.
     *
     * @param id El ID del Pedido que se desea eliminar.
     * @return ResponseEntity con el código de estado HTTP 204 (NO CONTENT) si la eliminación fue exitosa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@RequestHeader(name = "Accept-Language", required = false) String acceptLanguage,@PathVariable Long id) {
        Locale locale = parseLocale(acceptLanguage);
        pedidoService.eliminarPedido(id,locale);
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
