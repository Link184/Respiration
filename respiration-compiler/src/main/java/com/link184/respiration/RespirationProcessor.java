package com.link184.respiration;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class RespirationProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;
    private Elements elements;
    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(RespirationRepository.class)) {
            Map<Element, String> repositoriesWithPackages = new HashMap<>();
            if (element.getKind() == ElementKind.FIELD) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Processing respiration field: " + element.getSimpleName());
                repositoriesWithPackages.put(
                        element, elements.getPackageOf(element).getQualifiedName().toString());
                RepositoryClassGenerator repositoryClassGenerator= new RepositoryClassGenerator();
                JavaFile javaFile = repositoryClassGenerator.generateRepositories(repositoriesWithPackages);
                if (javaFile != null) {
                    try {
                        javaFile.writeTo(filer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        for (Element element : roundEnvironment.getElementsAnnotatedWith(RespirationRepository.class)) {
            Map<TypeElement, String> repositoriesWithPackages = new HashMap<>();
            if (element.getKind() == ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Processing respiration repository: " + element.getSimpleName());
                TypeElement typeElement = (TypeElement) element;
                repositoriesWithPackages.put(
                        typeElement, elements.getPackageOf(typeElement).getQualifiedName().toString());
                RepositoryBuilderGenerator repositoryBuilderGenerator = new RepositoryBuilderGenerator();
                List<JavaFile> javaFiles = repositoryBuilderGenerator.generateRepository(repositoriesWithPackages);
                javaFiles.forEach(javaFile -> {
                    try {
                        javaFile.writeTo(filer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        for (Element element : roundEnvironment.getElementsAnnotatedWith(RespirationModule.class)) {
            Map<TypeElement, String> repositoriesWithPackages = new HashMap<>();

            if (element.getKind() == ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Processing respiration module: " + element.getSimpleName());
                TypeElement typeElement = (TypeElement) element;
                repositoriesWithPackages.put(
                        typeElement, elements.getPackageOf(typeElement).getQualifiedName().toString());
                RespirationModuleGenerator respirationModuleGenerator = new RespirationModuleGenerator();
                List<JavaFile> javaFiles = respirationModuleGenerator.generateModule(repositoriesWithPackages);
                javaFiles.forEach(javaFile -> {
                    try {
                        javaFile.writeTo(filer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(RespirationRepository.class.getCanonicalName());
        types.add(RespirationModule.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
