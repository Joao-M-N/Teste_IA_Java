package br.com.patternsshop.service;

import br.com.patternsshop.model.Pedido;
import br.com.patternsshop.model.ResumoPedido;
import br.com.patternsshop.shipping.FreteFactory;
import br.com.patternsshop.validation.CepValido;
import br.com.patternsshop.validation.ItensObrigatorios;
import br.com.patternsshop.validation.LimitePorProduto;
import br.com.patternsshop.validation.ValidacaoPedido;
import java.math.BigDecimal;
import java.util.Objects;

/** Facade: oferece uma operação simples para coordenar os subsistemas. */
public final class CheckoutFacade {
    private final ValidacaoPedido validacoes;
    private final FreteFactory fretes;

    public CheckoutFacade() {
        this(new ItensObrigatorios(new CepValido(new LimitePorProduto(null))), new FreteFactory());
    }

    public CheckoutFacade(ValidacaoPedido validacoes, FreteFactory fretes) {
        this.validacoes = Objects.requireNonNull(validacoes);
        this.fretes = Objects.requireNonNull(fretes);
    }

    public ResumoPedido simular(Pedido pedido) {
        Objects.requireNonNull(pedido, "Pedido obrigatório");
        validacoes.validar(pedido);
        BigDecimal subtotal = pedido.subtotal();
        BigDecimal frete = fretes.criar(pedido.modalidade()).calcular(pedido);
        return new ResumoPedido(subtotal, frete, subtotal.add(frete), pedido.modalidade());
    }
}
