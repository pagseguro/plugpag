package br.com.uol.pagseguro.plugpagandroiddemo;

import java.util.Stack;

public final class PreviousTransactions {

    // ---------------------------------------------------------------------------------------------
    // Class attributes
    // ---------------------------------------------------------------------------------------------

    private static final Stack<String[]> sStack = new Stack<>();

    // ---------------------------------------------------------------------------------------------
    // Stacking
    // ---------------------------------------------------------------------------------------------

    /**
     * Pushes a value into the stack.
     *
     * @param value Value to be pushed.
     */
    public static final void push(String[] value) {
        PreviousTransactions.sStack.push(value);
    }

    /**
     * Pops a value from the stack.
     *
     * @return Popped value.
     */
    public static final String[] pop() {
        String[] value = null;

        if (PreviousTransactions.sStack.size() > 0) {
            value = PreviousTransactions.sStack.pop();
        }

        return value;
    }

}
