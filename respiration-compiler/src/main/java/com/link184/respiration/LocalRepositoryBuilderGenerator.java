package com.link184.respiration;

import com.link184.respiration.base.BaseBuilderGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Created by eugeniu on 3/6/18.
 */

public class LocalRepositoryBuilderGenerator extends BaseBuilderGenerator {
    private static final String CONTEXT_PARAMETER_NAME = "context";

    @Override
    public List<JavaFile> generateRepository(Map<Element, String> repositoriesWithPackages) {
        List<JavaFile> javaFiles = new ArrayList<>();

        for (Map.Entry<Element, String> entry : repositoriesWithPackages.entrySet()) {
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

    @Override
    public FieldSpec generateRepositoryInstance(Map.Entry<Element, String> entry) {
        return FieldSpec
                .builder(TypeName.get(entry.getKey().asType()), VARIABLE_INSTANCE, Modifier.PRIVATE, Modifier.STATIC)
                .build();
    }

    @Override
    public MethodSpec generateMethodCreate(Map.Entry<Element, String> entry) {
        final ClassName contextClass = ClassName.get("android.content", "Context");

        return MethodSpec
                .methodBuilder(METHOD_GET_INSTANCE)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(contextClass, CONTEXT_PARAMETER_NAME)
                .returns(TypeName.get(entry.getKey().asType()))
                .beginControlFlow("if ($L == null)", VARIABLE_INSTANCE)
                .addStatement("$L = $L($L)", "INSTANCE", "createInstance", CONTEXT_PARAMETER_NAME)
                .endControlFlow()
                .addStatement("return $N", "INSTANCE")
                .build();
    }

    @Override
    public MethodSpec generateMethodSingleInit(Map.Entry<Element, String> entry) {
        final ClassName configurationClass = ClassName.get("com.link184.respiration.repository.local", "LocalConfiguration");
        final ClassName contextClass = ClassName.get("android.content", "Context");

        LocalRepository annotation = entry.getKey().getAnnotation(LocalRepository.class);
        TypeName modelTypeName = GenerationUtils.extractTypeName(annotation);
        MethodSpec.Builder createMethod = MethodSpec
                .methodBuilder(METHOD_CREATE_INSTANCE)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addParameter(contextClass, CONTEXT_PARAMETER_NAME)
                .returns(TypeName.get(entry.getKey().asType()))
                .addStatement("$T<$T> configuration = new $T<>($T.class)",
                        configurationClass, modelTypeName, configurationClass, modelTypeName)
                .addStatement("configuration.setContext($L)", CONTEXT_PARAMETER_NAME)
                .addStatement("configuration.setDbName($S)", annotation.dataBaseName())
                .addStatement("configuration.setAssetDbFilePath($S)", annotation.dataBaseAssetPath());
        if (annotation.children().length > 0 && !annotation.children()[0].isEmpty()) {
            createMethod.addStatement("configuration.setDatabaseChildren($L)", GenerationUtils.generateChildrenArray(annotation));
        }
        createMethod.addStatement("return new $T($N)", entry.getKey(), "configuration");
        return createMethod.build();
    }
}
