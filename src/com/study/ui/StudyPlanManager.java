package com.study.ui;

import com.study.ui.StudyPlan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 学习计划管理系统主窗口
 * 学号：你的学号 姓名：你的姓名
 */
public class StudyPlanManager extends JDialog {
    private JTable planTable;
    private DefaultTableModel tableModel;
    private List<StudyPlan> planList;

    // 列名
    private String[] columnNames = {"计划名称", "学科分类", "优先级", "每日时长", "学习天数", "难度", "创建时间"};

    public StudyPlanManager(Frame parent) {
        super(parent, "学习计划管理器", true);
        planList = new ArrayList<>();
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

        // 标题面板
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // 中心面板 - 表格和按钮
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // 底部状态栏
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("学习计划管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("管理您的学习计划，提高学习效率");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // 按钮面板
        JPanel buttonPanel = createTableButtonPanel();
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        // 表格面板
        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTableButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);
        
        // 添加计划按钮
        JButton addButton = createStyledButton("添加学习计划", new Color(46, 204, 113));
        addButton.setToolTipText("添加新的学习计划");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewPlan();
            }
        });
        
        // 编辑按钮
        JButton editButton = createStyledButton("编辑计划", new Color(52, 152, 219));
        editButton.setToolTipText("编辑选中的学习计划");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedPlan();
            }
        });
        
        // 删除按钮
        JButton deleteButton = createStyledButton("删除计划", new Color(231, 76, 60));
        deleteButton.setToolTipText("删除选中的学习计划");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedPlan();
            }
        });
        
        // 刷新按钮
        JButton refreshButton = createStyledButton("刷新", new Color(155, 89, 182));
        refreshButton.setToolTipText("刷新计划列表");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            "学习计划列表",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("微软雅黑", Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        
        // 创建表格模型
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };
        
        planTable = new JTable(tableModel);
        planTable.setRowHeight(30);
        planTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        planTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        planTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        planTable.setGridColor(new Color(230, 230, 230));
        
        // 设置列宽
        planTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        planTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        planTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        planTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        planTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        planTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        planTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(planTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        JLabel statusLabel = new JLabel("就绪");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);
        
        JLabel infoLabel = new JLabel("学号：你的学号 姓名：你的姓名");
        infoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(200, 200, 200));
        
        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(infoLabel, BorderLayout.EAST);
        
        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        // 使用HTML标签防止文本省略
        String buttonText = "<html><nobr>" + text + "</nobr></html>";
        JButton button = new JButton(buttonText);
        button.setFont(new Font("微软雅黑", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);

        // 设置按钮大小
        int buttonWidth = 120; // 根据你的按钮设置合适的宽度
        int buttonHeight = 35;

        if (text.contains("学习计划")) {
            buttonWidth = 140; // "添加学习计划"需要更宽的按钮
        } else if (text.contains("计划")) {
            buttonWidth = 100; // "编辑计划"、"删除计划"
        } else {
            buttonWidth = 80; // "刷新"、"关闭"
        }

        Dimension size = new Dimension(buttonWidth, buttonHeight);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void addNewPlan() {
        // 打开数据输入窗口（模态对话框）
        // 需要将StudyPlanManager转换为Frame类型，使用SwingUtilities.getWindowAncestor
        Window window = SwingUtilities.getWindowAncestor(this);
        DataInputWindow inputWindow;

        if (window instanceof Frame) {
            inputWindow = new DataInputWindow((Frame) window);
        } else {
            // 如果无法获取Frame，使用null（会创建独立窗口）
            inputWindow = new DataInputWindow(null);
        }

        inputWindow.setVisible(true);

        // 当数据输入窗口关闭后，检查是否添加了新计划
        StudyPlan newPlan = inputWindow.getCreatedPlan();
        if (newPlan != null) {
            planList.add(newPlan);
            addPlanToTable(newPlan);
            JOptionPane.showMessageDialog(this,
                    "学习计划添加成功！\n计划名称：" + newPlan.getPlanName(),
                    "添加成功",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editSelectedPlan() {
        int selectedRow = planTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "请先选择一个学习计划！",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StudyPlan selectedPlan = planList.get(selectedRow);

        // 打开编辑窗口（这里使用与添加相同的窗口）
        Window window = SwingUtilities.getWindowAncestor(this);
        DataInputWindow editWindow;

        if (window instanceof Frame) {
            editWindow = new DataInputWindow((Frame) window, selectedPlan);
        } else {
            editWindow = new DataInputWindow(null, selectedPlan);
        }

        editWindow.setVisible(true);

        // 更新计划
        StudyPlan updatedPlan = editWindow.getCreatedPlan();
        if (updatedPlan != null) {
            planList.set(selectedRow, updatedPlan);
            updateTableRow(selectedRow, updatedPlan);
            JOptionPane.showMessageDialog(this,
                    "学习计划更新成功！\n计划名称：" + updatedPlan.getPlanName(),
                    "更新成功",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void deleteSelectedPlan() {
        int selectedRow = planTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请先选择一个学习计划！",
                "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除选中的学习计划吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            StudyPlan removedPlan = planList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this,
                "已删除学习计划：" + removedPlan.getPlanName(),
                "删除成功",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (StudyPlan plan : planList) {
            addPlanToTable(plan);
        }
        JOptionPane.showMessageDialog(this,
            "计划列表已刷新，共 " + planList.size() + " 个学习计划",
            "刷新完成",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void addPlanToTable(StudyPlan plan) {
        Object[] rowData = {
            plan.getPlanName(),
            plan.getCategory(),
            plan.getPriority(),
            plan.getDailyTime(),
            plan.getStudyDays(),
            plan.getDifficulty(),
            plan.getCreateTime()
        };
        tableModel.addRow(rowData);
    }
    
    private void updateTableRow(int row, StudyPlan plan) {
        tableModel.setValueAt(plan.getPlanName(), row, 0);
        tableModel.setValueAt(plan.getCategory(), row, 1);
        tableModel.setValueAt(plan.getPriority(), row, 2);
        tableModel.setValueAt(plan.getDailyTime(), row, 3);
        tableModel.setValueAt(plan.getStudyDays(), row, 4);
        tableModel.setValueAt(plan.getDifficulty(), row, 5);
        tableModel.setValueAt(plan.getCreateTime(), row, 6);
    }
    
    private void loadSampleData() {
        // 添加一些示例数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        StudyPlan plan1 = new StudyPlan();
        plan1.setPlanName("Java编程学习");
        plan1.setCategory("编程");
        plan1.setPriority("高");
        plan1.setDailyTime("2小时 30分钟");
        plan1.setStudyDays("周一 周三 周五");
        plan1.setDifficulty("7");
        plan1.setCreateTime(sdf.format(new Date()));
        planList.add(plan1);
        
        StudyPlan plan2 = new StudyPlan();
        plan2.setPlanName("英语六级备考");
        plan2.setCategory("英语");
        plan2.setPriority("中");
        plan2.setDailyTime("1小时 45分钟");
        plan2.setStudyDays("周二 周四 周六");
        plan2.setDifficulty("8");
        plan2.setCreateTime(sdf.format(new Date()));
        planList.add(plan2);
        
        StudyPlan plan3 = new StudyPlan();
        plan3.setPlanName("高等数学复习");
        plan3.setCategory("数学");
        plan3.setPriority("高");
        plan3.setDailyTime("2小时 0分钟");
        plan3.setStudyDays("周一 周二 周三 周四 周五");
        plan3.setDifficulty("9");
        plan3.setCreateTime(sdf.format(new Date()));
        planList.add(plan3);
        
        // 添加到表格
        for (StudyPlan plan : planList) {
            addPlanToTable(plan);
        }
    }
    
    // 获取选中的计划
    public StudyPlan getSelectedPlan() {
        int selectedRow = planTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < planList.size()) {
            return planList.get(selectedRow);
        }
        return null;
    }
}