package br.com.patternsshop.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record ItemPedido(String sku, String nome, BigDecimal precoUnitario, int quantidade) {
    public ItemPedido {
        Objects.requireNonNull(sku, "SKU obrigatório");
        Objects.requireNonNull(nome, "Nome obrigatório");
        Objects.requireNonNull(precoUnitario, "Preço obrigatório");
        if (sku.isBlank() || nome.isBlank()) {
            throw new IllegalArgumentException("SKU e nome não podem estar vazios");
        }
        if (precoUnitario.signum() <= 0 || quantidade <= 0) {
            throw new IllegalArgumentException("Preço e quantidade devem ser positivos");
        }
        // Não arredondamos silenciosamente um preço informado com fração de centavo.
        precoUnitario = precoUnitario.setScale(2, RoundingMode.UNNECESSARY);
    }

    public BigDecimal subtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
