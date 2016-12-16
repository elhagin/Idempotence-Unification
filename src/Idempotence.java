import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by omarelhagin on 13/12/16.
 */
public class Idempotence {
    public static Term parseFOLTerm(String termStr) {
        Term finalTerm = null;
        int level = 0;

        if (termStr.length() == 1) {
            finalTerm = new Term(termStr);
        }
        else {
            ArrayList<Term> tree = new ArrayList<>();
            ArrayList<Term> children = new ArrayList<>();
            finalTerm = new Term(termStr.charAt(0) + "", children);
            tree.add(finalTerm);
            for (int i = 1; i < termStr.length(); i++) {
                Term currentTerm = tree.get(level);
                char character = termStr.charAt(i);
                switch(character) {
                    case '(':
                        break;
                    case ')':
                        tree.remove(tree.get(level--));
                        break;
                    case ',':
                        break;
                    default:
                        if (i + 1 < termStr.length() && termStr.charAt(i + 1) == '(') {
                            ArrayList<Term> chil = new ArrayList<>();
                            Term term = new Term(character + "", chil);
                            currentTerm.children.add(term);
                            tree.add(term);
                            level++;
                        }
                        else {
                            Term term = new Term(character + "");
                            currentTerm.children.add(term);
                        }
                }
            }
        }

        return finalTerm;
    }

    /**
     *
     * @param term1Str
     * @param term2Str
     * @param MGU
     * @param idempotentFuncs
     * @return
     */
    public static LinkedHashMap<Term, Term> unifyId(String term1Str, String term2Str, LinkedHashMap<Term, Term> MGU,
                                              ArrayList<Character> idempotentFuncs) {
        Term term1 = parseFOLTerm(term1Str);
        Term term2 = parseFOLTerm(term2Str);

        term1.normalize(idempotentFuncs);
        term2.normalize(idempotentFuncs);

        LinkedHashMap<Term, Term> result = unify(term1, term2, MGU, idempotentFuncs);
        Iterator iter = result.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Term, Term> entry = (Map.Entry<Term, Term>) iter.next();
            Iterator iter2 = result.entrySet().iterator();
            while (iter2.hasNext()) {
                if (entry.getKey().equals(new Term("x"))) {
                    System.out.println();
                }
                Map.Entry<Term, Term> entry2 = (Map.Entry<Term, Term>) iter2.next();
                if (entry.getKey().occursIn(entry2.getValue())) {
                    entry2.getValue().replace(entry.getKey().term, entry.getValue());
                }
            }
        }

        return result;
//        System.out.println(term1 + "\n" + term2);
//        if (MGU.get(new Term("")) != null) {
//            return null;
//        }
//        if (term1.equals(term2)) {
//            return MGU;
//        }

        /**
         * Constants are the first 13 letters in the English alphabet. Variables are the last 13 letters.
         */
//        if (term1.term.length() == 1) {
//
//            char term1Char = term1.term.toLowerCase().charAt(0);
//            if (term1Char >= 'n' && term1Char <= 'z') { //if term1 is a variable
////            return unifyVar(term1, term2, MGU);
//            }
//        }
//
//        if (term2.term.length() == 1) {
//            char term2Char = term2.term.toLowerCase().charAt(0);
//            if (term2Char >= 'n' && term2Char <= 'z') { //if term2 is a variable
////            return unifyVar(term2, term1, MGU);
//            }
//        }
//
//        if (term1.children != null && term2.children != null) {
////            return unifyId()
//        }
//        System.out.println(term1.children + "\n" + term2.children);
//        if (!term1.term.equals(term2.term) && (term1.children == null || term2.children == null)) {
//            if (term1.children == null && term2.children == null) {
//                return term2 + "/" + term1;
//            }
//            if (term1.children != null) {
//
//            }
//        }

//        return null;
    }

    public static LinkedHashMap<Term, Term> unifyVar(Term variable, Object secondTerm, LinkedHashMap<Term, Term> MGU,
                                                     ArrayList<Character> idempotentFuncs) {
        if (MGU != null && MGU.containsKey(variable)) {
            return unify(MGU.get(variable), secondTerm, MGU, idempotentFuncs);
        }
        if (MGU != null && secondTerm instanceof Term && MGU.containsKey(secondTerm)) {
            return unify(variable, MGU.get(secondTerm), MGU, idempotentFuncs);
        }
        if (variable.occursIn(secondTerm)) {
            LinkedHashMap<Term, Term> failMGU = new LinkedHashMap<>();
            failMGU.put(new Term(""), new Term(""));
            return failMGU;
        }
        if (MGU == null) {
            LinkedHashMap<Term, Term> theMGU = new LinkedHashMap<>();
            theMGU.put(variable, ((Term) secondTerm));
            return theMGU;
        }
        else {
            MGU.put(variable, ((Term) secondTerm));
            Iterator iter = MGU.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Term, Term> entry = (Map.Entry<Term, Term>) iter.next();
                if (entry.getValue().equals(variable)) {
                    entry.setValue(((Term) secondTerm));
                }
            }
            return MGU;
        }
    }

    public static LinkedHashMap<Term, Term> unify(Object firstTerm, Object secondTerm, LinkedHashMap<Term, Term> MGU,
                                                  ArrayList<Character> idempotentFuncs) {
        if (MGU != null && !MGU.isEmpty()) {
            Iterator iter = MGU.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Term, Term> entry = (Map.Entry<Term, Term>) iter.next();
                if (firstTerm instanceof Term) {
                    Term firstTermT = (Term) firstTerm;
                    firstTermT.replace(entry.getKey().term, entry.getValue());
                }
                if (firstTerm instanceof ArrayList) {
                    ArrayList<Term> firstTermL = (ArrayList) firstTerm;
                    firstTermL.forEach(t -> t.replace(entry.getKey().term, entry.getValue()));
                }
                if (secondTerm instanceof Term) {
                    Term secondTermT = (Term) secondTerm;
                    secondTermT.replace(entry.getKey().term, entry.getValue());
                }
                if (secondTerm instanceof ArrayList) {
                    ArrayList<Term> secondTermL = (ArrayList) secondTerm;
                    secondTermL.forEach(t -> t.replace(entry.getKey().term, entry.getValue()));
                }
            }
        }
        if (firstTerm instanceof Term) {
            Term firstTermT = (Term) firstTerm;
            firstTermT.normalize(idempotentFuncs);
        }
        if (firstTerm instanceof ArrayList) {
            ArrayList<Term> firstTermL = (ArrayList) firstTerm;
            firstTermL.forEach(t -> t.normalize(idempotentFuncs));
        }
        if (secondTerm instanceof Term) {
            Term secondTermT = (Term) secondTerm;
            secondTermT.normalize(idempotentFuncs);
        }
        if (secondTerm instanceof ArrayList) {
            ArrayList<Term> secondTermL = (ArrayList) secondTerm;
            secondTermL.forEach(t -> t.normalize(idempotentFuncs));
        }
        if (MGU != null && MGU.get(new Term("")) != null) {//The empty string term represents failure
            return MGU;
        }

        if (firstTerm.equals(secondTerm)) {
            return MGU;
        }

        if (firstTerm instanceof Term && secondTerm instanceof Term) {
            Term term1 = (Term) firstTerm;
            Term term2 = (Term) secondTerm;

            if (term1.children != null && term2.children != null) {
                return unify(term1.children, term2.children,
                        unify(term1.term, term2.term, MGU, idempotentFuncs), idempotentFuncs);
            }
        }

        /**
         * Constants are the first 13 letters in the English alphabet. Variables are the last 13 letters.
         */
        if (firstTerm instanceof Term && ((Term) firstTerm).term.length() == 1) {
            Term term1 = (Term) firstTerm;
            char term1Char = term1.term.toLowerCase().charAt(0);
            if (term1Char >= 'n' && term1Char <= 'z') { //if term1 is a variable
                return unifyVar(term1, secondTerm, MGU, idempotentFuncs);
            }
        }

        if (secondTerm instanceof Term && ((Term) secondTerm).term.length() == 1) {
            Term term2 = (Term) secondTerm;
            char term2Char = term2.term.toLowerCase().charAt(0);
            if (term2Char >= 'n' && term2Char <= 'z') { //if term2 is a variable
                return unifyVar(term2, firstTerm, MGU, idempotentFuncs);
            }
        }
        if (firstTerm instanceof ArrayList && secondTerm instanceof ArrayList) {
            ArrayList<Term> firstTermCopy = cloneList((ArrayList) firstTerm);
            ArrayList<Term> secondTermCopy = cloneList((ArrayList) secondTerm);
            Object firstInT1 = firstTermCopy.remove(0);
            Object firstInT2 = secondTermCopy.remove(0);
            return unify(firstTermCopy, secondTermCopy,
                    unify(firstInT1, firstInT2, MGU, idempotentFuncs), idempotentFuncs);
        }

        LinkedHashMap<Term, Term> failMGU = new LinkedHashMap<>();
        failMGU.put(new Term(""), new Term(""));
        return failMGU;
    }

    public static ArrayList<Term> cloneList(ArrayList<Term> a) {
        ArrayList<Term> b = new ArrayList<>(a.size());
        for (Term t : a) {
            try {
                b.add((Term) t.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        return b;
    }

    public static void main(String[] args) {
        ArrayList<Character> fIdem = new ArrayList<>();
        ArrayList<Character> gIdem = new ArrayList<>();
        ArrayList<Character> noIdem = new ArrayList<>();
        String[] firstTest = {"P(x,g(x),g(f(a)))", "P(f(u),v,v)"};
        String[] secondTest = {"P(a,y,f(y))", "P(z,z,u)"};
        String[] thirdTest = {"f(x,g(x),x)", "f(g(u),g(g(z)),z)"};
        String[] fourthTest = {"f(x,g(x))", "f(g(x),x)"};
        String normalizationTest = "g(s(x),e(y),f(y,f(y,y)),m(f(f(y))))";
        fIdem.add('f');
        gIdem.add('g');
        System.out.println(unifyId(firstTest[0], firstTest[1], new LinkedHashMap<>(),
                fIdem));
        System.out.println(unifyId(secondTest[0], secondTest[1], new LinkedHashMap<>(),
                noIdem));
        System.out.println(unifyId(thirdTest[0], thirdTest[1], new LinkedHashMap<>(),
                fIdem));
        System.out.println(unifyId(thirdTest[0], thirdTest[1], new LinkedHashMap<>(),
                gIdem));
        System.out.println(unifyId(fourthTest[0], fourthTest[1], new LinkedHashMap<>(),
                gIdem));
    }
}
