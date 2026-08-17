package org.rakam.bootstrap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;
import io.airlift.configuration.ConfigurationFactory;
import io.airlift.configuration.ConfigurationInspector;
import org.rakam.plugin.RakamModule;
import org.rakam.util.ConditionalModule;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ModuleDescriptorFactory {

    @Inject
    public ModuleDescriptorFactory() {
    }

    public List<SystemRegistry.ModuleDescriptor> create(
            Set<Module> modules,
            Set<Module> installedModules) {

        return modules.stream()
                .filter(module -> module instanceof RakamModule)
                .map(module -> createDescriptor(
                        (RakamModule) module,
                        installedModules.contains(module)))
                .collect(Collectors.toList());
    }

    private SystemRegistry.ModuleDescriptor createDescriptor(
            RakamModule module,
            boolean active) {

        Optional<SystemRegistry.ModuleDescriptor.Condition> condition =
                createCondition(module);

        List<SystemRegistry.ConfigItem> properties =
                createConfigItems();

        return new SystemRegistry.ModuleDescriptor(
                module.name(),
                module.description(),
                module.getClass().getName(),
                active,
                condition,
                properties
        );
    }

    private Optional<SystemRegistry.ModuleDescriptor.Condition>
    createCondition(RakamModule module) {

        ConditionalModule annotation =
                module.getClass()
                        .getAnnotation(ConditionalModule.class);

        if (annotation == null) {
            return Optional.empty();
        }

        return Optional.of(
                new SystemRegistry.ModuleDescriptor.Condition(
                        annotation.config(),
                        annotation.value()
                )
        );
    }

    private List<SystemRegistry.ConfigItem> createConfigItems() {

        ConfigurationFactory configurationFactory =
                new ConfigurationFactory(ImmutableMap.of());

        configurationFactory
                .validateRegisteredConfigurationProvider();

        ImmutableList.Builder<SystemRegistry.ConfigItem> builder =
                ImmutableList.builder();

        ConfigurationInspector inspector =
                new ConfigurationInspector();

        for (ConfigurationInspector.ConfigRecord<?> record
                : inspector.inspect(configurationFactory)) {

            for (ConfigurationInspector.ConfigAttribute attribute
                    : record.getAttributes()) {

                builder.add(
                        new SystemRegistry.ConfigItem(
                                attribute.getPropertyName(),
                                attribute.getDefaultValue(),
                                attribute.getDescription()
                        )
                );
            }
        }

        return builder.build();
    }
}
