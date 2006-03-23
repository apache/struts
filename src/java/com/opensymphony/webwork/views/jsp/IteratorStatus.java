/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;


/**
 * The iterator tag can export an IteratorStatus object so that
 * one can get information about the status of the iteration, such as
 * the size, current index, and whether any more items are available.
 *
 * @author Rickard Öberg (rickard@dreambean.com)
 */
public class IteratorStatus {
    protected StatusState state;

    public IteratorStatus(StatusState aState) {
        state = aState;
    }

    public int getCount() {
        return state.index + 1;
    }

    public boolean isEven() {
        return ((state.index + 1) % 2) == 0;
    }

    public boolean isFirst() {
        return state.index == 0;
    }

    public int getIndex() {
        return state.index;
    }

    public boolean isLast() {
        return state.last;
    }

    public boolean isOdd() {
        return ((state.index + 1) % 2) == 1;
    }

    public int modulus(int operand) {
        return (state.index + 1) % operand;
    }

    public static class StatusState {
        boolean last = false;
        int index = 0;

        public void setLast(boolean isLast) {
            last = isLast;
        }

        public void next() {
            index++;
        }
    }
}
