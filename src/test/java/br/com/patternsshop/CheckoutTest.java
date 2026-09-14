package br.com.patternsshop;

import br.com.patternsshop.model.*;
import br.com.patternsshop.service.CheckoutFacade;
import br.com.patternsshop.shipping.*;
import br.com.patternsshop.validation.ValidacaoPedido;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Testes executáveis com o próprio JDK, sem bibliotecas externas. */
public final class CheckoutTest {
    private static final CheckoutFacade CHECKOUT = new CheckoutFacade();
    private static int executados;

    public static void main(String[] args) {
        testar("frete normal abaixo do limite", () -> {
            ResumoPedido resumo = CHECKOUT.simular(pedido("199.99", 1, ModalidadeFrete.NORMAL));
            valor("15.00", resumo.frete());
            valor("214.99", resumo.total());
        });
        testar("frete grátis exatamente em 200", () -> {
            ResumoPedido resumo = CHECKOUT.simular(pedido("100.00", 2, ModalidadeFrete.NORMAL));
            valor("0.00", resumo.frete());
            valor("200.00", resumo.total());
        });
        testar("frete grátis acima de 200", () ->
                valor("0.00", CHECKOUT.simular(pedido("200.01", 1, ModalidadeFrete.NORMAL)).frete()));
        testar("expresso cobra por unidade, sem isenção", () -> {
            Pedido pedido = new Pedido(List.of(item("A", "100.00", 2), item("B", "25.00", 1)),
                    "01001-000", ModalidadeFrete.EXPRESSO);
            ResumoPedido resumo = CHECKOUT.simular(pedido);
            valor("225.00", resumo.subtotal());
            valor("31.00", resumo.frete());
            valor("256.00", resumo.total());
        });
        testar("precisão decimal", () ->
                valor("15.30", CHECKOUT.simular(pedido("0.10", 3, ModalidadeFrete.NORMAL)).total()));
        testar("carrinho vazio interrompe antes do CEP", () -> rejeita(IllegalArgumentException.class,
                "pelo menos um item", () -> CHECKOUT.simular(new Pedido(List.of(), "inválido", ModalidadeFrete.NORMAL))));
        testar("CEP inválido", () -> rejeita(IllegalArgumentException.class, "8 dígitos", () ->
                CHECKOUT.simular(new Pedido(List.of(item("A", "10", 1)), "123", ModalidadeFrete.NORMAL))));
        testar("CEP com e sem hífen", () -> {
            for (String cep : List.of("01001000", "01001-000")) {
                CHECKOUT.simular(new Pedido(List.of(item("A", "10", 1)), cep, ModalidadeFrete.NORMAL));
            }
        });
        testar("dez unidades são aceitas", () -> CHECKOUT.simular(pedido("10", 10, ModalidadeFrete.NORMAL)));
        testar("onze unidades são recusadas", () -> rejeita(IllegalArgumentException.class, "10 unidades", () ->
                CHECKOUT.simular(pedido("10", 11, ModalidadeFrete.NORMAL))));
        testar("SKU repetido não contorna limite", () -> rejeita(IllegalArgumentException.class, "10 unidades", () ->
                CHECKOUT.simular(new Pedido(List.of(item("A", "10", 6), item("A", "10", 5)),
                        "01001000", ModalidadeFrete.NORMAL))));
        testar("limite é individual por SKU", () -> CHECKOUT.simular(new Pedido(
                List.of(item("A", "10", 10), item("B", "10", 10)), "01001000", ModalidadeFrete.NORMAL)));
        testar("quantidade inválida", () -> rejeita(IllegalArgumentException.class, "positivos", () -> item("A", "10", 0)));
        testar("preço inválido", () -> {
            for (String preco : List.of("0", "-1")) {
                rejeita(IllegalArgumentException.class, "positivos", () -> item("A", preco, 1));
            }
        });
        testar("fração de centavo é recusada", () -> rejeita(ArithmeticException.class, "", () -> item("A", "0.001", 1)));
        testar("campos obrigatórios", () -> {
            rejeita(IllegalArgumentException.class, "vazios", () -> item(" ", "10", 1));
            rejeita(NullPointerException.class, "Pedido obrigatório", () -> CHECKOUT.simular(null));
            rejeita(NullPointerException.class, "Modalidade obrigatória", () ->
                    new Pedido(List.of(), "01001000", null));
        });
        testar("pedido copia e protege os itens", () -> {
            List<ItemPedido> origem = new ArrayList<>(List.of(item("A", "10", 1)));
            Pedido pedido = new Pedido(origem, "01001000", ModalidadeFrete.NORMAL);
            origem.clear();
            valor("10.00", pedido.subtotal());
            rejeita(UnsupportedOperationException.class, "", () -> pedido.itens().clear());
        });
        testar("factory seleciona os algoritmos", () -> {
            FreteFactory factory = new FreteFactory();
            if (!(factory.criar(ModalidadeFrete.NORMAL) instanceof FreteNormal)
                    || !(factory.criar(ModalidadeFrete.EXPRESSO) instanceof FreteExpresso)) {
                throw new AssertionError("Estratégia incorreta");
            }
        });
        testar("cadeia interrompe após a primeira falha", () -> {
            List<String> eventos = new ArrayList<>();
            ValidacaoPedido terceiro = elo("terceiro", false, null, eventos);
            ValidacaoPedido segundo = elo("segundo", true, terceiro, eventos);
            ValidacaoPedido primeiro = elo("primeiro", false, segundo, eventos);
            rejeita(IllegalArgumentException.class, "segundo", () ->
                    primeiro.validar(pedido("10", 1, ModalidadeFrete.NORMAL)));
            if (!eventos.equals(List.of("primeiro", "segundo"))) {
                throw new AssertionError("Ordem ou interrupção incorreta: " + eventos);
            }
        });
        System.out.println("\n" + executados + " testes passaram.");
    }

    private static ValidacaoPedido elo(String nome, boolean falha, ValidacaoPedido proxima, List<String> eventos) {
        return new ValidacaoPedido(proxima) {
            @Override
            protected void verificar(Pedido pedido) {
                eventos.add(nome);
                if (falha) throw new IllegalArgumentException(nome);
            }
        };
    }

    private static ItemPedido item(String sku, String preco, int quantidade) {
        return new ItemPedido(sku, "Produto " + sku, new BigDecimal(preco), quantidade);
    }

    private static Pedido pedido(String preco, int quantidade, ModalidadeFrete modalidade) {
        return new Pedido(List.of(item("A", preco, quantidade)), "01001000", modalidade);
    }

    private static void valor(String esperado, BigDecimal atual) {
        if (new BigDecimal(esperado).compareTo(atual) != 0) {
            throw new AssertionError("Esperado " + esperado + ", recebido " + atual);
        }
    }

    private static void rejeita(Class<? extends Throwable> tipo, String trecho, Runnable acao) {
        try {
            acao.run();
        } catch (Throwable erro) {
            if (!tipo.isInstance(erro) || (erro.getMessage() == null ? !trecho.isEmpty() : !erro.getMessage().contains(trecho))) {
                throw new AssertionError("Exceção inesperada", erro);
            }
            return;
        }
        throw new AssertionError("Era esperada uma exceção " + tipo.getSimpleName());
    }

    private static void testar(String nome, Runnable teste) {
        try {
            teste.run();
            executados++;
            System.out.println("OK | " + nome);
        } catch (Throwable erro) {
            throw new AssertionError("FALHOU | " + nome, erro);
        }
    }
}
