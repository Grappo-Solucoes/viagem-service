package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class MonitorId extends DomainObjectId {

    public static final MonitorId VAZIO = new MonitorId();

    protected MonitorId() {
        super("");
    }

    public MonitorId(String uuid) {
        super(uuid);
    }

    public static MonitorId randomId() {
        return randomId(MonitorId.class);
    }

    public static MonitorId fromString(@NonNull String uuid) {
        return fromString(uuid, MonitorId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new MonitorId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
