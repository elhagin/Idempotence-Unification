import java.lang.reflect.Array;
import java.util.ArrayList;

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

    public static void unifyId(String term1Str, String term2Str, ArrayList<Character> idempotentFuncs) {
        Term term1 = parseFOLTerm(term1Str);
        Term term2 = parseFOLTerm(term2Str);

        term1.normalize(idempotentFuncs);
        term2.normalize(idempotentFuncs);

        System.out.println(term1 + "\n" + term2);
//        System.out.println(term1.term + ": " + term1.children.get(0).term + "\n"
//                + term1.children.get(0).term + ": " + term1.children.get(0).children.get(0).term);
    }

    public static void main(String[] args) {
        ArrayList<Character> s = new ArrayList<>();
        s.add('P');
        unifyId("P(P(P(x)))", "f(g(u),g(g(z)),z)",
                s);
    }
}
