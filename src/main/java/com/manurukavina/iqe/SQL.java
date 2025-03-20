package com.manurukavina.iqe;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class SQL {
    public static String test(String sql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);

            if (statement instanceof Select) {
                PlainSelect select = (PlainSelect) ((Select) statement).getSelectBody();
                System.out.println("Parsed SELECT: " + select);
                return select.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing SQL: " + e.getMessage();
        }

        return "Invalid SQL statement";
    }
}
