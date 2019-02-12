/*
 * EditPersonnelLogDialog.java
 * 
 * Copyright (c) 2009 Jay Lawson <jaylawson39 at yahoo.com>. All rights reserved.
 * 
 * This file is part of MekHQ.
 * 
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MekHQ.  If not, see <http://www.gnu.org/licenses/>.
 */

package mekhq.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import megamek.common.util.EncodeControl;
import mekhq.campaign.Campaign;
import mekhq.campaign.log.CustomLogEntry;
import mekhq.campaign.log.LogEntry;
import mekhq.campaign.personnel.Person;
import mekhq.gui.model.LogTableModel;

/**
 *
 * @author  Taharqa
 */
public class EditPersonnelLogDialog extends javax.swing.JDialog {
	private static final long serialVersionUID = -8038099101234445018L;
    private Frame frame;
    private Campaign campaign;
    private Person person;
    private ArrayList<LogEntry> log;
    private LogTableModel logModel;
    
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnOK;
    private JTable logTable;
    private JScrollPane scrollLogTable;
    
    /** Creates new form EditPersonnelLogDialog */
    public EditPersonnelLogDialog(java.awt.Frame parent, boolean modal, Campaign c, Person p) {
        super(parent, modal);
        this.frame = parent;
        campaign = c;
        person = p;
        log = p.getPersonnelLog();
        logModel = new LogTableModel(log);
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {

        btnOK = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        ResourceBundle resourceMap = ResourceBundle.getBundle("mekhq.resources.EditPersonnelLogDialog", new EncodeControl()); //$NON-NLS-1$
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setTitle(resourceMap.getString("Form.title") + " " + person.getName());
        getContentPane().setLayout(new java.awt.BorderLayout());
        
        JPanel panBtns = new JPanel(new GridLayout(1,0));
        btnAdd.setText(resourceMap.getString("btnAdd.text")); // NOI18N
        btnAdd.setName("btnAdd"); // NOI18N
        btnAdd.addActionListener(evt -> addEntry());
        panBtns.add(btnAdd);
        btnEdit.setText(resourceMap.getString("btnEdit.text")); // NOI18N
        btnEdit.setName("btnEdit"); // NOI18N
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(evt -> editEntry());
        panBtns.add(btnEdit);
        btnDelete.setText(resourceMap.getString("btnDelete.text")); // NOI18N
        btnDelete.setName("btnDelete"); // NOI18N
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(evt -> deleteEntry());
        panBtns.add(btnDelete);
        getContentPane().add(panBtns, BorderLayout.PAGE_START);
        
        logTable = new JTable(logModel);
        logTable.setName("logTable"); // NOI18N
		TableColumn column;
        for (int i = 0; i < LogTableModel.N_COL; i++) {
            column = logTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(logModel.getColumnWidth(i));
            column.setCellRenderer(logModel.getRenderer());
        }
        logTable.setIntercellSpacing(new Dimension(0, 0));
		logTable.setShowGrid(false);
		logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		logTable.getSelectionModel().addListSelectionListener(this::logTableValueChanged);
		scrollLogTable = new JScrollPane();
		scrollLogTable.setName("scrollPartsTable"); // NOI18N
		scrollLogTable.setViewportView(logTable);
        getContentPane().add(scrollLogTable, BorderLayout.CENTER);

        
        btnOK.setText(resourceMap.getString("btnOK.text")); // NOI18N
        btnOK.setName("btnOK"); // NOI18N
        btnOK.addActionListener(this::btnOKActionPerformed);
        getContentPane().add(btnOK, BorderLayout.PAGE_END);

        pack();
    }

    
    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHireActionPerformed
    	this.setVisible(false);
    }
    
    private void logTableValueChanged(javax.swing.event.ListSelectionEvent evt) {
		int row = logTable.getSelectedRow();
		btnDelete.setEnabled(row != -1);
		btnEdit.setEnabled(row != -1);
	}
    
    private void addEntry() {
    	EditLogEntryDialog eeld = new EditLogEntryDialog(frame, true, new CustomLogEntry(campaign.getDate(), ""), person);
		eeld.setVisible(true);
		if(null != eeld.getEntry()) {
			person.addLogEntry(eeld.getEntry());
		}
		refreshTable();
    }
    
    private void editEntry() {
    	LogEntry entry = logModel.getEntry(logTable.getSelectedRow());
    	if(null != entry) {
    		EditLogEntryDialog eeld = new EditLogEntryDialog(frame, true, entry, person);
    		eeld.setVisible(true);
    		refreshTable();
    	}
    }
    
    private void deleteEntry() {
    	person.getPersonnelLog().remove(logTable.getSelectedRow());
    	refreshTable();
    }
    
    private void refreshTable() {
		int selectedRow = logTable.getSelectedRow();
    	logModel.setData(person.getPersonnelLog());
    	if(selectedRow != -1) {
    		if(logTable.getRowCount() > 0) {
    			if(logTable.getRowCount() == selectedRow) {
    				logTable.setRowSelectionInterval(selectedRow-1, selectedRow-1);
    			} else {
    				logTable.setRowSelectionInterval(selectedRow, selectedRow);
    			}
    		}
    	}
    }
}
