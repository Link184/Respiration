package com.link184.respiration;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;

/**
 * Created by jora on 11/25/17.
 */

public class RepositoryModuleGenerator {
    private static final String CLASS_NAME_PREFIX = "Respiration";

    List<JavaFile> generateModule(Map<Element, String> repositoriesWithPackages) {
        List<JavaFile> javaFiles = new ArrayList<>();

        for (Map.Entry<Element, String> entry : repositoriesWithPackages.entrySet()) {
            String repositoryName = entry.getKey().getSimpleName().toString();
            String packageName = entry.getValue();

            TypeSpec.Builder repositoryClass = TypeSpec
                    .classBuilder(ClassName.get(packageName, CLASS_NAME_PREFIX + repositoryName))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethods(generateGetters(entry));

            javaFiles.add(JavaFile.builder(packageName, repositoryClass.build())
                    .build());
        }
        return javaFiles;
    }

    private List<MethodSpec> generateGetters(Map.Entry<Element, String> entry) {
        List<MethodSpec> getters = new ArrayList<>();
        List<? extends Element> enclosedElements = entry.getKey().getEnclosedElements();
        for (Element element : enclosedElements) {
            if (element.getAnnotation(FirebaseRepository.class) != null) {
                getters.add(generateGetter(element, entry.getValue()));
            }
        }
        return getters;
    }

    private MethodSpec generateGetter(Element element, String packageName) {
        Name simpleName = element.getSimpleName();
        String capitalizedRepoName = simpleName.toString().substring(0, 1).toUpperCase()
                + simpleName.toString().substring(1);
        FirebaseRepository annotation = element.getAnnotation(FirebaseRepository.class);
        TypeName modelType = GenerationUtils.extractTypeName(annotation);
        ClassName returnTypeClassName = ClassName.get(packageName,
                capitalizedRepoName);
        return MethodSpec.methodBuilder("get" + capitalizedRepoName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(returnTypeClassName)
                .addStatement("return $N.$N()", returnTypeClassName.simpleName() + FirebaseRepositoryBuilderGenerator.CLASS_NAME_SUFFIX,
                        FirebaseRepositoryBuilderGenerator.METHOD_GET_INSTANCE)
                .build();
    }
}
