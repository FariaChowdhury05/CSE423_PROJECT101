package org.rakam;

import org.rakam.bootstrap.SystemRegistry;
import org.rakam.bootstrap.SystemRegistry.ModuleDescriptor.Condition;
import org.rakam.util.JsonHelper;

import java.io.PrintWriter;
import java.util.Locale;

public class SystemRegistryOutputGenerator {

    public void generate(
            SystemRegistry systemRegistry,
            String format) {

        if (format.equals("json")) {
            generateJson(systemRegistry);
        } else {
            generateProperties(systemRegistry);
        }
    }

    private void generateJson(SystemRegistry systemRegistry) {
        System.out.print(JsonHelper.encode(systemRegistry));
    }

    private void generateProperties(
            SystemRegistry systemRegistry) {

        PrintWriter printWriter =
                new PrintWriter(System.out);

        for (SystemRegistry.ModuleDescriptor moduleDescriptor :
                systemRegistry.getModules()) {

            printModule(printWriter, moduleDescriptor);
        }

        printWriter.flush();
    }

    private void printModule(
            PrintWriter printWriter,
            SystemRegistry.ModuleDescriptor moduleDescriptor) {

        String name = moduleDescriptor.name == null
                ? moduleDescriptor.className
                : moduleDescriptor.name;

        printWriter.println(
                "#------------------------------------------------------------------------------"
        );

        printWriter.println(
                "#" + name.toUpperCase(Locale.ENGLISH)
        );

        if (moduleDescriptor.description != null) {
            printWriter.println(
                    "#" + moduleDescriptor.description
            );
        }

        printWriter.println(
                "#------------------------------------------------------------------------------"
        );

        printCondition(
                printWriter,
                moduleDescriptor
        );

        for (SystemRegistry.ConfigItem property :
                moduleDescriptor.properties) {

            printProperty(
                    printWriter,
                    property
            );
        }

        printWriter.print("\n\n");
    }

    private void printCondition(
            PrintWriter printWriter,
            SystemRegistry.ModuleDescriptor moduleDescriptor) {

        if (!moduleDescriptor.condition.isPresent()) {
            return;
        }

        Condition condition =
                moduleDescriptor.condition.get();

        printWriter.println(
                "#Condition for this plugin to be is_active:"
        );

        if (condition.expectedValue.isEmpty()) {
            printWriter.println(
                    "#" + condition.property +
                    " property must be set"
            );
        } else {
            printWriter.println(
                    "#" + condition.property +
                    "=" + condition.expectedValue + "\n"
            );
        }
    }

    private void printProperty(
            PrintWriter printWriter,
            SystemRegistry.ConfigItem property) {

        if (property.description != null &&
                !property.description.isEmpty()) {

            // TODO: support for breaking words to multiple lines
            printWriter.println(
                    "# " + property.description
            );
        }

        String value =
                property.defaultValue.equals("null")
                        ? ""
                        : property.defaultValue;

        printWriter.println(
                "#" + property.property + "=" + value
        );
    }
}
