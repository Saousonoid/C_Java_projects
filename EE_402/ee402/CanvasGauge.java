package ee402;

import javax.swing.*;
import java.awt.*;
@SuppressWarnings("serial")
public class CanvasGauge extends JPanel {
    private double min,max,avg,bound;
    private Color avgColor;


    public CanvasGauge(String title) {
        this.min = 0;
        this.max = 0;
        this.avg = 0;
        this.bound=1;
 

        setBorder(BorderFactory.createTitledBorder(title));
    }

    public void setValues(double min,  double avg,double max,double bound,Color avgColor) {
        this.min = min;
        this.avg = avg;
        this.max = max;
        this.avgColor=avgColor;
        this.bound=bound;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        drawRectangle(g2d, new Color(255,255,0), (int)(250*max/bound),max);
        drawRectangle(g2d, avgColor, (int)(250*avg/bound), avg);
        drawRectangle(g2d, new Color(80,40,0),(int) (250*min/bound), min);
    }

    private void drawRectangle(Graphics2D g2d, Color color, int barWidth, double value) {
        g2d.setColor(color);
        System.out.println(barWidth);
        g2d.fillRect(20, 25, barWidth, 30);
        int labelX =  barWidth+20 ;
        int labelY = 60;
        g2d.drawString("" + (int) (value ), labelX , labelY);
    }


}

