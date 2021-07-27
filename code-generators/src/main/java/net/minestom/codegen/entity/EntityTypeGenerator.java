package net.minestom.codegen.entity;

import com.google.gson.JsonObject;
import com.squareup.javapoet.*;
import net.minestom.codegen.MinestomCodeGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public final class EntityTypeGenerator extends MinestomCodeGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityTypeGenerator.class);
    private final InputStream entitiesFile;
    private final File outputFolder;

    public EntityTypeGenerator(@Nullable InputStream entitiesFile, @NotNull File outputFolder) {
        this.entitiesFile = entitiesFile;
        this.outputFolder = outputFolder;
    }

    @Override
    public void generate() {
        if (entitiesFile == null) {
            LOGGER.error("Failed to find entities.json.");
            LOGGER.error("Stopped code generation for entities.");
            return;
        }
        ClassName entityCN = ClassName.get("net.minestom.server.entity", "EntityType");

        JsonObject entities;
        entities = GSON.fromJson(new InputStreamReader(entitiesFile), JsonObject.class);
        ClassName entitiesCN = ClassName.get("net.minestom.server.entity", "EntityTypeConstants");
        // BlockConstants class
        TypeSpec.Builder blockConstantsClass = TypeSpec.interfaceBuilder(entitiesCN)
                // Add @SuppressWarnings("unused")
                .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unused").build())
                .addJavadoc("AUTOGENERATED by " + getClass().getSimpleName());

        // Use data
        entities.keySet().forEach(namespace -> {
            final String constantName = namespace.replace("minecraft:", "").toUpperCase(Locale.ROOT);
            blockConstantsClass.addField(
                    FieldSpec.builder(entityCN, constantName)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer(
                                    // Material.STONE = Material.fromNamespaceId("minecraft:stone")
                                    "$T.fromNamespaceId($S)",
                                    entityCN,
                                    namespace
                            )
                            .build()
            );
        });
        writeFiles(
                List.of(
                        JavaFile.builder("net.minestom.server.entity", blockConstantsClass.build())
                                .indent("    ")
                                .skipJavaLangImports(true)
                                .build()
                ),
                outputFolder
        );
    }
}
