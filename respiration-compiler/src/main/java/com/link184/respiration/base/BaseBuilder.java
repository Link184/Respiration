package com.link184.respiration.base;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;

import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;

/**
 * Created by eugeniu on 3/6/18.
 */

public interface BaseBuilder {
    String CLASS_NAME_SUFFIX = "Builder";
    String METHOD_GET_INSTANCE = "getInstance";
    String METHOD_CREATE_INSTANCE = "createInstance";

    List<JavaFile> generateRepository(Map<Element, String> repositoriesWithPackages);

    FieldSpec generateRepositoryInstance(Map.Entry<Element, String> entry);

    MethodSpec generateMethodCreate(Map.Entry<Element, String> entry);

    MethodSpec generateMethodSingleInit(Map.Entry<Element, String> entry);
}
