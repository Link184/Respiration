package com.example.respiration_processor;

import com.google.auto.service.AutoService;
import com.link184.respiration_annotation.RespirationRepository;
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
        Map<TypeElement, String> repositoriesWithPackages = new HashMap<>();
        for (Element element : roundEnvironment.getElementsAnnotatedWith(RespirationRepository.class)) {

            messager.printMessage(Diagnostic.Kind.NOTE, "Processing respiration repository: " + element.getSimpleName());
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.NOTE, "RespirationRepository annotation cant be applied to class.");
                return true;
            }

            TypeElement typeElement = (TypeElement) element;
            repositoriesWithPackages.put(
                    typeElement, elements.getPackageOf(typeElement).getQualifiedName().toString());
            RepositoryGenerator repositoryGenerator = new RepositoryGenerator();
            List<JavaFile> javaFiles = repositoryGenerator.generateRepository(repositoriesWithPackages);
            javaFiles.forEach(javaFile -> {
                try {
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(RespirationRepository.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
