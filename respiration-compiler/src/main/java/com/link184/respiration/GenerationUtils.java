package com.link184.respiration;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Created by jora on 12/24/17.
 */

public class GenerationUtils {
    static CodeBlock generateChildrenArray(RespirationRepository annotation) {
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


    static TypeName extractTypeName(RespirationRepository annotation) {
        TypeMirror classModel = null;
        try {
            annotation.dataSnapshotType();
        } catch (MirroredTypeException mte) {
            classModel = mte.getTypeMirror();
        }
        return TypeName.get(classModel);
    }
}
