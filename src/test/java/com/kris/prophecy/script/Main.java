package com.kris.prophecy.script;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        List<String> list =new ArrayList<>(Arrays.asList(s.split("")));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals("*")) {
                list.remove(i);
                list.add(0, "*");
            }
        }
        list.forEach(System.out::print);
    }

}


