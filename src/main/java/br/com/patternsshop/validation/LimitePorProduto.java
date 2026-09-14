package br.com.patternsshop.validation;

import br.com.patternsshop.model.ItemPedido;
import br.com.patternsshop.model.Pedido;
import java.util.HashMap;
import java.util.Map;

public final class LimitePorProduto extends ValidacaoPedido {
    public static final int LIMITE = 10;

    public LimitePorProduto(ValidacaoPedido proxima) {
        super(proxima);
    }

    @Override
    protected void verificar(Pedido pedido) {
        Map<String, Long> quantidades = new HashMap<>();
        for (ItemPedido item : pedido.itens()) {
            long total = quantidades.merge(item.sku(), (long) item.quantidade(), Long::sum);
            if (total > LIMITE) {
                throw new IllegalArgumentException("Limite de 10 unidades por produto: " + item.sku());
            }
        }
    }
}
