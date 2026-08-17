package org.rakam.bootstrap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Module;
import org.rakam.plugin.RakamModule;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

public class SystemRegistry {

    private final Set<Module> installedModules;
    private final Set<Module> allModules;
    private final ModuleDescriptorFactory descriptorFactory;

    private List<ModuleDescriptor> moduleDescriptors;

    @Inject
    public SystemRegistry(
            Set<Module> allModules,
            Set<Module> installedModules,
            ModuleDescriptorFactory descriptorFactory) {

        this.allModules = allModules;
        this.installedModules = installedModules;
        this.descriptorFactory = descriptorFactory;
    }

    public synchronized List<ModuleDescriptor> getModules() {
        if (moduleDescriptors == null) {
            Set<Module> modules =
                    allModules == null
                            ? installedModules
                            : allModules;

            moduleDescriptors =
                    descriptorFactory.create(
                            modules,
                            installedModules
                    );
        }

        return moduleDescriptors;
    }

    public static class ModuleDescriptor {

        public final String name;
        public final String description;
        public final String className;
        public final boolean isActive;
        public final java.util.Optional<Condition> condition;
        public final List<ConfigItem> properties;

        @JsonCreator
        public ModuleDescriptor(
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("className") String className,
                @JsonProperty("isActive") boolean isActive,
                @JsonProperty("condition")
                java.util.Optional<Condition> condition,
                @JsonProperty("properties")
                List<ConfigItem> properties) {

            this.name = name;
            this.description = description;
            this.className = className;
            this.isActive = isActive;
            this.condition = condition;
            this.properties = properties;
        }

        public static class Condition {

            public final String property;
            public final String expectedValue;

            public Condition(
                    @JsonProperty("property") String property,
                    @JsonProperty("expectedValue") String expectedValue) {

                this.property = property;
                this.expectedValue = expectedValue;
            }
        }
    }

    public static class ConfigItem {

        public final String property;
        public final String defaultValue;
        public final String description;

        @JsonCreator
        public ConfigItem(
                @JsonProperty("property") String property,
                @JsonProperty("defaultValue") String defaultValue,
                @JsonProperty("description") String description) {

            this.property = property;
            this.defaultValue = defaultValue;
            this.description = description;
        }
    }
}
