package out.production.test.src.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class DenGUI extends JPanel {

    public DenGUI(){}
    static int num = 10;
    static Map<String,Integer> lightMap; //灯泡Map，格式为<灯泡字母，灯泡状态>(1为开，0为关)
    static ArrayList<char[]> control; //control为对应关系二维数组，每一行为一个开关，列为开关对应的灯泡
    static Map<String,JLabel> lightLabelMap;//存储ligthlabel的map,<灯泡字母，灯泡label>
    static ArrayList<JButton> switchArr;//存储开关控件的list
    static  JLabel listOfbuttonLable;//展示序列
    static int min = Integer.MAX_VALUE;//最少的灯数，求最大能关多少时使用
    static int ON = 0;//亮着的灯的数量
    static int[] swichOrder;//开关按下数组，0为不按，1为按
    static int[] copyt;//数组t的拷贝
    static String listOfButtonStr;
    static boolean isBreak = false;
    static boolean hasSovle = false;
    static Map<String, Integer> solveLightMap;

    public static void main(String args[])
    {
        DenGUI box=new DenGUI();
//        box.go();
//        box.test();
        // 创建 JFrame 实例
        JFrame frame = new JFrame("Login Example");
        // Setting the width and height of frame
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        frame.add(panel);
        //获取输入
        placeInputComponents(panel);

        frame.setVisible(true);

    }

    //放置输入组件
    private static void placeInputComponents(JPanel panel) {

        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        // 创建输入JLabel
        JLabel lightInputLabel = new JLabel("lightInput:");
        lightInputLabel.setBounds(10,20,80,25);
        panel.add(lightInputLabel);
        // 开关输入
        JLabel switchInputLabel = new JLabel("switchInput:");
        switchInputLabel.setBounds(10,50,80,25);
        panel.add(switchInputLabel);

        // 灯泡输出标签
        JLabel lightOutputLabel = new JLabel("lightOutput:");
        lightOutputLabel.setBounds(10,150,80,25);
        panel.add(lightOutputLabel);
        // 开关输出标签
        JLabel switchOutputLabel = new JLabel("switchOutput:");
        switchOutputLabel.setBounds(10,200,80,25);
        panel.add(switchOutputLabel);
        // 序列输出标签
        JLabel listOfButtonLabel = new JLabel("listOfButton:");
        listOfButtonLabel.setBounds(10, 250, 80, 25);
        panel.add(listOfButtonLabel);
        /*
         * 创建文本域用于用户输入
         */
        JTextField firstLine = new JTextField(20);
        firstLine.setBounds(100,20,165,25);
        panel.add(firstLine);
        JTextField secondLine = new JTextField(20);
        secondLine.setBounds(100,50,165,25);
        panel.add(secondLine);

        // 创建load按钮
        JButton loadButton = new JButton("Load");
        loadButton.setBounds(10, 80, 80, 25);
        //添加处理用户输入监听，生成灯和开关
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //先移除上次控件
                if(lightLabelMap!=null){
                    for(Map.Entry<String, JLabel> entry : lightLabelMap.entrySet()){
                        JLabel lightLabel = entry.getValue();
                        panel.remove(lightLabel);
                    }
                    lightLabelMap.clear();
                    lightMap.clear();
                    solveLightMap.clear();
                }
                if(listOfbuttonLable!=null){
                    panel.remove(listOfbuttonLable);
                }
                if(switchArr!=null){
                    for(JButton jButton : switchArr){
                        panel.remove(jButton);
                    }
                    switchArr.clear();
                }

                String inputLine1 = "A B C D";
                String inputLine2 = "AB AD BD CA";
                inputLine1 = firstLine.getText();
                inputLine2 = secondLine.getText();
                String[] line1Arr = inputLine1.split(" ");
                lightMap = new LinkedHashMap();
                for (int i = 0; i <line1Arr.length; i++) {
                    lightMap.put(line1Arr[i],1);
                }
                String[] line2Arr = inputLine2.split(" ");
                control = new ArrayList<>();

                for (int i = 0; i < line2Arr.length; i++) {
                    control.add(line2Arr[i].toCharArray());
                }
                //生成灯和开关
                int mapCounter = 0;
                int interval = 120;
                lightLabelMap = new HashMap<>();
                //生成灯
                for(Map.Entry<String, Integer> entry : lightMap.entrySet()){
                    String light = entry.getKey();
                    JLabel lightLabel = new JLabel(light);
                    lightLabel.setBounds(100 + mapCounter * interval, 150, 100, 25);
                    lightLabel.setBorder(BorderFactory.createLineBorder(Color.yellow,20));
                    lightLabelMap.put(light,lightLabel);
                    panel.add(lightLabel);
                    mapCounter++;
                }
                //生成开关并为开关添加监听
                switchArr = new ArrayList<JButton>();
                Listener listener = new Listener();
                for (int i = 0; i < control.size(); i++) {
                    JButton switchButton = new JButton(String.valueOf(control.get(i)));
                    switchButton.setBounds(100 + i * interval, 200, 100, 25);
                    switchButton.addActionListener(listener);
                    switchArr.add(switchButton);
                    panel.add(switchButton);
                }
                panel.repaint();
                //计算结果
                solve();
            }
        });
        panel.add(loadButton);

        //创建solve按钮
        JButton solveButton = new JButton("solve");
        solveButton.setBounds(150, 80, 80, 25);
        //添加监听，调用solve算法并演示
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(hasSovle && isBreak){
                    for (int i = 0; i < swichOrder.length; i++) {
                        if (copyt[i] == 1){
                                changeStaticOfLight(i);
                                copyt[i] = 0;
                                break;
                        }
                    }
                }
            }
        });
        panel.add(solveButton);

        //创建序列按钮
        JButton listOfButton = new JButton("listOfButton");
        listOfButton.setBounds(290, 80, 160, 25);
        listOfButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listOfbuttonLable!=null){
                    panel.remove(listOfbuttonLable);
                }
                listOfbuttonLable = new JLabel(listOfButtonStr);
                listOfbuttonLable.setBounds(100, 250, 80, 25);
                panel.add(listOfbuttonLable);
                panel.repaint();
            }
        });
        panel.add(listOfButton);

    }
    //开关监听
    static class Listener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCommand=e.getActionCommand();
            for (int i = 0; i <control.size() ; i++) {
                if(actionCommand.equals(String.valueOf(control.get(i)))){
                    changeStaticOfLight(i);
                    break;
                }
            }
        }
    }
    //改变灯泡状态，参数为传入开关序号
    static void changeStaticOfLight(int index){
        for(char ligth:control.get(index)){
            JLabel lightLabel = lightLabelMap.get(String.valueOf(ligth));
            //根据灯泡现在状态改变
            if(lightMap.get(String.valueOf(ligth)) == 1){
                lightMap.put(String.valueOf(ligth),0);
                lightLabel.setBorder(BorderFactory.createLineBorder(Color.black,1));
            }else{
                lightMap.put(String.valueOf(ligth),1);
                lightLabel.setBorder(BorderFactory.createLineBorder(Color.yellow,20));
            }
        }
    }


    //solve方法
    static void solve(){
        //初始化
        isBreak = false;
        hasSovle = false;
        listOfButtonStr = "";
        //拷贝当前lightmap，用于计算
        solveLightMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : lightMap.entrySet()) {
            solveLightMap.put(entry.getKey(),entry.getValue());
        }
        swichOrder = new int[control.size()];

        //调用回溯函数
        backTrace(0);
        if(min!=0){
            ON = min;
            swichOrder = new int[control.size()];
            for (Map.Entry<String, Integer> entry : lightMap.entrySet()) {
                solveLightMap.put(entry.getKey(),entry.getValue());
            }
            backTrace(0);
        }

    }
    //回溯算法,cur为目前运算到的开关序号
    static void backTrace(int cur){
        if (isBreak) return;
        if(cur == control.size()){
            int sum = 0;
            //统计开灯数量
            for (Map.Entry<String, Integer> entry : solveLightMap.entrySet()) {
                sum += entry.getValue();
            }
            min = sum<min ? sum:min;
            if( sum == ON){
                isBreak = true;
                if(ON == 0){
                    hasSovle = true;
                }
                copyt = new int[swichOrder.length];
                for (int i = 0; i < swichOrder.length; i++) {
                    if(swichOrder[i] == 1){
                        listOfButtonStr = listOfButtonStr + String.valueOf(control.get(i)) + " ";
                    }
                    copyt[i]=swichOrder[i];
                    System.out.print(swichOrder[i]);
                }
                System.out.println();
            }
            return;
        }
        swichOrder[cur] = 0;
        backTrace(cur+1);
        for (int i = 0; i<control.get(cur).length; i++) {
            if(solveLightMap.get(String.valueOf(control.get(cur)[i]))==1){
                solveLightMap.put(String.valueOf(control.get(cur)[i]),0);
            }else{
                solveLightMap.put(String.valueOf(control.get(cur)[i]),1);
            }
        }
        swichOrder[cur] = 1;
        backTrace( cur+1); //被选择需要改回来。
        for (int i = 0; i<control.get(cur).length; i++) {
            if(solveLightMap.get(String.valueOf(control.get(cur)[i]))==1){
                solveLightMap.put(String.valueOf(control.get(cur)[i]),0);
            }else{
                solveLightMap.put(String.valueOf(control.get(cur)[i]),1);
            }
        }
    }
}
