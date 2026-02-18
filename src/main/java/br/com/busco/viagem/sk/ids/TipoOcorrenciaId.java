package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class TipoOcorrenciaId extends DomainObjectId {

    public static final TipoOcorrenciaId VAZIO = new TipoOcorrenciaId();

    protected TipoOcorrenciaId() {
        super("");
    }

    public TipoOcorrenciaId(String uuid) {
        super(uuid);
    }

    public static TipoOcorrenciaId randomId() {
        return randomId(TipoOcorrenciaId.class);
    }

    public static TipoOcorrenciaId fromString(@NonNull String uuid) {
        return fromString(uuid, TipoOcorrenciaId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new TipoOcorrenciaId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
