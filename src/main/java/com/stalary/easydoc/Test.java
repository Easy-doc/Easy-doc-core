/**
 * @Test.java, 2018-09-25.
 * <p>
 * Copyright 2018 Stalary.
 */
package com.stalary.easydoc;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Controller: Test
 * @author lirongqian
 * @since 2018/09/25
 */
public class Test {

    public static void main(String[] args) throws Exception {
        // 获取当前路径
        String curPath = System.getProperty("user.dir");
        FileReader fileReader = new FileReader(curPath + "/src/test/java/com/stalary/easydoc/ReadTest.java");
        BufferedReader reader = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();
        String s = reader.readLine();
        while (s != null) {
            sb.append(s);
            s = reader.readLine();
        }
        String regex="\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*\\/";
        String string="代码1/**注释1*/代码2/**注释2*/代码3";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(sb);
        while(matcher.find()){
            String temp = matcher.group().replace("/*", "<doc>").replace("*/", "</doc>").replace("*", "");
            System.out.println(temp);
            Document document = DocumentHelper.parseText(temp);
            Element root = document.getRootElement();
            // iterate through child elements of root
            for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
                Element element = it.next();
                System.out.println(element.getName());
                System.out.println(element.getData());
            }
        }
    }

}