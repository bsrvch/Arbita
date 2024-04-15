package com.bsrvch.arbita.state.scanner;

public enum ScannerState {
    SCANNER_OPEN(0),
    SCANNER_START(1),
    SCANNER_STOP(2);

    private int index;

    ScannerState(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
