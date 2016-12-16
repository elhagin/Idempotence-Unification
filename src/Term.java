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
//        if (children != null) {
//            if (idempotentFuncs.contains(term.charAt(0))) {
//                if (children.size() == 1 && children.get(0).term.equals(term)) {
//                    children.add(children.get(0).children.get(0));
//                    children.remove(0);
//                    children.get(0).normalize(idempotentFuncs);
//                }
//                if (children.size() == 2 && children.get(0).term.equals(children.get(1).term)) {
//                    term = children.get(0).term;
//                    children = null;
//                }
//            }
//        }
        if (children != null) {
//            if (children.size() > 2 || (children.size() == 2 && (children.get(0).children != null
//                    || children.get(1).children != null))) {
                children.forEach(term1 -> term1.normalize(idempotentFuncs));
//            }
            if (children.size() == 1 && children.get(0).term.equals(term)) {
                if (idempotentFuncs.contains(term.charAt(0))) {
                    children.add(children.get(0).children.get(0));
                    children.remove(0);
                    children.get(0).normalize(idempotentFuncs);
                }
            }
            else {
                if (children.size() == 2 && children.get(0).term.equals(children.get(1).term)) {
                    if (idempotentFuncs.contains(term.charAt(0))) {
                        term = children.get(0).term;
                        children = null;
                    }
                }
                else {

                }
            }

        }
    }

    @Override
    public String toString() {
        if (children == null) {
            return term;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(term + "(");
        Iterator<Term> iter = children.iterator();
        while (iter.hasNext()) {
            Term t = iter.next();
            if (children.get(0) != t) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(t.toString());
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Term)) {
            return false;
        }
        return term.equals(((Term) obj).term)
                && (children == null ? ((Term) obj).children == null : children.equals(((Term) obj).children));
    }

    @Override
    public int hashCode() {
        int h = term.hashCode();
        if (children != null) {
            h += children.hashCode();
        }
        return h;
    }

    public boolean occursIn(Object secondTerm) {
        Term term2 = (Term) secondTerm;
        if (this.term.equals(term2.term)) {
            return true;
        }
        if (term2.children == null) {
            return false;
        }

        return term2.children.stream().map(t -> this.occursIn(t)).anyMatch(Boolean::booleanValue);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        if (children == null) {
            return new Term(term, null);
        }
        else {
            return new Term(term, (ArrayList) children.clone());
        }
    }

    public void replace(String originalVal, Term replacement) {
        if (term.equals(originalVal) && children == null) {
            term = replacement.term;
            children = replacement.children;
        }

        if (children != null) {
            children.forEach(t -> t.replace(originalVal, replacement));
        }
    }
}
