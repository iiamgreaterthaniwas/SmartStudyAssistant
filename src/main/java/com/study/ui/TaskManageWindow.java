package com.study.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * 任务管理窗口 - 非模态窗口
 * 用于管理学习任务和跟踪进度
 * 学号：你的学号  姓名：你的姓名
 */
public class TaskManageWindow extends JDialog {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private ArrayList<Task> tasks;
    private JComboBox<String> filterCombo;
    private JProgressBar overallProgressBar;

    public TaskManageWindow(Frame parent) {
        super(parent, "任务管理", true);
        tasks = new ArrayList<>();
        initComponents();
        setupUI();
        loadSampleData();
    }

    private void initComponents() {
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));

        // 标题面板（统一风格）
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // 中间：任务表格和控制面板
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // 底部：操作按钮
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("任务管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("管理您的学习任务，追踪完成进度");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // 整体进度面板
        JPanel progressPanel = createProgressPanel();

        // 控制面板（筛选）
        JPanel controlPanel = createControlPanel();

        // 表格面板 - 使用中心区域，这样它可以占据剩余空间
        JPanel tablePanel = createTablePanel();

        // 创建顶部面板，包含进度和控制面板
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(progressPanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.SOUTH);

        // 添加分隔空间
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER); // 改为CENTER，这样表格可以占据剩余空间

        return panel;
    }

    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel progressLabel = new JLabel("整体完成度：");
        progressLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));

        overallProgressBar = new JProgressBar(0, 100);
        overallProgressBar.setStringPainted(true);
        overallProgressBar.setPreferredSize(new Dimension(300, 25));
        overallProgressBar.setForeground(new Color(46, 204, 113));
        overallProgressBar.setBackground(new Color(236, 240, 241));
        overallProgressBar.setToolTipText("总体任务完成进度 - 学号：你的学号");

        panel.add(progressLabel, BorderLayout.WEST);
        panel.add(overallProgressBar, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel filterLabel = new JLabel("任务筛选：");
        filterLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));

        String[] filters = {"全部任务", "进行中", "已完成", "未开始", "逾期"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        filterCombo.setToolTipText("筛选任务状态 - 学号：你的学号 姓名：你的姓名");
        filterCombo.addActionListener(e -> filterTasks());

        panel.add(filterLabel);
        panel.add(filterCombo);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "任务列表",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14),
                new Color(52, 73, 94)
        ));

        String[] columns = {"任务名称", "优先级", "状态", "截止日期", "进度"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 所有列都不可编辑
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(35);
        taskTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        taskTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setGridColor(new Color(230, 230, 230));
        taskTable.setToolTipText("双击行查看任务详情 - 学号：你的学号 姓名：你的姓名");

        // 设置列宽
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        // 添加双击事件
        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showTaskDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5)); // 增加上边距
        scrollPane.setPreferredSize(new Dimension(950, 350)); // 设置表格面板的推荐大小
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // 统一按钮样式设置
        JButton addButton = createStyledButton("新建任务", new Color(46, 204, 113));
        addButton.setToolTipText("创建新任务 - 学号：你的学号 姓名：你的姓名");
        addButton.addActionListener(e -> addTask());

        JButton editButton = createStyledButton("编辑任务", new Color(52, 152, 219));
        editButton.setToolTipText("编辑选中任务 - 姓名：你的姓名");
        editButton.addActionListener(e -> editTask());

        JButton completeButton = createStyledButton("标记完成", new Color(26, 188, 156));
        completeButton.setToolTipText("标记为完成");
        completeButton.addActionListener(e -> completeTask());

        JButton deleteButton = createStyledButton("删除任务", new Color(231, 76, 60));
        deleteButton.setToolTipText("删除选中任务");
        deleteButton.addActionListener(e -> deleteTask());

        JButton refreshButton = createStyledButton("刷新", new Color(149, 165, 166));
        refreshButton.setToolTipText("刷新任务列表");
        refreshButton.addActionListener(e -> refreshTable());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(completeButton);
        panel.add(deleteButton);
        panel.add(refreshButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);

        // 关键设置：禁止文字省略
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);

        // 计算最佳宽度（确保足够显示完整文字）
        FontMetrics metrics = button.getFontMetrics(button.getFont());
        int textWidth = metrics.stringWidth(text);

        // 使用固定边距而不是计算，避免按钮过小
        int buttonWidth = textWidth + 40; // 左右各20像素内边距
        int buttonHeight = 35;

        Dimension size = new Dimension(buttonWidth, buttonHeight);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size); // 添加这一行

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void loadSampleData() {
        tasks.add(new Task("完成Java大作业", "高", "进行中", "2026-01-04", 60));
        tasks.add(new Task("复习数据结构", "高", "进行中", "2026-01-05", 40));
        tasks.add(new Task("背诵英语单词", "中", "进行中", "2026-01-06", 75));
        tasks.add(new Task("准备期末考试", "高", "未开始", "2026-01-10", 0));
        tasks.add(new Task("阅读专业论文", "低", "进行中", "2026-01-08", 20));

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        int completedCount = 0;

        for (Task task : tasks) {
            Object[] row = {
                    task.getName(),
                    task.getPriority(),
                    task.getStatus(),
                    task.getDeadline(),
                    task.getProgress() + "%"
            };
            tableModel.addRow(row);

            if ("已完成".equals(task.getStatus())) {
                completedCount++;
            }
        }

        // 更新整体进度
        int overallProgress = tasks.isEmpty() ? 0 : (completedCount * 100 / tasks.size());
        overallProgressBar.setValue(overallProgress);
        overallProgressBar.setString(overallProgress + "% (" + completedCount + "/" + tasks.size() + ")");
    }

    private void addTask() {
        JDialog dialog = new JDialog(this, "新建任务", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField();
        String[] priorities = {"高", "中", "低"};
        JComboBox<String> priorityCombo = new JComboBox<>(priorities);
        String[] statuses = {"未开始", "进行中", "已完成"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        JTextField deadlineField = new JTextField("2026-01-10");
        JSpinner progressSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 10));

        panel.add(new JLabel("任务名称："));
        panel.add(nameField);
        panel.add(new JLabel("优先级："));
        panel.add(priorityCombo);
        panel.add(new JLabel("状态："));
        panel.add(statusCombo);
        panel.add(new JLabel("截止日期："));
        panel.add(deadlineField);
        panel.add(new JLabel("进度(%)："));
        panel.add(progressSpinner);

        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(new JLabel());
        panel.add(buttonPanel);

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请输入任务名称");
                return;
            }

            tasks.add(new Task(
                    name,
                    (String) priorityCombo.getSelectedItem(),
                    (String) statusCombo.getSelectedItem(),
                    deadlineField.getText(),
                    (Integer) progressSpinner.getValue()
            ));

            refreshTable();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "任务已添加！\n学号：你的学号 姓名：你的姓名");
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editTask() {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要编辑的任务");
            return;
        }

        Task task = tasks.get(row);
        JDialog dialog = new JDialog(this, "编辑任务", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField(task.getName());
        String[] priorities = {"高", "中", "低"};
        JComboBox<String> priorityCombo = new JComboBox<>(priorities);
        priorityCombo.setSelectedItem(task.getPriority());

        String[] statuses = {"未开始", "进行中", "已完成"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setSelectedItem(task.getStatus());

        JTextField deadlineField = new JTextField(task.getDeadline());
        JSpinner progressSpinner = new JSpinner(new SpinnerNumberModel(task.getProgress(), 0, 100, 10));

        panel.add(new JLabel("任务名称："));
        panel.add(nameField);
        panel.add(new JLabel("优先级："));
        panel.add(priorityCombo);
        panel.add(new JLabel("状态："));
        panel.add(statusCombo);
        panel.add(new JLabel("截止日期："));
        panel.add(deadlineField);
        panel.add(new JLabel("进度(%)："));
        panel.add(progressSpinner);

        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(new JLabel());
        panel.add(buttonPanel);

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请输入任务名称");
                return;
            }

            task.setName(name);
            task.setPriority((String) priorityCombo.getSelectedItem());
            task.setStatus((String) statusCombo.getSelectedItem());
            task.setDeadline(deadlineField.getText());
            task.setProgress((Integer) progressSpinner.getValue());

            refreshTable();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "任务已更新！\n学号：你的学号 姓名：你的姓名");
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void completeTask() {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要完成的任务");
            return;
        }

        tasks.get(row).setStatus("已完成");
        tasks.get(row).setProgress(100);
        refreshTable();
        JOptionPane.showMessageDialog(this, "任务已标记为完成！");
    }

    private void deleteTask() {
        int row = taskTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的任务");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, "确定要删除这个任务吗？");
        if (choice == JOptionPane.YES_OPTION) {
            tasks.remove(row);
            refreshTable();
        }
    }

    private void showTaskDetails() {
        int row = taskTable.getSelectedRow();
        if (row >= 0) {
            Task task = tasks.get(row);
            String details = String.format(
                    "任务详情\n\n" +
                            "名称：%s\n" +
                            "优先级：%s\n" +
                            "状态：%s\n" +
                            "截止日期：%s\n" +
                            "进度：%d%%\n\n" +
                            "学号：你的学号 姓名：你的姓名",
                    task.getName(), task.getPriority(), task.getStatus(),
                    task.getDeadline(), task.getProgress()
            );
            JOptionPane.showMessageDialog(this, details, "任务详情", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filterTasks() {
        String filter = (String) filterCombo.getSelectedItem();
        tableModel.setRowCount(0);
        int completedCount = 0;

        for (Task task : tasks) {
            // 根据筛选条件显示任务
            boolean show = false;
            switch (filter) {
                case "全部任务":
                    show = true;
                    break;
                case "进行中":
                    show = "进行中".equals(task.getStatus());
                    break;
                case "已完成":
                    show = "已完成".equals(task.getStatus());
                    break;
                case "未开始":
                    show = "未开始".equals(task.getStatus());
                    break;
                case "逾期":
                    // 简单的逾期判断（假设今天的日期是2026-01-05）
                    show = task.getDeadline().compareTo("2026-01-05") < 0 && !"已完成".equals(task.getStatus());
                    break;
            }

            if (show) {
                Object[] row = {
                        task.getName(),
                        task.getPriority(),
                        task.getStatus(),
                        task.getDeadline(),
                        task.getProgress() + "%"
                };
                tableModel.addRow(row);

                if ("已完成".equals(task.getStatus())) {
                    completedCount++;
                }
            }
        }

        // 更新整体进度
        int overallProgress = tasks.isEmpty() ? 0 : (completedCount * 100 / tasks.size());
        overallProgressBar.setValue(overallProgress);
        overallProgressBar.setString(overallProgress + "% (" + completedCount + "/" + tasks.size() + ")");
    }

    // 内部类：任务
    private class Task {
        private String name;
        private String priority;
        private String status;
        private String deadline;
        private int progress;

        public Task(String name, String priority, String status, String deadline, int progress) {
            this.name = name;
            this.priority = priority;
            this.status = status;
            this.deadline = deadline;
            this.progress = progress;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getDeadline() { return deadline; }
        public void setDeadline(String deadline) { this.deadline = deadline; }

        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
    }
}