package net.alexandroid.network.cctvportscanner.utils;


import java.util.ArrayList;

public class Utils {

    public static ArrayList<Integer> convertStringToIntegerList(String ports) {
        ArrayList<Integer> list = new ArrayList<>();
        String regex = "[^0-9,\\-]";
        String result = ports.replaceAll(regex, "");

        String[] lines = result.split(",");
        for (String line : lines) {
            if (line.contains("-")) {
                String[] p = line.split("\\-");
                if (p.length > 1) {
                    if (p[0].length() > 0 && p[1].length() > 0
                            && p[0].length() < 6 && p[1].length() < 6) {
                        int from = Integer.valueOf(p[0]);
                        int to = Integer.valueOf(p[1]);
                        if (from < to && to < 65535 && from > 0) {
                            for (int a = from; a <= to; a++) {
                                list.add(a);
                            }
                        }
                    }
                }
            } else {
                if (line.length() > 0 && line.length() < 6) {
                    int check_port = Integer.valueOf(line);
                    if (check_port > 0 & check_port < 65536)
                        list.add(check_port);
                }
            }
        }
        return list;
    }

    public static String convertIntegerListToString(ArrayList<Integer> list) {
        StringBuilder stringBuilder = new StringBuilder();
        int firstRangeNum = -1;
        for (int i = 0; i < list.size(); i++) {
            if (i + 1 < list.size() && list.get(i + 1) - 1 == list.get(i)) {
                if (firstRangeNum < 0) {
                    firstRangeNum = list.get(i);
                }
            } else {
                if (firstRangeNum > -1) {
                    stringBuilder.append(firstRangeNum);
                    stringBuilder.append("-");
                    firstRangeNum = -1;
                }
                stringBuilder.append(list.get(i));
                if (i + 1 < list.size()) {
                    stringBuilder.append(",");
                }
            }

        }

        return stringBuilder.toString();
    }


    public static void appendRedText(StringBuilder pResult, Integer num) {
        pResult.append("<font color=#FF0000>");
        pResult.append(num);
        pResult.append("</font> ");

    }

    public static void appendRedText(StringBuilder pResult, String str) {
        pResult.append("<font color=#FF0000>");
        pResult.append(str);
        pResult.append("</font> ");
    }

    public static void appendGreenText(StringBuilder pResult, Integer num) {
        pResult.append("<font color=#4CAF50>");
        pResult.append(num);
        pResult.append("</font> ");


    }

    public static void appendGreenText(StringBuilder pResult, String str) {
        pResult.append("<font color=#4CAF50>");
        pResult.append(str);
        pResult.append("</font> ");
    }
}
