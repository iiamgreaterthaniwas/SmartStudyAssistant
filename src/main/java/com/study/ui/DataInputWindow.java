package com.study.ui;

import com.study.ui.StudyPlan;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 数据输入窗口 - 模态对话框
 * 符合实验02数据输入界面设计要求
 * 学号：你的学号  姓名：你的姓名
 */
public class DataInputWindow extends JDialog {
    // 各种输入控件
    private JTextField nameField;
    private JComboBox<String> categoryCombo;
    private JRadioButton lowPriorityRadio, mediumPriorityRadio, highPriorityRadio;
    private JCheckBox mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, 
                      fridayCheck, saturdayCheck, sundayCheck;
    private JSpinner hourSpinner, minuteSpinner;
    private JSlider difficultySlider;
    private JTextArea descriptionArea;
    private JList<String> tagList;
    private JComboBox<String> reminderCombo;
    private JTextField targetField;
    
    private StudyPlan createdPlan;
    private boolean isEditMode = false;
    
    public DataInputWindow(Frame parent) {
        super(parent, "新建学习计划 - 数据输入", true);
        this.isEditMode = false;
        initComponents();
        setupUI();
    }
    
    // 编辑模式的构造函数
    public DataInputWindow(Frame parent, StudyPlan planToEdit) {
        super(parent, "编辑学习计划 - 数据输入", true);
        this.isEditMode = true;
        initComponents();
        setupUI();
        loadPlanData(planToEdit);
    }

    private void initComponents() {
        setSize(750, 800);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // 标题
        JPanel titlePanel = createTitlePanel();
        
        // 主要输入区域（使用滚动面板）
        JScrollPane scrollPane = new JScrollPane(createInputPanel());
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // 底部按钮
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(245, 245, 245));
        
        String titleText = isEditMode ? "编辑学习计划" : "新建学习计划";
        String subtitleText = isEditMode ? "请修改以下学习计划信息" : "请填写以下信息创建您的学习计划";
        
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setForeground(new Color(41, 128, 185));
        
        JLabel subtitleLabel = new JLabel(subtitleText);
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 基本信息部分
        panel.add(createSection("基本信息", createBasicInfoPanel()));
        panel.add(Box.createVerticalStrut(15));
        
        // 时间设置部分
        panel.add(createSection("时间设置", createTimeSettingsPanel()));
        panel.add(Box.createVerticalStrut(15));
        
        // 优先级和难度部分
        panel.add(createSection("优先级与难度", createPriorityPanel()));
        panel.add(Box.createVerticalStrut(15));
        
        // 学习天数选择部分
        panel.add(createSection("学习天数", createWeekdaysPanel()));
        panel.add(Box.createVerticalStrut(15));
        
        // 标签和提醒部分
        panel.add(createSection("标签与提醒", createTagsAndRemindersPanel()));
        panel.add(Box.createVerticalStrut(15));
        
        // 详细描述部分
        panel.add(createSection("详细描述", createDescriptionPanel()));
        
        return panel;
    }
    
    private JPanel createSection(String title, JPanel content) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 13),
                new Color(52, 73, 94)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        content.setBackground(Color.WHITE);
        panel.add(content, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 计划名称
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel nameLabel = new JLabel("*计划名称：");
        nameLabel.setToolTipText("必填项 - 学号：你的学号");
        panel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        nameField = new JTextField(30);
        nameField.setToolTipText("请输入学习计划名称（必填） - 学号：你的学号 姓名：你的姓名");
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(nameField, gbc);
        
        // 学科分类
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel categoryLabel = new JLabel("*学科分类：");
        categoryLabel.setToolTipText("必填项 - 姓名：你的姓名");
        panel.add(categoryLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] categories = {"数学", "编程", "英语", "物理", "化学", "专业课", "其他"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setToolTipText("选择学科分类 - 学号：你的学号 姓名：你的姓名");
        panel.add(categoryCombo, gbc);
        
        // 学习目标
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        panel.add(new JLabel("学习目标："), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        targetField = new JTextField();
        targetField.setToolTipText("设定具体的学习目标 - 学号：你的学号");
        targetField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(targetField, gbc);
        
        return panel;
    }
    
    private JPanel createTimeSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 每日学习时长
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel timeLabel = new JLabel("*每日时长：");
        timeLabel.setToolTipText("必填项");
        panel.add(timeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        timePanel.setBackground(Color.WHITE);
        
        hourSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 12, 1));
        hourSpinner.setToolTipText("选择小时数 - 学号：你的学号 姓名：你的姓名");
        ((JSpinner.DefaultEditor) hourSpinner.getEditor()).getTextField().setColumns(3);
        
        minuteSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 59, 15));
        minuteSpinner.setToolTipText("选择分钟数 - 学号：你的学号");
        ((JSpinner.DefaultEditor) minuteSpinner.getEditor()).getTextField().setColumns(3);
        
        timePanel.add(hourSpinner);
        timePanel.add(new JLabel("小时"));
        timePanel.add(minuteSpinner);
        timePanel.add(new JLabel("分钟"));
        panel.add(timePanel, gbc);
        
        // 开始日期
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(new JLabel("开始日期："), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        datePanel.setBackground(Color.WHITE);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JTextField startDateField = new JTextField(sdf.format(new Date()), 12);
        startDateField.setToolTipText("开始日期 - 姓名：你的姓名");
        startDateField.setEditable(false);
        JButton calendarButton = new JButton("📅");
        calendarButton.setToolTipText("选择日期");
        calendarButton.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "日历选择器功能已加载\n学号：你的学号 姓名：你的姓名"));
        
        datePanel.add(startDateField);
        datePanel.add(calendarButton);
        panel.add(datePanel, gbc);
        
        return panel;
    }
    
    private JPanel createPriorityPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 优先级 - 单选按钮
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel priorityLabel = new JLabel("*优先级：");
        priorityLabel.setToolTipText("必填项 - 学号：你的学号");
        panel.add(priorityLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPanel priorityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        priorityPanel.setBackground(Color.WHITE);
        
        ButtonGroup priorityGroup = new ButtonGroup();
        lowPriorityRadio = new JRadioButton("低");
        lowPriorityRadio.setBackground(Color.WHITE);
        lowPriorityRadio.setToolTipText("低优先级 - 学号：你的学号");
        
        mediumPriorityRadio = new JRadioButton("中", true);
        mediumPriorityRadio.setBackground(Color.WHITE);
        mediumPriorityRadio.setToolTipText("中优先级 - 姓名：你的姓名");
        
        highPriorityRadio = new JRadioButton("高");
        highPriorityRadio.setBackground(Color.WHITE);
        highPriorityRadio.setToolTipText("高优先级 - 学号：你的学号 姓名：你的姓名");
        
        priorityGroup.add(lowPriorityRadio);
        priorityGroup.add(mediumPriorityRadio);
        priorityGroup.add(highPriorityRadio);
        
        priorityPanel.add(lowPriorityRadio);
        priorityPanel.add(mediumPriorityRadio);
        priorityPanel.add(highPriorityRadio);
        panel.add(priorityPanel, gbc);
        
        // 难度 - 滑块
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        panel.add(new JLabel("难度评估："), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPanel difficultyPanel = new JPanel(new BorderLayout(10, 0));
        difficultyPanel.setBackground(Color.WHITE);
        
        difficultySlider = new JSlider(1, 10, 5);
        difficultySlider.setMajorTickSpacing(1);
        difficultySlider.setPaintTicks(true);
        difficultySlider.setPaintLabels(true);
        difficultySlider.setBackground(Color.WHITE);
        difficultySlider.setToolTipText("拖动滑块选择难度级别（1-10） - 学号：你的学号 姓名：你的姓名");
        
        JLabel difficultyValueLabel = new JLabel("难度: 5");
        difficultyValueLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        difficultySlider.addChangeListener(e -> 
            difficultyValueLabel.setText("难度: " + difficultySlider.getValue()));
        
        difficultyPanel.add(difficultySlider, BorderLayout.CENTER);
        difficultyPanel.add(difficultyValueLabel, BorderLayout.EAST);
        panel.add(difficultyPanel, gbc);
        
        return panel;
    }
    
    private JPanel createWeekdaysPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel("选择学习天数：");
        label.setToolTipText("至少选择一天 - 学号：你的学号");
        panel.add(label);
        
        // 创建星期复选框
        mondayCheck = new JCheckBox("周一", true);
        mondayCheck.setBackground(Color.WHITE);
        mondayCheck.setToolTipText("周一学习 - 学号：你的学号");
        
        tuesdayCheck = new JCheckBox("周二", true);
        tuesdayCheck.setBackground(Color.WHITE);
        tuesdayCheck.setToolTipText("周二学习 - 姓名：你的姓名");
        
        wednesdayCheck = new JCheckBox("周三", true);
        wednesdayCheck.setBackground(Color.WHITE);
        wednesdayCheck.setToolTipText("周三学习");
        
        thursdayCheck = new JCheckBox("周四", true);
        thursdayCheck.setBackground(Color.WHITE);
        
        fridayCheck = new JCheckBox("周五", true);
        fridayCheck.setBackground(Color.WHITE);
        
        saturdayCheck = new JCheckBox("周六");
        saturdayCheck.setBackground(Color.WHITE);
        
        sundayCheck = new JCheckBox("周日");
        sundayCheck.setBackground(Color.WHITE);
        
        panel.add(mondayCheck);
        panel.add(tuesdayCheck);
        panel.add(wednesdayCheck);
        panel.add(thursdayCheck);
        panel.add(fridayCheck);
        panel.add(saturdayCheck);
        panel.add(sundayCheck);
        
        return panel;
    }
    
    private JPanel createTagsAndRemindersPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 标签选择 - 列表
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        panel.add(new JLabel("相关标签："), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] tags = {"基础知识", "重点难点", "考试准备", "实践项目", "理论学习", "复习巩固"};
        tagList = new JList<>(tags);
        tagList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tagList.setVisibleRowCount(4);
        tagList.setToolTipText("按住Ctrl可多选标签 - 学号：你的学号 姓名：你的姓名");
        JScrollPane tagScrollPane = new JScrollPane(tagList);
        tagScrollPane.setPreferredSize(new Dimension(0, 80));
        panel.add(tagScrollPane, gbc);
        
        // 提醒设置
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.weighty = 0;
        panel.add(new JLabel("提醒方式："), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] reminders = {"不提醒", "学习前15分钟", "学习前30分钟", "学习前1小时", "每天定时提醒"};
        reminderCombo = new JComboBox<>(reminders);
        reminderCombo.setSelectedIndex(2);
        reminderCombo.setToolTipText("选择提醒时间 - 学号：你的学号");
        panel.add(reminderCombo, gbc);
        
        return panel;
    }
    
    private JPanel createDescriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel("计划描述：");
        label.setToolTipText("详细描述学习计划 - 姓名：你的姓名");
        
        descriptionArea = new JTextArea(5, 40);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        descriptionArea.setToolTipText("输入学习计划的详细描述 - 学号：你的学号 姓名：你的姓名");
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(0, 120));
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panel.setBackground(Color.WHITE);

        String saveButtonText = isEditMode ? "更新计划" : "保存计划";
        JButton saveButton = createStyledButton(saveButtonText, new Color(46, 204, 113));
        saveButton.setToolTipText("保存学习计划 - 学号：你的学号 姓名：你的姓名");
        saveButton.addActionListener(e -> savePlan());

        JButton resetButton = createStyledButton("重置", new Color(52, 152, 219));
        resetButton.setToolTipText("重置所有输入 - 姓名：你的姓名");
        resetButton.addActionListener(e -> resetForm());

        JButton cancelButton = createStyledButton("取消", new Color(231, 76, 60));
        cancelButton.setToolTipText("取消并关闭");
        cancelButton.addActionListener(e -> {
            createdPlan = null;
            dispose();
        });

        panel.add(saveButton);
        panel.add(resetButton);
        panel.add(cancelButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 13));

        // 计算合适的宽度：文字长度 × 每个字符宽度 + 左右内边距
        FontMetrics metrics = button.getFontMetrics(button.getFont());
        int textWidth = metrics.stringWidth(text);
        int buttonWidth = textWidth + 40; // 左右各20像素内边距

        button.setPreferredSize(new Dimension(buttonWidth, 35));
        button.setMinimumSize(new Dimension(buttonWidth, 35));
        button.setMaximumSize(new Dimension(buttonWidth, 35));

        // 关键设置：禁止文字省略
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        // 其他样式设置
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
    
    private void savePlan() {
        // 验证必填项
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "请输入计划名称！",
                "验证失败",
                JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        // 验证至少选择一天
        if (!mondayCheck.isSelected() && !tuesdayCheck.isSelected() && 
            !wednesdayCheck.isSelected() && !thursdayCheck.isSelected() &&
            !fridayCheck.isSelected() && !saturdayCheck.isSelected() && 
            !sundayCheck.isSelected()) {
            JOptionPane.showMessageDialog(this,
                "请至少选择一天学习！",
                "验证失败",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 创建学习计划对象
        createdPlan = new StudyPlan();
        createdPlan.setPlanName(nameField.getText().trim());
        createdPlan.setCategory(categoryCombo.getSelectedItem().toString());
        createdPlan.setTarget(targetField.getText().trim());
        createdPlan.setDescription(descriptionArea.getText());
        createdPlan.setReminder(reminderCombo.getSelectedItem().toString());
        
        // 设置优先级
        if (lowPriorityRadio.isSelected()) {
            createdPlan.setPriority("低");
        } else if (mediumPriorityRadio.isSelected()) {
            createdPlan.setPriority("中");
        } else {
            createdPlan.setPriority("高");
        }
        
        // 设置每日时长
        String dailyTime = hourSpinner.getValue() + "小时 " + minuteSpinner.getValue() + "分钟";
        createdPlan.setDailyTime(dailyTime);
        
        // 设置难度
        createdPlan.setDifficulty(String.valueOf(difficultySlider.getValue()));
        
        // 设置学习天数
        StringBuilder days = new StringBuilder();
        if (mondayCheck.isSelected()) days.append("周一 ");
        if (tuesdayCheck.isSelected()) days.append("周二 ");
        if (wednesdayCheck.isSelected()) days.append("周三 ");
        if (thursdayCheck.isSelected()) days.append("周四 ");
        if (fridayCheck.isSelected()) days.append("周五 ");
        if (saturdayCheck.isSelected()) days.append("周六 ");
        if (sundayCheck.isSelected()) days.append("周日 ");
        createdPlan.setStudyDays(days.toString().trim());
        
        // 设置标签
        if (tagList.getSelectedValuesList() != null && !tagList.getSelectedValuesList().isEmpty()) {
            String[] selectedTags = tagList.getSelectedValuesList().toArray(new String[0]);
            createdPlan.setTags(selectedTags);
        }
        
        // 显示成功消息
        String successMessage = isEditMode ? "学习计划更新成功！" : "学习计划创建成功！";
        JOptionPane.showMessageDialog(this,
            successMessage + "\n\n" +
            "计划名称：" + createdPlan.getPlanName() + "\n" +
            "学科分类：" + createdPlan.getCategory() + "\n" +
            "每日时长：" + createdPlan.getDailyTime() + "\n" +
            "优先级：" + createdPlan.getPriority() + "\n" +
            "难度：" + createdPlan.getDifficulty() + "/10\n" +
            "学习天数：" + createdPlan.getStudyDays() + "\n" +
            "\n学号：你的学号 姓名：你的姓名",
            isEditMode ? "更新成功" : "保存成功",
            JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    }
    
    private void resetForm() {
        int choice = JOptionPane.showConfirmDialog(this,
            "确定要重置所有输入吗？",
            "确认重置",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            nameField.setText("");
            categoryCombo.setSelectedIndex(0);
            targetField.setText("");
            hourSpinner.setValue(1);
            minuteSpinner.setValue(30);
            mediumPriorityRadio.setSelected(true);
            difficultySlider.setValue(5);
            mondayCheck.setSelected(true);
            tuesdayCheck.setSelected(true);
            wednesdayCheck.setSelected(true);
            thursdayCheck.setSelected(true);
            fridayCheck.setSelected(true);
            saturdayCheck.setSelected(false);
            sundayCheck.setSelected(false);
            tagList.clearSelection();
            reminderCombo.setSelectedIndex(2);
            descriptionArea.setText("");
            
            JOptionPane.showMessageDialog(this, "表单已重置！\n学号：你的学号 姓名：你的姓名");
        }
    }
    
    // 加载计划数据（编辑模式）
    private void loadPlanData(StudyPlan plan) {
        if (plan == null) return;
        
        nameField.setText(plan.getPlanName());
        
        // 设置分类
        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            if (categoryCombo.getItemAt(i).equals(plan.getCategory())) {
                categoryCombo.setSelectedIndex(i);
                break;
            }
        }
        
        targetField.setText(plan.getTarget());
        descriptionArea.setText(plan.getDescription());
        
        // 设置优先级
        if ("低".equals(plan.getPriority())) {
            lowPriorityRadio.setSelected(true);
        } else if ("中".equals(plan.getPriority())) {
            mediumPriorityRadio.setSelected(true);
        } else {
            highPriorityRadio.setSelected(true);
        }
        
        // 设置每日时长（需要解析字符串）
        String dailyTime = plan.getDailyTime();
        if (dailyTime != null) {
            try {
                String[] parts = dailyTime.split(" ");
                if (parts.length >= 2) {
                    int hours = Integer.parseInt(parts[0].replace("小时", ""));
                    int minutes = Integer.parseInt(parts[1].replace("分钟", ""));
                    hourSpinner.setValue(hours);
                    minuteSpinner.setValue(minutes);
                }
            } catch (NumberFormatException e) {
                // 使用默认值
            }
        }
        
        // 设置难度
        try {
            difficultySlider.setValue(Integer.parseInt(plan.getDifficulty()));
        } catch (NumberFormatException e) {
            difficultySlider.setValue(5);
        }
        
        // 设置学习天数
        String studyDays = plan.getStudyDays();
        mondayCheck.setSelected(studyDays.contains("周一"));
        tuesdayCheck.setSelected(studyDays.contains("周二"));
        wednesdayCheck.setSelected(studyDays.contains("周三"));
        thursdayCheck.setSelected(studyDays.contains("周四"));
        fridayCheck.setSelected(studyDays.contains("周五"));
        saturdayCheck.setSelected(studyDays.contains("周六"));
        sundayCheck.setSelected(studyDays.contains("周日"));
        
        // 设置提醒
        if (plan.getReminder() != null) {
            for (int i = 0; i < reminderCombo.getItemCount(); i++) {
                if (reminderCombo.getItemAt(i).equals(plan.getReminder())) {
                    reminderCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    // 获取创建的计划对象
    public StudyPlan getCreatedPlan() {
        return createdPlan;
    }
}