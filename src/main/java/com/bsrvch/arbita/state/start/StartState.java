package com.bsrvch.arbita.state.start;

public enum StartState {
    START(0),
    CHOICE_LOCALE(1),
    REGISTER(2),
    FINISH(3);
    private int index;

    StartState(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
