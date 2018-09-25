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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Controller: Test
 * @author lirongqian
 * @since 2018/09/25
 */
public class Test {

    @SuppressWarnings("unchecked")
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
        List<Map<String, String>> list = new ArrayList<>();
        while(matcher.find()){
            Map<String, String> map = new HashMap<>();
            String temp = matcher.group().replace("/*", "<doc>").replace("*/", "</doc>").replace("*", "");
            Document document = DocumentHelper.parseText(temp);
            Element root = document.getRootElement();
            // iterate through child elements of root
            for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
                Element element = it.next();
                map.put(element.getName(), element.getText());
            }
            list.add(map);
        }
        System.out.println(list);
    }

}