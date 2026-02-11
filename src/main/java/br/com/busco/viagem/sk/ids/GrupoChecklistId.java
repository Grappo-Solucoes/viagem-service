package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class GrupoChecklistId extends DomainObjectId {

    public static final GrupoChecklistId VAZIO = new GrupoChecklistId();

    protected GrupoChecklistId() {
        super("");
    }

    public GrupoChecklistId(String uuid) {
        super(uuid);
    }

    public static GrupoChecklistId randomId() {
        return randomId(GrupoChecklistId.class);
    }

    public static GrupoChecklistId fromString(@NonNull String uuid) {
        return fromString(uuid, GrupoChecklistId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new GrupoChecklistId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
