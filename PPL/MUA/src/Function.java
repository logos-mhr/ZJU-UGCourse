import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

class Function {
    private static String getWordName(WordStream stream)
            throws SyntaxException, RunningException {
        Value value = Interpreter.value(stream);
        String name = value.toString();
        if (value.isList() || !name.matches("[_a-zA-Z].*"))
            throw new SyntaxException("Except a word literary but get " + name);
        else
            return name;
    }


    private static Value booleanValue(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        if (first.isBool())
            return first;
        String op = stream.next();
        Value second;
        switch (op) {
            case "<":
                second = Interpreter.value(stream);
                return compare(first, second, (BigDecimal a, BigDecimal b) -> a.compareTo(b) < 0, (String a, String b) -> a.compareTo(b) < 0);
            case "<=":
                second = Interpreter.value(stream);
                return compare(first, second, (BigDecimal a, BigDecimal b) -> a.compareTo(b) <= 0, (String a, String b) -> a.compareTo(b) <= 0);
            case "=":
                second = Interpreter.value(stream);
                return compare(first, second, (BigDecimal a, BigDecimal b) -> a.compareTo(b) == 0, (String a, String b) -> a.compareTo(b) == 0);
            case ">":
                second = Interpreter.value(stream);
                return compare(first, second, (BigDecimal a, BigDecimal b) -> a.compareTo(b) > 0, (String a, String b) -> a.compareTo(b) > 0);
            case ">=":
                second = Interpreter.value(stream);
                return compare(first, second, (BigDecimal a, BigDecimal b) -> a.compareTo(b) >= 0, (String a, String b) -> a.compareTo(b) >= 0);
            default:
                throw new SyntaxException("Expected >, <, =, <=, >=, but got" + op);
        }
    }

    static Value thing(WordStream stream)
            throws RunningException, SyntaxException {
        return thing(getWordName(stream));
    }

    static Value thing(String name)
            throws RunningException {
        return Interpreter.wordList.thing(name);
    }

    static Value isName(WordStream stream)
            throws RunningException, SyntaxException {
        return Interpreter.wordList.isname(getWordName(stream));
    }

    static Value read(WordStream stream) {
        System.out.print("Input a word: ");
        WordStream tmp = new WordStream();
        String word = tmp.next();
        stream.merge(tmp);
        return new Word(word);
    }

    static Value readLinst() {
        System.out.print("Input a list: > ");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = bufferedReader.readLine();
            String[] list = line.split("\\s+");
            return new List(list);
        } catch (IOException e) {
            e.printStackTrace();
            return new Word();
        }
    }

    static Value numericOperate(Value first, Value second, NumericOperator numericOperator)
            throws RunningException {
        BigDecimal a = first.toNumeric();
        BigDecimal b = second.toNumeric();
        try {
            return new Word(numericOperator.apply(a, b));
        }catch(ArithmeticException e) {
            throw new RunningException("Cannot divided by zero");
        }
    }

    private static Value numericOperate(WordStream stream, NumericOperator numericOperator)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        Value second = Interpreter.value(stream);
        return numericOperate(first, second, numericOperator);
    }

    private static Value compare(WordStream stream, NumericComparator numericComparator, StringComparator stringComparator)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        Value second = Interpreter.value(stream);
        return compare(first, second, numericComparator, stringComparator);
    }

    private static Value compare(Value first, Value second, NumericComparator numericComparator, StringComparator stringComparator)
            throws RunningException {
        if (first.isNumber() && second.isNumber()) {
            BigDecimal a = first.toNumeric();
            BigDecimal b = second.toNumeric();
            return new Word(numericComparator.apply(a, b).toString());
        } else {
            String a = first.toString();
            String b = second.toString();
            return new Word(stringComparator.apply(a, b));
        }
    }

    private static Value booleanOperate(Value first, Value second, BooleanBinaryOperator booleanBinaryOperator)
            throws RunningException {
        Boolean a = first.toBoolean();
        Boolean b = second.toBoolean();
        return new Word(String.valueOf(booleanBinaryOperator.apply(a, b)));
    }

    private static Value booleanOperate(WordStream stream, BooleanBinaryOperator booleanBinaryOperator)
            throws RunningException, SyntaxException {
        Value first = booleanValue(stream);
        Value second = booleanValue(stream);
        return booleanOperate(first, second, booleanBinaryOperator);
    }

    static Value add(WordStream stream)
            throws RunningException, SyntaxException {
        return numericOperate(stream, BigDecimal::add);
    }

    static Value sub(WordStream stream)
            throws RunningException, SyntaxException {
        return numericOperate(stream, BigDecimal::subtract);
    }

    static Value mul(WordStream stream)
            throws RunningException, SyntaxException {
        return numericOperate(stream, BigDecimal::multiply);
    }

    static Value div(WordStream stream)
            throws RunningException, SyntaxException {
        return numericOperate(stream, (BigDecimal a, BigDecimal b) -> a.divide(b, 16, BigDecimal.ROUND_HALF_EVEN));

    }

    static Value mod(WordStream stream)
            throws RunningException, SyntaxException {
        return numericOperate(stream, BigDecimal::remainder);
    }

    static Value eq(WordStream stream)
            throws RunningException, SyntaxException {
        return compare(stream, BigDecimal::equals, String::equals);
    }

    static Value lt(WordStream stream)
            throws RunningException, SyntaxException {
        return compare(stream, (BigDecimal a, BigDecimal b) -> a.compareTo(b) < 0, (String a, String b) -> a.compareTo(b) < 0);
    }

    static Value gt(WordStream stream)
            throws RunningException, SyntaxException {
        return compare(stream, (BigDecimal a, BigDecimal b) -> a.compareTo(b) > 0, (String a, String b) -> a.compareTo(b) > 0);
    }

    static Value and(WordStream stream)
            throws RunningException, SyntaxException {
        return booleanOperate(stream, Boolean::logicalAnd);
    }

    static Value or(WordStream stream)
            throws RunningException, SyntaxException {
        return booleanOperate(stream, Boolean::logicalOr);
    }

    static Value not(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = booleanValue(stream);
        Boolean a = first.toBoolean();
        return new Word(String.valueOf(!a));
    }

    static Value random(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        try {
            BigDecimal max = first.toNumeric();
            if (max.compareTo(new BigDecimal("0")) <= 0)
                throw new RunningException("Range errors.");
            BigDecimal randFromDouble = new BigDecimal(Math.random());
            BigDecimal actualRandomDec = randFromDouble.multiply(max);
            return new Word(actualRandomDec.toString());
        } catch (NumberFormatException e) {
            throw new RunningException(first.toString() + " cannot be converted to numeric");
        }
    }

    private static BigDecimal bigDecimalSqrt(BigDecimal n, BigDecimal ni, BigDecimal precision) {
        BigDecimal f = ni.pow(2).subtract(n);
        BigDecimal df = ni.multiply(new BigDecimal(2));
        BigDecimal f_df = f.divide(df, 300, BigDecimal.ROUND_HALF_DOWN);
        ni = ni.subtract(f_df);
        BigDecimal currentSquare = ni.pow(2);
        BigDecimal currentPrecision = currentSquare.subtract(n);
        currentPrecision = currentPrecision.abs();
        if (currentPrecision.compareTo(precision) <= -1) {
            return ni;
        }
        return bigDecimalSqrt(n, ni, precision);
    }

    private static BigDecimal bigDecimalSqrt(BigDecimal c) {
        //noinspection BigDecimalMethodWithoutRoundingCalled
        return bigDecimalSqrt(c, BigDecimal.ONE, BigDecimal.ONE.divide(BigDecimal.TEN.pow(300))).setScale(16, BigDecimal.ROUND_HALF_EVEN);
    }

    static Value sqrt(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        BigDecimal a = first.toNumeric();
        if (a.compareTo(BigDecimal.ZERO) < 0)
            throw new RunningException(first.toString() + " is less than zero.");
        return new Word(bigDecimalSqrt(a));

    }

    static Value isnumber(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        if (first.isNumber())
            return Value.TRUE;
        else
            return Value.FALSE;
    }

    static Value islist(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        if (first.isList())
            return Value.TRUE;
        else
            return Value.FALSE;
    }

    static Value isbool(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        if (first.isBool())
            return Value.TRUE;
        else
            return Value.FALSE;
    }

    static Value isempty(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        if (first.isEmpty())
            return Value.TRUE;
        else
            return Value.FALSE;
    }


    static Value word(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        Value second = Interpreter.value(stream);
        if (first.isList() || second.isList())
            throw new RunningException("Word can only concatenate two words.");
        return new Word(first.toString() + second.toString());
    }

    static Value list(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        Value second = Interpreter.value(stream);
        return List.list(first, second);
    }

    static Value sentence(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        Value second = Interpreter.value(stream);
        return List.sentence(first, second);
    }

    static Value join(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        Value second = Interpreter.value(stream);
        return List.join(first, second);
    }

    static Value item(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        Value second = Interpreter.value(stream);
        BigInteger index = first.toInt();
        return second.item(index);
    }


    static Value first(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        return first.item(BigInteger.ZERO);
    }

    static Value last(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        return first.item(BigInteger.valueOf(first.size() - 1));
    }

    static Value butfirst(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        return first.but(0);
    }

    static Value butlast(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        return first.but(first.size() - 1);
    }

    static Value Int(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        BigDecimal value = first.toNumeric();
        return new Word(value.setScale(0, BigDecimal.ROUND_FLOOR));
    }

    static Value abs(WordStream stream)
            throws RunningException, SyntaxException {
        Value first = Interpreter.value(stream);
        BigDecimal value = first.toNumeric();
        return new Word(value.abs());
    }

    static void make(WordStream stream)
            throws RunningException, SyntaxException {
        Interpreter.wordList.make(getWordName(stream), stream);
    }

    static void erase(WordStream stream)
            throws RunningException, SyntaxException {
        Interpreter.wordList.erase(getWordName(stream));
    }

    static void print(WordStream stream)
            throws RunningException, SyntaxException {
        System.out.println(Interpreter.value(stream).toString());
    }

    static void output(WordStream stream)
            throws RunningException, SyntaxException {
        Interpreter.wordList.make(WordList.outputWordName, stream);
    }

    private static Value getFunction(String functionName)
            throws RunningException, SyntaxException {
        if (Interpreter.wordList.contains(functionName)) {
            Value function = Interpreter.wordList.thing(functionName);
            if (!function.isList() || function.isEmpty())
                throw new RunningException(functionName + " is not a function");
            Value functionArg = function.item(0);
            if (functionArg == null || !functionArg.isList())
                throw new RunningException(functionName + " is not a function");
            Value functionBody = function.item(1);
            if (functionBody == null || !functionBody.isList())
                throw new RunningException(functionName + " is not a function");
            Value dummy = function.item(2);
            if (dummy != null)
                throw new RunningException(functionName + " is not a function");
            return function;
        } else {
            throw new SyntaxException("Unexpected token: " + functionName);
        }
    }

    static Value run(String functionName, WordStream stream)
            throws RunningException, SyntaxException {
        Value function = getFunction(functionName);
        HashMap<String, Value> tmpSpace = new HashMap<>();
        addFunctionArg(stream, function, tmpSpace);
        try {
            Interpreter.wordList.addSpace(tmpSpace);
            Interpreter.interpret(function.item(1).toWordStream());
            if (Interpreter.wordList.contains(WordList.outputWordName)) {
                return Interpreter.wordList.getOutput();
            } else {
                return null;
            }
        } finally {
            Interpreter.wordList.endSpace();
        }
    }

    static boolean Return(WordStream stream)
            throws SyntaxException, RunningException {
        String next = stream.next();
        if (WordStream.keyWrods.contains(next) ||
                next.matches("^[:\\[(\"].*$") ||
                next.matches("[+-]?\\d+(\\.\\d+)?")) {
            Interpreter.wordList.make(WordList.outputWordName, stream.putBack(next));
            return true;
        }
        Value function = getFunction(next);
        HashMap<String, Value> tmpSpace = new HashMap<>();
        addFunctionArg(stream, function, tmpSpace);
        stream.replace(function.item(1).toWordStream());
        Interpreter.wordList.replaceSpace(tmpSpace);
        return false;
    }

    private static void addFunctionArg(WordStream stream, Value function, HashMap<String, Value> space)
            throws RunningException, SyntaxException {
        for (Value arg : function.item(0)) {
            if (arg.isList()) {
                throw new RunningException("List is not a valid argument.");
            }
            Value v = Interpreter.value(stream);
            space.put(arg.toString(), v);
        }
    }

    static void run(WordStream stream)
            throws RunningException, SyntaxException {
        Value runBody = Interpreter.value(stream);
        if (!runBody.isList())
            throw new RunningException("Only list can be run.");
        stream.putBack(runBody.toWordStream());
    }

    static void repeat(WordStream stream)
            throws RunningException, SyntaxException {
        Value timesValue = Interpreter.value(stream);
        BigInteger times = timesValue.toInt();
        Value loopBody = Interpreter.value(stream);
        if (!loopBody.isList())
            throw new RunningException("Only list can be repeated.");
        WordStream loopBodyStream = loopBody.toWordStream();
        for (BigInteger i = BigInteger.ZERO; i.compareTo(times) < 0; i = i.add(BigInteger.ONE))
            stream.putBack(loopBodyStream);
    }

    static void export() {
        for (Map.Entry<String, Value> entry : Interpreter.wordList)
            if (!entry.getKey().equals(WordList.outputWordName))
                Interpreter.wordList.export(entry.getKey(), entry.getValue());
    }

    static void wait(WordStream stream)
            throws RunningException, SyntaxException {
        Value timeValue = Interpreter.value(stream);
        long i = timeValue.toInt().longValue();
        try {
            sleep(i);
        } catch (InterruptedException e) {
            throw new RunningException("Sleeping was interrupted.");
        }
    }

    static void save(WordStream stream)
            throws RunningException, SyntaxException {
        Value path = Interpreter.value(stream);
        if (path.isList())
            throw new RunningException("List is not a valid file name");
        Interpreter.wordList.save(path.toString());
    }

    static void load(WordStream stream)
            throws RunningException, SyntaxException {
        Value path = Interpreter.value(stream);
        if (path.isList())
            throw new RunningException("List is not a valid file name");
        Interpreter.wordList.load(path.toString());
    }

    static void erall() {
        Interpreter.wordList.clear();

    }

    static void poall() {
        Interpreter.wordList.print();
    }

    static void If(WordStream stream)
            throws RunningException, SyntaxException {
        Value value = booleanValue(stream);
        if (!value.isBool())
            throw new RunningException("Expected boolean value, but got " + value.toString());
        Value trueSection = Interpreter.value(stream);
        Value falseSection = Interpreter.value(stream);
        if (!trueSection.isList())
            throw new RunningException("Expected a list, but got " + trueSection.toString());
        if (!falseSection.isList())
            throw new RunningException("Expected a list, but got " + falseSection.toString());
        switch (value.toString()) {
            case "true":
                stream.putBack(trueSection.toWordStream());
                break;
            case "false":
                stream.putBack(falseSection.toWordStream());
                break;
        }
    }

}
