package uk.org.harden;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
//import java.util.logging.Logger;

class AppView extends JPanel implements ActionListener {

    //private static final Logger LOGGER = Logger.getLogger(AppView.class.getName());

    private final TimerDatabase timerDatabase = new TimerDatabase();
    private final AtomicInteger referenceNumber = new AtomicInteger();
    private final JComponent timersPane = new JPanel();
    private final JTextArea taskOutput = new JTextArea(5, 50);
    private final JTable statistics;
    //private final Executor executor = Executors.newFixedThreadPool( TimerConstants.MaxNumberOfThreads );
    private final Executor executor = Executors.newFixedThreadPool( TimerConstants.MaxNumberOfThreads );


    public AppView( final int count ) {

        setLayout(new BorderLayout());

        timersPane.setLayout(new BoxLayout(timersPane, BoxLayout.PAGE_AXIS));
        for (int i = 0; i < Math.max(count, 2); i++) {
            addNewTimer();
        }
        final JScrollPane timersScrollPane = new JScrollPane(timersPane);

        final JComponent controlPane = new JPanel();

        controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.PAGE_AXIS));

        final JButton addButton = new JButton(TimerConstants.ACTION_ADD);
        controlPane.add(addButton, BorderLayout.NORTH);
        addButton.setActionCommand(TimerConstants.ACTION_ADD);
        addButton.addActionListener(this);

        taskOutput.setText("Output:" + TimerConstants.NEW_LINE);
        controlPane.add(new JScrollPane(taskOutput), BorderLayout.CENTER);

        statistics = new JTable(new MyTableModel());
        statistics.setCellSelectionEnabled(true);
        controlPane.add(statistics);

        add(timersScrollPane, BorderLayout.CENTER);
        add(controlPane, BorderLayout.SOUTH);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " {" + "To do" + TimerConstants.NEW_LINE + "}";
    }

    public void actionPerformed(final ActionEvent evt) {
        final String command = evt.getActionCommand();
        appMessage("actionPerformed: " + command);
        if (command.equals(TimerConstants.ACTION_ADD)) {
            addNewTimer();
            refresh();
            collectStats("", "");
        }
    }

    public void refresh() {
        invalidate();
        validate();
        repaint();
    }

    private void addNewTimer() {
        final int count = referenceNumber.incrementAndGet();
        final TimerView newContentPane = new TimerView(executor, count, this);
        newContentPane.setVisible(true);
        newContentPane.setAutoscrolls(true);
        timersPane.add(newContentPane);
        appMessage("Added (" + count + "): " + newContentPane.getUuid());
        timerDatabase.tdAddTimer(newContentPane.getUuid(), count);
    }

    void appMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final String threadName = Thread.currentThread().getName();
                taskOutput.append(threadName + ": " + message + TimerConstants.NEW_LINE);
                taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
            }
        });
    }

    void collectStats(final String uuid, final String action) {
        if (uuid.length() > 0) {
            final String threadName = Thread.currentThread().getName();
            taskOutput.append(threadName + ": " + "Update stats" + TimerConstants.NEW_LINE);
            timerDatabase.tdUpdateTimer(uuid, action);
        }
        timerDatabase.tdGetTimerStats();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final String threadName = Thread.currentThread().getName();
                taskOutput.append(threadName + ": " + "Display stats" + TimerConstants.NEW_LINE);
                ((MyTableModel) statistics.getModel()).refreshData();
            }
        });
    }

    class MyTableModel extends AbstractTableModel {
        private Map<String, ArrayList<String>> data = timerDatabase.tdGetTimerStats();

        public MyTableModel() {
            refreshData();
        }

        @Override
        public int getColumnCount() {
            return ((ArrayList) data.get("Header")).size();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public String getColumnName(int col) {
            return (String) ((ArrayList) data.get("Header")).get(col);
        }

        @Override
        public Object getValueAt(int row, int col) {
            String keyValue;
            if (row == 0) {
                keyValue = "Header";
            } else {
                keyValue = "Timer" + row;
            }
            return ((ArrayList) data.get(keyValue)).get(col);
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void refreshData() {
            Map<String, ArrayList<String>> newData = timerDatabase.tdGetTimerStats();
            String keyValue;

            if (newData.size() == data.size()) {
                for (int r = 0; r < getRowCount(); r++) {
                    if (r == 0) {
                        keyValue = "Header";
                    } else {
                        keyValue = "Timer" + r;
                    }

                    ArrayList<String> newRow = newData.get(keyValue);
                    ArrayList<String> oldRow = data.get(keyValue);
                    if (newRow.size() == oldRow.size()) {
                        for (int c = 0; c < getColumnCount(); c++) {
                            if (!newRow.get(c).equals(oldRow.get(c))) {
                                //System.out.println("Diff row=" + r + ", col=" + c);
                                final int row = r;
                                final int col = c;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        final String threadName = Thread.currentThread().getName();
                                        taskOutput.append(threadName + ": " + "Flash" + TimerConstants.NEW_LINE);
                                        ((MyTableModel) statistics.getModel()).flashData(row, col);
                                    }
                                });
                            }
                        }
                    }
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        TimerTCP.TimerTCPSend(data);
                    }
                });
            }
            data = newData;
            fireTableDataChanged();
        }

        public void flashData(final int row, final int col) {
            statistics.changeSelection(row, col, true, false);
        }
    }
}
