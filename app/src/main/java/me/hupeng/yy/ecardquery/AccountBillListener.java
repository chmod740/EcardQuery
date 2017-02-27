package me.hupeng.yy.ecardquery;

import java.util.List;

/**
 * Created by HUPENG on 2017/2/27.
 */
public interface AccountBillListener {
    public void done(List<EcardQuery.AccountBill>accountBills,Exception e);
}
