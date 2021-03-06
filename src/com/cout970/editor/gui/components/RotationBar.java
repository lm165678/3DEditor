package com.cout970.editor.gui.components;

import com.cout970.editor.display.InputHandler;
import com.cout970.editor.gui.api.IGui;
import com.cout970.editor.gui.api.ISizedComponent;
import com.cout970.editor.render.texture.TextureStorage;
import com.cout970.editor.util.Color;
import com.cout970.editor.util.Vect2i;

/**
 * Created by cout970 on 14/02/2016.
 */
public class RotationBar implements ISizedComponent {

    private final Vect2i size = new Vect2i(150, 16);
    private NumberEdit parent;
    private SimpleButton button;
    private double cursor;
    protected int level;
    protected double max;
    protected double min;
    protected double cycle;

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    public RotationBar(NumberEdit parent, double min, double max, double cycle) {
        this.parent = parent;
        this.max = max;
        this.min = min;
        this.cycle = cycle;
        button = new SimpleButton(Vect2i.nullVector(), new Vect2i(14, 14), TextureStorage.BUTTONS, (button1, mouse, mouseButton) -> false, this::uvMapper) {
            @Override
            protected ButtonState onButtonRelease() {
                return ButtonState.NORMAL;
            }
        };
    }

    public Vect2i uvMapper(AbstractStateButton.ButtonState state) {
        if (state == AbstractStateButton.ButtonState.DOWN) {
            return new Vect2i(132, 14);
        }
        return new Vect2i(132, 0);
    }

    @Override
    public Vect2i getPos() {
        return parent.getPos().add(parent.getSize().getX() + 3, 0);
    }

    @Override
    public Vect2i getSize() {
        return size.copy();
    }

    @Override
    public boolean isMouseOnTop(IGui gui, Vect2i mouse, InputHandler.MouseButton button) {
        return IGui.isInside(mouse, getPos(), getSize());
    }

    @Override
    public void renderBackground(IGui gui, Vect2i mouse, float partialTicks) {
        Vect2i margin = new Vect2i(1, 1);
        gui.getGuiRenderer().drawRectangle(getPos(), getPos().add(getSize()), new Color(0));
        gui.getGuiRenderer().drawRectangle(getPos().add(margin), getPos().add(getSize()).sub(margin), new Color(0x999999));
        gui.getGuiRenderer().drawRectangle(getPos().add(0, getSize().getY() / 2 - 1), getPos().add(getSize().getX(), getSize().getY() / 2 + 1), new Color(0x333333));
        if (cycle > 0) {
            double val = parent.getValue() % cycle;
            if (val > max) { val -= cycle; }
            if (val < min) { val += cycle; }
            cursor = val / max;
        } else {
            double val = parent.getValue();
            if (val > max) { val = max; }
            if (val < min) { val = min; }
            cursor = (val - min) / (max - min) * 2 - 1;
        }
        double x = getSize().getX() / 2D - 7;
        button.setPos(getPos().add((int) (cursor * x + x), 1));
        button.renderBackground(gui, mouse, partialTicks);
        if (button.getState() == AbstractStateButton.ButtonState.DOWN) {
            Vect2i disp = mouse.copy().sub(getPos());
            cursor = (double) disp.getX() / getSize().getX() * 2 - 1;
            if (cursor > 1) { cursor = 1; }
            if (cursor < -1) { cursor = -1; }
            if (cycle > 0) {
                parent.setValue(cursor * max);
            } else {
                parent.setValue((cursor + 1) / 2 * (max - min) + min);
            }
        }
    }

    @Override
    public void renderForeground(IGui gui, Vect2i mouse) {
        button.renderForeground(gui, mouse);
    }

    @Override
    public void onMouseClick(IGui gui, Vect2i mouse, InputHandler.MouseButton button) {
        this.button.onMouseClick(gui, mouse, button);
    }

    @Override
    public boolean onKeyPressed(IGui gui, int key, int scancode, int action) {
        button.onKeyPressed(gui, key, scancode, action);
        return false;
    }

    @Override
    public void onCharPress(IGui gui, int key) {
        button.onCharPress(gui, key);
    }

    @Override
    public void onWheelMoves(IGui gui, double amount) {
        button.onWheelMoves(gui, amount);
    }
}
