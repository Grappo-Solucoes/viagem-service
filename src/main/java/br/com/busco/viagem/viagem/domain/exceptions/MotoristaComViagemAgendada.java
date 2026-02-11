package br.com.busco.viagem.viagem.domain.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class MotoristaComViagemAgendada extends IllegalStateException {
    public MotoristaComViagemAgendada(LocalDateTime novaViagem) {
        super(  String.format(
                "O motorista já possui uma viagem próxima ao horário %s do dia %s",
                novaViagem.format(DateTimeFormatter.ofPattern("HH:mm")),
                novaViagem.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ));
    }

}
