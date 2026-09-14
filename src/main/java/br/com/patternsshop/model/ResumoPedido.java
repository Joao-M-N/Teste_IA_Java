package br.com.patternsshop.model;

import java.math.BigDecimal;

public record ResumoPedido(BigDecimal subtotal, BigDecimal frete, BigDecimal total,
                           ModalidadeFrete modalidade) {
}
