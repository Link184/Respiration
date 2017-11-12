package com.example.respiration_processor;

import com.google.auto.service.AutoService;
import com.link184.respiration_annotation.RespirationRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class RespirationProcessor extends AbstractProcessor{
    private Messager messager;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Map<String, String> repositoriesWithPackages = new HashMap<>();
        for (Element element : roundEnvironment.getElementsAnnotatedWith(RespirationRepository.class)) {

            messager.printMessage(Diagnostic.Kind.NOTE, "Processing respiration repository: " + element.getSimpleName());
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.NOTE, "RespirationRepository annotation can be applied to class.");
                return true;
            }

            TypeElement typeElement = (TypeElement) element;
            repositoriesWithPackages.put(
                    typeElement.getSimpleName().toString(),
                    elements.getPackageOf(typeElement).getQualifiedName().toString());
        }

        return false;
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(RespirationRepository.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
