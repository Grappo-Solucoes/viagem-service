package br.com.busco.viagem.viagem;
import org.springframework.modulith.ApplicationModule;

@ApplicationModule(type = ApplicationModule.Type.CLOSED, allowedDependencies = {
        "sk"
})
public interface ViagemModule {
}
