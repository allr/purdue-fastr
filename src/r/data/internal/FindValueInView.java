package r.data.internal;

import r.data.*;

public class FindValueInView implements ValueVisitor {

    final Object lookFor;
    int count;

    public FindValueInView(Object value) {
        this.lookFor = value;
    }

    public int getCount() {
        return count;
    }

    public static int countOccurrences(RAny view, Object value) {
        FindValueInView find = new FindValueInView(value);
        find.visit(view);
        return find.getCount();
    }

    public void visit(RAny value) {
        if (lookFor == value) {
            count++;
        }
        value.visit_all(this);
    }

}
