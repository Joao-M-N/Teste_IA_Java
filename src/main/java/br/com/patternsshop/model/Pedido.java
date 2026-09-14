package br.com.patternsshop.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record Pedido(List<ItemPedido> itens, String cep, ModalidadeFrete modalidade) {
    public Pedido {
        itens = List.copyOf(Objects.requireNonNull(itens, "Itens obrigatórios"));
        Objects.requireNonNull(cep, "CEP obrigatório");
        Objects.requireNonNull(modalidade, "Modalidade obrigatória");
    }

    public BigDecimal subtotal() {
        return itens.stream().map(ItemPedido::subtotal)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    public long quantidadeTotal() {
        return itens.stream().mapToLong(ItemPedido::quantidade).sum();
    }
}
