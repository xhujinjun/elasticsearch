/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.sql.expression.function.scalar.whitelist;

import org.elasticsearch.index.fielddata.ScriptDocValues;
import org.elasticsearch.script.JodaCompatibleZonedDateTime;
import org.elasticsearch.xpack.sql.SqlIllegalArgumentException;
import org.elasticsearch.xpack.sql.expression.function.scalar.datetime.DateTimeFunction;
import org.elasticsearch.xpack.sql.expression.function.scalar.datetime.NamedDateTimeProcessor.NameExtractor;
import org.elasticsearch.xpack.sql.expression.function.scalar.datetime.QuarterProcessor;
import org.elasticsearch.xpack.sql.expression.function.scalar.math.BinaryMathProcessor.BinaryMathOperation;
import org.elasticsearch.xpack.sql.expression.function.scalar.math.MathProcessor.MathOperation;
import org.elasticsearch.xpack.sql.expression.function.scalar.string.BinaryStringNumericProcessor.BinaryStringNumericOperation;
import org.elasticsearch.xpack.sql.expression.function.scalar.string.BinaryStringStringProcessor.BinaryStringStringOperation;
import org.elasticsearch.xpack.sql.expression.function.scalar.string.ConcatFunctionProcessor;
import org.elasticsearch.xpack.sql.expression.function.scalar.string.InsertFunctionProcessor;
import org.elasticsearch.xpack.sql.expression.function.scalar.string.LocateFunctionProcessor;
import org.elasticsearch.xpack.sql.expression.function.scalar.string.ReplaceFunctionProcessor;
import org.elasticsearch.xpack.sql.expression.function.scalar.string.StringProcessor.StringOperation;
import org.elasticsearch.xpack.sql.expression.function.scalar.string.SubstringFunctionProcessor;
import org.elasticsearch.xpack.sql.expression.predicate.logical.BinaryLogicProcessor.BinaryLogicOperation;
import org.elasticsearch.xpack.sql.expression.predicate.conditional.CoalesceProcessor;
import org.elasticsearch.xpack.sql.expression.predicate.logical.NotProcessor;
import org.elasticsearch.xpack.sql.expression.predicate.nulls.IsNotNullProcessor;
import org.elasticsearch.xpack.sql.expression.predicate.operator.arithmetic.BinaryArithmeticProcessor.BinaryArithmeticOperation;
import org.elasticsearch.xpack.sql.expression.predicate.operator.arithmetic.UnaryArithmeticProcessor.UnaryArithmeticOperation;
import org.elasticsearch.xpack.sql.expression.predicate.operator.comparison.BinaryComparisonProcessor.BinaryComparisonOperation;
import org.elasticsearch.xpack.sql.expression.predicate.operator.comparison.InProcessor;
import org.elasticsearch.xpack.sql.expression.predicate.regex.RegexProcessor.RegexOperation;
import org.elasticsearch.xpack.sql.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Whitelisted class for SQL scripts.
 * Acts as a registry of the various static methods used <b>internally</b> by the scalar functions
 * (to simplify the whitelist definition).
 */
@SuppressWarnings("unused")
public final class InternalSqlScriptUtils {

    private InternalSqlScriptUtils() {}

    //
    // Utilities
    //

    // safe missing mapping/value extractor
    public static <T> Object docValue(Map<String, ScriptDocValues<T>> doc, String fieldName) {
        if (doc.containsKey(fieldName)) {
            ScriptDocValues<T> docValues = doc.get(fieldName);
            if (!docValues.isEmpty()) {
                return docValues.get(0);
            }
        }
        return null;
    }
    
    public static boolean nullSafeFilter(Boolean filter) {
        return filter == null ? false : filter.booleanValue();
    }

    public static double nullSafeSortNumeric(Number sort) {
        return sort == null ? 0.0d : sort.doubleValue();
    }

    public static String nullSafeSortString(Object sort) {
        return sort == null ? StringUtils.EMPTY : sort.toString();
    }


    //
    // Operators
    //

    //
    // Logical
    //
    public static Boolean eq(Object left, Object right) {
        return BinaryComparisonOperation.EQ.apply(left, right);
    }

    public static Boolean neq(Object left, Object right) {
        return BinaryComparisonOperation.NEQ.apply(left, right);
    }

    public static Boolean lt(Object left, Object right) {
        return BinaryComparisonOperation.LT.apply(left, right);
    }
    
    public static Boolean lte(Object left, Object right) {
        return BinaryComparisonOperation.LTE.apply(left, right);
    }

    public static Boolean gt(Object left, Object right) {
        return BinaryComparisonOperation.GT.apply(left, right);
    }

    public static Boolean gte(Object left, Object right) {
        return BinaryComparisonOperation.GTE.apply(left, right);
    }

    public static Boolean and(Boolean left, Boolean right) {
        return BinaryLogicOperation.AND.apply(left, right);
    }
    
    public static Boolean or(Boolean left, Boolean right) {
        return BinaryLogicOperation.OR.apply(left, right);
    }

    public static Boolean not(Boolean expression) {
        return NotProcessor.apply(expression);
    }

    public static Boolean notNull(Object expression) {
        return IsNotNullProcessor.apply(expression);
    }

    public static Boolean in(Object value, List<Object> values) {
        return InProcessor.apply(value, values);
    }

    //
    // Null
    //
    public static Object coalesce(List<Object> expressions) {
        return CoalesceProcessor.apply(expressions);
    }

    //
    // Regex
    //
    public static Boolean regex(String value, String pattern) {
        return RegexOperation.match(value, pattern);
    }

    //
    // Math
    //
    public static Number add(Number left, Number right) {
        return BinaryArithmeticOperation.ADD.apply(left, right);
    }

    public static Number div(Number left, Number right) {
        return BinaryArithmeticOperation.DIV.apply(left, right);
    }

    public static Number mod(Number left, Number right) {
        return BinaryArithmeticOperation.MOD.apply(left, right);
    }

    public static Number mul(Number left, Number right) {
        return BinaryArithmeticOperation.MUL.apply(left, right);
    }

    public static Number neg(Number value) {
        return UnaryArithmeticOperation.NEGATE.apply(value);
    }

    public static Number sub(Number left, Number right) {
        return BinaryArithmeticOperation.SUB.apply(left, right);
    }

    public static Number round(Number v, Number s) {
        return BinaryMathOperation.ROUND.apply(v, s);
    }

    public static Number truncate(Number v, Number s) {
        return BinaryMathOperation.TRUNCATE.apply(v, s);
    }

    public static Double abs(Number value) {
        return MathOperation.ABS.apply(value);
    }

    public static Double acos(Number value) {
        return MathOperation.ACOS.apply(value);
    }

    public static Double asin(Number value) {
        return MathOperation.ASIN.apply(value);
    }

    public static Double atan(Number value) {
        return MathOperation.ATAN.apply(value);
    }

    public static Double cbrt(Number value) {
        return MathOperation.CBRT.apply(value);
    }

    public static Double ceil(Number value) {
        return MathOperation.CEIL.apply(value);
    }

    public static Double cos(Number value) {
        return MathOperation.COS.apply(value);
    }

    public static Double cosh(Number value) {
        return MathOperation.COSH.apply(value);
    }

    public static Double cot(Number value) {
        return MathOperation.COT.apply(value);
    }

    public static Double degrees(Number value) {
        return MathOperation.DEGREES.apply(value);
    }

    public static Double e(Number value) {
        return MathOperation.E.apply(value);
    }

    public static Double exp(Number value) {
        return MathOperation.EXP.apply(value);
    }

    public static Double expm1(Number value) {
        return MathOperation.EXPM1.apply(value);
    }

    public static Double floor(Number value) {
        return MathOperation.FLOOR.apply(value);
    }

    public static Double log(Number value) {
        return MathOperation.LOG.apply(value);
    }

    public static Double log10(Number value) {
        return MathOperation.LOG10.apply(value);
    }

    public static Double pi(Number value) {
        return MathOperation.PI.apply(value);
    }

    public static Double radians(Number value) {
        return MathOperation.RADIANS.apply(value);
    }

    public static Double random(Number value) {
        return MathOperation.RANDOM.apply(value);
    }

    public static Double sign(Number value) {
        return MathOperation.SIGN.apply(value);
    }

    public static Double sin(Number value) {
        return MathOperation.SIN.apply(value);
    }

    public static Double sinh(Number value) {
        return MathOperation.SINH.apply(value);
    }

    public static Double sqrt(Number value) {
        return MathOperation.SQRT.apply(value);
    }

    public static Double tan(Number value) {
        return MathOperation.TAN.apply(value);
    }

    //
    // Date/Time functions
    //
    public static Integer dateTimeChrono(Object dateTime, String tzId, String chronoName) {
        if (dateTime == null || tzId == null || chronoName == null) {
            return null;
        }
        return DateTimeFunction.dateTimeChrono(asDateTime(dateTime), tzId, chronoName);
    }
    
    public static String dayName(Object dateTime, String tzId) {
        if (dateTime == null || tzId == null) {
            return null;
        }
        return NameExtractor.DAY_NAME.extract(asDateTime(dateTime), tzId);
    }
    
    public static String monthName(Object dateTime, String tzId) {
        if (dateTime == null || tzId == null) {
            return null;
        }
        return NameExtractor.MONTH_NAME.extract(asDateTime(dateTime), tzId);
    }
    
    public static Integer quarter(Object dateTime, String tzId) {
        if (dateTime == null || tzId == null) {
            return null;
        }
        return QuarterProcessor.quarter(asDateTime(dateTime), tzId);
    }

    private static ZonedDateTime asDateTime(Object dateTime) {
        if (dateTime instanceof JodaCompatibleZonedDateTime) {
            return ((JodaCompatibleZonedDateTime) dateTime).getZonedDateTime();
        }
        throw new SqlIllegalArgumentException("Invalid date encountered [{}]", dateTime);
    }
    
    //
    // String functions
    //
    public static Integer ascii(String s) {
        return (Integer) StringOperation.ASCII.apply(s);
    }
    
    public static Integer bitLength(String s) {
        return (Integer) StringOperation.BIT_LENGTH.apply(s);
    }

    public static String character(Number n) {
        return (String) StringOperation.CHAR.apply(n);
    }

    public static Integer charLength(String s) {
        return (Integer) StringOperation.CHAR_LENGTH.apply(s);
    }
    
    public static String concat(String s1, String s2) {
        return (String) ConcatFunctionProcessor.process(s1, s2);
    }

    public static String insert(String s, Number start, Number length, String r) {
        return (String) InsertFunctionProcessor.doProcess(s, start, length, r);
    }

    public static String lcase(String s) {
        return (String) StringOperation.LCASE.apply(s);
    }

    public static String left(String s, Number count) {
        return BinaryStringNumericOperation.LEFT.apply(s, count);
    }

    public static Integer length(String s) {
        return (Integer) StringOperation.LENGTH.apply(s);
    }

    public static Integer locate(String s1, String s2) {
        return locate(s1, s2, null);
    }
    
    public static Integer locate(String s1, String s2, Number pos) {
        return LocateFunctionProcessor.doProcess(s1, s2, pos);
    }

    public static String ltrim(String s) {
        return (String) StringOperation.LTRIM.apply(s);
    }
    
    public static Integer octetLength(String s) {
        return (Integer) StringOperation.OCTET_LENGTH.apply(s);
    }

    public static Integer position(String s1, String s2) {
        return (Integer) BinaryStringStringOperation.POSITION.apply(s1, s2);
    }
    
    public static String repeat(String s, Number count) {
        return BinaryStringNumericOperation.REPEAT.apply(s, count);
    }
    
    public static String replace(String s1, String s2, String s3) {
        return (String) ReplaceFunctionProcessor.doProcess(s1, s2, s3);
    }
    
    public static String right(String s, Number count) {
        return BinaryStringNumericOperation.RIGHT.apply(s, count);
    }

    public static String rtrim(String s) {
        return (String) StringOperation.RTRIM.apply(s);
    }

    public static String space(Number n) {
        return (String) StringOperation.SPACE.apply(n);
    }

    public static String substring(String s, Number start, Number length) {
        return (String) SubstringFunctionProcessor.doProcess(s, start, length);
    }

    public static String ucase(String s) {
        return (String) StringOperation.UCASE.apply(s);
    }
}
