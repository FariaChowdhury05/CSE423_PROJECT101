package org.rakam;

import com.google.inject.Module;
import org.rakam.bootstrap.SystemRegistry;
import org.rakam.util.JsonHelper;

import java.io.IOException;
import java.util.Set;

public final class SystemRegistryGenerator {

    private final SystemRegistryOutputGenerator outputGenerator;

    private SystemRegistryGenerator(
            SystemRegistryOutputGenerator outputGenerator) {
        this.outputGenerator = outputGenerator;
    }

    public static void main(String[] args) throws IOException {
        try {
            if (args.length != 1 ||
                    (!args[0].equals("json") &&
                     !args[0].equals("properties"))) {

                System.err.println("Usage: [json] or [properties]");
                System.exit(1);
            }

            Set<Module> allModules = ServiceStarter.getModules();

            SystemRegistry systemRegistry =
                    new SystemRegistry(allModules, allModules);

            SystemRegistryOutputGenerator generator =
                    new SystemRegistryOutputGenerator();

            generator.generate(
                    systemRegistry,
                    args[0]
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
