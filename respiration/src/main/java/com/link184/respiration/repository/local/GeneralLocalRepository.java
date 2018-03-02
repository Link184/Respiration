package com.link184.respiration.repository.local;

import android.content.Context;

/**
 * Created by Ryzen on 3/2/2018.
 */

public class GeneralLocalRepository<M> extends LocalRepository<M>{
    public GeneralLocalRepository(Context context, LocalConfiguration localConfiguration) {
        super(context, localConfiguration);
    }

    @Override
    protected void initRepository() {

    }

    @Override
    protected void setValue(M newValue) {

    }

    @Override
    protected void removeValue() {

    }

}
