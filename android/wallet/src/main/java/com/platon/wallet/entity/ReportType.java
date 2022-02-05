package com.platon.wallet.entity;


import androidx.annotation.IntDef;

@IntDef({
        ReportType.DUPLICATE_SIGN
})
public @interface ReportType {

    /**
     * 双签
     */
    int DUPLICATE_SIGN = 1;
}
