////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.gui;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * This is a wrapper class takes a TreeTableModel and implements
 * the table model interface. The implementation is trivial, with
 * all of the event dispatching support provided by the superclass:
 * the AbstractTableModel.
 *
 * <a href=
 * "https://docs.oracle.com/cd/E48246_01/apirefs.1111/e13403/oracle/ide/controls/TreeTableModel.html">
 * Original&nbsp;Source&nbsp;Location</a>
 *
 * @author Philip Milne
 * @author Scott Violet
 */
public class TreeTableModelAdapter extends AbstractTableModel {
    /** For Serialisation that will never happen. */
    private static final long serialVersionUID = 8269213416115369275L;

    private final JTree tree;
    private final transient TreeTableModel treeTableModel;

    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;

        tree.addTreeExpansionListener(new UpdatingTreeExpansionListener());

        // Install a TreeModelListener that can update the table when
        // mTree changes. We use delayedFireTableDataChanged as we can
        // not be guaranteed the mTree will have finished processing
        // the event before us.
        treeTableModel.addTreeModelListener(new UpdatingTreeModelListener());
    }

    // Wrappers, implementing TableModel interface.

    @Override
    public int getColumnCount() {
        return treeTableModel.getColumnCount();
    }

    @Override
    public String getColumnName(int column) {
        return treeTableModel.getColumnName(column);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return treeTableModel.getColumnClass(column);
    }

    @Override
    public int getRowCount() {
        return tree.getRowCount();
    }

    private Object nodeForRow(int row) {
        final TreePath treePath = tree.getPathForRow(row);
        return treePath.getLastPathComponent();
    }

    @Override
    public Object getValueAt(int row, int column) {
        return treeTableModel.getValueAt(nodeForRow(row), column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return treeTableModel.isCellEditable(column);
    }

    /**
     * Invokes fireTableDataChanged after all the pending events have been
     * processed. SwingUtilities.invokeLater is used to handle this.
     */
    private void delayedFireTableDataChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fireTableDataChanged();
            }
        });
    }

    /**
     * TreeExpansionListener that can update the table when tree changes
     */
    private class UpdatingTreeExpansionListener implements TreeExpansionListener {
        // Don't use fireTableRowsInserted() here; the selection model
        // would get updated twice.
        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            fireTableDataChanged();
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
            fireTableDataChanged();
        }
    }

    /**
     * TreeModelListener that can update the table when tree changes
     */
    private class UpdatingTreeModelListener implements TreeModelListener {
        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            delayedFireTableDataChanged();
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
            delayedFireTableDataChanged();
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
            delayedFireTableDataChanged();
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            delayedFireTableDataChanged();
        }
    }
}
