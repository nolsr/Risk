package de.hsbremen.risk.client.components;

import java.awt.*;

public class MapCountryCircle extends Component {
    private final Color c;
    private final int diam;
    private final int armies;

    public MapCountryCircle(int offsetX, int offsetY, int x, int y, double scaling, int armies, Color c) {
        int x1 = offsetX + (int) Math.floor(x * scaling);
        int y1 = offsetY + (int) Math.floor(y * scaling);
        this.diam = (int) Math.floor(30 * scaling);
        this.c = c;
        this.armies = armies;

        this.setBounds(x1 - this.diam / 2, y1 - this.diam / 2, this.diam, this.diam);
    }

    /**
     * Overrides the paint method to draw the country circle. Scales circle and font size according to the scale factor of the map.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Dimension arcs = new Dimension(this.diam, this.diam);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (this.c != null) {
            graphics.setColor(this.c);
        } else {
            graphics.setColor(getBackground());
        }

        Font font = new Font("Arial", Font.BOLD, 12);
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics(font);
        String numberString = String.valueOf(this.armies);
        int textWidth = metrics.stringWidth(numberString);
        int textHeight = metrics.getHeight();
        int textX = (width - textWidth) / 2;
        int textY = (height - textHeight) / 2 + metrics.getAscent();

        graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
        graphics.setColor(new Color(18, 20, 24));
        graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
        graphics.drawString(numberString, textX, textY);
    }
}
