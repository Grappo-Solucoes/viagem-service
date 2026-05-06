package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class ParadaExecucaoId extends DomainObjectId {

    public static final ParadaExecucaoId VAZIO = new ParadaExecucaoId();

    protected ParadaExecucaoId() {
        super("");
    }

    public ParadaExecucaoId(String uuid) {
        super(uuid);
    }

    public static ParadaExecucaoId randomId() {
        return randomId(ParadaExecucaoId.class);
    }

    public static ParadaExecucaoId fromString(@NonNull String uuid) {
        return fromString(uuid, ParadaExecucaoId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new ParadaExecucaoId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
