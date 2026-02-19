package br.com.busco.viagem.sk.ids;

import br.com.busco.viagem.sk.ddd.DomainObjectId;
import lombok.NonNull;

public class UserId extends DomainObjectId {

    public static final UserId VAZIO = new UserId();

    protected UserId() {
        super("");
    }

    public UserId(String uuid) {
        super(uuid);
    }

    public static UserId randomId() {
        return randomId(UserId.class);
    }

    public static UserId fromString(@NonNull String uuid) {
        return fromString(uuid, UserId.class);
    }

    public boolean isEmpty() {
        return this.equals(VAZIO) || this.equals(new UserId());
    }

    public boolean isPresent() {
        return !isEmpty();
    }
}
