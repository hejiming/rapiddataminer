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
package com.rapidminer.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.dialog.InitialSettingsDialog;
import com.rapidminer.gui.tools.IconSize;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tools.ParameterService;

/**
 * Start the corresponding action.
 * 
 * @author Ingo Mierswa
 * @version $Id: SwitchWorkspaceAction.java,v 1.8 2008/05/09 19:22:46 ingomierswa Exp $
 */
public class SwitchWorkspaceAction extends AbstractAction {

	private static final long serialVersionUID = 7009218190859329793L;

	private static final String ICON_NAME = "refresh.png";
	
	private static final Icon[] ICONS = new Icon[IconSize.values().length];
	
	static {
		int counter = 0;
		for (IconSize size : IconSize.values()) {
			ICONS[counter++] = SwingTools.createIcon(size.getSize() + "/" + ICON_NAME);
		}
	}
	
	public SwitchWorkspaceAction(IconSize size) {
		super("Switch Workspace...", ICONS[size.ordinal()]);
		putValue(SHORT_DESCRIPTION, "Opens a dialog in a new workspace can be selected.");
		putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_K));
	}

	public void actionPerformed(ActionEvent e) {
		InitialSettingsDialog dialog = new InitialSettingsDialog(RapidMinerGUI.getMainFrame(), ParameterService.getUserWorkspace(), "rm_workspace", null, -1, false);
		dialog.setVisible(true);
		String newPath = dialog.getWorkspacePath();
		File newWorkspace = new File(newPath);
		ParameterService.setUserWorkspace(newWorkspace);
	}
}
