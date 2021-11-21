package com.t0ugh.server.utils;

import com.t0ugh.server.GlobalContext;
import com.t0ugh.server.enums.TransactionState;

import java.util.Objects;

public class StateUtils {
    public static boolean isNowTransaction(GlobalContext globalContext){
        return Objects.equals(TransactionState.Transaction, globalContext.getGlobalState().getTransactionState());
    }
    public static void startTransaction(GlobalContext globalContext){
        globalContext.getGlobalState().setTransactionState(TransactionState.Transaction);
    }
    public static void endTransaction(GlobalContext globalContext){
        globalContext.getGlobalState().setTransactionState(TransactionState.Normal);
    }
}
