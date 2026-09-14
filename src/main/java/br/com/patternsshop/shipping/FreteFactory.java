package br.com.patternsshop.shipping;

import br.com.patternsshop.model.ModalidadeFrete;
import java.util.Objects;

/** Simple Factory: centraliza a criação; não é o Factory Method do GoF. */
public final class FreteFactory {
    public CalculadoraFrete criar(ModalidadeFrete modalidade) {
        Objects.requireNonNull(modalidade, "Modalidade obrigatória");
        return switch (modalidade) {
            case NORMAL -> new FreteNormal();
            case EXPRESSO -> new FreteExpresso();
        };
    }
}
