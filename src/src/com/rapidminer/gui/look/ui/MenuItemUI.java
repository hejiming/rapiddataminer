/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2008 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package com.rapidminer.gui.look.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

import com.rapidminer.gui.look.RapidLookTools;
import com.rapidminer.gui.look.painters.CashedPainter;

/**
 * The UI for menu items.
 *
 * @author Ingo Mierswa
 * @version $Id: MenuItemUI.java,v 1.3 2008/05/09 19:22:42 ingomierswa Exp $
 */
public class MenuItemUI extends BasicMenuItemUI {
	
	public static ComponentUI createUI(JComponent c) {
		return new MenuItemUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
	}

	@Override
	protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
		super.paintText(g, menuItem, textRect, text);
	}

	@Override
	protected void paintBackground(Graphics g, JMenuItem m, Color c) {
		Color oldColor = g.getColor();
		if (!m.isArmed()) {
			CashedPainter.drawMenuItemFading(m, g);
		} else {
			RapidLookTools.drawMenuItemBackground(g, m);
		}
		g.setColor(oldColor);
	}
}
