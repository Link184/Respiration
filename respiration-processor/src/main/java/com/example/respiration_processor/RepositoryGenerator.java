package com.example.respiration_processor;

import com.link184.respiration_annotation.RespirationRepository;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by jora on 11/12/17.
 */

public class RepositoryGenerator {

    public List<JavaFile> generateRepository(Map<TypeElement, String> repositoriesWithPackages) {
        List<JavaFile> javaFiles = new ArrayList<>();

        for (Map.Entry<TypeElement, String> entry : repositoriesWithPackages.entrySet()) {
            String repositoryName = entry.getKey().getSimpleName().toString();
            String packageName = entry.getValue();

            RespirationRepository annotation = entry.getKey().getAnnotation(RespirationRepository.class);
            TypeSpec.Builder repositoryClass = TypeSpec
                    .classBuilder(ClassName.get(packageName, repositoryName + "Impl"))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addTypeVariable(TypeVariableName.get("?", annotation.dataSnapshotType()))
                    .addMethod(generateMethodCreate(entry));

            javaFiles.add(JavaFile.builder(packageName, repositoryClass.build())
                    .build());
//            MethodSpec intentMethod = MethodSpec
//                    .methodBuilder(METHOD_PREFIX + repositoryName)
//                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                    .returns(classIntent)
//                    .addParameter(classContext, "context")
//                    .addStatement("return new $T($L, $L)", classIntent, "context", activityClass + ".class")
//                    .build();

//            repositoryClass.addMethod(intentMethod);
        }
        return javaFiles;
    }

    private MethodSpec generateMethodCreate(Map.Entry<TypeElement, String> entry) {
        return MethodSpec
                .methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(TypeName.get(entry.getKey().asType()))
                .addStatement("return new $T()", entry.getKey())
                .build();
    }

    private void generateConfiguration() {
//        TypeSpec.classBuilder()
    }

//    public static class Builder<M> {
//        private Configuration<M> configuration;
//
//        /**
//         * @param dataSnapshotType just a firebase model Class. Because of erasing is impossible take
//         *                         java class type form generic in runtime. So we are forced to ask
//         *                         model type explicitly in constructor alongside generic type.
//         */
//        public Builder(Class<M> dataSnapshotType) {
//            configuration = new Configuration<>(dataSnapshotType);
//        }
//
//        /**
//         * Firebase data persistence.
//         */
//        public Builder<M> setPersistence(boolean persistence) {
//            configuration.setPersistence(persistence);
//            return this;
//        }
//
//        /**
//         * @param databaseChildren enumerate all children to build a {@link DatabaseReference} object.
//         */
//        public Builder<M> setChildren(String... databaseChildren) {
//            configuration.setDatabaseChildren(databaseChildren);
//            return this;
//        }
//
//        /**
//         * Set true if the data is private for non logged in users. That logic will handle all
//         * authentication cases. Be careful when repository is already built with no
//         * authenticated user with uid in database reference path, just call resetRepository() method
//         * after successful authentication with right uid in path.
//         */
//        public Builder<M> setAccessPrivate(boolean accessPrivate) {
//            configuration.setAccessPrivate(accessPrivate);
//            return this;
//        }
//
//        public GeneralRepository<M> build() {
//            return new GeneralRepository<>(configuration);
//        }
//    }
}
