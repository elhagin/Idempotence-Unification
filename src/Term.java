import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by omarelhagin on 13/12/16.
 */
public class Term {
    String term;
    ArrayList<Term> children;

    public Term(String term) {
        this.term = term;
    }

    public Term(String term, ArrayList<Term> children) {
        this.term = term;
        this.children = children;
    }

    public void normalize(ArrayList<Character> idempotentFuncs) {
        if (idempotentFuncs.contains(term.charAt(0))) {
            if (children.size() == 1 && children.get(0).term.equals(term)) {
                children.add(children.get(0).children.get(0));
                children.remove(0);
                children.get(0).normalize(idempotentFuncs);
            }
            if (children.size() == 2 && children.get(0).term.equals(children.get(1).term)) {
                term = children.get(0).term;
                children = null;
            }
        }
    }

    @Override
    public String toString() {
        if (children == null) {
            return term;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(term + ": [");
        Iterator<Term> iter = children.iterator();
        while (iter.hasNext()) {
            Term t = iter.next();
            if (children.get(0) != t) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(t.toString());
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
