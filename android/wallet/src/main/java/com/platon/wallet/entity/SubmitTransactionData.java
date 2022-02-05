package com.platon.wallet.entity;

/**
 * @author ziv
 * date On 2020-03-02
 */
public class SubmitTransactionData {

    public String signedData;

    public String remark;

    public SubmitTransactionData(String signedData, String remark) {
        this.signedData = signedData;
        this.remark = remark;
    }

    public String getSignedData() {
        return signedData;
    }

    public void setSignedData(String signedData) {
        this.signedData = signedData;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
