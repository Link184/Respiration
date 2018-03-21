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
        roundEnvironment.getElementsAnnotatedWith(FirebaseRepository.class).forEach(element -> {
            Map<Element, String> repositoriesWithPackages = processAnnotatedElements(element, ElementKind.FIELD);
            JavaFile javaFiles = new FirebaseRepositoryClassGenerator()
                    .generateRepositories(repositoriesWithPackages);
            writeJavaFile(javaFiles);
        });

        roundEnvironment.getElementsAnnotatedWith(FirebaseRepository.class).forEach(element -> {
            Map<Element, String> repositoriesWithPackages = processAnnotatedElements(element, ElementKind.CLASS);
            List<JavaFile> javaFiles = new FirebaseRepositoryBuilderGenerator()
                    .generateRepository(repositoriesWithPackages);
            writeJavaFiles(javaFiles);
        });

        roundEnvironment.getElementsAnnotatedWith(LocalRepository.class).forEach(element -> {
            Map<Element, String> repositoriesWithPackages = processAnnotatedElements(element, ElementKind.CLASS);
            List<JavaFile> javaFiles = new LocalRepositoryBuilderGenerator()
                    .generateRepository(repositoriesWithPackages);
            writeJavaFiles(javaFiles);
        });

        roundEnvironment.getElementsAnnotatedWith(RespirationModule.class).forEach(element -> {
            Map<Element, String> repositoriesWithPackages = processAnnotatedElements(element, ElementKind.CLASS);
            List<JavaFile> javaFiles = new RepositoryModuleGenerator().generateModule(repositoriesWithPackages);
            writeJavaFiles(javaFiles);
        });

        return false;
    }

    private Map<Element, String> processAnnotatedElements(Element element, ElementKind elementKind) {
        Map<Element, String> repositoriesWithPackages = new HashMap<>();
        if (element.getKind() == elementKind) {
//            messager.printMessage(Diagnostic.Kind.NOTE, "Processing respiration repository: " + element.getSimpleName());
            repositoriesWithPackages.put(
                    element, elements.getPackageOf(element).getQualifiedName().toString());
        }
        return repositoriesWithPackages;
    }

    private void writeJavaFiles(List<JavaFile> javaFiles) {
        javaFiles.forEach(this::writeJavaFile);
    }

    private void writeJavaFile(JavaFile javaFile) {
        if (javaFile != null) {
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(FirebaseRepository.class.getCanonicalName());
        types.add(LocalRepository.class.getCanonicalName());
        types.add(RespirationModule.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
