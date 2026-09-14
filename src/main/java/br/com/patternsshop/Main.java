package br.com.patternsshop;

import br.com.patternsshop.model.*;
import br.com.patternsshop.service.CheckoutFacade;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public final class Main {
    public static void main(String[] args) {
        CheckoutFacade checkout = new CheckoutFacade();
        NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        List<ItemPedido> itens = List.of(
                new ItemPedido("LIVRO-01", "Livro de Java", new BigDecimal("79.90"), 2),
                new ItemPedido("CADERNO-01", "Caderno", new BigDecimal("25.00"), 1));

        System.out.println("PATTERNS SHOP | Simulação de checkout\n");
        for (ModalidadeFrete modalidade : ModalidadeFrete.values()) {
            ResumoPedido resumo = checkout.simular(new Pedido(itens, "01001-000", modalidade));
            System.out.printf("%s | Produtos: %s | Frete: %s | Total: %s%n", modalidade,
                    moeda.format(resumo.subtotal()), moeda.format(resumo.frete()), moeda.format(resumo.total()));
        }

        try {
            checkout.simular(new Pedido(itens, "123", ModalidadeFrete.NORMAL));
        } catch (IllegalArgumentException erro) {
            System.out.println("\nPedido recusado: " + erro.getMessage());
        }
    }
}
