//Maksim KOZLOV

import java.util.*;


class Solver {
    //operations priorities
    private final Map<String, Integer> priorities = new HashMap<>();
    //truth table
    private final boolean[][] table;
    //string in Reverse Polish Notation
    private final String rpn;
    //table to determine the operand location in the truth table
    private final Map<String, Integer> operands = new HashMap<>();
    private final int operandCnt;
    private final int rowCnt;

    private void initializePriorities(){
        priorities.put("~", 0);
        priorities.put("&", 1);
        priorities.put("|", 2);
        priorities.put("-->", 3);
        priorities.put("<-->", 4);

    }

    //constructor. receives the infix record
    public Solver(String infix){
        initializePriorities();
        this.rpn = toRPN(infix);
        initializeOperandMap();
        this.operandCnt = this.operands.size();
        this.rowCnt =(int) Math.pow(2, this.operandCnt);
        this.table = getTruthTable();
    }

    private boolean isLetterOrBracket(char ch){
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '(' || ch == ')';
    }

    //Split a string into parts for convenience
    public List<String> tokenize(String s){
        int i = 0;
        int n = s.length();
        StringBuilder token = new StringBuilder();
        List<String> result = new ArrayList<>();
        while (i < n){
            char ch = s.charAt(i);
            token.append(ch);
            if (ch == '-'){
                token.append(s, i+1, i+3);
                i+=3;
            }
            else if (ch == '<'){
                token.append(s, i+1, i+4);
                i+=4;
            }

            else if (isLetterOrBracket(ch)){
                i++;
                while (i < n && isLetterOrBracket(s.charAt(i))){
                    token.append(s.charAt(i));
                    i++;
                }
            }
            else
                i++;
            if (!token.toString().equals(" "))
                result.add(token.toString());
            token.setLength(0);

        }
        return result;
    }

    //RPN wiki algorithm realization (it's not coded there, just explanation)
    private String toRPN(String infix){
        StringBuilder builder = new StringBuilder();
        Stack<String> stack = new Stack<>();
        List<String> tokens = tokenize(infix);
        for (String cur : tokens) {
            if (cur.equals("~")){
                stack.push(cur);
            }
            else if (priorities.containsKey(cur)) {
                if (!stack.isEmpty()) {
                    String top = stack.pop();
                    while (priorities.containsKey(top) && priorities.get(top) <= priorities.get(cur)) {
                        builder.append(top).append(' ');
                        if (stack.isEmpty()) break;
                        top = stack.pop();
                    }
                }
                stack.push(cur);
            }
            else {
                if (cur.equals("["))
                    stack.push(cur);
                else if (cur.equals("]")) {
                    String top = stack.pop();
                    while (!top.equals("[")) {
                        builder.append(top).append(' ');
                        if (stack.isEmpty()) break;
                        top = stack.pop();
                    }
                } else {
                    builder.append(cur).append(' ');
                }
            }
        }
        while(!stack.isEmpty()){
            String top = stack.pop();
            builder.append(top).append(' ');
        }
        return builder.toString();
    }

    //get unique operans
    private List<String> getOperands(){
        String[] tokens = this.rpn.split(" ");
        Set<String> operands = new HashSet<>();
        for (String token : tokens){
            if (!priorities.containsKey(token) && !token.equals("~"))
                operands.add(token);
        }
        return new ArrayList<>(operands);
    }

    //according to the operand record his number
    private void initializeOperandMap(){
        List<String> operands = getOperands();
        int n = operands.size();
        for (int i = 0; i < n; i++){
            this.operands.put(operands.get(i), i);
        }
    }

    //generating of truth table
    private boolean[][] getTruthTable(){
        boolean[][] table = new boolean[rowCnt][this.operandCnt + 1];
        for (int i = 0; i < rowCnt; i++){
            int t = i;
            int j = 0;
            while (t > 0){
                table[i][j++] = t % 2 != 0;
                t/=2;
            }
            table[i][this.operandCnt] = evaluate(table[i]);
        }
        return table;
    }

    //substitute a row from a table into an expression
    //calculate with RPN as wiki says to
    private boolean evaluate(boolean[] row){
        String[] tokens = this.rpn.split(" ");
        Stack<Boolean> stack = new Stack<>();
        for (String token : tokens) {
            if (token.equals("~")) {
                stack.push(!stack.pop());
            } else if (priorities.containsKey(token)) {
                boolean op2 = stack.pop();
                boolean op1 = stack.pop();
                boolean res = false;
                switch (token) {
                    case "&" -> res = op1 & op2;
                    case "|" -> res = op1 | op2;
                    case "-->" -> res = !op1 | op2;
                    case "<-->" -> res = op1 == op2;
                }
                stack.push(res);
            }
            else {
                //to realize which operand is used, address to the table
                stack.push(row[this.operands.get(token)]);
            }
        }
        return stack.pop();
    }


    //in my opinion, it exists only if the table of truth is only 1 false, 
    //although perhaps the logic is based rather on construction through disjunct, but is constructed through implication
    private String getHornClause(){
        //count the amount of false
        int falseCnt = 0;
        int falseRow = -1;
        for (int row = 0; row < rowCnt; row++) {
            if (!table[row][operandCnt]) {
                falseCnt++;
                falseRow = row;
            }
        }
        if (falseCnt != 1)
            return "not exist";

        //there can be only 1 not inversed 
        int inverseCnt = 0;
        StringBuilder builder = new StringBuilder();
        String inverseOperand = null;
        for (int i = 0; i < operandCnt; i++){
            String operand = null;
            for (String op : operands.keySet())
                if (operands.get(op) == i)
                    operand = op;
            //need to inverse
            if (!table[falseRow][i]){
                inverseCnt++;
                inverseOperand = operand;
            }
            builder.append(operand);
            if (i != operandCnt - 1)
                builder.append(" & ");
        }
        if (inverseCnt <= 1) {
            builder.append(" --> ");
            //if nothing has been inversed, by definition of Horn Clause implication into false
            builder.append(Objects.requireNonNullElse(inverseOperand, "F"));
            return builder.toString();
        }
        else
            return "not exist";
    }

    //get result
    public String getNormalForm(String type){
        String bracketSeparator = null;
        String separator = null;
        boolean isDNF = false;
        switch (type){
            case "A" -> {
                bracketSeparator = " | ";
                separator = " & ";
            }
            case "B" -> {
                isDNF = true;
                bracketSeparator = " & ";
                separator = " | ";
            }
            case "C" -> {
                return getHornClause();
            }
        }
        StringBuilder builder = new StringBuilder();
        int trueCnt = 0;
        for (int row = 0; row < rowCnt; row++){
            if (table[row][operandCnt]) trueCnt++;
            StringBuilder bracketBuilder = new StringBuilder();
            for (int i = 0; i < operandCnt; i++){
                //define the opearnd
                String operand = null;
                for (String op : operands.keySet())
                    if (operands.get(op) == i)
                        operand = op;
                //decide whether to add in response with inverstion (на вики по СДНФ и СКНФ смотрится)
                if (!table[row][i] && isDNF || table[row][i] && !isDNF)
                    bracketBuilder.append("~");
                bracketBuilder.append(operand);
                if (i != operandCnt - 1){
                    bracketBuilder.append(bracketSeparator);
                }
            }
            //according to the table, this is the working condition
            //if isDNF == false -> we got CNF -> needed 0 in the truth table ; same if isDNF = true
            if (table[row][operandCnt] == isDNF)
                builder.append("[").append(bracketBuilder).append("]").append(separator);
        }
        if (trueCnt == rowCnt)
            return "T";
        if (trueCnt == 0)
            return "F";
        int len = builder.length();
        builder.delete(len-2, len-1);
        return builder.toString();
    }
}

public class q2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        String X = scanner.nextLine();
        Solver solver = new Solver(s);
        System.out.println(solver.getNormalForm(X));
        /*
        for self tests
        System.out.println(solver.getNormalForm("A"));
        System.out.println(solver.getNormalForm("B"));
        System.out.println(solver.getNormalForm("C"));
        */
    }
}
