package br.com.busco.viagem.app.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesembarcarAluno {
    private UUID id;
    private String aluno;
    private Double latitude;
    private Double longitude;
}
