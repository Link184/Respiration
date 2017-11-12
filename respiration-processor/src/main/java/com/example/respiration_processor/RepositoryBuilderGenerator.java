package com.example.respiration_processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Created by jora on 11/12/17.
 */

public class RepositoryBuilderGenerator {

    public void generateRepository(Map<String, String> repositoriesWithPackages) {
        TypeSpec.Builder repositoryClass = TypeSpec
                .classBuilder("Navigator")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Map.Entry<String, String> element : repositoriesWithPackages.entrySet()) {
            String repositoryName = element.getKey();
            String packageName = element.getValue();
            ClassName activityClass = ClassName.get(packageName, repositoryName);
//            MethodSpec intentMethod = MethodSpec
//                    .methodBuilder(METHOD_PREFIX + repositoryName)
//                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                    .returns(classIntent)
//                    .addParameter(classContext, "context")
//                    .addStatement("return new $T($L, $L)", classIntent, "context", activityClass + ".class")
//                    .build();

//            repositoryClass.addMethod(intentMethod);
        }
    }
}
