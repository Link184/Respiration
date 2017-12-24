package com.link184.respiration;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by jora on 11/12/17.
 */

class RepositoryBuilderGenerator {
    static final String CLASS_NAME_SUFFIX = "Builder";
    static final String METHOD_GET_INSTANCE = "getInstance";
    private static final String METHOD_CREATE_INSTANCE = "createInstance";
    private String VARIABLE_INSTANCE = "INSTANCE";

    List<JavaFile> generateRepository(Map<TypeElement, String> repositoriesWithPackages) {
        List<JavaFile> javaFiles = new ArrayList<>();

        for (Map.Entry<TypeElement, String> entry : repositoriesWithPackages.entrySet()) {
            String repositoryName = entry.getKey().getSimpleName().toString();
            String packageName = entry.getValue();

            TypeSpec.Builder repositoryClass = TypeSpec
                    .classBuilder(ClassName.get(packageName, repositoryName + CLASS_NAME_SUFFIX))
                    .addModifiers(Modifier.PUBLIC)
                    .addField(generateRepositoryInstance(entry))
                    .addMethod(generateMethodCreate(entry))
                    .addMethod(generateMethodSingleInit(entry));

            javaFiles.add(JavaFile.builder(packageName, repositoryClass.build())
                    .build());
        }
        return javaFiles;
    }

    private FieldSpec generateRepositoryInstance(Map.Entry<TypeElement, String> entry) {
        return FieldSpec
                .builder(TypeName.get(entry.getKey().asType()), VARIABLE_INSTANCE, Modifier.PRIVATE, Modifier.STATIC)
                .build();
    }

    private MethodSpec generateMethodCreate(Map.Entry<TypeElement, String> entry) {
        return MethodSpec
                .methodBuilder(METHOD_GET_INSTANCE)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.get(entry.getKey().asType()))
                .beginControlFlow("if ($L == null)", VARIABLE_INSTANCE)
                .addStatement("$L = $L", "INSTANCE", "createInstance()")
                .endControlFlow()
                .addStatement("return $N", "INSTANCE")
                .build();
    }

    private MethodSpec generateMethodSingleInit(Map.Entry<TypeElement, String> entry) {
        ClassName configurationClass = ClassName.get("com.link184.respiration.repository", "Configuration");

        RespirationRepository annotation = entry.getKey().getAnnotation(RespirationRepository.class);
        TypeName modelTypeName = GenerationUtils.extractTypeName(annotation);
        MethodSpec.Builder createMethod = MethodSpec
                .methodBuilder(METHOD_CREATE_INSTANCE)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(TypeName.get(entry.getKey().asType()))
                .addStatement("$T<$T> configuration = new $T<>($T.class)",
                        configurationClass, modelTypeName, configurationClass, modelTypeName)
                .addStatement("configuration.setPersistence($L)", annotation.isAccessPrivate())
                .addStatement("configuration.setAccessPrivate($L)", annotation.isAccessPrivate());
        if (annotation.children().length > 0 && !annotation.children()[0].isEmpty()) {
            createMethod.addStatement("configuration.setDatabaseChildren($L)", GenerationUtils.generateChildrenArray(annotation));
        }
        createMethod.addStatement("return new $T($N)", entry.getKey(), "configuration");
        return createMethod.build();
    }

    private TypeVariableName extractModelClass(RespirationRepository annotation) {
        return TypeVariableName.get("T", GenerationUtils.extractTypeName(annotation));
    }
}
