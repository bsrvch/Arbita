package com.bsrvch.arbita.state.payment;

public enum PaymentState {
    PAY_CREATE(0),
    PAY_SHOW(1),
    PAY_CONFIRM(2);

    private int index;

    PaymentState(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
