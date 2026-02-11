package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class PlanejamentoId extends DomainObjectId {

    public static final PlanejamentoId VAZIO = new PlanejamentoId();

    protected PlanejamentoId() {
        super("");
    }

    public PlanejamentoId(String uuid) {
        super(uuid);
    }

    public static PlanejamentoId randomId() {
        return randomId(PlanejamentoId.class);
    }

    public static PlanejamentoId fromString(@NonNull String uuid) {
        return fromString(uuid, PlanejamentoId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new PlanejamentoId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
