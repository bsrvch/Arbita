package com.bsrvch.arbita.state.filter;

public enum FilterState {

    FILTER_START(0),
    FILTER_CHOOSE(1),
    FILTER_SEARCH(2),
    FILTER_ADD(3);



    private int index;
    FilterState(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
