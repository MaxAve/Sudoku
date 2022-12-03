import java.awt.*;
import java.util.ArrayList;

interface UIAction { void perform(); }

/**
 * Use this class for general User Interface. The 'Button' class was too difficult to use,
 * this should be used as a simpler alternative.
 */
public class UI {
    private ArrayList<UIButton> UIButtons = new ArrayList<>();
    private ArrayList<UIText> UITexts = new ArrayList<>();

    public UI(){}

    public void add(UIButton button) {
        this.UIButtons.add(button);
    }
    public void add(UIText text) {
        this.UITexts.add(text);
    }

    // This lets us hide/show the UI
    public void renderUI(Graphics g) {
        for (UIButton button : UIButtons) {
            button.draw(g);
        }
        for (UIText text : UITexts) {
            text.displayText(g);
        }
    }

    public static class UIText {
        public int x, y, fontSize;
        public String text;

        // Constructor
        public UIText(String txt, int size) {
            this.text = txt;
            this.fontSize = size;
        }

        public void setXPosition(String locationRelativeToScreen) {
            if (locationRelativeToScreen.equalsIgnoreCase("middle") || locationRelativeToScreen.equalsIgnoreCase("mid")) {
                this.x = 30 + Panel.SCREEN_WIDTH/2 - (int)(this.text.length() * fontSize) / 2;
            } else {
                throw new IllegalArgumentException("Invalid argument \""+locationRelativeToScreen+"\"");
            }
        }

        // Displays text
        public void displayText(Graphics g) {
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            g.setColor(new Color(0, 75, 155));
            g.drawString(text, x, y+10); // Shadow
            g.setColor(new Color(0, 175, 255));
            g.drawString(text, x, y); // Actual text
        }
    }

    public static class UIButton {
        public int x, y, wid, hei;
        public String text;
        public int lMargin = 0;
        public int uMargin = 0;

        public UIButton(int x, int y, int wid, int hei, String txt) {
            this.x = x;
            this.y = y;
            this.wid = wid;
            this.hei = hei;
            this.text = txt;
        }

        public void draw(Graphics g) {
            g.setColor(new Color(0, 75, 155));
            g.fillRoundRect(x - wid/2, y+10 - hei/2, wid, hei, 15, 15); // Shadow
            g.setColor(new Color(0, 175, 255));
            g.fillRoundRect(x - wid/2, y - hei/2, wid, hei, 15, 15); // Button

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, hei/2));
            g.drawString(text, x - wid/2 + lMargin, y - hei/2 + uMargin);
        }

        /**
         * This method does 2 things:
         * 1) Check if it is being clicked
         * 2) Executes the provided UIAction (parameter should be a Lambda Expression)
         * @param a
         */
        public void onClick(UIAction a) {
            // Check if the button was clicked
            if (Frame.mouseClickX < x + wid && Frame.mouseClickX > x - wid && Frame.mouseClickY < y + hei && Frame.mouseClickY > y - hei/2) {
                a.perform();
            }
        }
    }
}