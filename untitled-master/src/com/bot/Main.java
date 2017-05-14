package com.bot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static final double EPS = 0.01; //точность вычислений. Нужна для сравнения вещественных чисел.

    private static ArrayList<Point> points = new ArrayList<Point>(); //список точек
    public static void createGUI() {
        final JFrame frame = new JFrame("Testframe");
	    frame.setPreferredSize(new Dimension(700,700));
	    final JPanel panel = new JPanel(new BorderLayout());
        final Panel butPanel = new Panel();
        butPanel.setLayout(null);
        butPanel.setPreferredSize(new Dimension(250,700));
        final Panel pointpane = new Panel();
        pointpane.setLayout(null);
        //pointpane.setPreferredSize(new Dimension(350,700));

        // Добавлние полей и надписей на панель
	    JLabel addPointwithCoords = new JLabel("Добавить точку по координатам");
	    addPointwithCoords.setBounds(2,2,300,25);
	    butPanel.add(addPointwithCoords);
	    JLabel addRandomPoints = new JLabel("Добавить рандомное количество точек");
	    addRandomPoints.setBounds(2,50,300,25);
	    butPanel.add(addRandomPoints);
        JLabel X = new JLabel("X:");
        X.setBounds(2,25,15,25);
        butPanel.add(X);
        JLabel Y = new JLabel("Y:");
        Y.setBounds(45,25,15,25);
        butPanel.add(Y);
        JLabel Weight = new JLabel("W:");
        Weight.setBounds(88,25,15,25);
        butPanel.add(Weight);
        JLabel N = new JLabel("NUM:");
        N.setBounds(2,70,30,25);
        butPanel.add(N);
        final JTextField x = new JTextField();
        x.setBounds(17,25, 25,25);
        butPanel.add(x);
        final JTextField y = new JTextField();
        y.setBounds(60,25, 25,25);
        butPanel.add(y);
        final JTextField w = new JTextField();
        w.setBounds(111,25, 25,25);
        butPanel.add(w);
        final JTextField n = new JTextField();
        n.setBounds(35,70,25,25);
        butPanel.add(n);


        JButton button1 = new JButton("Добавить точку");
        button1.setBounds(25,100,160,40);
        butPanel.add(button1);
        button1.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                int X = (!x.getText().equals("")?Integer.parseInt(x.getText()):0);
                int Y= (!y.getText().equals("")?Integer.parseInt(y.getText()):0);
                int N = (!n.getText().equals("")?Integer.parseInt(n.getText()):0);
                double W = (!w.getText().equals("")?Double.parseDouble(w.getText()):0.0);
                if ((X>0)&&(Y>0)&&(W>0.0)) { //ввод числа по координатам
                    Point b = new Point(X, Y, W); //создаем объект класса Point -- передаём координаты и вес
                    /* Давайте хранить массив точек (points) отсортированным -- самые "тяжелые" по массе (weight)
                    точки будем хранить в конце массива, а самые "лёгкие" -- в начале. Тогда при добавлении новой точки
                    нам необходимо знать куда её добавлять, чтобы не сбить порядок. Давайте узнаем позицию, в которую
                    нужно добавить новую точку и запишем ее в r
                     */
                    int r = 0;
                    while (r != points.size() && points.get(r).weight < W) { /*увеличиваем r, пока мы вес r-го элемента списка не стал больше,
                                                                           чем вес новой точки, ну или пока массив не закончится (тогда точку
                                                                           надо просто добавить в конец).*/
                        r++;
                    }
                    points.add(r, b);
                    b.setBounds(b.x,b.y,b.x+3,b.y+3);
                    pointpane.add(b);
                    pointpane.revalidate();
                    pointpane.repaint();
                }
                else { //генерирование случайных точек
                    if (N>0){
                        for (int i=0;i<N;i++){
                            Point b = new Point(50 + (int)(Math.random()*(frame.getWidth()-300)), //Создаем объект класса Point --
                                    50 + (int)(Math.random()*(frame.getHeight() - 50)), //генерируем случайные координаты и случайную массу
                                    (double)Math.random()*10.0); //и передаём ей. Math.random() генерирует случайное число от 0 до 1.

                            int r = 0; // всё то ж самое, что выше
                            while (r != points.size() || points.get(r).weight < W) {
                                r++;
                            }
                            points.add(r, b);
                            b.setBounds(b.x,b.y,b.x+3,b.y+3);
                            pointpane.add(b);
                            pointpane.revalidate();
                            pointpane.repaint();
                        }
                    }
                }
            }
        });

        JButton button2 = new JButton("очистить");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { //функция, стирающая все точки
                while(points.size() > 0) {
                    int index = points.size() - 1;
                    Point point = points.remove(index);
                    pointpane.remove(point);
                    pointpane.repaint();
                    pointpane.revalidate();
                }
            }
        });
        button2.setBounds(25,150,160,40);
        butPanel.add(button2);

        JButton button3 = new JButton("Решить задачу");
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int lastElemIndex = points.size() - 1; // индекс самой последней (то есть самой тяжёлой) точки в списке
                while(Math.abs(points.get(lastElemIndex).weight - points.get(0).weight) > EPS){ /* Первая точка списка
                 (points.get(0)) -- самая маленькая по весу, а последняя (points.get(lastElemIndex) -- самая большая).
                 Если массы самой лёгкой и самой тяжёлой точки оказались равны, то это означает, что все точки в списке
                 имеют одинаковую массую. А именно такая ситуация нас и интересует при решении задачи! Поэтому будем
                 удалять точки последовательно, пока веса первой и последней точек не сравняются.

                 Вещественные числа (коими являются веса точек) нельзя сравнивать с помощью оператора '==', их нужно сравнивать с
                 некоторой точностью. Напримр при точности EPS = 0.01, программа посчитает веса 23.915 и 23.918 одинаковыми
                 (потому что их разность меньше точности -- 0.003 < 0.01). А веса 11.3 и 11.5 программа посчитает разными (потому
                 что их разнонсть не меньше точности -- 0.02 > 0.01)
                */
                    double MassToDivide = 0.9 * points.get(lastElemIndex).weight; /*
                        points.get(lastElemIndex).weight -- масса самой тяжёлой на данный момент точки,
                        MassToDivide -- сколько суммарно получат остальные точки после ее исчезновения
                    */

                    Point point = points.remove(lastElemIndex); //удаление точки отовсюду
                    pointpane.remove(point);
                    lastElemIndex--; //теперь, когда мы удалили точку -- нужно указать индекс новой самой "тяжёлой точки".
                    //а это просто точка, предшествующая удалённой.

                    double delta = MassToDivide / (double)points.size(); /* delta -- величина, которую должна получить каждая
                                                                          точка (MassToDivide поделить на кол-во точек */
                    for (int i = 0; i < points.size(); ++i){ //прибавим delta к каждой точке
                        Point tmp = points.get(i);
                        tmp.weight += delta;
                        points.set(i, tmp);
                    }
                }

                //теперь, когда все точки в массиве сравнялись -- посчитаем ответ, для этого просто сложим веса всех точек
                double answer = 0.0;
                for (int i = 0; i < points.size(); ++i){
                    answer += points.get(i).weight;
                }
                JLabel answerLabel = new JLabel("Ответ на задачу: " + String.format("%.2f", answer)); /*
                String.format("%.2f", answer) выводит число с точностью до двух знаком после запятой (1.234567
                будет выведено как 1.23)
                */
                answerLabel.setBounds(2,250,200,50);
                butPanel.add(answerLabel);
                butPanel.repaint();
                butPanel.revalidate();
                pointpane.repaint();
                pointpane.revalidate();
                panel.repaint();
                panel.revalidate();
            }
        });
        button3.setBounds(25,200,160,40);
        butPanel.add(button3);

        panel.add(pointpane,BorderLayout.CENTER);
        panel.add(butPanel,BorderLayout.EAST);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }



    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                createGUI();
            }
        });
    }
}
