package com.study.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * 智能提醒窗口 - 基于艾宾浩斯遗忘曲线的复习提醒
 */
public class ReminderWindow extends JFrame {
    private MainFrame mainFrame;
    private JTable reminderTable;
    private DefaultTableModel tableModel;
    private JLabel totalRemindersLabel;
    private JLabel todayRemindersLabel;
    private JLabel urgentRemindersLabel;
    private Timer checkTimer;

    // 艾宾浩斯遗忘曲线复习间隔（分钟）
    private static final int[] REVIEW_INTERVALS = {
        20,           // 20分钟
        60,           // 1小时
        480,          // 8小时
        1440,         // 1天
        2880,         // 2天
        8640,         // 6天
        44640         // 31天
    };

    private List<ReminderItem> reminders;

    public ReminderWindow(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.reminders = new ArrayList<>();
        initComponents();
        loadSampleData();
        startAutoCheck();
    }

    private void initComponents() {
        setTitle("智能提醒");
        setSize(1000, 700);
        setLocationRelativeTo(mainFrame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 设置窗口图标
        try {
            if (mainFrame != null) {
                setIconImage(mainFrame.getIconImage());
            }
            if (getIconImage() == null) {
                Image icon = Toolkit.getDefaultToolkit().getImage("resources/icons/app_icon.png");
                setIconImage(icon);
            }
        } catch (Exception e) {
            System.err.println("图标加载失败: " + e.getMessage());
        }

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 顶部统计面板
        mainPanel.add(createStatisticsPanel(), BorderLayout.NORTH);

        // 中部表格面板
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);

        // 底部按钮面板
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBorder(BorderFactory.createTitledBorder("提醒统计"));
        panel.setPreferredSize(new Dimension(0, 100));

        // 总提醒数
        JPanel totalPanel = createStatCard("总提醒", "0", new Color(52, 152, 219));
        totalRemindersLabel = (JLabel) ((JPanel) totalPanel.getComponent(0)).getComponent(1);

        // 今日提醒
        JPanel todayPanel = createStatCard("今日提醒", "0", new Color(46, 204, 113));
        todayRemindersLabel = (JLabel) ((JPanel) todayPanel.getComponent(0)).getComponent(1);

        // 紧急提醒
        JPanel urgentPanel = createStatCard("紧急提醒", "0", new Color(231, 76, 60));
        urgentRemindersLabel = (JLabel) ((JPanel) urgentPanel.getComponent(0)).getComponent(1);

        panel.add(totalPanel);
        panel.add(todayPanel);
        panel.add(urgentPanel);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        contentPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(127, 140, 141));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        valueLabel.setForeground(color);

        contentPanel.add(titleLabel);
        contentPanel.add(valueLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));

        // 工具栏
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        JButton addBtn = new JButton("添加提醒");
        addBtn.addActionListener(e -> addReminder());

        JButton deleteBtn = new JButton("删除选中");
        deleteBtn.addActionListener(e -> deleteSelectedReminder());

        JButton completeBtn = new JButton("标记完成");
        completeBtn.addActionListener(e -> markAsCompleted());

        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> refreshTable());

        JComboBox<String> filterCombo = new JComboBox<>(new String[]{
            "全部提醒", "今日提醒", "已过期", "未完成", "已完成"
        });
        filterCombo.addActionListener(e -> filterReminders((String) filterCombo.getSelectedItem()));

        toolbarPanel.add(addBtn);
        toolbarPanel.add(deleteBtn);
        toolbarPanel.add(completeBtn);
        toolbarPanel.add(refreshBtn);
        toolbarPanel.add(Box.createHorizontalStrut(20));
        toolbarPanel.add(new JLabel("筛选："));
        toolbarPanel.add(filterCombo);

        panel.add(toolbarPanel, BorderLayout.NORTH);

        // 表格
        String[] columns = {"学习内容", "复习阶段", "提醒时间", "状态", "优先级", "备注"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reminderTable = new JTable(tableModel);
        reminderTable.setRowHeight(35);
        reminderTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        reminderTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        reminderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 设置列宽
        reminderTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        reminderTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        reminderTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        reminderTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        reminderTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        reminderTable.getColumnModel().getColumn(5).setPreferredWidth(200);

        // 自定义渲染器
        reminderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 3);
                    if ("已过期".equals(status)) {
                        c.setBackground(new Color(255, 240, 240));
                    } else if ("已完成".equals(status)) {
                        c.setBackground(new Color(240, 255, 240));
                    } else if ("今日提醒".equals(status)) {
                        c.setBackground(new Color(255, 250, 230));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }

                // 优先级列使用不同颜色
                if (column == 4) {
                    if ("高".equals(value)) {
                        setForeground(new Color(231, 76, 60));
                    } else if ("中".equals(value)) {
                        setForeground(new Color(243, 156, 18));
                    } else {
                        setForeground(new Color(52, 152, 219));
                    }
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(reminderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 左侧：艾宾浩斯曲线说明
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton curveInfoBtn = new JButton("查看遗忘曲线");
        curveInfoBtn.addActionListener(e -> showForgettingCurveInfo());
        infoPanel.add(curveInfoBtn);

        // 右侧：操作按钮
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton closeBtn = new JButton("关闭");
        closeBtn.addActionListener(e -> dispose());

        actionPanel.add(closeBtn);

        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(actionPanel, BorderLayout.EAST);

        return panel;
    }

    private void addReminder() {
        JDialog dialog = new JDialog(this, "添加学习提醒", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 学习内容
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("学习内容："), gbc);
        gbc.gridx = 1;
        JTextField contentField = new JTextField(20);
        panel.add(contentField, gbc);

        // 开始时间
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("开始学习时间："), gbc);
        gbc.gridx = 1;
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "yyyy-MM-dd HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new Date());
        panel.add(timeSpinner, gbc);

        // 优先级
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("优先级："), gbc);
        gbc.gridx = 1;
        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"低", "中", "高"});
        priorityCombo.setSelectedIndex(1);
        panel.add(priorityCombo, gbc);

        // 备注
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("备注："), gbc);
        gbc.gridx = 1;
        JTextArea noteArea = new JTextArea(3, 20);
        noteArea.setLineWrap(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);
        panel.add(noteScroll, gbc);

        // 按钮
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> {
            String content = contentField.getText().trim();
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请输入学习内容！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Date startTime = (Date) timeSpinner.getValue();
            String priority = (String) priorityCombo.getSelectedItem();
            String note = noteArea.getText().trim();

            // 创建提醒项
            ReminderItem item = new ReminderItem(content, startTime, priority, note);
            reminders.add(item);

            refreshTable();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "提醒添加成功！将按照艾宾浩斯遗忘曲线自动提醒。", "成功", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedReminder() {
        int selectedRow = reminderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的提醒！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该提醒吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String content = (String) tableModel.getValueAt(selectedRow, 0);
            reminders.removeIf(r -> r.getContent().equals(content));
            refreshTable();
        }
    }

    private void markAsCompleted() {
        int selectedRow = reminderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要标记的提醒！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String content = (String) tableModel.getValueAt(selectedRow, 0);
        String stage = (String) tableModel.getValueAt(selectedRow, 1);

        for (ReminderItem item : reminders) {
            if (item.getContent().equals(content)) {
                item.completeCurrentStage();
                break;
            }
        }

        refreshTable();
        JOptionPane.showMessageDialog(this, "已标记完成：" + stage, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        Date now = new Date();
        int todayCount = 0;
        int urgentCount = 0;

        for (ReminderItem item : reminders) {
            for (ReviewStage stage : item.getReviewStages()) {
                if (!stage.isCompleted()) {
                    String status;
                    if (stage.getRemindTime().before(now)) {
                        status = "已过期";
                        urgentCount++;
                    } else if (isSameDay(stage.getRemindTime(), now)) {
                        status = "今日提醒";
                        todayCount++;
                    } else {
                        status = "未到期";
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    tableModel.addRow(new Object[]{
                        item.getContent(),
                        stage.getStageName(),
                        sdf.format(stage.getRemindTime()),
                        status,
                        item.getPriority(),
                        item.getNote()
                    });
                }
            }
        }

        // 更新统计
        totalRemindersLabel.setText(String.valueOf(tableModel.getRowCount()));
        todayRemindersLabel.setText(String.valueOf(todayCount));
        urgentRemindersLabel.setText(String.valueOf(urgentCount));
    }

    private void filterReminders(String filter) {
        tableModel.setRowCount(0);
        Date now = new Date();

        for (ReminderItem item : reminders) {
            for (ReviewStage stage : item.getReviewStages()) {
                boolean shouldShow = false;
                String status = "";

                if (stage.isCompleted()) {
                    if ("全部提醒".equals(filter) || "已完成".equals(filter)) {
                        shouldShow = true;
                        status = "已完成";
                    }
                } else {
                    if (stage.getRemindTime().before(now)) {
                        status = "已过期";
                        shouldShow = "全部提醒".equals(filter) || "已过期".equals(filter) || "未完成".equals(filter);
                    } else if (isSameDay(stage.getRemindTime(), now)) {
                        status = "今日提醒";
                        shouldShow = "全部提醒".equals(filter) || "今日提醒".equals(filter) || "未完成".equals(filter);
                    } else {
                        status = "未到期";
                        shouldShow = "全部提醒".equals(filter) || "未完成".equals(filter);
                    }
                }

                if (shouldShow) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    tableModel.addRow(new Object[]{
                        item.getContent(),
                        stage.getStageName(),
                        sdf.format(stage.getRemindTime()),
                        status,
                        item.getPriority(),
                        item.getNote()
                    });
                }
            }
        }
    }

    private void showForgettingCurveInfo() {
        JDialog dialog = new JDialog(this, "艾宾浩斯遗忘曲线", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        textArea.setText(
            "艾宾浩斯遗忘曲线说明\n\n" +
            "德国心理学家艾宾浩斯（Hermann Ebbinghaus）研究发现，遗忘在学习之后立即开始，" +
            "而且遗忘的进程并不是均匀的。最初遗忘速度很快，以后逐渐缓慢。\n\n" +
            "最佳复习时间点：\n" +
            "• 第1次复习：学习后 20分钟\n" +
            "• 第2次复习：学习后 1小时\n" +
            "• 第3次复习：学习后 8小时\n" +
            "• 第4次复习：学习后 1天\n" +
            "• 第5次复习：学习后 2天\n" +
            "• 第6次复习：学习后 6天\n" +
            "• 第7次复习：学习后 31天\n\n" +
            "复习建议：\n" +
            "1. 及时复习：在遗忘发生前进行复习\n" +
            "2. 间隔复习：遵循科学的时间间隔\n" +
            "3. 主动回忆：尝试在不看资料的情况下回忆内容\n" +
            "4. 多样化方式：采用不同方式复习同一内容\n" +
            "5. 重点标记：对难点内容增加复习次数\n\n" +
            "使用本系统，您将获得科学的复习提醒，帮助您有效巩固知识！"
        );

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("知道了");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }


    private void startAutoCheck() {
        checkTimer = new Timer();
        checkTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkReminders();
            }
        }, 60000, 60000); // 每分钟检查一次
    }

    private void checkReminders() {
        Date now = new Date();
        for (ReminderItem item : reminders) {
            for (ReviewStage stage : item.getReviewStages()) {
                if (!stage.isCompleted() && !stage.isNotified()) {
                    long diff = stage.getRemindTime().getTime() - now.getTime();
                    if (diff <= 0 && diff > -60000) { // 在1分钟内
                        showNotification(item, stage);
                        stage.setNotified(true);
                    }
                }
            }
        }
    }

    private void showNotification(ReminderItem item, ReviewStage stage) {
        SwingUtilities.invokeLater(() -> {
            int result = JOptionPane.showConfirmDialog(this,
                "复习提醒：\n\n" +
                "学习内容：" + item.getContent() + "\n" +
                "复习阶段：" + stage.getStageName() + "\n" +
                "优先级：" + item.getPriority() + "\n\n" +
                "是否现在就开始复习？",
                "复习提醒 - " + stage.getStageName(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                stage.setCompleted(true);
                refreshTable();
            }
        });
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void loadSampleData() {
        // 添加示例数据
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -2);
        reminders.add(new ReminderItem("Java多线程编程", cal.getTime(), "高", "重点复习synchronized关键字"));
        
        cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -5);
        reminders.add(new ReminderItem("数据结构-树与图", cal.getTime(), "中", "理解深度优先遍历"));
        
        cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -30);
        reminders.add(new ReminderItem("英语单词Unit 5", cal.getTime(), "中", "记忆50个新单词"));
        
        refreshTable();
    }

    @Override
    public void dispose() {
        if (checkTimer != null) {
            checkTimer.cancel();
        }
        super.dispose();
    }

    // 内部类：提醒项
    class ReminderItem {
        private String content;
        private Date startTime;
        private String priority;
        private String note;
        private List<ReviewStage> reviewStages;

        public ReminderItem(String content, Date startTime, String priority, String note) {
            this.content = content;
            this.startTime = startTime;
            this.priority = priority;
            this.note = note;
            this.reviewStages = new ArrayList<>();
            generateReviewStages();
        }

        private void generateReviewStages() {
            String[] stageNames = {
                "第1次复习(20分钟后)",
                "第2次复习(1小时后)",
                "第3次复习(8小时后)",
                "第4次复习(1天后)",
                "第5次复习(2天后)",
                "第6次复习(6天后)",
                "第7次复习(31天后)"
            };

            Calendar cal = Calendar.getInstance();
            cal.setTime(startTime);

            for (int i = 0; i < REVIEW_INTERVALS.length; i++) {
                cal.add(Calendar.MINUTE, REVIEW_INTERVALS[i]);
                if (i > 0) {
                    cal.add(Calendar.MINUTE, -REVIEW_INTERVALS[i - 1]);
                }
                reviewStages.add(new ReviewStage(stageNames[i], (Date) cal.getTime().clone()));
                cal.setTime(startTime);
            }
        }

        public void completeCurrentStage() {
            for (ReviewStage stage : reviewStages) {
                if (!stage.isCompleted()) {
                    stage.setCompleted(true);
                    break;
                }
            }
        }

        public String getContent() { return content; }
        public String getPriority() { return priority; }
        public String getNote() { return note; }
        public List<ReviewStage> getReviewStages() { return reviewStages; }
    }

    // 内部类：复习阶段
    class ReviewStage {
        private String stageName;
        private Date remindTime;
        private boolean completed;
        private boolean notified;

        public ReviewStage(String stageName, Date remindTime) {
            this.stageName = stageName;
            this.remindTime = remindTime;
            this.completed = false;
            this.notified = false;
        }

        public String getStageName() { return stageName; }
        public Date getRemindTime() { return remindTime; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        public boolean isNotified() { return notified; }
        public void setNotified(boolean notified) { this.notified = notified; }
    }
}