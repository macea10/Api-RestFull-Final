package com.henrry.Api_crud.Service;

import com.henrry.Api_crud.Models.Pedido;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Service
public class PedidoService {



        // Inicializamos con algunos datos para pruebas
        public List<Pedido> pedidoList(Locale locale) {
            final List<Pedido> pedidos = new ArrayList<>();
            if (locale.getLanguage().equals("es")) {
                pedidos.add(new Pedido(1L, "Cliente 1", "Producto 1", 2, calcularPrecioPorIdioma(99.99,locale.toLanguageTag())));
                pedidos.add(new Pedido(2L, "Cliente 2", "Producto 2", 1, calcularPrecioPorIdioma(199.99,locale.toLanguageTag())));
                pedidos.add(new Pedido(3L, "Cliente 3", "Producto 3", 3, calcularPrecioPorIdioma(49.99,locale.toLanguageTag())));
            } else if (locale.getLanguage().equals("fr")) {
                pedidos.add(new Pedido(1L, "Client 1", "Produit 1", 2, calcularPrecioPorIdioma(99.99,locale.toLanguageTag())));
                pedidos.add(new Pedido(2L, "Client 2", "Produit 2", 1, calcularPrecioPorIdioma(199.99,locale.toLanguageTag())));
                pedidos.add(new Pedido(3L, "Client 3", "Produit 3", 3, calcularPrecioPorIdioma(49.99,locale.toLanguageTag())));
            } else if (locale.getLanguage().equals("en")) {
                pedidos.add(new Pedido(1L, "Customer 1", "Product 1", 2, calcularPrecioPorIdioma(99.99,locale.toLanguageTag())));
                pedidos.add(new Pedido(2L, "Customer 2", "Product 2", 1, calcularPrecioPorIdioma(199.99,locale.toLanguageTag())));
                pedidos.add(new Pedido(3L, "Customer 3", "Product 3", 3, calcularPrecioPorIdioma(49.99,locale.toLanguageTag())));
            }
            return pedidos;
        }

        /***
         * Agregar un pedido
         *
         * @param pedido
         * @return
         */
        public Mono<Pedido> agregarPedido(Pedido pedido,Locale locale) {
            pedido.setId((long) (pedidoList(locale).size() + 1)); // Simulamos un ID autoincrementable
            pedidoList(locale).add(pedido);
            return Mono.just(pedido);
        }

        /**
         * Listar todos los pedidos
         *
         * @return
         */
        public Flux<Pedido> listarPedidos(Locale locale) {
            return Flux.fromIterable(pedidoList(locale));
        }

        /**
         * Método para obtener un pedido por id
         *
         * @param id
         * @return
         */
        public Mono<Pedido> obtenerPedidoPorId(Long id,Locale locale) {
            return Mono.justOrEmpty(
                    pedidoList(locale).stream()
                            .filter(p -> p.getId().equals(id))
                            .findFirst()
            );
        }

        /**
         * Método para actualizar un pedido
         * Se utiliza Optional para manejar el caso en el que un pedido no se encuentra.
         *
         * @param pedido
         * @return
         */
        public Mono<Pedido> actualizarPedido(Pedido pedido,Locale locale) {
            Mono<Pedido> pedidoExistente = obtenerPedidoPorId(pedido.getId(),locale);
            if (pedidoExistente.blockOptional().isPresent()) {
                Mono<Pedido> p = pedidoExistente;
                p.block().setCliente(pedido.getCliente());
                p.block().setProducto(pedido.getProducto());
                p.block().setCantidad(pedido.getCantidad());
                p.block().setPrecioTotal(pedido.getPrecioTotal());
                return p;
            } else {
                return null;
            }
        }

        /**
         * Método para eliminar un pedido
         *
         * @param id
         */
        public void eliminarPedido(Long id,Locale locale) {
            pedidoList(locale).removeIf(p -> p.getId().equals(id));
        }

    /**
     * Método para calcular el precio total de un pedido según el idioma.
     *
     * @param precioTotal El precio total del pedido.
     * @param idioma      El idioma en el que se desea calcular el precio.
     * @return El precio total ajustado según el idioma.
     */
    private double calcularPrecioPorIdioma(double precioTotal, String idioma) {
        switch (idioma.toLowerCase()) {
            case "es":
                // Precio en euros (sin conversión)
                return precioTotal;
            case "en":
                // Convertir a dólares (1 EUR = 1.18 USD)
                return precioTotal * 1.18;
            case "fr":
                // Convertir a francos suizos (1 EUR = 1.08 CHF)
                return precioTotal * 1.08;
            default:
                // Si el idioma no es válido, devolver el precio original
                return precioTotal;
        }
    }
}
