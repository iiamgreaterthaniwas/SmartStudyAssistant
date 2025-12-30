package com.study.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * 番茄钟窗口 - 模态对话框
 * 用于专注学习时间管理
 * 学号：你的学号  姓名：你的姓名
 */
public class PomodoroWindow extends JDialog {
    private int totalSeconds = 25 * 60; // 默认25分钟
    private int currentSeconds;
    private Timer timer;
    private boolean isRunning = false;

    private JLabel timeLabel;
    private JButton startPauseButton;
    private JButton resetButton;
    private JProgressBar progressBar;
    private JComboBox<String> presetCombo;
    private JLabel statusLabel;
    private int completedPomodoros = 0;

    public PomodoroWindow(Frame parent) {
        super(parent, "番茄钟 - 专注学习", true);
        currentSeconds = totalSeconds;
        initComponents();
        setupUI();
    }

    private void initComponents() {
        setSize(550, 550);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));

        // 标题面板（统一风格）
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // 中间：时间显示和进度条
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // 底部：控制按钮和统计
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // 创建计时器
        timer = new Timer(1000, e -> updateTimer());
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("番茄钟计时器");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("专注学习，高效管理时间");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // 控制面板：预设时间选择
        JPanel controlPanel = createControlPanel();

        // 时间显示面板
        JPanel timeDisplayPanel = createTimeDisplayPanel();

        // 进度条面板
        JPanel progressPanel = createProgressPanel();

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(timeDisplayPanel, BorderLayout.CENTER);
        panel.add(progressPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);

        JLabel presetLabel = new JLabel("预设时间：");
        presetLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
        presetLabel.setToolTipText("选择预设的学习时间 - 学号：你的学号");

        String[] presets = {"25分钟（标准）", "15分钟（短时）", "45分钟（长时）", "自定义"};
        presetCombo = new JComboBox<>(presets);
        presetCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        presetCombo.setToolTipText("选择学习时长 - 姓名：你的姓名");
        presetCombo.addActionListener(e -> changePreset());

        panel.add(presetLabel);
        panel.add(presetCombo);

        return panel;
    }

    private JPanel createTimeDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        timeLabel = new JLabel(formatTime(currentSeconds), SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 72));
        timeLabel.setForeground(new Color(46, 204, 113));
        timeLabel.setToolTipText("剩余时间 - 学号：你的学号 姓名：你的姓名");

        panel.add(timeLabel, BorderLayout.CENTER);

        // 状态标签
        statusLabel = new JLabel("准备开始学习", SwingConstants.CENTER);
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(127, 140, 141));

        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        progressBar = new JProgressBar(0, totalSeconds);
        progressBar.setValue(currentSeconds);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 30));
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(236, 240, 241));
        progressBar.setToolTipText("学习进度 - 学号：你的学号");

        panel.add(progressBar, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        startPauseButton = createStyledButton("开始", new Color(46, 204, 113));
        startPauseButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        startPauseButton.setToolTipText("开始/暂停计时 - 学号：你的学号 姓名：你的姓名");
        startPauseButton.addActionListener(e -> toggleTimer());

        resetButton = createStyledButton("重置", new Color(231, 76, 60));
        resetButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        resetButton.setToolTipText("重置计时器 - 姓名：你的姓名");
        resetButton.addActionListener(e -> resetTimer());

        buttonPanel.add(startPauseButton);
        buttonPanel.add(resetButton);

        // 统计信息面板
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "今日统计",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 12),
                new Color(52, 73, 94)
        ));

        JLabel completedLabel = new JLabel("已完成番茄钟: " + completedPomodoros, SwingConstants.CENTER);
        completedLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JLabel focusTimeLabel = new JLabel("专注时长: " + (completedPomodoros * 25) + "分钟", SwingConstants.CENTER);
        focusTimeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        statsPanel.add(completedLabel);
        statsPanel.add(focusTimeLabel);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);

        // 设置按钮大小
        int buttonWidth = 120;
        int buttonHeight = 40;

        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        button.setMinimumSize(new Dimension(buttonWidth, buttonHeight));

        // 关键设置：禁止文字省略
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void changePreset() {
        if (isRunning) {
            JOptionPane.showMessageDialog(this,
                    "请先停止当前计时器再切换时间设置",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selected = (String) presetCombo.getSelectedItem();
        if (selected.contains("25")) {
            totalSeconds = 25 * 60;
        } else if (selected.contains("15")) {
            totalSeconds = 15 * 60;
        } else if (selected.contains("45")) {
            totalSeconds = 45 * 60;
        } else {
            String input = JOptionPane.showInputDialog(this,
                    "请输入自定义时间（分钟）：",
                    "自定义时间",
                    JOptionPane.QUESTION_MESSAGE);
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int minutes = Integer.parseInt(input.trim());
                    if (minutes > 0 && minutes <= 120) {
                        totalSeconds = minutes * 60;
                    } else {
                        JOptionPane.showMessageDialog(this, "请输入1-120之间的数字");
                        presetCombo.setSelectedIndex(0);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "请输入有效的数字");
                    presetCombo.setSelectedIndex(0);
                    return;
                }
            } else {
                presetCombo.setSelectedIndex(0);
                return;
            }
        }

        currentSeconds = totalSeconds;
        progressBar.setMaximum(totalSeconds);
        progressBar.setValue(currentSeconds);
        timeLabel.setText(formatTime(currentSeconds));
        statusLabel.setText("时间已更新");
    }

    private void toggleTimer() {
        if (isRunning) {
            // 暂停
            timer.stop();
            isRunning = false;
            startPauseButton.setText("继续");
            startPauseButton.setBackground(new Color(52, 152, 219));
            statusLabel.setText("已暂停");
            timeLabel.setForeground(new Color(243, 156, 18));
        } else {
            // 开始/继续
            timer.start();
            isRunning = true;
            startPauseButton.setText("暂停");
            startPauseButton.setBackground(new Color(230, 126, 34));
            statusLabel.setText("专注学习中...");
            timeLabel.setForeground(new Color(46, 204, 113));
        }
    }

    private void resetTimer() {
        if (isRunning) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "确定要重置计时器吗？当前进度将丢失。",
                    "确认重置",
                    JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        timer.stop();
        isRunning = false;
        currentSeconds = totalSeconds;
        progressBar.setValue(currentSeconds);
        timeLabel.setText(formatTime(currentSeconds));
        timeLabel.setForeground(new Color(46, 204, 113));
        startPauseButton.setText("开始");
        startPauseButton.setBackground(new Color(46, 204, 113));
        statusLabel.setText("已重置");
    }

    private void updateTimer() {
        currentSeconds--;
        progressBar.setValue(currentSeconds);
        timeLabel.setText(formatTime(currentSeconds));

        // 最后一分钟变色提醒
        if (currentSeconds <= 60 && currentSeconds > 0) {
            timeLabel.setForeground(new Color(231, 76, 60));
        }

        if (currentSeconds <= 0) {
            timer.stop();
            isRunning = false;
            completedPomodoros++;

            // 播放提示音
            Toolkit.getDefaultToolkit().beep();

            JOptionPane.showMessageDialog(this,
                    "恭喜！您完成了一个番茄钟！\n\n" +
                            "建议休息5-10分钟，然后继续下一个番茄钟。\n\n" +
                            "今日已完成：" + completedPomodoros + " 个番茄钟",
                    "番茄钟完成",
                    JOptionPane.INFORMATION_MESSAGE);

            resetTimer();
            statusLabel.setText("番茄钟已完成！今日第 " + completedPomodoros + " 个");
        }
    }

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}