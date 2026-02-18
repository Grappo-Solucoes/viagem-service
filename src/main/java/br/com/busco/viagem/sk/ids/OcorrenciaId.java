package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class OcorrenciaId extends DomainObjectId {

    public static final OcorrenciaId VAZIO = new OcorrenciaId();

    protected OcorrenciaId() {
        super("");
    }

    public OcorrenciaId(String uuid) {
        super(uuid);
    }

    public static OcorrenciaId randomId() {
        return randomId(OcorrenciaId.class);
    }

    public static OcorrenciaId fromString(@NonNull String uuid) {
        return fromString(uuid, OcorrenciaId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new OcorrenciaId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
