package com.link184.respiration;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;

/**
 * Created by eugeniu on 3/22/18.
 */

public class LocalRepositoryClassGenerator {
    JavaFile generateRepositories(Map<Element, String> repositoriesWithPackages) {
        for (Map.Entry<Element, String> entry : repositoriesWithPackages.entrySet()) {
            if (entry.getKey().getAnnotation(LocalRepository.class) != null) {
                return generateRepository(entry.getKey(), entry.getValue());
            }
        }
        return null;
    }

    private JavaFile generateRepository(Element element, String packageName) {
        Name simpleName = element.getSimpleName();
        String capitalizedRepoName = simpleName.toString().substring(0, 1).toUpperCase()
                + simpleName.toString().substring(1);
        LocalRepository annotation = element.getAnnotation(LocalRepository.class);
        TypeName modelType = GenerationUtils.extractTypeName(annotation);
        ParameterizedTypeName superClass = ParameterizedTypeName.get(ClassName.bestGuess(element.asType().toString()), modelType);
        TypeSpec.Builder repositoryClass = TypeSpec.classBuilder(capitalizedRepoName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(generateRepositoryAnnotation(element))
                .superclass(superClass)
                .addMethod(generateRepositoryConstructor(element));
        return JavaFile.builder(packageName, repositoryClass.build())
                .build();
    }

    private AnnotationSpec generateRepositoryAnnotation(Element element) {
        LocalRepository annotation = element.getAnnotation(LocalRepository.class);
        AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(annotation.annotationType());
        annotationBuilder
                .addMember("dataSnapshotType", "$T.class", GenerationUtils.extractTypeName(annotation))
                .addMember("dataBaseAssetPath", "$S", annotation.dataBaseAssetPath());
        if (annotation.children().length > 0 && !annotation.children()[0].isEmpty()) {
            annotationBuilder.addMember("children", "$L", GenerationUtils.generateChildrenArrayForAnnotations(annotation));
        }
        if (!annotation.dataBaseName().isEmpty()) {
            annotationBuilder.addMember("dataBaseName", "$S", annotation.dataBaseName());
        }
        return annotationBuilder.build();
    }

    private MethodSpec generateRepositoryConstructor(Element element) {
        final ClassName configurationClass = ClassName.get("com.link184.respiration.repository.local", "LocalConfiguration");
        final String configurationParameterName = "configuration";
        TypeName modelType = GenerationUtils.extractTypeName(element.getAnnotation(LocalRepository.class));
        ParameterizedTypeName parametrizedConfigurationClass = ParameterizedTypeName.get(configurationClass, modelType);
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parametrizedConfigurationClass, configurationParameterName)
                .addStatement("super($N)", configurationParameterName)
                .build();
    }
}
