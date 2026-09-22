package com.hab.emmaus;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ArchitecturalTests {

    private final ApplicationModules modules =
            ApplicationModules.of(Application.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void generatesArchitectureDocumentation() {
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml()
                .writeModuleCanvases()
                .writeAggregatingDocument();
    }

}
