package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class EmergenciaId extends DomainObjectId {

    public static final EmergenciaId VAZIO = new EmergenciaId();

    protected EmergenciaId() {
        super("");
    }

    public EmergenciaId(String uuid) {
        super(uuid);
    }

    public static EmergenciaId randomId() {
        return randomId(EmergenciaId.class);
    }

    public static EmergenciaId fromString(@NonNull String uuid) {
        return fromString(uuid, EmergenciaId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new EmergenciaId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
