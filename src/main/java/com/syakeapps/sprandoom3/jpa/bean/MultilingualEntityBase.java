package com.syakeapps.sprandoom3.jpa.bean;

import java.util.Locale;

public abstract class MultilingualEntityBase {

    protected abstract String getJpnName();

    protected abstract String getEngName();

    public String getLocalizedName(Locale locale) {
        return locale.equals(Locale.JAPANESE) ? getJpnName() : getEngName();
    }
}
