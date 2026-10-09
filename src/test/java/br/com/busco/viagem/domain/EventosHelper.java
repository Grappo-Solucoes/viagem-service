package br.com.busco.viagem.domain;

import br.com.busco.viagem.sk.ddd.AbstractAggregateRoot;

import java.lang.reflect.Method;
import java.util.Collection;

/** Lê os eventos registrados no agregado (via reflexão, só para testes). */
public final class EventosHelper {

    private EventosHelper() {
    }

    public static Collection<Object> eventosDe(AbstractAggregateRoot<?> agregado) {
        try {
            Method metodo = AbstractAggregateRoot.class.getDeclaredMethod("domainEvents");
            metodo.setAccessible(true);
            @SuppressWarnings("unchecked")
            Collection<Object> eventos = (Collection<Object>) metodo.invoke(agregado);
            return eventos;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
