package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class ViagemPlanejadaId extends DomainObjectId {

    public static final ViagemPlanejadaId VAZIO = new ViagemPlanejadaId();

    protected ViagemPlanejadaId() {
        super("");
    }

    public ViagemPlanejadaId(String uuid) {
        super(uuid);
    }

    public static ViagemPlanejadaId randomId() {
        return randomId(ViagemPlanejadaId.class);
    }

    public static ViagemPlanejadaId fromString(@NonNull String uuid) {
        return fromString(uuid, ViagemPlanejadaId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new ViagemPlanejadaId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
