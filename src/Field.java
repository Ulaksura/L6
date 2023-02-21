import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Time;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseListener;
import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseMotionListener;

public class Field extends JPanel {
    private boolean paused;
    // Динамический список скачущих мячей
    boolean changeMode = false;
    boolean scaleMode = false;
    boolean charisma = false;
    private ArrayList<BouncingBall> balls = new ArrayList<BouncingBall>(10);
    // Класс таймер отвечает за регулярную генерацию событий ActionEvent
// При создании его экземпляра используется анонимный класс,
// реализующий интерфейс ActionListener
    private Timer repaintTimer = new Timer(10, new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
// Задача обработчика события ActionEvent - перерисовка окна
            repaint();
        }
    });
    // Конструктор класса BouncingBall
    public Field() {
// Установить цвет заднего фона белым
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseMotionHandler());

        setBackground(Color.WHITE);
// Запустить таймер
        repaintTimer.start();
    }
    // Унаследованный от JPanel метод перерисовки компонента
    public void paintComponent(Graphics g) {
// Вызвать версию метода, унаследованную от предка
        super.paintComponent(g);
        Graphics2D canvas = (Graphics2D) g;
// Последовательно запросить прорисовку от всех мячей из списка
        for (BouncingBall ball: balls) {
            ball.paint(canvas);
        }
    }
    public void addBall() {
//Заключается в добавлении в список нового экземпляра BouncingBall
// Всю инициализацию положения, скорости, размера, цвета
// BouncingBall выполняет сам в конструкторе
        balls.add(new BouncingBall(this));
    }
    // Метод синхронизированный, т.е. только один поток может
// одновременно быть внутри
    public synchronized void pause() {
// Включить режим паузы
        paused = true;
    }
    public boolean getCharisma()
    {
       return charisma;
    }
    public void setCharisma(boolean b)
    {
        charisma = b;
    }
    public void CharismaReSpeed()
    {
        for (BouncingBall ball : balls) {
            ball.ReSpeed(Math.random());
        }
    }
    // Метод синхронизированный, т.е. только один поток может
// одновременно быть внутри
    public synchronized void resume() {
// Выключить режим паузы
        paused = false;
// Будим все ожидающие продолжения потоки
        notifyAll();
    }
    // Синхронизированный метод проверки, может ли мяч двигаться
// (не включен ли режим паузы?)
    public synchronized void canMove(BouncingBall ball) throws
            InterruptedException {
        if (paused) {
// Если режим паузы включен, то поток, зашедший
// внутрь данного метода, засыпает
            wait();
        }
    }

    public class MouseHandler extends MouseAdapter  {
        public void mouseClicked(MouseEvent ev) {
            if (ev.getButton() == MouseEvent.BUTTON3) {
                addBall();
            }
        }

        public void mousePressed(MouseEvent ev) {
            if (ev.getButton() != 1)
                return;
            if (balls.size()!=0) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                pause();
            }
            else
            {
                setCursor((Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)));
                addBall();
            }
        }
        public void mouseReleased(MouseEvent ev) {
            if (ev.getButton() != 1)
                return;
            resume();
            setCursor(Cursor.getPredefinedCursor(0));

        }
    }
    public class MouseMotionHandler implements MouseMotionListener{

        @Override
        public void mouseDragged(MouseEvent e) {
            for (BouncingBall ball : balls) {
                if(ball.checkMouse(e))
                {
                //    ball.setX(e.getX());      //Если эти строки, то шарик будет передвигаться
                //    ball.setY(e.getY());
                    ball.ReSpeed(e, e.getWhen());

                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if(charisma) {
                for (BouncingBall ball : balls) {
                    ball.ReSpeed(e);
                }
            }
        }
    }

}


