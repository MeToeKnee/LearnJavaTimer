package uk.org.harden;

//import javax.swing.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
//import java.util.concurrent.atomic.AtomicInteger;

class TimerView extends JPanel implements ActionListener, PropertyChangeListener {

    private static final Logger LOGGER = Logger.getLogger(TimerView.class.getName());

    private final String uuid = UUID.randomUUID().toString();

    private final int threadId;
    private final int valueInitial;
    private final int valueIncrement;
    private final JPanel timerPanel = new JPanel();
    private final JButton startButton = new JButton(TimerConstants.ACTION_START);
    private final JButton stopButton = new JButton(TimerConstants.ACTION_STOP);
    private final JButton resetButton = new JButton(TimerConstants.ACTION_RESET);
    private final JButton deleteButton = new JButton(TimerConstants.ACTION_DELETE);
    private final JFormattedTextField nowText;
    private final JProgressBar progressBar;
    private final AppView appView;
    private AtomicInteger valueNow;

    private Task task;

    public TimerView(final int count, final AppView parentView) {

        threadId = count;
        appView = parentView;
        valueInitial = 20 * count;
        valueIncrement = count;
        valueNow = new AtomicInteger(valueInitial);

        final JLabel timerLabel = new JLabel("Timer " + count + ":");

        final JLabel initialLabel = new JLabel("Initial:");
        final JLabel incrementLabel = new JLabel("Increment:");
        final JLabel nowLabel = new JLabel("Now:");

        final NumberFormat nf = NumberFormat.getInstance();
        final JFormattedTextField initialText = new JFormattedTextField(nf);
        final JFormattedTextField incrementText = new JFormattedTextField(nf);
        nowText = new JFormattedTextField(nf);
        progressBar = new JProgressBar(0, 100);

        initialLabel.setLabelFor(initialText);
        incrementLabel.setLabelFor(incrementText);
        nowLabel.setLabelFor(nowText);

        initialText.setValue(valueInitial);
        initialText.setColumns(3);
        incrementText.setValue(valueIncrement);
        incrementText.setColumns(2);
        nowText.setValue(valueNow);
        nowText.setColumns(3);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        resetButton.setEnabled(true);
        deleteButton.setEnabled(true);
        initialText.setEditable(false);
        incrementText.setEditable(false);
        nowText.setEditable(false);
        progressBar.setValue(0);

        startButton.setActionCommand(TimerConstants.ACTION_START);
        startButton.addActionListener(this);
        stopButton.setActionCommand(TimerConstants.ACTION_STOP);
        stopButton.addActionListener(this);
        resetButton.setActionCommand(TimerConstants.ACTION_RESET);
        resetButton.addActionListener(this);
        deleteButton.setActionCommand(TimerConstants.ACTION_DELETE);
        deleteButton.addActionListener(this);

        timerPanel.add(timerLabel);
        timerPanel.add(startButton);
        timerPanel.add(stopButton);
        timerPanel.add(resetButton);
        timerPanel.add(deleteButton);
        timerPanel.add(initialLabel);
        timerPanel.add(initialText);
        timerPanel.add(incrementLabel);
        timerPanel.add(incrementText);
        timerPanel.add(nowLabel);
        timerPanel.add(nowText);
        timerPanel.add(progressBar);
        add(timerPanel);
    }

    private static float getPercentage(int n, int total) {
        float proportion = ((float) n) / ((float) total);
        return proportion * 100;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        final String NEW_LINE = System.getProperty("line.separator");

        return this.getClass().getName() + " {" + NEW_LINE +
                " Initial: " + valueInitial + NEW_LINE +
                " Increment: " + valueIncrement + NEW_LINE +
                " Now: " + valueNow + NEW_LINE +
                "}";
    }

    public void actionPerformed(ActionEvent evt) {
        final String command = evt.getActionCommand();
        appView.appMessage("(" + threadId + ") actionPerformed: " + command);
        if (command.equals(TimerConstants.ACTION_START)) {
            task = new Task();
            task.addPropertyChangeListener(this);
            task.execute();
        } else {
            if (command.equals(TimerConstants.ACTION_STOP)) {
                if (task == null) {
                    appView.appMessage("(" + threadId + ") Thread is not running!");
                } else {
                    task.cancel(true);
                }
            } else {
                if (command.equals(TimerConstants.ACTION_RESET)) {
                    appView.appMessage("(" + threadId + ") " + toString());
                    valueNow = new AtomicInteger(valueInitial);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            nowText.setValue(valueNow);
                            progressBar.setValue(0);
                        }
                    });
                } else {
                    if (command.equals(TimerConstants.ACTION_DELETE)) {
                        remove(timerPanel);
                        appView.refresh();
                    } else {
                        assert true : "Unexpected action.";
                    }
                }
            }
        }
        appView.collectStats(getUuid(), command);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            final int progress = (Integer) evt.getNewValue();
            progressBar.setIndeterminate(false);
            progressBar.setValue(progress);
        }
    }

    private class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        protected Void doInBackground() {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            resetButton.setEnabled(false);
            deleteButton.setEnabled(false);
            int progress = valueInitial - valueNow.get();
            int percentage = (int) getPercentage(Math.min(progress, valueInitial), valueInitial);
            setProgress(percentage);
            while (valueNow.get() > 0) {
                try {
                    Thread.sleep(1000 * valueIncrement);
                    //valueNow -= valueIncrement;
                    valueNow = new AtomicInteger(valueNow.get() - valueIncrement);
                } catch (InterruptedException ie) {
                    //ignore, we expect this to happen when we cancel the timer!
                }
                if (task.isCancelled()) {
                    LOGGER.info("Cancelled");
                    break;
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        nowText.setValue(valueNow);
                    }
                });

                progress = valueInitial - valueNow.get();
                percentage = (int) getPercentage(Math.min(progress, valueInitial), valueInitial);
                setProgress(percentage);

                appView.appMessage("(" + threadId + ") progress " + valueNow + "/" + progress + "(" + percentage + ")/" + valueIncrement);
            }

            if (valueNow.get() <= 0 || task.isCancelled()) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        startButton.setEnabled(true);
                        stopButton.setEnabled(false);
                        resetButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                        appView.appMessage("(" + threadId + ") Done!");
                        if (valueNow.get() <= 0) {
                            appView.collectStats(getUuid(), TimerConstants.ACTION_DONE);
                        }
                    }
                });
            }

            return null;
        }
    }

}
