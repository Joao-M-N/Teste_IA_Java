package br.com.patternsshop.shipping;

import br.com.patternsshop.model.Pedido;
import java.math.BigDecimal;

/** Strategy: contrato comum para algoritmos intercambiáveis de frete. */
@FunctionalInterface
public interface CalculadoraFrete {
    BigDecimal calcular(Pedido pedido);
}
