package com.link184.respiration.repository.local;

/**
 * Created by eugeniu on 3/6/18.
 */

public class NotListableRepository extends Throwable {
    public NotListableRepository() {
        super("Primitive json is not allowed in a ListRepository");
    }
}
