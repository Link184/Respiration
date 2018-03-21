package com.link184.respiration.base;

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

public abstract class BaseBuilderGenerator implements BaseBuilder {
    private String VARIABLE_INSTANCE = "INSTANCE";

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
}
