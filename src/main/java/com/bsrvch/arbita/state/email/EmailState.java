package com.bsrvch.arbita.state.email;

public enum EmailState {
    EMAIL_REQ(0),
    EMAIL_ENTER(1),
    EMAIL_CONFIRM(2);
    private int index;
    EmailState(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
