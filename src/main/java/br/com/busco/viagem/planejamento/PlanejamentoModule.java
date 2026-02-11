package br.com.busco.viagem.planejamento;

import org.springframework.modulith.ApplicationModule;

@ApplicationModule(type = ApplicationModule.Type.CLOSED, allowedDependencies = {
        "sk"
})
public interface PlanejamentoModule {
}
