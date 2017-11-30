package com.link184.respiration;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created by jora on 11/25/17.
 */

public class RespirationModuleGenerator {
    private Messager messager;

    public RespirationModuleGenerator(Messager messager) {
        this.messager = messager;
    }

    List<JavaFile> generateModule(Map<TypeElement, String> repositoriesWithPackages) {
        List<JavaFile> javaFiles = new ArrayList<>();

        for (Map.Entry<TypeElement, String> entry : repositoriesWithPackages.entrySet()) {
            String repositoryName = entry.getKey().getSimpleName().toString();
            String packageName = entry.getValue();

            TypeSpec.Builder repositoryClass = TypeSpec
                    .classBuilder(ClassName.get(packageName, repositoryName + "Builder"))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(generateGetters(entry));
//                    .addField(generateRepositoryInstance(entry))
//                    .addMethod(generateMethodCreate(entry))
//                    .addMethod(generateMethodSingleInit(entry));

            javaFiles.add(JavaFile.builder(packageName, repositoryClass.build())
                    .build());
        }
        return javaFiles;
    }

    private MethodSpec generateGetters(Map.Entry<TypeElement, String> entry) {
        List<? extends Element> enclosedElements = entry.getKey().getEnclosedElements();
        for (Element element : enclosedElements) {
            String diffAnnotationName = findAnnotationValue(element,
                    RespirationRepository.class.getCanonicalName(), element.asType().toString(), element.asType());
            if (element.asType().getAnnotation(RespirationRepository.class) != null) {
                messager.printMessage(Diagnostic.Kind.NOTE, element.asType().toString() );
//                messager.printMessage(Diagnostic.Kind.NOTE, "" + element.getAnnotationMirrors().size());
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "end");
        return null;
//        MethodSpec.methodBuilder()
    }

    private FieldSpec generateRepositoryInstance(Map.Entry<TypeElement, String> entry) {
        return FieldSpec
                .builder(TypeName.get(entry.getKey().asType()), "INSTANCE", Modifier.PRIVATE, Modifier.STATIC)
                .build();
    }


    private MethodSpec generateMethodCreate(Map.Entry<TypeElement, String> entry) {
        return MethodSpec
                .methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.get(entry.getKey().asType()))
                .beginControlFlow("if ($L == null)", "INSTANCE")
                .addStatement("$L = $L", "INSTANCE", "createInstance()")
                .endControlFlow()
                .addStatement("return $N", "INSTANCE")
                .build();
    }

    private MethodSpec generateMethodSingleInit(Map.Entry<TypeElement, String> entry) {
        ClassName configurationClass = ClassName.get("com.link184.respiration.repository", "Configuration");

        RespirationRepository annotation = entry.getKey().getAnnotation(RespirationRepository.class);
        TypeName modelTypeName = extractTypeName(annotation);
        return MethodSpec
                .methodBuilder("createInstance")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(TypeName.get(entry.getKey().asType()))
                .addStatement("$T<$T> configuration = new $T<>($T.class)",
                        configurationClass, modelTypeName, configurationClass, modelTypeName)
                .addStatement("configuration.setPersistence($L)", annotation.isAccessPrivate())
                .addStatement("configuration.setDatabaseChildren($L)", generateChildrenArray(annotation))
                .addStatement("configuration.setAccessPrivate($L)", annotation.isAccessPrivate())
                .addStatement("return new $T($N)", entry.getKey(), "configuration")
                .build();
    }

    private TypeVariableName extractModelClass(RespirationRepository annotation) {
        return TypeVariableName.get("T", extractTypeName(annotation));
    }

    private TypeName extractTypeName(RespirationRepository annotation) {
        TypeMirror classModel = null;
        try {
            annotation.dataSnapshotType();
        } catch (MirroredTypeException mte) {
            classModel = mte.getTypeMirror();
        }
        return TypeName.get(classModel);
    }

    private CodeBlock generateChildrenArray(RespirationRepository annotation) {
        String[] children = annotation.children();
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.add("new $T{", String[].class);
        for (int i = 0; i < children.length; i++) {
            if (i < children.length - 1) {
                builder.add("$S,", children[i]);
            } else {
                builder.add("$S", children[i]);
            }
        }
        builder.add("$N", "}");
        return builder.build();
    }

    private <T> T findAnnotationValue(Element element, String annotationClass,
                                             String valueName, Class<T> expectedType) {
        T ret = null;
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            DeclaredType annotationType = annotationMirror.getAnnotationType();
            TypeElement annotationElement = (TypeElement) annotationType
                    .asElement();
            if (annotationElement.getQualifiedName().contentEquals(
                    annotationClass)) {
                ret = extractValue(annotationMirror, valueName, expectedType);
                break;
            }
        }
        return ret;
    }

    private <T> T extractValue(AnnotationMirror annotationMirror,
                                      String valueName, Class<T> expectedType) {
        Map<ExecutableElement, AnnotationValue> elementValues = new HashMap<ExecutableElement, AnnotationValue>(
                annotationMirror.getElementValues());
        for (Map.Entry<ExecutableElement, AnnotationValue> entry : elementValues
                .entrySet()) {
            if (entry.getKey().getSimpleName().contentEquals(valueName)) {
                Object value = entry.getValue().getValue();
                return expectedType.cast(value);
            }
        }
        return null;
    }
}
