package com.deepwelldevelopment.tmos.lib.gui.element;

import com.deepwelldevelopment.tmos.lib.gui.GuiBase;
import com.deepwelldevelopment.tmos.lib.gui.GuiProps;
import com.deepwelldevelopment.tmos.lib.gui.TabTracker;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public abstract class TabBase extends ElementBase {

    public static int tabExpandSpeed = 8;

    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    protected int offsetX = 0;
    protected int offsetY = 0;

    public boolean open;
    public boolean fullyOpen;
    public int side = RIGHT;

    public int headerColor = 0xe1c92f;
    public int subheaderColor = 0xaaafb8;
    public int textColor = 0x000000;
    public int backgroundColor = 0xffffff;

    protected int currentShiftX = 0;
    protected int currentShiftY = 0;

    public int minWidth = 22;
    public int maxWidth = 124;
    public int currentWidth = minWidth;

    public int minHeight = 22;
    public int maxHeight = 22;
    public int currentHeight = minHeight;

    protected ArrayList<ElementBase> elements = new ArrayList<ElementBase>();

    public static final ResourceLocation DEFAULT_TEXTURE_LEFT = new ResourceLocation(GuiProps.PATH_ELEMENTS + "Tab_Left.png");
    public static final ResourceLocation DEFAULT_TEXTURE_RIGHT = new ResourceLocation(GuiProps.PATH_ELEMENTS + "Tab_Right.png");

    public TabBase(GuiBase gui) {
        super(gui, 0, 0);
        texture = DEFAULT_TEXTURE_RIGHT;
    }

    public TabBase(GuiBase gui, int side) {
        super(gui, 0, 0);
        this.side = side;

        if (side == LEFT) {
            texture = DEFAULT_TEXTURE_LEFT;
        } else {
            texture = DEFAULT_TEXTURE_RIGHT;
        }
    }

    public TabBase setOffsets(int x, int y) {
        posX -= offsetX;
        posY -= offsetY;
        offsetX = x;
        offsetY = y;
        posX += offsetX;
        posY += offsetY;

        return this;
    }

    @Override
    public TabBase setPosition(int posX, int posY) {
        this.posX = posX + offsetX;
        this.posY = posY + offsetY;
        return this;
    }

    protected void drawForeground() {
        // TODO: this and drawBackground() need to be called after the matrix translation (not for back compat)
    }

    protected void drawBackground() {
        float colorR = (backgroundColor >> 16 & 255) / 255.0F;
        float colorG = (backgroundColor >> 8 & 255) / 255.0F;
        float colorB = (backgroundColor & 255) / 255.0F;

        GL11.glColor4f(colorR, colorG, colorB, 1.0F);

        gui.mc.renderEngine.bindTexture(texture);

        int xPosition = posX();

        gui.drawTexturedModalRect(xPosition, posY + 4, 0, 256 - currentHeight + 4, 4, currentHeight - 4);
        gui.drawTexturedModalRect(xPosition + 4, posY, 256 - currentWidth + 4, 0, currentWidth - 4, 4);
        gui.drawTexturedModalRect(xPosition, posY, 0, 0, 4, 4);
        gui.drawTexturedModalRect(xPosition + 4, posY + 4, 256 - currentWidth + 4, 256 - currentHeight + 4, currentWidth - 4, currentHeight - 4);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float gameTicks) {
        mouseX -= this.posX();
        mouseY -= this.posY;

        GL11.glPushMatrix();

        drawBackground();

        GL11.glTranslatef(this.posX(), this.posY, 0.0F);

        for (int i = 0; i < elements.size(); i++) {
            ElementBase element = elements.get(i);
            if (element.isVisible()) {
                element.drawBackground(mouseX, mouseY, gameTicks);
            }
        }
        GL11.glPopMatrix();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        mouseX -= this.posX();
        mouseY -= this.posY;

        GL11.glPushMatrix();

        drawForeground();

        GL11.glTranslatef(this.posX(), this.posY, 0.0F);

        for (int i = 0; i < elements.size(); i++) {
            ElementBase element = elements.get(i);
            if (element.isVisible()) {
                element.drawForeground(mouseX, mouseY);
            }
        }
        GL11.glPopMatrix();
    }

    @Override
    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);

        mouseX -= this.posX();
        mouseY -= this.posY;

        for (int i = elements.size(); i-- > 0;) {
            ElementBase c = elements.get(i);
            if (c.isVisible() && c.isEnabled()) {
                c.update(mouseX, mouseY);
            }
        }
    }

    @Override
    public void update() {
        if (open && currentWidth < maxWidth) {
            currentWidth += tabExpandSpeed;
        } else if (!open && currentWidth > minWidth) {
            currentWidth -= tabExpandSpeed;
        }
        if (currentWidth > maxWidth) {
            currentWidth = maxWidth;
        } else if (currentWidth < minWidth) {
            currentWidth = minWidth;
        }
        if (open && currentHeight < maxHeight) {
            currentHeight += tabExpandSpeed;
        } else if (!open && currentHeight > minHeight) {
            currentHeight -= tabExpandSpeed;
        }
        if (currentHeight > maxHeight) {
            currentHeight = maxHeight;
        } else if (currentHeight < minHeight) {
            currentHeight = minHeight;
        }
        if (!fullyOpen && open && currentWidth == maxWidth && currentHeight == maxHeight) {
            setFullyOpen();
        }
    }

    /**
     * Shortcut to correct for the proper X position.
     */
    protected int posX() {
        if (side == LEFT) {
            return posX - currentWidth;
        }
        return posX;
    }

    /**
     * Corrects for shadowing differences in tabs to ensure that they always look nice - used in font rendering, typically.
     */
    protected int posXOffset() {
        return posX() + sideOffset();
    }

    protected int sideOffset() {
        return (side == LEFT ? 4 : 2);
    }

    public boolean intersectsWith(int mouseX, int mouseY, int shiftX, int shiftY) {
        shiftX += offsetX;
        shiftY += offsetY;

        if (side == LEFT) {
            if (mouseX <= shiftX && mouseX >= shiftX - currentWidth && mouseY >= shiftY && mouseY <= shiftY + currentHeight) {
                return true;
            }
        } else if (mouseX >= shiftX && mouseX <= shiftX + currentWidth && mouseY >= shiftY && mouseY <= shiftY + currentHeight) {
            return true;
        }
        return false;
    }

    public boolean isFullyOpened() {
        return fullyOpen;
    }

    public void setCurrentShift(int x, int y) {
        updateElements();

        currentShiftX = x + offsetX;
        currentShiftY = y + offsetY;
    }

    public void setFullyOpen() {
        open = true;
        currentWidth = maxWidth;
        currentHeight = maxHeight;
        fullyOpen = true;

        updateElements();
    }

    public void toggleOpen() {
        if (open) {
            open = false;
            if (side == LEFT) {
                TabTracker.setOpenedLeftTab(null);
            } else {
                TabTracker.setOpenedRightTab(null);
            }
            fullyOpen = false;
        } else {
            open = true;
            if (side == LEFT) {
                TabTracker.setOpenedLeftTab(this.getClass());
            } else {
                TabTracker.setOpenedRightTab(this.getClass());
            }
        }

        updateElements();
    }

    /* Elements */
    public ElementBase addElement(ElementBase element) {
        elements.add(element);
        return element;
    }

    protected ElementBase getElementAtPosition(int mX, int mY) {
        for (int i = elements.size(); i-- > 0;) {
            ElementBase element = elements.get(i);
            if (element.intersectsWith(mX, mY)) {
                return element;
            }
        }
        return null;
    }

	/* Redirects to Elements */

    @Override
    public boolean onMouseWheel(int mouseX, int mouseY, int movement) {
        int wheelMovement = Mouse.getEventDWheel();

        mouseX -= this.posX();
        mouseY -= this.posY;

        if (wheelMovement != 0) {
            for (int i = elements.size(); i-- > 0;) {
                ElementBase c = elements.get(i);
                if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(mouseX, mouseY)) {
                    continue;
                }
                if (c.onMouseWheel(mouseX, mouseY, wheelMovement)) {
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    public void addTooltip(List<String> list) {
        for (int i = 0; i < this.elements.size(); i++) {
            ElementBase c = elements.get(i);

            if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(gui.getMouseX(), gui.getMouseY())) {
                continue;
            }
            c.addTooltip(list);
        }
    }

    @Override
    public boolean onKeyTyped(char characterTyped, int keyPressed) {
        for (int i = elements.size(); i-- > 0;) {
            ElementBase c = elements.get(i);
            if (!c.isVisible() || !c.isEnabled()) {
                continue;
            }
            if (c.onKeyTyped(characterTyped, keyPressed)) {
                return true;
            }
        }
        return super.onKeyTyped(characterTyped, keyPressed);
    }

    @Override
    /**
     * @return Whether the tab should stay open or not.
     */
    public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {
        mouseX -= this.posX();
        mouseY -= this.posY;

        boolean shouldStayOpen = false;

        for (int i = 0; i < this.elements.size(); i++) {
            ElementBase c = elements.get(i);
            if (!c.isVisible() || !c.isEnabled() || !c.intersectsWith(mouseX, mouseY)) {
                continue;
            }

            shouldStayOpen = true;

            if (c.onMousePressed(mouseX, mouseY, mouseButton)) {
                return true;
            }
        }

        return shouldStayOpen;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY) {
        mouseX -= this.posX();
        mouseY -= this.posY;

        for (int i = elements.size(); i-- > 0;) {
            ElementBase c = elements.get(i);
            if (!c.isVisible() || !c.isEnabled()) { // no bounds checking on mouseUp events
                continue;
            }
            c.onMouseReleased(mouseX, mouseY);
        }
    }

    private void updateElements() {
        for (ElementBase element : elements) {
            element.setVisible(this.isFullyOpened());
        }
    }
}
