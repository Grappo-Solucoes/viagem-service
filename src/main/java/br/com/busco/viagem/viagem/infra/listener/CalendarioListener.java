package br.com.busco.viagem.viagem.infra.listener;

import br.com.busco.viagem.viagem.infra.listener.events.CalendarioAtualizado;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Log
@Component
@AllArgsConstructor
public class CalendarioListener {

    @EventListener
    public void on(CalendarioAtualizado evt) {

    }
}
