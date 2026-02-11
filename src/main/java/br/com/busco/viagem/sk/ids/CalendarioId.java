package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class CalendarioId extends DomainObjectId {

    public static final CalendarioId VAZIO = new CalendarioId();

    protected CalendarioId() {
        super("");
    }

    public CalendarioId(String uuid) {
        super(uuid);
    }

    public static CalendarioId randomId() {
        return randomId(CalendarioId.class);
    }

    public static CalendarioId fromString(@NonNull String uuid) {
        return fromString(uuid, CalendarioId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new CalendarioId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
